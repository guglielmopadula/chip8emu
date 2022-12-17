package org.dssc.chip8;

import java.util.Timer;

public class CPU {
    Keyboard keyboard;
    RAM ram;
    Registers registers;
    Screen screen;
    Stack stack;
    Timers timers;
    short  pc;


    public CPU(Keyboard keyboard,RAM ram, Registers registers, Screen screen, Timers timers){
        this.keyboard=keyboard;
        this.ram=ram;
        this.registers=registers;
        this.screen=screen;
        this.timers=timers;
    }

    public Short fetch(){
        Short OPCODE = 0;
        Short lowByte = 0;
        Short highByte = 0;

        lowByte = (short) ram.memory[this.pc];
        this.pc+=1;
        highByte =  (short) ram.memory[this.pc];
        this.pc+=1;

        //System.out.println(lowByte);
        //System.out.println(highByte);

        OPCODE= (short) ((lowByte<<8) | (highByte));

        return OPCODE;
    }
}
