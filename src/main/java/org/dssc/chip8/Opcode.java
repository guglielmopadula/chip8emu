package org.dssc.chip8;

public class Opcode   {

   private int value;

    public Opcode(int opcode){
        this.value =opcode;
    }

    public int  x() {
        return (value & 0x0f00) >> 8;
    }

    public int  y() {
        return (value & 0x00f0) >> 4;
    }

    public int  nnn() {
        return (value & 0x0fff) ;
    }

    public int nn() {
        return (value & 0x00ff) ;
    }
    public int n() {
        return (value & 0x000f) ;
    }
    public int value() {
        return this.value;
    }


}
