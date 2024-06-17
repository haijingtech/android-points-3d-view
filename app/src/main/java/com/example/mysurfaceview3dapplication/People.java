package com.example.mysurfaceview3dapplication;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class People {
    public ArrayList<AgeHeight> myRecords;
    int color;
    Bitmap bitmap;
    String birthday;
    public People(ArrayList<AgeHeight> records, String bd,int color,
                  Bitmap bitmap){
        myRecords=records;
        this.color=color;
        this.bitmap=bitmap;
        birthday=bd;
    }
}
