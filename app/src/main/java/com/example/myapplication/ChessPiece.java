package com.example.myapplication;

/**
 * Created by 안탄 on 2018-04-03.
 */

public class ChessPiece {
    public int height;
    public int width;
    public String name;
    public String xy;

    public ChessPiece(int height, int width, String name){
        this.height = height;
        this.width = width;
        this.name = name;
        this.xy = ( Character.toString((char)(height+65)) + Integer.toString(8-width));
    }



}
