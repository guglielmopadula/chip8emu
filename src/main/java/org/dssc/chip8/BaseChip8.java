package org.dssc.chip8;
import java.io.*;
import java.util.stream.IntStream;

public class BaseChip8  {
    BaseKeyboard basekeyboard;
    RAM ram;
    Screen screen;
    Timers timers;
    CPU cpu;

      BaseChip8(int scale){
        this.basekeyboard=new BaseKeyboard();
        this.ram=new RAM();
        this.screen=new Screen(scale);
        this.timers=new Timers();
        this.cpu=new CPU(basekeyboard,ram,screen,timers);
    }

    BaseChip8(){
        this.basekeyboard=new BaseKeyboard();
        this.ram=new RAM();
        this.screen=new Screen(5);
        this.timers=new Timers();
        this.cpu=new CPU(basekeyboard,ram,screen,timers);

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
            throw new MessageException("File not found");
        }
    }

     void loadRomToRam(Integer[] rom) {
        IntStream.range(0,rom.length).forEach(i -> this.ram.setAt(i+512,rom[i]));
    }

}
