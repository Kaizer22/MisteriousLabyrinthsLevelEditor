package com.example.denis.mlleveleditor;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Arrays;

/**
 * Created by denis on 11.07.17.
 */

public class EditorView extends View {
    Paint paint;

    int levelSixeX;
    int levelSizeY;

    final int scaleH = 14;
    int scaleW;

    int cellSize;
    int buttonSize;

    int currentX;
    int currentY;

    int startEventMoveX;
    int startEventMoveY;

    boolean moveMode = true;

    Type[][] encodedLevelMap;
    Type typeBrush;

    MyButton toBG;
    MyButton toWALL;
    MyButton toWALLWT;
    MyButton toSTART;
    MyButton toFINISH;

    public EditorView(Context context, int x,int y) {
        super(context);
        paint = new Paint();
        levelSixeX = x;
        levelSizeY = y;

        currentY = 0;
        currentX = 0;

        encodedLevelMap = new Type[levelSizeY][levelSixeX];
        typeBrush = Type.BACKGROUND;
        newMap();
        Log.d("fdgsgsdf",buttonSize+"");


    }


    void newMap(){
        for (int i = 0; i < encodedLevelMap.length; i++) {
            Arrays.fill(encodedLevelMap[i],Type.WALL);
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
        drawMap(canvas);
        canvas.drawBitmap(toBG.icon,toBG.x,toBG.y,paint);
        canvas.drawBitmap(toWALL.icon,toWALL.x,toWALL.y,paint);
        canvas.drawBitmap(toWALLWT.icon,toWALLWT.x,toWALLWT.y,paint);
        super.onDraw(canvas);

    }

    private void drawMap(Canvas canvas){
        for (int i = currentY, k = 0; i < currentY+scaleH && i < encodedLevelMap.length; i++,k++) {
            for (int j = currentX, d = 0; j < currentX+scaleW && j < encodedLevelMap[0].length ; j++,d++) {
                switch (encodedLevelMap[i][j]){
                    case BACKGROUND:
                        paint.setColor(Color.GRAY);
                        canvas.drawRect(d*cellSize,k*cellSize,d*cellSize+cellSize,k*cellSize+cellSize,paint);
                        break;
                    case WALL:
                        paint.setColor(Color.RED);
                        canvas.drawRect(d*cellSize,k*cellSize,d*cellSize+cellSize,k*cellSize+cellSize,paint);
                        break;
                    case WALL_WITH_TORCH:
                        paint.setColor(Color.YELLOW);
                        canvas.drawRect(d*cellSize,k*cellSize,d*cellSize+cellSize,k*cellSize+cellSize,paint);

                }
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cellSize = h/ scaleH;
        buttonSize = (int)(cellSize*1.5);
        scaleW = w/cellSize;

        toBG = new MyButton(0,0,buttonSize,BitmapFactory.decodeResource(getResources(), R.drawable.bg)) {
            @Override
            void makeAction() {
                super.makeAction();
                typeBrush = Type.BACKGROUND;
            }
        };

        toWALL = new MyButton(buttonSize,0,buttonSize,BitmapFactory.decodeResource(getResources(), R.drawable.wall)) {
            @Override
            void makeAction() {
                super.makeAction();
                typeBrush = Type.WALL;
            }
        };
        toWALLWT = new MyButton(buttonSize*2,0,buttonSize,BitmapFactory.decodeResource(getResources(), R.drawable.torch)) {
            @Override
            void makeAction() {
                super.makeAction();
                typeBrush = Type.WALL_WITH_TORCH;
            }
        };
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int cellX = (int)x/cellSize+currentX;
        int cellY = (int)y/cellSize+currentY;
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (toBG.zone.contains((int)x,(int)y)){
                    toBG.makeAction();
                }else  if (toWALL.zone.contains((int)x,(int)y)){
                    toWALL.makeAction();
                }else  if (toWALLWT.zone.contains((int)x,(int)y)) {
                    toWALLWT.makeAction();
                }else
                    encodedLevelMap[cellY][cellX] = typeBrush;

                startEventMoveX = (int)x;
                startEventMoveY = (int)y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (moveMode){
                    if (startEventMoveX-x > cellSize*4 && currentX<encodedLevelMap[0].length-1)
                        currentX++;
                    else if (startEventMoveX-x < (-1)*cellSize*4 && currentX>0)
                        currentX--;
                    if (startEventMoveY-y > cellSize*4 && currentY<encodedLevelMap.length-1 )
                        currentY++;
                    else if (startEventMoveY-y < (-1)*cellSize*4 && currentY>0)
                        currentY--;
                }else
                    encodedLevelMap[cellY][cellX] = typeBrush;
                onUpdate();
                break;
            case MotionEvent.ACTION_UP:
                 if (toBG.zone.contains((int)x,(int)y)){
                    toBG.makeAction();
                }else  if (toWALL.zone.contains((int)x,(int)y)){
                    toWALL.makeAction();
                }else  if (toWALLWT.zone.contains((int)x,(int)y)) {
                    toWALLWT.makeAction();
                }else
                    encodedLevelMap[cellY][cellX] = typeBrush;
                onUpdate();
        }

        return true;
    }

    void onUpdate(){
        invalidate();
    }


    enum Type{
        BACKGROUND, WALL, WALL_WITH_TORCH;
    }
}
