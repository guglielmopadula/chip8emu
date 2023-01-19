package org.dssc.chip8;

import javax.swing.*;

public class Chip8 extends BaseChip8{
    Keyboard keyboard;
    public Chip8(){
        super();
        keyboard=new Keyboard();
        this.setJavaComponents();
        this.cpu.keyboard=keyboard;
    }
    void setJavaComponents(){
        JPanel p= new JPanel();
        this.setSize(12*62,12*31);
        this.addKeyListener(this.keyboard);
        this.pack();
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


}
