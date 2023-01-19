package org.dssc.chip8;

import java.util.Objects;

class CPU {
    BaseKeyboard keyboard;
    RAM ram;
    Registers registers;
    Screen screen;
    Stack stack;
    Timers timers;
    short  pc;
    //private Boolean[][]  chip8_pixels = new Boolean[64][32]; //
    int i;

    CPU(BaseKeyboard keyboard,RAM ram, Registers registers, Screen screen, Timers timers){
        this.keyboard=keyboard;
        this.ram=ram;
        this.registers=registers;
        this.screen=screen;
        this.timers=timers;
    }

     int fetch(){
        int opcode = 0;
        int lowByte = 0;
        int highByte = 0 ;
        lowByte = ram.memory[this.pc];
        highByte = ram.memory[this.pc+1];
         opcode=((lowByte<<8) | highByte);
        return opcode;
    }

    void decodeExecute(int opcode) {
        int x,y;
        int nn;
        int nnn;

        switch (opcode & 0xf000) {
            case  0x3000:
                // 0x3xNN
                x = (opcode & 0x0f00) >> 8;
                nn = opcode & 0x00ff;
                if (this.registers.v[x] == nn)
                    this.pc+=2;
                this.pc+=2;
                break;
            case  0x4000:
                // 0x4xNN
                x = (opcode & 0x0f00) >> 8;
                nn = opcode & 0x00ff;
                if (this.registers.v[x] != nn)
                    this.pc+=2;
                this.pc+=2;
                break;
            case  0x5000:
                // 0x5xy0
                x = (opcode & 0x0f00) >> 8;
                y = (opcode & 0x00f0) >> 4;
                if (Objects.equals(this.registers.v[x], this.registers.v[y]))
                    this.pc+=2;
                this.pc+=2;
                break;

            case  0x6000:
                // 0x6xNN
                x = (opcode & 0x0f00) >> 8;
                nn = opcode & 0x00ff;
                this.registers.v[x] = nn;
                break;

            case  0x7000:
                // 0x7xNN
                x = (opcode & 0x0f00) >> 8;
                nn = opcode & 0x00ff;
                this.registers.v[x] += nn;
                break;
            case 0x8000:
                switch (opcode & 0xf00f){
                    case 0x8000:
                        // 0x8xy0
                        x = (opcode & 0x0f00) >> 8;
                        y = (opcode & 0x00f0) >> 4;
                        this.registers.v[x]=this.registers.v[y];
                        this.pc+=2;
                        break;
                    case 0x8001:
                        // 0x8xy1
                        x = (opcode & 0x0f00) >> 8;
                        y = (opcode & 0x00f0) >> 4;
                        this.registers.v[x]=this.registers.v[x] | this.registers.v[y];
                        this.pc+=2;
                        break;
                    case 0x8002:
                        // 0x8xy2
                        x = (opcode & 0x0f00) >> 8;
                        y = (opcode & 0x00f0) >> 4;
                        this.registers.v[x]=this.registers.v[x] & this.registers.v[y];
                        this.pc+=2;
                        break;
                    case 0x8003:
                        // 0x8xy3
                        x = (opcode & 0x0f00) >> 8;
                        y = (opcode & 0x00f0) >> 4;
                        this.registers.v[x]=this.registers.v[x] ^ this.registers.v[y];
                        this.pc+=2;
                        break;
                    case 0x8004:
                        // 0x8xy4
                        x = (opcode & 0x0f00) >> 8;
                        y = (opcode & 0x00f0) >> 4;
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
                        x = (opcode & 0x0f00) >> 8;
                        y = (opcode & 0x00f0) >> 4;
                        if (this.registers.v[x] > this.registers.v[y] ) {
                            this.registers.v[0xf]=0; //ricontrollare se non è il contrario
                        } else {
                            this.registers.v[0xf]=1;
                        }
                        this.registers.v[x]=this.registers.v[x] - this.registers.v[y];
                        this.pc+=2;
                        break;

                    case 0x8006:
                        // 0x8xy6
                        x = (opcode & 0x0f00) >>> 8;
                        this.registers.v[0xf] = this.registers.v[x] & 0x1;
                        this.registers.v[x] = this.registers.v[x] >>> 1;
                        this.pc+=2;
                        break;
                    case 0x8007:
                        // 0x8xy7
                        x = (opcode & 0x0f00) >> 8;
                        y = (opcode & 0x00f0) >> 4;
                        if (this.registers.v[y] > this.registers.v[x] ) {
                            this.registers.v[0xf]=0;  //ricontrollare se non è il contrario
                        } else {
                            this.registers.v[0xf] = 1;
                        }
                        this.registers.v[x]=this.registers.v[y] - this.registers.v[x];
                        this.pc+=2;
                        break;
                    case 0x800E:
                        // 0x8xyE
                        x = (opcode & 0x0f00) >> 8;
                        this.registers.v[0xf] = this.registers.v[x] & 0x80;
                        this.registers.v[x] = this.registers.v[x] << 1;
                        System.out.println(this.pc);
                        this.pc+=2;
                        System.out.println(this.pc);

                        break;
                }
                break;
            case 0x9000:
                // 0X9xy0
                x = (opcode & 0x0f00) >> 8;
                y = (opcode & 0x00f0) >> 4;
                if (!Objects.equals(this.registers.v[x], this.registers.v[y]))
                    this.pc+=2;
                this.pc+=2;
                break;
            case 0xA000:
                // 0xANNN
                this.i = opcode & 0x0fff;
                this.pc +=2;
                break;
            case 0xB000:
                // 0xBNNN
                this.pc =(short) (this.registers.v[0] + (opcode & 0x0fff));
                break;
            case 0xC000:
                // 0xCxNN
                x = (opcode & 0x0f00) >> 8;
                this.registers.v[x] = (int) (Math.random()*255) & (opcode & 0x00ff);
                this.pc +=2;
                break;
            case 0XD000:
                //this OPCODE will render a sprite
                int N = opcode & 0x000f;
                int Vx = this.registers.v[(opcode & 0x0f00) >> 8];
                int Vy = this.registers.v[(opcode & 0x00f0) >> 4];
                //this.renderSprite(Vx,Vy,N);
                break;
            case 0xF000:
                switch (opcode & 0xf0ff) {
                    case 0xF007:
                        break;
                    case 0xF00A:
                        break;
                    case 0xF015:
                        break;
                    case 0xF018:
                        break;
                    case 0xF01E:
                        // 0xFx1E
                        x = (opcode & 0x0f00) >> 8;
                        this.i += this.registers.v[x];
                        this.pc +=2;
                        break;
                    case 0xF029:
                        break;
                    case 0xF033:
                        break;

                    case 0xF055:
                        // 0xFx55
                        x = (opcode & 0x0f00) >> 8;
                        for(int counter=0;counter < x; counter++){
                            this.ram.memory[this.i + counter]=this.registers.v[counter];
                        }
                        this.pc += 2;
                        break;

                    case 0xF065:
                        x = (opcode & 0x0f00) >> 8;
                        for(int counter=0;counter < x; counter++){
                            this.registers.v[counter] =  this.ram.memory[this.i + counter];
                        }
                        this.pc += 2;
                        break;

                }
                break;

        }


    }
    void renderSprite(int x, int y, int N){
        //this.chip8_pixels[][];
        //this.screen.render(chip8_pixels);
        for(int riga=0;riga<N;riga++){
            for (int colonna=0;colonna<8;colonna++){
                screen.DrawPixel(riga,colonna);
            }
        }
        //opzione 1 - this.screen.drawpixel()
        //opzione 2 - scrivo solo su chip8_pixel poi faccio una routine che renderizza a 30 FPS
        //opzione 3 - a ogni draw scrivo tutto quanto

    }



}
