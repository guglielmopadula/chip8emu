package org.dssc.chip8;

import javax.swing.*;
import java.awt.*;

public class Chip8 extends BaseChip8{
    Keyboard keyboard;
    JFrame jFrame;

    JPanel panel;
    public Chip8(){
        super();

        keyboard=new Keyboard();
        this.cpu.keyboard=keyboard;
        this.jFrame=new JFrame();
        this.setJavaComponents();



    }
    void setJavaComponents(){
         this.panel= new JPanel() {
            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                g.drawImage(screen.snapshot(),0,0,this);
            }
        };
        jFrame.add(panel);
        jFrame.setSize(20*62,20*31);
        jFrame.addKeyListener(this.keyboard);
        jFrame.setPreferredSize(new Dimension(500, 500));
        jFrame.pack();
        jFrame.setVisible(true);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void startChip8(String filePath) {
        Integer[] rom = readRomFromString(filePath);
        loadRomToRam(rom);
        this.cpu.pc=512;

        int opcode = this.cpu.fetch();
        while(opcode != 0x00FD) {
            //System.out.printf("pc: %d, opcode: %d\n",this.cpu.pc,opcode);
            if (this.cpu.registers.v[1]==199)
                System.out.println(this.cpu.registers.v[1]);

            this.cpu.decodeExecute(opcode);
            opcode = this.cpu.fetch();
             if (this.timers.Delaytimer>0) this.timers.Delaytimer-=1;
            if (this.timers.Soundtimer>0) this.timers.Soundtimer-=1;
            jFrame.repaint();

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }


        }



    }

}
