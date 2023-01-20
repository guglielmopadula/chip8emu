package org.dssc.chip8;
import java.io.*;
import java.util.stream.IntStream;
import java.util.Stack;

public class BaseChip8  {
    BaseKeyboard basekeyboard;
    RAM ram;
    Registers registers;
    Screen screen;
    Timers timers;
    CPU cpu;

      BaseChip8(){
        this.basekeyboard=new BaseKeyboard();
        this.ram=new RAM();
        this.registers=new Registers();
        this.screen=new Screen(5); // extract this MAGIC number !
        this.timers=new Timers();
        this.cpu=new CPU(basekeyboard,ram,registers,screen,timers);

    }



        public void startChip8(String filePath) {
         Integer[] rom = readRomFromString(filePath);
         loadRomToRam(rom);
        }
       Integer[] readRomFromString(String filePath){
        File romFile=new File(filePath);
        try (DataInputStream romStream = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream(romFile)))
        ){
            byte[] romArray = new byte[(int) romFile.length()];
            romStream.readFully(romArray);
            Integer[] romArrayObj=new Integer[(int) romFile.length()];
            int i=0;
            for(byte b: romArray)
                romArrayObj[i++] = b & 0xff;
            return romArrayObj;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

     void loadRomToRam(Integer[] rom) {
        IntStream.range(0,rom.length).forEach(i -> this.ram.memory[i+512]=rom[i]);
    }

}
