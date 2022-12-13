package org.dssc.chip8;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Tests {

    @Test
    void test_clear_register(){
        Registers reg=new Registers();
        Byte[] temp=new Byte[16];
        Arrays.fill(temp,(byte) 0);
        reg.clear();
        assertTrue(Arrays.equals(reg.V,temp));
    }


}
