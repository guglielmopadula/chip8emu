package org.dssc.chip8;


class CPU {
    Keyboard keyboard;
    RAM ram;
    Registers registers;
    Screen screen;
    Stack stack;
    Timers timers;
    short  pc;
    //private Boolean[][]  chip8_pixels = new Boolean[64][32]; //


    CPU(Keyboard keyboard,RAM ram, Registers registers, Screen screen, Timers timers){
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
        //NON VOGLIO MERGIARE
    }

    void decodeExecute(int opcode) {
        switch (opcode & 0xf000) {
            case 0x8000:
                //this.registers.v[]
                //this.registers.v[]
                break;
            case 0XD000:
                //this OPCODE will render a sprite
                int N = opcode & 0x000f;
                int Vx = this.registers.v[(opcode & 0x0f00) >> 8];
                int Vy = this.registers.v[(opcode & 0x00f0) >> 4];
                this.renderSprite(Vx,Vy,N);
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
