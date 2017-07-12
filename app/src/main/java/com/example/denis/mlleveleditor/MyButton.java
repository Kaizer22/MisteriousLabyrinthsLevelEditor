package com.example.denis.mlleveleditor;

import android.graphics.Bitmap;
import android.support.annotation.Size;
import android.support.constraint.solver.widgets.Rectangle;

/**
 * Created by denis on 12.07.17.
 */

abstract class MyButton {
    int x;
    int y;
    int size;
    Rectangle zone;
    Bitmap icon;

    MyButton(int bx,int by,int bsize, Bitmap bitmap){
        x = bx;
        y = by;
        size = bsize;
        icon = Bitmap.createScaledBitmap(bitmap,size,size,false);
        zone = new Rectangle();
        zone.setBounds(x,y,size,size);
    }


    void makeAction(){}
}
