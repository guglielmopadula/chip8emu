package org.dssc.chip8;

import java.util.Arrays;

public class Registers {

    Byte[] V=new Byte[16];


    void clear(){
        Arrays.fill(V,(byte) 0);
    }

    public Registers(){
        this.clear();
    }



}
