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
        short OPCODE = 0;
        short low_byte = 0;
        short high_byte = 0;

        return (short) 0;

    }
}
