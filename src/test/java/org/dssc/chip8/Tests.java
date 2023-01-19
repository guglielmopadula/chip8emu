package org.dssc.chip8;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import java.awt.*;
import java.awt.image.BufferedImage;
import static java.awt.image.BufferedImage.TYPE_BYTE_BINARY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.concurrent.ThreadLocalRandom;


import static org.junit.jupiter.api.Assertions.assertTrue;

 class Tests {
     static boolean compareImages(BufferedImage Image1, BufferedImage Image2){
         int width=(int) Image1.getWidth();
         int height=(int) Image2.getHeight();
         boolean flag=true;
         for(int i=0; i<width; i++){
             for (int j=0; j<height; j++){
                 flag=flag & (Image1.getRGB(i,j)==Image2.getRGB(i,j));
             }
         }
         return flag;
     }

    @Test
    void testClearRegister(){
        Registers reg=new Registers();
        Integer[] temp=new Integer[16];
        Arrays.fill(temp,(int) 0);
        assertTrue(Arrays.equals(reg.v,temp));
    }

    //@Test
    //void testClearRam() {
    //    RAM ram=new RAM();
    //    Integer[] temp=new Integer[4096];
    //    Arrays.fill(temp,(int) 0);
    //    assertTrue(Arrays.equals(ram.memory,temp));
    //}

    @ParameterizedTest
    @ValueSource(strings = {"rom1", "rom2"})
    void testReadRomFromString(String rom) throws IOException {
        String dump = rom+".txt";
        String path = rom+".ch8";
        BaseChip8 mychip8 = new BaseChip8();
        Integer[] java_dump = mychip8.readRomFromString(path);
        String javaDumpString = "";
        Integer[] hexdump;


        try {
            List<Integer> tmp = Files.lines(Paths.get(dump)).map(Integer::parseInt).toList();
            int[] tmp2 = tmp.stream().mapToInt(i -> i).toArray();
            hexdump = Arrays.stream(tmp2).boxed().toArray(Integer[]::new);
            assertTrue(Arrays.equals(hexdump,  java_dump));
        } catch (IOException e) {
            assertTrue(false,"IO exception");
            throw new IOException(e);
        }
    }
    @Test
    void fetchOpcodeFromRam() throws IOException{
        BaseChip8 mychip8=new BaseChip8();
        String path="test_opcode.ch8";
        String dump="test_fetch.txt";
        Integer[] javaDump=mychip8.readRomFromString(path);
        mychip8.loadRomToRam(javaDump);
        Integer[] hexdump;

        mychip8.cpu.pc=512;
        try {
            List<Integer> opcodesList= Files.lines(Paths.get(dump)).map(Integer::parseInt).toList();
            hexdump =Arrays.stream( opcodesList.stream().mapToInt(i->i).toArray() ).boxed().toArray( Integer[]::new );
            Integer[] opcodes = new Integer[hexdump.length];

            for (int i=0;i < hexdump.length; i++) {
                Integer opcode = mychip8.cpu.fetch();
                opcodes[i]=opcode;
                mychip8.cpu.pc+=2;
            }

            assertTrue(Arrays.equals(hexdump,opcodes));
        }

        catch (IOException e) {
            assertTrue(false,"IO exception");
            throw new IOException(e);
        }

    }

     @ParameterizedTest
     @ValueSource(ints = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15})
     void test_6xnn(int register) {
         BaseChip8 mychip= new BaseChip8();
         int nn = ThreadLocalRandom.current().nextInt(0, 256);
         mychip.cpu.decodeExecute(0x6000 | nn | (register << 8) );
        assertEquals(nn, mychip.registers.v[register]);
    }
     @ParameterizedTest
     @ValueSource(ints = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15})
     void test_7xnn(int register) {
         BaseChip8 mychip= new BaseChip8();
         int nn = ThreadLocalRandom.current().nextInt(0, 256);
         mychip.registers.v[register]=nn;
         mychip.cpu.decodeExecute(0x7000 | nn | (register << 8));
         assertEquals(nn + nn, mychip.registers.v[register]);
     }
     @ParameterizedTest
     @ValueSource(ints = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15})
     void test_8xy0(int register) {
         BaseChip8 mychip= new BaseChip8();
         int vx =ThreadLocalRandom.current().nextInt(0, 256);
         int vy =ThreadLocalRandom.current().nextInt(0, 256);
         mychip.registers.v[register]=vx;
         mychip.registers.v[4]=vy;
         mychip.cpu.decodeExecute(0x8040 | (register << 8));
         assertEquals(mychip.registers.v[4], mychip.registers.v[register]);
     }

     @Test
     void test_8xy1() {
         BaseChip8 mychip= new BaseChip8();
         int vx =ThreadLocalRandom.current().nextInt(0, 256);
         int vy =ThreadLocalRandom.current().nextInt(0, 256);
         mychip.registers.v[3]=vx;
         mychip.registers.v[4]=vy;
         mychip.cpu.decodeExecute(0x8341);
         assertEquals(mychip.registers.v[3], vx | vy );
     }
     @Test
     void test_8xy2() {
         BaseChip8 mychip= new BaseChip8();
         int vx =ThreadLocalRandom.current().nextInt(0, 256);
         int vy =ThreadLocalRandom.current().nextInt(0, 256);
         mychip.registers.v[3]=vx;
         mychip.registers.v[4]=vy;
         mychip.cpu.decodeExecute(0x8342);
         assertEquals(mychip.registers.v[3], vx & vy );
     }

     @Test
     void test_8xy3() {
         BaseChip8 mychip= new BaseChip8();
         int vx =ThreadLocalRandom.current().nextInt(0, 256);
         int vy =ThreadLocalRandom.current().nextInt(0, 256);
         mychip.registers.v[3]=vx;
         mychip.registers.v[4]=vy;
         mychip.cpu.decodeExecute(0x8343);
         assertEquals(mychip.registers.v[3], vx ^ vy );
     }
     @Test
     void test_8xy4() {
         BaseChip8 mychip= new BaseChip8();
         int vx =ThreadLocalRandom.current().nextInt(0, 256);
         int vy =ThreadLocalRandom.current().nextInt(0, 256);
         mychip.registers.v[3]=vx;
         mychip.registers.v[4]=vy;
         mychip.cpu.decodeExecute(0x8344);
         assertEquals(mychip.registers.v[3], vx + vy );
         //aggiungere test per il carry, non sono sicuro che funzioni attualmente
     }

     @Test
     void test_8xy5() {
         BaseChip8 mychip= new BaseChip8();
         mychip.registers.v[3]=0x001;
         mychip.registers.v[4]=0x009;
         mychip.cpu.decodeExecute(0x8345);
         assertEquals(mychip.registers.v[3], 0x001 - 0x009 );
         //aggiungere test per il carry, non sono sicuro che funzioni attualmente
     }
     @Test
     void test_8xy6() {
         BaseChip8 mychip= new BaseChip8();
         mychip.registers.v[3]=0x001;
         mychip.cpu.decodeExecute(0x8346);
         assertEquals(mychip.registers.v[3], 0x001 >>> 1);
         //aggiungere test per il carry, non sono sicuro che funzioni attualmente
     }
     @Test
     void test_8xy7() {
         BaseChip8 mychip= new BaseChip8();
         mychip.registers.v[3]=0x001;
         mychip.registers.v[4]=0x009;
         mychip.cpu.decodeExecute(0x8347);
         assertEquals(mychip.registers.v[3],  0x009 -0x001 );

         //aggiungere test per il carry, non sono sicuro che funzioni attualmente
     }
     @Test
     void test_8xyE() {
         BaseChip8 mychip= new BaseChip8();
         mychip.registers.v[3]=0x001;
         mychip.cpu.decodeExecute(0x834E);
         assertEquals(mychip.registers.v[3],  0x001 << 1 );
         //aggiungere test per il carry, non sono sicuro che funzioni attualmente
     }

     @ParameterizedTest
     @ValueSource(ints = {0x8340, 0x8341,0x8342,0x8343,0x8344,0x8345,0x8346,0x8347,0x834E})
     void testPcAdvance8000(int opcode)  {
         BaseChip8 mychip= new BaseChip8();
         short pc = mychip.cpu.pc;
         mychip.cpu.decodeExecute(opcode);
         assertEquals(mychip.cpu.pc,  pc + 2 );
     }

     @Test
     void test_compare_images_true() {
         BufferedImage Image1 = new BufferedImage(10, 20,TYPE_BYTE_BINARY);
         Image1.setRGB(0, 0, Color.WHITE.getRGB());
         BufferedImage Image2 = new BufferedImage(10, 20, TYPE_BYTE_BINARY);
         Image2.setRGB(0, 0, Color.WHITE.getRGB());
         assertEquals(true, compareImages(Image1, Image2));
     }

     @Test
     void test_compare_images_false() {
         BufferedImage Image1 = new BufferedImage(20, 10,TYPE_BYTE_BINARY);
         Image1.setRGB(0, 0, Color.WHITE.getRGB());
         BufferedImage Image2 = new BufferedImage(20, 10, TYPE_BYTE_BINARY);
         Image2.setRGB(0, 0, Color.BLACK.getRGB());
         assertEquals(false,compareImages(Image1, Image2));
     }
     @Test
     void test_render_pixel(){
         Screen screen1= new Screen(1);
         screen1.DrawPixel(0,1);
         assertEquals(true, Color.WHITE.getRGB()==screen1.getPixel(0,1));
     }



     @Test
     void test_snapshot() {
         Screen screen1 = new Screen(1);
         screen1.DrawPixel(0, 0);
         screen1.DrawPixel(1, 0);
         BufferedImage Image1= screen1.snapshot();
         BufferedImage Image2 = new BufferedImage(64, 32,TYPE_BYTE_BINARY);
         Image2.setRGB(0, 0, Color.WHITE.getRGB());
         Image2.setRGB(1, 0, Color.WHITE.getRGB());
         assertEquals(true,compareImages(Image1, Image2));
     }




 }
