package org.dssc.chip8;

import java.util.Arrays;

public class RAM {

    Integer[] memory = new Integer[4096];

    public void clear() {
        Arrays.fill(memory, (int) 0);
    }

    public RAM(){
        this.clear();
    }
}
