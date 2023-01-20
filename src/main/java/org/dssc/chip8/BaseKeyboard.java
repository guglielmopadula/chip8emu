package org.dssc.chip8;
import java.util.HashMap;

public class BaseKeyboard {
    boolean[] internal_keyboard;
    Integer lastPressed=-1;
    HashMap<Integer, Integer> map;
    public BaseKeyboard(){
        this.internal_keyboard= new boolean[16];
        this.map= new HashMap<Integer, Integer>();
        configureHashMap(this.map);
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

    void press(int keycode) {
        this.internal_keyboard[this.map.get(keycode)]=true;
        this.lastPressed=this.map.get(keycode);
    }

    void release(int keycode){
        this.lastPressed=-1;
        this.internal_keyboard[this.map.get(keycode)]=false;
    }
    int key() {
        return this.lastPressed;
    }
}
