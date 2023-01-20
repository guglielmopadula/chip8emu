package org.dssc.chip8;

import javax.swing.*;
import java.awt.*;

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
        JPanel p= new JPanel() {
            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                g.drawImage(screen.snapshot(),0,0,this);
            }
        };
        jFrame.add(p);
        jFrame.setSize(20*62,20*31);
        jFrame.addKeyListener(this.keyboard);
        jFrame.pack();
        jFrame.setVisible(true);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


}
