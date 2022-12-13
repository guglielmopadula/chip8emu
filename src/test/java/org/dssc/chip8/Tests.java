package org.dssc.chip8;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        Byte[] temp=new Byte[4096];
        Arrays.fill(temp,(byte) 0);
        assertTrue(Arrays.equals(ram.memory,temp));
    }

    @Test
    void test_read_rom_from_string(){
        String dump="test_opcode.txt";
        String path="test_opcode.ch8";
        Chip8 mychip8=new Chip8();
        Byte[] java_dump=mychip8.read_rom_from_string(path);
        String java_dump_string="";
        for (Byte x: java_dump) {
            java_dump_string+=x.toString()+"\n";
        }
        System.out.println(java_dump_string.length());
        try {
            String content = Files.readString(Paths.get(dump));
            assertTrue(content.equals(java_dump_string));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}
