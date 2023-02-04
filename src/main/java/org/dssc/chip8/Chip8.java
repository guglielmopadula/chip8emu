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
            jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }


    public void startChip8(String filePath) {
        Integer[] rom = readRomFromString(filePath);
        loadRomToRam(rom);
        this.cpu.pc=512;

        Opcode opcode = this.cpu.fetch();
        long time = System.nanoTime();
        long rendertime= System.nanoTime();
        while(opcode.value() != 0x00FD) {
            this.cpu.decodeExecute(opcode);
            opcode = this.cpu.fetch();
            if (this.timers.delaytimer >0) this.timers.delaytimer -=1;
            if (this.timers.soundtimer >0) {
                this.timers.soundtimer -=1;
                Toolkit.getDefaultToolkit().beep();
            }

            jFrame.repaint();

            while (System.nanoTime() - time < 3500000 ) {
                try {
                    Thread.sleep(0);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new MessageException("failed to sleep thread");
                }
            }
            time = System.nanoTime();
        }

    }


}
