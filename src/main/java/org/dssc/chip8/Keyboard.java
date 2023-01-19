package org.dssc.chip8;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keyboard extends BaseKeyboard implements KeyListener {
    public Keyboard(){
        super();
    }
    @Override
    public void keyTyped(KeyEvent e) {
        ;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        this.internal_keyboard[this.map.get(keyCode)]=true;
        this.lastPressed=this.map.get(keyCode);
        System.out.println(this.lastPressed);
        System.out.println("I pressed"+this.map.get(keyCode));
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        this.lastPressed=-1;
        this.internal_keyboard[this.map.get(keyCode)]=false;
    }
}
