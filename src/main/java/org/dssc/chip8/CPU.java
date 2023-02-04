package org.dssc.chip8;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.ArrayDeque;
import java.security.SecureRandom;
import java.awt.Color;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;


class CPU {
    BaseKeyboard keyboard;
    RAM ram;
    Screen screen;
    ArrayDeque<Integer> stack;
    Timers timers;
    short  pc;
    int i;

    HashMap<Integer,generalExecute> opcodemap;
    private interface generalExecute {

        // Method signatures of pointed method

        void execute(Opcode opcode);

    }

    Integer[] vreg;
    SecureRandom rng;
    CPU(BaseKeyboard keyboard,RAM ram, Screen screen, Timers timers){
        this.keyboard=keyboard;
        this.ram=ram;
        this.screen=screen;
        this.timers=timers;
        this.stack = new ArrayDeque<>();
        this.rng= new SecureRandom();
        this.vreg = new Integer[16];
        Arrays.fill(this.vreg,0);
        this.opcodemap = new HashMap<>();
        configureHashMap(this.opcodemap);

    }

    void configureHashMap(HashMap<Integer,generalExecute> map){

        map.put(0x0000,this::execute0x0000);
        map.put(0x1000,this::execute0x1000);
        map.put(0x2000,this::execute0x2000);
        map.put(0x3000,this::execute0x3000);
        map.put(0x4000,this::execute0x4000);
        map.put(0x5000,this::execute0x5000);
        map.put(0x6000,this::execute0x6000);
        map.put(0x7000,this::execute0x7000);
        map.put(0x8000,this::execute0x8000);
        map.put(0x9000,this::execute0x9000);
        map.put(0xA000,this::execute0xA000);
        map.put(0xB000,this::execute0xB000);
        map.put(0xC000,this::execute0xC000);
        map.put(0xD000,this::execute0xD000);
        map.put(0xE000,this::execute0xE000);
        map.put(0xF000,this::execute0xF000);







    }

    Opcode fetch(){
        int opcode = 0;
        int lowByte = 0;
        int highByte = 0 ;
        lowByte = ram.getAt(this.pc);
        highByte = ram.getAt(this.pc+1);
        opcode=((lowByte<< 8 ) | (highByte ) );

        return new Opcode(opcode);
    }


    void execute0x0000(Opcode opcode){
        int nnn;
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
    }

    void execute0x1000(Opcode opcode) {
       this.pc = (short) opcode.nnn();;
    }

    void execute0x2000(Opcode opcode){
        this.stack.push((int) this.pc);
        this.pc = (short) opcode.nnn();
    }

    void execute0x3000(Opcode opcode){
        int x = opcode.x();
        int nn = opcode.nn();
        if (this.vreg[opcode.x()] == opcode.nn())
            this.pc+=2;
        this.pc+=2;
    }
    void execute0x4000(Opcode opcode){
        if (this.vreg[opcode.x()] != opcode.nn())
            this.pc+=2;
        this.pc+=2;
    }


    void execute0x5000(Opcode opcode) {
        if (Objects.equals(this.vreg[opcode.x()], this.vreg[opcode.y()]))
            this.pc += 2;
        this.pc += 2;
    }

    void execute0x6000(Opcode opcode) {
        this.vreg[opcode.x()] = opcode.nn();
        this.pc+=2;
    }


    void execute0x7000(Opcode opcode) {
        int x = opcode.x();
        this.vreg[x] += opcode.nn();
        this.vreg[x] = this.vreg[x] & 0xff; // in case of overflow !!
        this.pc+=2;
    }






    void execute0x8000(Opcode opcode){
        int x = opcode.x();
        int y = opcode.y();
        int vx;
        int vy;
        switch (opcode.value() & 0xf00f){

            case 0x8000:
                // 0x8xy0
                this.vreg[x]=this.vreg[y];
                this.pc+=2;
                break;
            case 0x8001:
                // 0x8xy1
                this.vreg[x]=this.vreg[x] | this.vreg[y];
                this.pc+=2;
                break;
            case 0x8002:
                // 0x8xy2
                this.vreg[x]=this.vreg[x] & this.vreg[y];
                this.pc+=2;
                break;
            case 0x8003:
                // 0x8xy3
                this.vreg[x]=this.vreg[x] ^ this.vreg[y];
                this.pc+=2;
                break;
            case 0x8004:
                // 0x8xy4
                this.vreg[x]=this.vreg[x] + this.vreg[y];
                if (this.vreg[x] > 255) {
                    this.vreg[x] = this.vreg[x] & 0xff;
                    this.vreg[0xf]  = 1;
                } else {
                    this.vreg[0xf]  = 0;
                }

                this.pc+=2;
                break;
            case 0x8005:
                // 0x8xy5
                vx = this.vreg[x];
                vy = this.vreg[y];

                this.vreg[x]=(this.vreg[x] - this.vreg[y]) & 0xff ;

                if (vx > vy) {
                    this.vreg[0xf]=1;
                } else {
                    this.vreg[0xf]=0;
                }
                this.pc+=2;
                break;

            case 0x8006:
                // 0x8xy6
                vx = this.vreg[x];

                this.vreg[x] = this.vreg[x] >>> 1;

                if ((vx& 0x1) == 1)
                    this.vreg[0xf] = 1;
                else
                    this.vreg[0xf] = 0;
                this.pc+=2;
                break;

            case 0x8007:
                // 0x8xy7

                vx = this.vreg[x]; //must change this !!! only a test
                vy = this.vreg[y];
                this.vreg[x] = (this.vreg[y] - this.vreg[x]) & 0xff;

                if (vy > vx) {
                    this.vreg[0xf] = 1;
                } else {
                    this.vreg[0xf] = 0;
                }

                this.pc+=2;
                break;
            case 0x800E:
                // 0x8xyE
                vx = this.vreg[x];
                this.vreg[x] = (this.vreg[x] << 1) & 0xff;
                this.vreg[0xf] = (vx & 0x80) >>> 7;
                this.pc+=2;

                break;
            default:
                throw new MessageException("opcode not found");
        }
    }


    void execute0x9000(Opcode opcode) {
        if (!Objects.equals(this.vreg[opcode.x()], this.vreg[opcode.y()]))
            this.pc+=2;
        this.pc+=2;
    }

    void execute0xA000(Opcode opcode) {
        this.i = opcode.nnn();
        this.pc +=2;
    }

    void execute0xB000(Opcode opcode) {
        this.pc =(short) (this.vreg[0] + (opcode.nnn()));
    }

    void execute0xC000(Opcode opcode) {
        this.vreg[opcode.x()] =  (rng.nextInt(255)) & (opcode.nn());
        this.pc +=2;    }

    void execute0xD000(Opcode opcode) {
        int vx = this.vreg[opcode.x()];
        int vy = this.vreg[opcode.y()];
        this.renderSprite(vx, vy, opcode.n(), this.i);
        this.pc += 2;

    }
    void execute0xE000(Opcode opcode) {
       int x=opcode.x();
        switch (opcode.value() & 0xf0ff) {
            case 0xE09E:
                if (this.keyboard.key() == this.vreg[x]) {
                    this.pc += 2;
                }
                this.pc += 2;
                break;
            case 0xE0A1:
                if (this.keyboard.key() != this.vreg[x]) {
                    this.pc += 2;
                }
                this.pc += 2;
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
                this.vreg[x]=this.timers.delaytimer;
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
                this.vreg[x]=key;
                this.pc+=2;
                break;

            case 0xF015:
                this.timers.delaytimer =this.vreg[x];
                this.pc+=2;
                break;
            case 0xF018:
                this.timers.soundtimer =this.vreg[x];
                this.pc+=2;
                break;

            case 0xF01E:
                // 0xFx1E
                this.i += this.vreg[x];
                this.pc +=2;
                break;
            case 0xF029:
                this.i = this.vreg[x] * 5 ;
                this.pc+=2;
                break;

            case 0xF033:
                vx = this.vreg[x];
                this.ram.setAt(this.i,vx / 100);
                this.ram.setAt(this.i+ 1,(vx % 100)/10);
                this.ram.setAt(this.i+ 2,(vx % 100)%10);
                this.pc+=2;
                break;

            case 0xF055:
                // 0xFx55
                IntStream.range(0,x+1).forEach(counter-> this.ram.setAt(this.i + counter,this.vreg[counter]));
                this.pc += 2;

                break;

            case 0xF065:
                IntStream.range(0,x+1).forEach(counter->   this.vreg[counter] =  this.ram.getAt(this.i + counter));
                this.pc += 2;
                break;
            default:
                throw new MessageException("File not found");


        }
    }
    void decodeExecute(Opcode opcode) {

        this.opcodemap.get(opcode.value() & 0xf000).execute(opcode);




    }
    void decodeExecute(int opcodeInteger) {
        Opcode opcode = new Opcode(opcodeInteger);
        this.decodeExecute(opcode);

    }


        void renderspriteInner(int riga,int colonna){
            if (screen.getPixel((riga ) % 32, (colonna ) % 64)== -1) {
                this.vreg[0xf] = 1;
                screen.drawPixel((riga ) % 32, (colonna ) % 64,Color.BLACK);
            }
            else {
                screen.drawPixel((riga ) % 32, (colonna ) % 64, Color.WHITE);
            }
        }

        void renderSprite(int x, int y, int n, int i){
            this.vreg[0xf] = 0;
            IntStream.range(0,n).forEach(riga->IntStream.range(0,8).filter(colonna->(this.ram.getAt(i+riga) & (0x80 >> colonna) ) != 0).forEach(colonna->renderspriteInner(riga+y,colonna+x)));

        }


}
