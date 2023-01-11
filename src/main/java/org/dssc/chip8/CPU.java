package org.dssc.chip8;


class CPU {
    Keyboard keyboard;
    RAM ram;
    Registers registers;
    Screen screen;
    Stack stack;
    Timers timers;
    short  pc;


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
    }
}
