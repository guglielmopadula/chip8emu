package org.dssc.chip8;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keyboard extends BaseKeyboard implements KeyListener {
    public Keyboard(){
        super();
    }
    @Override
    public void keyTyped(KeyEvent e) {
        //Questa funzione è necessaria perchè KeyListener la vuole. Noi non la utilizziamo.
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        press(keyCode);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        release(keyCode);
    }

}
