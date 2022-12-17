package org.dssc.chip8;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

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
        try {
            String content = Files.readString(Paths.get(dump));
            assertTrue(content.equals(java_dump_string));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void fetch_opcode_from_ram(){
        Chip8 mychip8=new Chip8();
        String path="test_opcode.ch8";
        String dump="test_fetch.txt";
        Byte[] java_dump=mychip8.read_rom_from_string(path);
        mychip8.loadRomToRam(java_dump);

        mychip8.cpu.pc=512;
        try {
            String content = Files.readString(Paths.get(dump));
            long steps = content.lines().count();
            String fetch_dump_string="";
            for (int i=0;i < steps ; i++) {
                Short opcode = mychip8.cpu.fetch();
                //opcode = (short) ( ((opcode & 0x00ff) << 8) + ((opcode & 0xff00) >> 8) );
                    opcode= Short.reverseBytes(opcode);
                 int test= Short.toUnsignedInt(opcode);
                 System.out.println(test);
                fetch_dump_string+=opcode.toString()+"\n";
            }

            assertTrue(content.equals(fetch_dump_string));

            System.out.println("ciao");
        }

        catch (IOException e) {
            throw new RuntimeException(e);
        }




    }


}
