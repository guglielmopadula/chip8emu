package org.dssc.chip8;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import static java.awt.image.BufferedImage.TYPE_BYTE_BINARY;

public class Screen {
    final int white = Color.WHITE.getRGB();
    final int black = Color.BLACK.getRGB();
    private static int height = 32;
    private static int width = 64;
    private int scale=5;
    private BufferedImage backend;
    public Screen(int scale) {
        this.scale=scale;
        backend=new BufferedImage(width*scale,height*scale,TYPE_BYTE_BINARY);
        int[] temp= new int[32*64*scale*scale];
        Arrays.fill(temp,Color.BLACK.getRGB());
        backend.setRGB(0,0,64*scale,32*scale,temp,0,
                64*scale);

    }

    public void ClearScreen(){
        int[] temp= new int[32*64*this.scale*this.scale];
        Arrays.fill(temp,Color.BLACK.getRGB());
        backend.setRGB(0,0,64*scale,32*scale,temp,0, scale);
    }
    public void DrawPixel(int i, int j){
        int[] temp=new int[scale*scale];
        Arrays.fill(temp,Color.WHITE.getRGB());
        backend.setRGB(j*scale,i*scale,scale,scale,temp,0, scale);
    }
    public void DrawPixel_black(int i, int j){
        int[] temp=new int[scale*scale];
        Arrays.fill(temp,Color.BLACK.getRGB());
        backend.setRGB(j*scale,i*scale,scale,scale,temp,0, scale);
    }

    public int getPixel(int i, int j){
        return backend.getRGB(j*scale,i*scale);
    }

    public BufferedImage snapshot(){
        return backend;
    }

}
