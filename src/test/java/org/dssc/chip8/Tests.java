package org.dssc.chip8;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Tests {

    @Test
    void test_clear_register(){
        Registers reg=new Registers();
        Byte[] temp=new Byte[16];
        Arrays.fill(temp,(byte) 0);
        assertTrue(Arrays.equals(reg.V,temp));
    }
    @Test
    void test_clear_ram(){
        RAM ram=new RAM();
        Integer[] temp=new Integer[4096];
        Arrays.fill(temp,(int) 0);
        assertTrue(Arrays.equals(ram.memory,temp));
    }

    @Test
    void test_read_rom_from_string(){
        String dump="test_opcode.txt";
        String path="test_opcode.ch8";
        Chip8 mychip8=new Chip8();
        Integer[] java_dump=mychip8.read_rom_from_string(path);
        String java_dump_string="";
        for (Integer x: java_dump) {
            java_dump_string+=x.toString()+"\n";
        }
        try {
            String content = Files.readString(Paths.get(dump));
            assertTrue(content.equals(java_dump_string));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void fetch_opcode_from_ram(){un
        Chip8 mychip8=new Chip8();
        String path="test_opcode.ch8";
        String dump="test_fetch.txt";
        Integer[] java_dump=mychip8.read_rom_from_string(path);
        mychip8.loadRomToRam(java_dump);
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
            System.out.println("ciao");
        }

        catch (IOException e) {
            throw new RuntimeException(e);
        }




    }


}
