package org.dssc.chip8;

import java.util.Arrays;

 class Registers {

    Integer[] v=new Integer[16];


    void clear(){
        Arrays.fill(v,(int) 0);
    }

     Registers(){
        this.clear();
    }



}
