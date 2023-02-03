package org.dssc.chip8;

public class Opcode   {

   private int opcode;

    public Opcode(int opcode){
        this.opcode=opcode;
    }

    public int  x() {
        return (opcode & 0x0f00) >> 8;
    }

    public int  y() {
        return (opcode & 0x00f0) >> 4;
    }

    public int  nnn() {
        return (opcode & 0x0fff) ;
    }

    public int nn() {
        return (opcode & 0x00ff) ;
    }
    public int n() {
        return (opcode & 0x000f) ;
    }
    public int value() {
        return this.opcode;
    }


}
