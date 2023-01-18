package org.dssc.chip8;
import javax.swing.*;
import java.io.*;
import java.util.stream.IntStream;

public class Chip8 extends JFrame{
    Keyboard keyboard;
    RAM ram;
    Registers registers;
    Screen screen;
    Stack stack;
    Timers timers;
    CPU cpu;

g     public Chip8(){
        this.keyboard=new Keyboard();
        this.ram=new RAM();
        this.registers=new Registers();
        this.screen=new Screen(5); // extract this MAGIC number !
        this.timers=new Timers();
        this.cpu=new CPU(keyboard,ram,registers,screen,timers);
        this.setJavaComponents();

    }

    void setJavaComponents(){
        JPanel p= new JPanel();
        this.setSize(12*62,12*31);
        this.addKeyListener(this.keyboard);
        this.pack();
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
