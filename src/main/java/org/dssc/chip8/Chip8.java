package org.dssc.chip8;

public class Chip8 {
    Keyboard keyboard;
    RAM ram;
    Registers registers;
    Screen screen;
    Stack stack;
    Timers timers;
    CPU cpu;
    public Chip8(){
        this.keyboard=new Keyboard();
        this.ram=new RAM();
        this.registers=new Registers();
        this.screen=new Screen();
        this.timers=new Timers();
        this.cpu=new CPU(keyboard,ram,registers,screen,timers);
    }

}
