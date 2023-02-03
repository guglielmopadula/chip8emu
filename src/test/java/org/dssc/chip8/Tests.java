package org.dssc.chip8;

import org.junit.jupiter.api.Disabled;
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
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.ThreadLocalRandom;

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
                Opcode opcode = mychip8.cpu.fetch();
                opcodes[i]=opcode.value();
                mychip8.cpu.pc+=2;
            }

            assertArrayEquals(hexdump, opcodes);
        }

        catch (IOException e) {
            fail("IO exception");
            throw new IOException(e);
        }

    }


    @Test
    void test_00EE(){
        BaseChip8 mychip= new BaseChip8();
        int memoryAddres = 0x012;
        mychip.cpu.stack.push(memoryAddres);
        mychip.cpu.decodeExecute(0x00EE);
        assertEquals(mychip.cpu.pc,memoryAddres + 2);
    }
     @Test
     void test_1NNN(){
         BaseChip8 mychip= new BaseChip8();
         int jumpLocation = 0x0123;
         mychip.cpu.decodeExecute(0x1000 | jumpLocation);
         assertEquals(mychip.cpu.pc,jumpLocation);
     }

     @Test
     void test_2NNN_stack(){
         BaseChip8 mychip= new BaseChip8();
         int routineLocation = 0x0123;
         mychip.cpu.pc=99;
         mychip.cpu.decodeExecute(0x2000 | routineLocation);
         assertEquals(99,mychip.cpu.stack.pop());
     }
     @Test
     void test_2NNN_pc(){
         BaseChip8 mychip= new BaseChip8();
         int routineLocation = 0x0123;
         mychip.cpu.pc=99;
         mychip.cpu.decodeExecute(0x2000 | routineLocation);
         assertEquals(0x0123,mychip.cpu.pc);
     }

     @Test
     void test_3xNN_equal(){
         BaseChip8 mychip= new BaseChip8();
         int pcBefore = mychip.cpu.pc;
         mychip.cpu.vreg[0]= 0x23;
         mychip.cpu.decodeExecute(0x3000 | 0x0023);
         assertEquals(mychip.cpu.pc,pcBefore + 4);
     }

     @Test
     void test_3xNN_Nequal(){
         BaseChip8 mychip= new BaseChip8();
         int pcBefore = mychip.cpu.pc;
         mychip.cpu.vreg[0]= 0x22;
         mychip.cpu.decodeExecute(0x3000 | 0x0023);
         assertEquals(mychip.cpu.pc,pcBefore + 2);
     }

     @Test
     void test_4xNN_equal(){
         BaseChip8 mychip= new BaseChip8();
         int pcBefore = mychip.cpu.pc;
         mychip.cpu.vreg[0]= 0x23;
         mychip.cpu.decodeExecute(0x4000 | 0x0023);
         assertEquals(mychip.cpu.pc,pcBefore + 2);
     }

     @Test
     void test_4xNN_Nequal(){
         BaseChip8 mychip= new BaseChip8();
         int pcBefore = mychip.cpu.pc;
         mychip.cpu.vreg[0]= 0x23;
         mychip.cpu.decodeExecute(0x4000 | 0x0022);
         assertEquals(mychip.cpu.pc,pcBefore + 4);
     }

     @Test
     void test_5xy0_equal(){
         BaseChip8 mychip= new BaseChip8();
         int pcBefore = mychip.cpu.pc;
         mychip.cpu.vreg[0]= 0x23;
         mychip.cpu.vreg[1]= 0x23;
         mychip.cpu.decodeExecute(0x5000 | 0x0000 | 0x0010);
         assertEquals(mychip.cpu.pc,pcBefore + 4);
     }

     @Test
     void test_5xy0_Nequal(){
         BaseChip8 mychip= new BaseChip8();
         int pcBefore = mychip.cpu.pc;
         mychip.cpu.vreg[0]= 0x23;
         mychip.cpu.vreg[1]= 0x24;
         mychip.cpu.decodeExecute(0x5000 | 0x0000 | 0x0010);
         assertEquals(mychip.cpu.pc,pcBefore + 2);
     }

     @ParameterizedTest
     @ValueSource(ints = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15})
     void test_6xnn(int register) {
         BaseChip8 mychip= new BaseChip8();
         int nn = ThreadLocalRandom.current().nextInt(0, 256);
         mychip.cpu.decodeExecute(0x6000 | nn | (register << 8) );
        assertEquals(nn, mychip.cpu.vreg[register]);
    }
     @ParameterizedTest
     @ValueSource(ints = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15})
     void test_7xnn(int register) {
         BaseChip8 mychip= new BaseChip8();
         int nn = ThreadLocalRandom.current().nextInt(0, 256);
         mychip.cpu.vreg[register]=nn;
         mychip.cpu.decodeExecute(0x7000 | nn | (register << 8));
         assertEquals((nn + nn) & 0xff, mychip.cpu.vreg[register]);
     }
     @ParameterizedTest
     @ValueSource(ints = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15})
     void test_8xy0(int register) {
         BaseChip8 mychip= new BaseChip8();
         int vx =ThreadLocalRandom.current().nextInt(0, 256);
         int vy =ThreadLocalRandom.current().nextInt(0, 256);
         mychip.cpu.vreg[register]=vx;
         mychip.cpu.vreg[4]=vy;
         mychip.cpu.decodeExecute(0x8040 | (register << 8));
         assertEquals(mychip.cpu.vreg[4], mychip.cpu.vreg[register]);
     }

     @Test
     void test_8xy1() {
         BaseChip8 mychip= new BaseChip8();
         int vx =ThreadLocalRandom.current().nextInt(0, 256);
         int vy =ThreadLocalRandom.current().nextInt(0, 256);
         mychip.cpu.vreg[3]=vx;
         mychip.cpu.vreg[4]=vy;
         mychip.cpu.decodeExecute(0x8341);
         assertEquals(mychip.cpu.vreg[3], vx | vy );
     }
     @Test
     void test_8xy2() {
         BaseChip8 mychip= new BaseChip8();
         int vx = ThreadLocalRandom.current().nextInt(0, 256);
         int vy = ThreadLocalRandom.current().nextInt(0, 256);
         mychip.cpu.vreg[3]=vx;
         mychip.cpu.vreg[4]=vy;
         mychip.cpu.decodeExecute(0x8342);
         assertEquals(mychip.cpu.vreg[3], vx & vy );
     }

     @Test
     void test_8xy3() {
         BaseChip8 mychip= new BaseChip8();
         int vx =ThreadLocalRandom.current().nextInt(0, 256);
         int vy =ThreadLocalRandom.current().nextInt(0, 256);
         mychip.cpu.vreg[3]=vx;
         mychip.cpu.vreg[4]=vy;
         mychip.cpu.decodeExecute(0x8343);
         assertEquals(mychip.cpu.vreg[3], vx ^ vy );
     }
     @Test
     void test_8xy4() {
         BaseChip8 mychip= new BaseChip8();
         int vx =ThreadLocalRandom.current().nextInt(0, 256);
         int vy =ThreadLocalRandom.current().nextInt(0, 256);
         mychip.cpu.vreg[3]=vx;
         mychip.cpu.vreg[4]=vy;
         mychip.cpu.decodeExecute(0x8344);
         assertEquals(mychip.cpu.vreg[3], (vx + vy) & 0xff );
         //aggiungere test per il carry, non sono sicuro che funzioni attualmente
     }

     @Test
     void test_8xy5() {
         BaseChip8 mychip= new BaseChip8();
         mychip.cpu.vreg[3]=0x001;
         mychip.cpu.vreg[4]=0x009;
         mychip.cpu.decodeExecute(0x8345);
         assertEquals(mychip.cpu.vreg[3], (0x001 - 0x009)  & 0xff );
         //aggiungere test per il carry, non sono sicuro che funzioni attualmente
     }
     @Test
     void test_8xy6() {
         BaseChip8 mychip= new BaseChip8();
         mychip.cpu.vreg[3]=0x001;
         mychip.cpu.decodeExecute(0x8346);
         assertEquals(mychip.cpu.vreg[3], 0x001 >>> 1);
         //aggiungere test per il carry, non sono sicuro che funzioni attualmente
     }
     @Test
     void test_8xy7() {
         BaseChip8 mychip= new BaseChip8();
         mychip.cpu.vreg[3]=0x001;
         mychip.cpu.vreg[4]=0x009;
         mychip.cpu.decodeExecute(0x8347);
         assertEquals(mychip.cpu.vreg[3],  0x009 -0x001 );

         //aggiungere test per il carry, non sono sicuro che funzioni attualmente
     }
     @Test
     void test_8xyE() {
         BaseChip8 mychip= new BaseChip8();
         mychip.cpu.vreg[3]=0x001;
         mychip.cpu.decodeExecute(0x834E);
         assertEquals(mychip.cpu.vreg[3],  0x001 << 1 );
         //aggiungere test per il carry, non sono sicuro che funzioni attualmente
     }

     @Test
     void test_9xy0_equal() {
         BaseChip8 mychip= new BaseChip8();
         int pcBefore = mychip.cpu.pc;
         mychip.cpu.vreg[2]=0x002;
         mychip.cpu.vreg[3]=0x002;
         mychip.cpu.decodeExecute(0x9000 | 0x0200 | 0x0030);
         assertEquals(mychip.cpu.pc,  pcBefore + 2);
     }

     @Test
     void test_9xy0_Nequal() {
         BaseChip8 mychip= new BaseChip8();
         int pcBefore = mychip.cpu.pc;
         mychip.cpu.vreg[2]=0x002;
         mychip.cpu.vreg[3]=0x003;
         mychip.cpu.decodeExecute(0x9000 | 0x0200 | 0x0030);
         assertEquals(mychip.cpu.pc,  pcBefore + 4);
     }

     @Test
     void test_Annn() {
         BaseChip8 mychip= new BaseChip8();
         mychip.cpu.decodeExecute(0xA000 | 0x0123);
         assertEquals(0x0123,mychip.cpu.i );
     }

     @Test
     void test_Bnnn() {
         BaseChip8 mychip= new BaseChip8();
         mychip.cpu.vreg[0]=0x1;
         mychip.cpu.decodeExecute(0xB000 | 0x0123);
         assertEquals(mychip.cpu.pc,  mychip.cpu.vreg[0] + 0x0123);
     }

     @Test
     void test_Fx15() {
         BaseChip8 mychip= new BaseChip8();
         mychip.cpu.vreg[1]=10;
         mychip.cpu.decodeExecute(0xF015 | 0x0100);
         assertEquals(10,mychip.cpu.timers.delaytimer  );
     }

     @Test
     void test_Fx18() {
         BaseChip8 mychip= new BaseChip8();
         mychip.cpu.vreg[1]=10;
         mychip.cpu.decodeExecute(0xF018 | 0x0100);
         assertEquals(10,mychip.cpu.timers.soundtimer);
     }

     @Test
     void test_Fx1E() {
         BaseChip8 mychip= new BaseChip8();
         mychip.cpu.i=12;
         mychip.cpu.vreg[1]=10;
         mychip.cpu.decodeExecute(0xF01E | 0x0100);
         assertEquals(22,mychip.cpu.i );
     }

     @Test
     void test_Fx33() {
         BaseChip8 mychip= new BaseChip8();
         mychip.cpu.vreg[6]=137;
         mychip.cpu.i=0;
         mychip.cpu.decodeExecute(0xF033 | 0x0600);
         assertEquals(1,mychip.cpu.ram.getAt(0));
         assertEquals(3,mychip.cpu.ram.getAt(1));
         assertEquals(7,mychip.cpu.ram.getAt(2));
     }

     @Test
     void test_Fx55() {
         BaseChip8 mychip= new BaseChip8();
         for(int counter=0;counter <= 0xF; counter++){
             mychip.cpu.vreg[counter]=99;
         }
         mychip.cpu.decodeExecute(0xF055 | 0x0F00);
         for(int counter=0;counter <= 0xF; counter++){
             assertEquals(99,mychip.cpu.ram.getAt(counter) );
         }

     }

     @Test
     void test_Fx65() {
         BaseChip8 mychip= new BaseChip8();
         for(int counter=0;counter <= 0xF; counter++){
             mychip.cpu.ram.setAt(counter,99);
         }
         mychip.cpu.decodeExecute(0xF065 | 0x0F00);
         for(int counter=0;counter <= 0xF; counter++){
             assertEquals(99,mychip.cpu.vreg[counter] );
         }

     }


     @ParameterizedTest
     @ValueSource(ints = {0x8340, 0x8341,0x8342,0x8343,0x8344,0x8345,0x8346,0x8347,0x834E})
     void testPcAdvance8000(int opcode)  {
         BaseChip8 mychip= new BaseChip8();
         short pc = mychip.cpu.pc;
         mychip.cpu.decodeExecute(opcode);
         assertEquals(mychip.cpu.pc,  pc + 2 );
     }

     @ParameterizedTest
     @ValueSource(ints = {0xF007, 0xF015,0xF018,0xF01E,0xF029,0xF033,0xF055,0xF065})
     void testPcAdvanceF000(int opcode)  {
         BaseChip8 mychip= new BaseChip8();
         short pc = mychip.cpu.pc;
         mychip.cpu.decodeExecute(opcode);
         assertEquals(mychip.cpu.pc,  pc + 2 );
     }

     @ParameterizedTest
     @ValueSource(ints = {0xA000, 0xC000,0XD000,0xF01E,0xF029,0xF033,0xF055,0xF065})
     void testPcAdvanceACD(int opcode)  {
         BaseChip8 mychip= new BaseChip8();
         short pc = mychip.cpu.pc;
         mychip.cpu.decodeExecute(opcode);
         assertEquals(mychip.cpu.pc,  pc + 2 );
     }

     @ParameterizedTest
     @ValueSource(ints = {0x6000, 0x7000,0x00E0})
     void testPcAdvanceMEM(int opcode)  {
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
         assertTrue(compareImages(Image1, Image2));
     }

     @Test
     void test_compare_images_false() {
         BufferedImage Image1 = new BufferedImage(20, 10,TYPE_BYTE_BINARY);
         Image1.setRGB(0, 0, Color.WHITE.getRGB());
         BufferedImage Image2 = new BufferedImage(20, 10, TYPE_BYTE_BINARY);
         Image2.setRGB(0, 0, Color.BLACK.getRGB());
         assertFalse(compareImages(Image1, Image2));
     }
     @Test
     void test_render_pixel(){
         Screen screen1= new Screen(1);
         screen1.drawPixel(0,1,Color.WHITE);
         assertEquals(Color.WHITE.getRGB(), screen1.getPixel(0, 1));
     }
     @Test
     void test_snapshot() {
         Screen screen1 = new Screen(1);
         screen1.drawPixel(0, 0,Color.WHITE);
         screen1.drawPixel(1, 0,Color.WHITE);
         BufferedImage Image1= screen1.snapshot();
         BufferedImage Image2 = new BufferedImage(64, 32,TYPE_BYTE_BINARY);
         Image2.setRGB(0, 0, Color.WHITE.getRGB());
         Image2.setRGB(0, 1, Color.WHITE.getRGB());
         assertTrue(compareImages(Image1, Image2));
     }


 }

