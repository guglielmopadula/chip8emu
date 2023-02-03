package org.dssc.chip8;

import java.util.Objects;
import java.util.ArrayDeque;
import java.security.SecureRandom;
import java.awt.Color;



class CPU {
    BaseKeyboard keyboard;
    RAM ram;
    Registers registers;
    Screen screen;
    ArrayDeque<Integer> stack;
    Timers timers;
    short  pc;
    int i;

    SecureRandom rng;
    CPU(BaseKeyboard keyboard,RAM ram, Registers registers, Screen screen, Timers timers){
        this.keyboard=keyboard;
        this.ram=ram;
        this.registers=registers;
        this.screen=screen;
        this.timers=timers;
        this.stack = new ArrayDeque<>();
        this.rng= new SecureRandom();
    }

     Opcode fetch(){
        int opcode = 0;
        int lowByte = 0;
        int highByte = 0 ;
        lowByte = ram.memory[this.pc];
        highByte = ram.memory[this.pc+1];
         opcode=((lowByte<< 8 ) | (highByte ) );

        return new Opcode(opcode);
    }

    void execute0x8000(Opcode opcode){
        int x = opcode.x();
        int y = opcode.y();
        switch (opcode.value() & 0xf00f){

            case 0x8000:
                // 0x8xy0
                this.registers.v[x]=this.registers.v[y];
                this.pc+=2;
                break;
            case 0x8001:
                // 0x8xy1
                this.registers.v[x]=this.registers.v[x] | this.registers.v[y];
                this.pc+=2;
                break;
            case 0x8002:
                // 0x8xy2
                this.registers.v[x]=this.registers.v[x] & this.registers.v[y];
                this.pc+=2;
                break;
            case 0x8003:
                // 0x8xy3
                this.registers.v[x]=this.registers.v[x] ^ this.registers.v[y];
                this.pc+=2;
                break;
            case 0x8004:
                // 0x8xy4
                this.registers.v[x]=this.registers.v[x] + this.registers.v[y];
                if (this.registers.v[x] > 255) {
                    this.registers.v[x] = this.registers.v[x] & 0xff;
                    this.registers.v[0xf]  = 1;
                } else {
                    this.registers.v[0xf]  = 0;
                }

                this.pc+=2;
                break;
            case 0x8005:
                // 0x8xy5
                int tmpx = this.registers.v[x];
                int tmpy = this.registers.v[y];

                this.registers.v[x]=(this.registers.v[x] - this.registers.v[y]) & 0xff ;

                if (tmpx > tmpy) {
                    this.registers.v[0xf]=1;
                } else {
                    this.registers.v[0xf]=0;
                }
                this.pc+=2;
                break;

            case 0x8006:
                // 0x8xy6
                tmpx = this.registers.v[x];

                this.registers.v[x] = this.registers.v[x] >>> 1;

                if ((tmpx& 0x1) == 1)
                    this.registers.v[0xf] = 1;
                else
                    this.registers.v[0xf] = 0;
                this.pc+=2;
                break;

            case 0x8007:
                // 0x8xy7

                tmpx = this.registers.v[x]; //must change this !!! only a test
                tmpy = this.registers.v[y];
                this.registers.v[x] = (this.registers.v[y] - this.registers.v[x]) & 0xff;

                if (tmpy > tmpx) {
                    this.registers.v[0xf] = 1;
                } else {
                    this.registers.v[0xf] = 0;
                }

                this.pc+=2;
                break;
            case 0x800E:
                // 0x8xyE
                tmpx = this.registers.v[x];
                this.registers.v[x] = (this.registers.v[x] << 1) & 0xff;
                this.registers.v[0xf] = (tmpx & 0x80) >>> 7;
                this.pc+=2;

                break;
            default:
                throw new MessageException("opcode not found");
        }
    }

    void execute0xF000(Opcode opcode){
        int x = opcode.x();
        int vx;

        switch (opcode.value() & 0xf0ff) {
            case 0xF007:
                this.registers.v[x]=this.timers.delaytimer;
                this.pc+=2;
                break;
            case 0xF00A:
                int key;
                while((key=this.keyboard.key()) == -1) {
                    try {
                        Thread.sleep(0);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new MessageException("Failed to sleep thread");
                    }

                }
                this.registers.v[x]=key;
                this.pc+=2;
                break;

            case 0xF015:
                this.timers.delaytimer =this.registers.v[x];
                this.pc+=2;
                break;
            case 0xF018:
                this.timers.soundtimer =this.registers.v[x];
                this.pc+=2;
                break;

            case 0xF01E:
                // 0xFx1E
                this.i += this.registers.v[x];
                this.pc +=2;
                break;
            case 0xF029:
                this.i = this.registers.v[x] * 5 ;
                this.pc+=2;
                break;

            case 0xF033:
                vx = this.registers.v[x];
                this.ram.memory[this.i] = vx / 100;
                this.ram.memory[this.i+ 1] = (vx % 100)/10;
                this.ram.memory[this.i+ 2] = (vx % 100)%10;
                this.pc+=2;
                break;

            case 0xF055:
                // 0xFx55
                for(int counter=0;counter <= x; counter++){
                    this.ram.memory[this.i + counter]=this.registers.v[counter];
                }
                this.pc += 2;

                break;

            case 0xF065:
                for(int counter=0;counter <= x; counter++){
                    this.registers.v[counter] =  this.ram.memory[this.i + counter];
                }

                this.pc += 2;
                break;
            default:
                throw new MessageException("File not found");


        }
    }
    void decodeExecute(Opcode opcode) {

        int x = opcode.x();
        int y =  opcode.y();;
        int vx;
        int vy;
        int n =  opcode.n();;
        int nn =  opcode.nn();;
        int nnn = opcode.nnn();;

        switch (opcode.value() & 0xf000) {
            case 0x0000:
                switch (opcode.value() & 0x0fff) {
                    case 0x00EE:
                        // return from subroutine
                        this.pc=this.stack.pop().shortValue();
                        this.pc+=2;
                        break;
                    case 0x00E0:
                        this.screen.clearScreen();
                        this.pc+=2;
                        break;
                    default:
                        // if not 00E0 and 00EE, the opcode must be 0NNN
                        // 0NNN call a subroutine at NNN
                        nnn = opcode.nnn();
                        this.stack.push((int) this.pc);
                        this.pc = (short) nnn;
                        break;
                }
                break;
            case 0x1000:
                this.pc = (short) nnn;
                break;
            case 0x2000:
                this.stack.push((int) this.pc);
                this.pc = (short) nnn;
                break;
            case  0x3000:
                // 0x3xNN
                if (this.registers.v[x] == nn)
                    this.pc+=2;
                this.pc+=2;
                break;
            case  0x4000:
                // 0x4xNN
                if (this.registers.v[x] != nn)
                    this.pc+=2;
                this.pc+=2;
                break;
            case  0x5000:
                // 0x5xy0
                if (Objects.equals(this.registers.v[x], this.registers.v[y]))
                    this.pc+=2;
                this.pc+=2;
                break;

            case  0x6000:
                // 0x6xNN
                this.registers.v[x] = nn;
                this.pc+=2;
                break;

            case  0x7000:
                // 0x7xNN
                this.registers.v[x] += nn;
                this.registers.v[x] = this.registers.v[x] & 0xff; // in case of overflow !!
                this.pc+=2;
                break;
            case 0x8000:
                execute0x8000(opcode);
                break;
            case 0x9000:
                // 0X9xy0
                if (!Objects.equals(this.registers.v[x], this.registers.v[y]))
                    this.pc+=2;
                this.pc+=2;
                break;
            case 0xA000:
                // 0xANNN
                this.i = opcode.nnn();
                this.pc +=2;
                break;
            case 0xB000:
                // 0xBNNN
                this.pc =(short) (this.registers.v[0] + (opcode.nnn()));
                break;
            case 0xC000:
                // 0xCxNN
                this.registers.v[x] =  (rng.nextInt(255)) & (opcode.nn());
                this.pc +=2;
                break;
            case 0XD000:
                //this OPCODE will render a sprite, 0xDxun
                vx = this.registers.v[x];
                vy = this.registers.v[y];
                this.renderSprite(vx,vy,n,this.i);
                this.pc+=2;
                break;

            case 0xE000:
                switch (opcode.value() & 0xf0ff) {
                    case 0xE09E:
                        if (this.keyboard.key() == this.registers.v[x]) {
                            this.pc +=2;
                        }
                        this.pc +=2;
                        break;
                    case 0xE0A1:
                        if (this.keyboard.key() != this.registers.v[x]) {
                            this.pc +=2;
                        }
                        this.pc +=2;
                        break;
                    default:
                        throw new MessageException("opcode not found");
                }
                break;
            case 0xF000:
                execute0xF000(opcode);
                break;
            default:
                throw new MessageException("File not found");
        }


    }
    void decodeExecute(int opcodeInteger) {
        Opcode opcode = new Opcode(opcodeInteger);
        this.decodeExecute(opcode);

    }
        void renderSprite(int x, int y, int n, int i){
        this.registers.v[0xf] = 0;
        for(int riga=0;riga < n;riga++){
            int currentline = this.ram.memory[i+riga];
            for (int colonna=0;colonna<8;colonna++){
                if ((currentline & (0x80 >> colonna)   ) != 0 ) {
                    if (screen.getPixel((riga + y) % 32, (colonna + x) % 64)== -1) {
                        this.registers.v[0xf] = 1;
                        screen.drawPixel((riga + y) % 32, (colonna + x) % 64,Color.BLACK);
                    }
                    else {
                        screen.drawPixel((riga + y) % 32, (colonna + x) % 64, Color.WHITE);
                    }
                }
            }
        }
        //opzione 1 - this.screen.drawpixel()
        //opzione 2 - scrivo solo su chip8_pixel poi faccio una routine che renderizza a 30 FPS
        //opzione 3 - a ogni draw scrivo tutto quanto

    }

}
