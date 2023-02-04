package org.dssc.chip8;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.awt.*;
import java.awt.image.BufferedImage;
import static java.awt.image.BufferedImage.TYPE_BYTE_BINARY;
import static org.junit.jupiter.api.Assertions.*;


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

    static void setRegister(BaseChip8 chip8,int idx,int value){
        chip8.cpu.vreg[idx]=value;
    }

    static int getRegister(BaseChip8 chip8,int idx) {
        return chip8.cpu.vreg[idx];
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
    void test00EE(){
        BaseChip8 mychip= new BaseChip8();
        int memoryAddres = 0x012;
        mychip.cpu.stack.push(memoryAddres);
        mychip.cpu.decodeExecute(0x00EE);
        assertEquals(mychip.cpu.pc,memoryAddres + 2);
    }
    @Test
    void test1NNN(){
        BaseChip8 mychip= new BaseChip8();
        int jumpLocation = 0x0123;
        mychip.cpu.decodeExecute(0x1000 | jumpLocation);
        assertEquals(mychip.cpu.pc,jumpLocation);
    }

    @Test
    void test2NNNstack(){
        BaseChip8 mychip= new BaseChip8();
        int routineLocation = 0x0123;
        mychip.cpu.pc=99;
        mychip.cpu.decodeExecute(0x2000 | routineLocation);
        assertEquals(99,mychip.cpu.stack.pop());
    }
    @Test
    void test2NNNpc(){
        BaseChip8 mychip= new BaseChip8();
        int routineLocation = 0x0123;
        mychip.cpu.pc=99;
        mychip.cpu.decodeExecute(0x2000 | routineLocation);
        assertEquals(0x0123,mychip.cpu.pc);
    }

    @RepeatedTest(255)
    void test3xNNequal(RepetitionInfo repetitionInfo) {
        BaseChip8 mychip= new BaseChip8();
        int pcBefore = mychip.cpu.pc;
        int current_value = repetitionInfo.getCurrentRepetition();

        setRegister(mychip,0,current_value);

        mychip.cpu.decodeExecute(0x3000 | current_value);
        assertEquals(mychip.cpu.pc,pcBefore + 4);
    }


    @RepeatedTest(255)
    void test3xNNnequal(RepetitionInfo repetitionInfo) {
        BaseChip8 mychip= new BaseChip8();
        int pcBefore = mychip.cpu.pc;
        int current_value = repetitionInfo.getCurrentRepetition();
        setRegister(mychip,0,current_value);
        mychip.cpu.decodeExecute(0x3000 | (current_value + 1) & 0xff);
        assertEquals(mychip.cpu.pc,pcBefore + 2);
    }


    @RepeatedTest(255)
    void test4xNNequal(RepetitionInfo repetitionInfo) {
        BaseChip8 mychip= new BaseChip8();
        int pcBefore = mychip.cpu.pc;
        int current_value = repetitionInfo.getCurrentRepetition();
        setRegister(mychip,0,current_value);
        mychip.cpu.decodeExecute(0x4000 | current_value);
        assertEquals(mychip.cpu.pc,pcBefore + 2);
    }

    @RepeatedTest(255)
    void test4xNNnequal(RepetitionInfo repetitionInfo) {
        BaseChip8 mychip= new BaseChip8();
        int pcBefore = mychip.cpu.pc;
        int current_value = repetitionInfo.getCurrentRepetition();
        setRegister(mychip,0,current_value);
        mychip.cpu.decodeExecute(0x4000 | (current_value+1)&0xff);
        assertEquals(mychip.cpu.pc,pcBefore + 4);
    }

    @RepeatedTest(255)
    void test5xy0equal(RepetitionInfo repetitionInfo) {
        BaseChip8 mychip= new BaseChip8();
        int pcBefore = mychip.cpu.pc;
        int current_value = repetitionInfo.getCurrentRepetition();
        setRegister(mychip,3,current_value);
        setRegister(mychip,2,current_value);
        mychip.cpu.decodeExecute(0x5000 | 0x0200 | 0x0030);
        assertEquals(mychip.cpu.pc,pcBefore + 4);
    }

    @RepeatedTest(255)
    void test5xy0nequal(RepetitionInfo repetitionInfo) {
        BaseChip8 mychip= new BaseChip8();
        int pcBefore = mychip.cpu.pc;
        int current_value = repetitionInfo.getCurrentRepetition();
        setRegister(mychip,3,current_value);
        setRegister(mychip,2,(current_value + 1 ) &  0xff);
        mychip.cpu.decodeExecute(0x5000 | 0x0200 | 0x0030);
        assertEquals(mychip.cpu.pc,pcBefore + 2);
    }


    @ParameterizedTest
    @ValueSource(ints = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15})
    void test6xNN(int register) {
        BaseChip8 mychip= new BaseChip8();
        int nn = 0x23;
        mychip.cpu.decodeExecute(0x6000 | nn | (register << 8) );
        assertEquals(nn, getRegister(mychip,register));
    }

    @ParameterizedTest
    @ValueSource(ints = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15})
    void test7xNN(int register) {
        BaseChip8 mychip= new BaseChip8();
        int nn = 0x23;
        setRegister(mychip,register,nn);
        mychip.cpu.decodeExecute(0x7000 | nn | (register << 8));
        assertEquals((nn + nn) & 0xff, getRegister(mychip,register));
    }
    @ParameterizedTest
    @ValueSource(ints = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15})
    void test8xy0(int register) {
        BaseChip8 mychip= new BaseChip8();
        int vx =0x23;
        int vy =0x25;
        setRegister(mychip,register,vx);
        setRegister(mychip,4,vy);
        mychip.cpu.decodeExecute(0x8040 | (register << 8));
        assertEquals(getRegister(mychip,4), getRegister(mychip,register));
    }

    @Test
    void test8xy1() {
        BaseChip8 mychip= new BaseChip8();
        int vx =0x23;
        int vy =0x25;
        setRegister(mychip,3,vx);
        setRegister(mychip,4,vy);
        mychip.cpu.decodeExecute(0x8341);
        assertEquals(mychip.cpu.vreg[3], vx | vy );
    }
    @Test
    void test8xy2() {
        BaseChip8 mychip= new BaseChip8();
        int vx =0x23;
        int vy =0x25;
        setRegister(mychip,3,vx);
        setRegister(mychip,4,vy);
        mychip.cpu.decodeExecute(0x8342);
        assertEquals(getRegister(mychip,3), vx & vy );
    }

    @Test
    void test8xy3() {
        BaseChip8 mychip= new BaseChip8();
        int vx =0x23;
        int vy =0x25;
        setRegister(mychip,3,vx);
        setRegister(mychip,4,vy);
        mychip.cpu.decodeExecute(0x8343);
        assertEquals(getRegister(mychip,3), vx ^ vy );
    }
    @Test
    void test8xy4() {
        BaseChip8 mychip= new BaseChip8();
        int vx =0x23;
        int vy =0x25;
        setRegister(mychip,3,vx);
        setRegister(mychip,4,vy);
        mychip.cpu.decodeExecute(0x8344);
        assertEquals(getRegister(mychip,3), (vx + vy) & 0xff );
    }

    @Test
    void test8xy5() {
        BaseChip8 mychip= new BaseChip8();
        setRegister(mychip,3,0x001);
        setRegister(mychip,4,0x009);
        mychip.cpu.decodeExecute(0x8345);
        assertEquals(getRegister(mychip,3), (0x001 - 0x009)  & 0xff );
    }

    @RepeatedTest(255)
    void test8xy5cf(RepetitionInfo repetitionInfo) {
        BaseChip8 mychip= new BaseChip8();
        int current_value=repetitionInfo.getCurrentRepetition();
        setRegister(mychip,3,125);
        setRegister(mychip,4,current_value);
        mychip.cpu.decodeExecute(0x8345);
        if (125 > current_value)
            assertEquals(1 ,getRegister(mychip,0xF));
        else
            assertEquals(0,getRegister(mychip,0xF)  );
    }

    @RepeatedTest(255)
    void test8xy6cf(RepetitionInfo repetitionInfo) {
        BaseChip8 mychip= new BaseChip8();
        int current_value=repetitionInfo.getCurrentRepetition();
        setRegister(mychip,3,current_value);
        mychip.cpu.decodeExecute(0x8306);
        if ((current_value & 0x1)==1)
            assertEquals(1,getRegister(mychip,0xF)  );
        else
            assertEquals(0,getRegister(mychip,0xF) );
    }

    @RepeatedTest(255)
    void test8xy7cf(RepetitionInfo repetitionInfo) {
        BaseChip8 mychip= new BaseChip8();
        int current_value=repetitionInfo.getCurrentRepetition();
        setRegister(mychip,3,125);
        setRegister(mychip,4,current_value);
        mychip.cpu.decodeExecute(0x8347);
        if (current_value > 125)
            assertEquals(1,getRegister(mychip,0xF) );
        else
            assertEquals(0,getRegister(mychip,0xF));
    }
    @RepeatedTest(255)
    void test8xyEcf(RepetitionInfo repetitionInfo) {
        BaseChip8 mychip= new BaseChip8();
        int current_value=repetitionInfo.getCurrentRepetition();
        setRegister(mychip,4,current_value);
        mychip.cpu.decodeExecute(0x844E);
        assertEquals(getRegister(mychip,0xF), (current_value & 0x80) >>> 7 );
    }

    @Test
    void test8xy6() {
        BaseChip8 mychip= new BaseChip8();
        setRegister(mychip,3,0x001);
        mychip.cpu.decodeExecute(0x8346);
        assertEquals(getRegister(mychip,3), 0x001 >>> 1);
    }
    @Test
    void test8xy7() {
        BaseChip8 mychip= new BaseChip8();
        setRegister(mychip,3,0x001);
        setRegister(mychip,4,0x009);
        mychip.cpu.decodeExecute(0x8347);
        assertEquals(0x009 -0x001,getRegister(mychip,3) );

    }
    @Test
    void test8xyE() {
        BaseChip8 mychip= new BaseChip8();
        setRegister(mychip,3,0x001);
        mychip.cpu.decodeExecute(0x834E);
        assertEquals(getRegister(mychip,3),  0x001 << 1 );
    }

     @Test
     void test9xy0equal() {
         BaseChip8 mychip= new BaseChip8();
         int pcBefore = mychip.cpu.pc;
         setRegister(mychip,2,0x002);
         setRegister(mychip,3,0x002);
         mychip.cpu.decodeExecute(0x9000 | 0x0200 | 0x0030);
         assertEquals(mychip.cpu.pc,  pcBefore + 2);
     }

    @Test
    void test9xy0nequal() {
        BaseChip8 mychip= new BaseChip8();
        int pcBefore = mychip.cpu.pc;
        setRegister(mychip,2,0x002);
        setRegister(mychip,3,0x003);
        mychip.cpu.decodeExecute(0x9000 | 0x0200 | 0x0030);
        assertEquals(mychip.cpu.pc,  pcBefore + 4);
    }

    @Test
    void testANNN() {
        BaseChip8 mychip= new BaseChip8();
        mychip.cpu.decodeExecute(0xA000 | 0x0123);
        assertEquals(0x0123,mychip.cpu.i );
    }

    @Test
    void testBNNN() {
        BaseChip8 mychip= new BaseChip8();
        setRegister(mychip,0,0x1);
        mychip.cpu.decodeExecute(0xB000 | 0x0123);
        assertEquals(mychip.cpu.pc,  getRegister(mychip,0) + 0x0123);
    }

    @Test
    void test_Fx15() {
        BaseChip8 mychip= new BaseChip8();
        setRegister(mychip,1,10);
        mychip.cpu.decodeExecute(0xF015 | 0x0100);
        assertEquals(10,mychip.cpu.timers.delaytimer  );
    }

    @Test
    void testFx18() {
        BaseChip8 mychip= new BaseChip8();
        setRegister(mychip,1,10);
        mychip.cpu.decodeExecute(0xF018 | 0x0100);
        assertEquals(10,mychip.cpu.timers.soundtimer);
    }

    @Test
    void testFx1E() {
        BaseChip8 mychip= new BaseChip8();
        mychip.cpu.i=12;
        setRegister(mychip,1,10);
        mychip.cpu.decodeExecute(0xF01E | 0x0100);
        assertEquals(22,mychip.cpu.i );
    }

    @Test
    void testFx33() {
        BaseChip8 mychip= new BaseChip8();
        setRegister(mychip,6,137);
        mychip.cpu.i=0;
        mychip.cpu.decodeExecute(0xF033 | 0x0600);
        assertEquals(1,mychip.cpu.ram.getAt(0));
        assertEquals(3,mychip.cpu.ram.getAt(1));
        assertEquals(7,mychip.cpu.ram.getAt(2));
    }

    @Test
    void testFx55() {
        BaseChip8 mychip= new BaseChip8();
        for(int counter=0;counter <= 0xF; counter++){
            setRegister(mychip,counter,99);;
        }
        mychip.cpu.decodeExecute(0xF055 | 0x0F00);
        for(int counter=0;counter <= 0xF; counter++){
            assertEquals(99,mychip.cpu.ram.getAt(counter) );
        }

    }

    @Test
    void testFx65() {
        BaseChip8 mychip= new BaseChip8();
        for(int counter=0;counter <= 0xF; counter++){
            mychip.cpu.ram.setAt(counter,99);
        }
        mychip.cpu.decodeExecute(0xF065 | 0x0F00);
        for(int counter=0;counter <= 0xF; counter++){
            assertEquals(99,getRegister(mychip,counter) );
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

    @ParameterizedTest
    @ValueSource(ints = {0x8009,0xF099,0xE099})
    void testUnknowOpcode(int opcode){
        BaseChip8 mychip= new BaseChip8();
        assertThrows(MessageException.class, () -> {
            mychip.cpu.decodeExecute(opcode);
        }  );
    }

    @Test
    @Disabled("Disable, since the test can't fail")
    void test0xF007stop(){
        BaseChip8 mychip= new BaseChip8();
        mychip.basekeyboard.press(49);
        assertTimeout(Duration.ofSeconds(3),() -> {
            mychip.cpu.decodeExecute(0xF00A);
        });
    }
    @Test
    void test0xF007press(){
        BaseChip8 mychip= new BaseChip8();
        mychip.basekeyboard.press(49);
        mychip.cpu.decodeExecute(0xF00A);
        assertEquals(1,getRegister(mychip,0));
    }

    @Test
    void test0xE09Eequal(){
        //disable, since that the test can't fail
        BaseChip8 mychip= new BaseChip8();
        int pcBefore = mychip.cpu.pc;
        mychip.basekeyboard.press(49);
        setRegister(mychip,0,1);
        mychip.cpu.decodeExecute(0xE09E);
        assertEquals(pcBefore+4,mychip.cpu.pc);
    }
    @Test
    void test0xE09Enequal(){
        //disable, since that the test can't fail
        BaseChip8 mychip= new BaseChip8();
        int pcBefore = mychip.cpu.pc;
        mychip.basekeyboard.press(51);
        setRegister(mychip,0,1);
        mychip.cpu.decodeExecute(0xE09E);
        assertEquals(pcBefore+2,mychip.cpu.pc);
    }

    @Test
    void test0xE0A1equal(){
        //disable, since that the test can't fail
        BaseChip8 mychip= new BaseChip8();
        int pcBefore = mychip.cpu.pc;
        mychip.basekeyboard.press(49);
        setRegister(mychip,0,1);
        mychip.cpu.decodeExecute(0xE0A1);
        assertEquals(pcBefore+2,mychip.cpu.pc);
    }

    @Test
    void test0xE0A1nequal(){
        //disable, since that the test can't fail
        BaseChip8 mychip= new BaseChip8();
        int pcBefore = mychip.cpu.pc;
        mychip.basekeyboard.press(51);
        mychip.cpu.vreg[0]=1;
        mychip.cpu.decodeExecute(0xE0A1);
        assertEquals(pcBefore+4,mychip.cpu.pc);
    }





    @Test
    void testCompareImagesTrue() {
        BufferedImage Image1 = new BufferedImage(10, 20,TYPE_BYTE_BINARY);
        Image1.setRGB(0, 0, Color.WHITE.getRGB());
        BufferedImage Image2 = new BufferedImage(10, 20, TYPE_BYTE_BINARY);
        Image2.setRGB(0, 0, Color.WHITE.getRGB());
        assertTrue(compareImages(Image1, Image2));
    }

    @Test
    void testCompareImagesFalse() {
        BufferedImage Image1 = new BufferedImage(20, 10,TYPE_BYTE_BINARY);
        Image1.setRGB(0, 0, Color.WHITE.getRGB());
        BufferedImage Image2 = new BufferedImage(20, 10, TYPE_BYTE_BINARY);
        Image2.setRGB(0, 0, Color.BLACK.getRGB());
        assertFalse(compareImages(Image1, Image2));
    }
    @Test
    void testRenderPixel(){
        Screen screen1= new Screen(1);
        screen1.drawPixel(0,1,Color.WHITE);
        assertEquals(Color.WHITE.getRGB(), screen1.getPixel(0, 1));
    }
    @Test
    void testSnap() {
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

