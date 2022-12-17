package org.dssc.chip8;

import java.util.Arrays;

 class Registers {

    Byte[] v=new Byte[16];


    void clear(){
        Arrays.fill(v,(byte) 0);
    }

     Registers(){
        this.clear();
    }



}
