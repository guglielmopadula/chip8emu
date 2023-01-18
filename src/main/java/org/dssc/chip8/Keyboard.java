package org.dssc.chip8;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

public class Keyboard implements KeyListener {
    boolean[] internal_keyboard;
    Integer lastPressed;
    HashMap<Integer, Integer> map;
    public Keyboard(){
        this.internal_keyboard= new boolean[16];
        this.map= new HashMap<Integer, Integer>();
        configureHashMap(this.map);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        ;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        this.internal_keyboard[this.map.get(keyCode)]=true;
        System.out.println("I pressed"+this.map.get(keyCode));
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        this.internal_keyboard[this.map.get(keyCode)]=false;
    }

    void configureHashMap(HashMap<Integer,Integer> map){
        map.put(49,0);
        map.put(50,1);
        map.put(51,2);
        map.put(52,3);
        map.put(81,4);
        map.put(87,5);
        map.put(69,6);
        map.put(82,7);
        map.put(65,8);
        map.put(83,9);
        map.put(68,10);
        map.put(70,11);
        map.put(90,12);
        map.put(88,13);
        map.put(67,14);
        map.put(86,15);
    }

}
