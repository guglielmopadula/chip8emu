package org.dssc.chip8;
import java.io.*;
import java.util.Arrays;
import java.util.stream.IntStream;

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

    public Byte[] read_rom_from_string(String file_path){
        File rom_file=new File(file_path);
        try {
            DataInputStream rom_stream = new DataInputStream(
                    new BufferedInputStream(
                            new FileInputStream(rom_file)));
            byte[] rom_array = new byte[(int) rom_file.length()];
            rom_stream.read(rom_array);
            Byte[] Rom_Array=new Byte[(int) rom_file.length()];
            int i=0;
            for(byte b: rom_array)
                Rom_Array[i++] = b;
            return Rom_Array;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void loadRomToRam(Byte[] rom) {
        IntStream.range(0,rom.length).forEach(i -> this.ram.memory[i+512]=rom[i]);
    }

}
