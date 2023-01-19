package org.dssc.chip8;

import javax.swing.*;

public class Chip8 extends BaseChip8{
    Keyboard keyboard;
    JFrame jFrame;
    public Chip8(){
        super();

        keyboard=new Keyboard();
        this.cpu.keyboard=keyboard;
        this.jFrame=new JFrame();
        this.setJavaComponents();



    }
    void setJavaComponents(){
        JPanel p= new JPanel();
        jFrame.setSize(12*62,12*31);
        jFrame.addKeyListener(this.keyboard);
        jFrame.pack();
        jFrame.setVisible(true);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


}
