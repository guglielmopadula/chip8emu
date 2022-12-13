package org.dssc.chip8;

import java.util.Arrays;

public class RAM {

    Byte[] memory = new Byte[4096];

    public void clear() {
        Arrays.fill(memory, (byte) 0);
    }

    public RAM(){
        this.clear();
    }
}
