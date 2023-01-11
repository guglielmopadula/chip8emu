package org.dssc.chip8;

import org.junit.jupiter.api.Test;

import java.beans.JavaBean;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

 class Tests {

    @Test
    void testClearRegister(){
        Registers reg=new Registers();
        Byte[] temp=new Byte[16];
        Arrays.fill(temp,(byte) 0);
        assertTrue(Arrays.equals(reg.v,temp));
    }
    @Test
    void testClearRam(){
        RAM ram=new RAM();
        Integer[] temp=new Integer[4096];
        Arrays.fill(temp,(int) 0);
        assertTrue(Arrays.equals(ram.memory,temp));
    }

    @Test
    void testReadRomFromString() {
        String dump = "test_opcode.txt";
        String path = "test_opcode.ch8";
        Chip8 mychip8 = new Chip8();
        Integer[] java_dump = mychip8.readRomFromString(path);
        String javaDumpString = "";
        Integer[] hexdump;


        try {
            List<Integer> tmp = Files.lines(Paths.get(dump)).map(Integer::parseInt).toList();
            int[] tmp2 = tmp.stream().mapToInt(i -> i).toArray();
            hexdump = Arrays.stream(tmp2).boxed().toArray(Integer[]::new);
            assertTrue(Arrays.equals(hexdump,  java_dump));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void fetchOpcodeFromRam(){
        Chip8 mychip8=new Chip8();
        String path="test_opcode.ch8";
        String dump="test_fetch.txt";
        Integer[] javaDump=mychip8.readRomFromString(path);
        mychip8.loadRomToRam(javaDump);
        Integer[] hexdump;

        mychip8.cpu.pc=512;
        try {
            List<Integer> tmp= Files.lines(Paths.get(dump)).map(Integer::parseInt).toList();
            int[] tmp2=tmp.stream().mapToInt(i->i).toArray();
            hexdump =Arrays.stream( tmp2 ).boxed().toArray( Integer[]::new );
            Integer[] opcodes = new Integer[hexdump.length];
            for (int i=0;i < hexdump.length; i++) {
                Integer opcode = mychip8.cpu.fetch();
                opcodes[i]=opcode;
                mychip8.cpu.pc+=2;
            }
            assertTrue(Arrays.equals(hexdump,opcodes));
        }

        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
