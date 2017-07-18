package com.example.denis.mlleveleditor;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by denis on 11.07.17.
 */

public class EditorView extends View {
    int levelNumber;

    Paint paint;
    Bitmap wallCell;

    int levelSixeX;
    int levelSizeY;

    int levelStartX;
    int levelStartY;

    int levelFinishX;
    int levelFinishY;

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
    MyButton chngMode;
    MyButton insertButton;
    MyButton toSTART;
    MyButton toFINISH;

    public EditorView(Context context, int x,int y, int ln) {
        super(context);
        paint = new Paint();
        levelSixeX = x;
        levelSizeY = y;
        levelNumber = ln;

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
        canvas.drawBitmap(chngMode.icon,chngMode.x,chngMode.y,paint);
        canvas.drawBitmap(toWALL.icon,toWALL.x,toWALL.y,paint);
        canvas.drawBitmap(toWALLWT.icon,toWALLWT.x,toWALLWT.y,paint);
        canvas.drawBitmap(toSTART.icon,toSTART.x,toSTART.y,paint);
        canvas.drawBitmap(toFINISH.icon,toFINISH.x,toFINISH.y,paint);
        canvas.drawBitmap(insertButton.icon,insertButton.x,insertButton.y,paint);
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

                        canvas.drawBitmap(wallCell,d*cellSize,k*cellSize,paint);
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

        wallCell = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.wall_cell),cellSize,cellSize,false);

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

        chngMode = new MyButton(w-buttonSize*2,h-buttonSize*2,buttonSize*2,BitmapFactory.decodeResource(getResources(), R.drawable.chng)) {
            @Override
            void makeAction() {
                super.makeAction();
                if (moveMode)
                    moveMode = false;
                else
                    moveMode = true;
            }
        };
        toSTART = new MyButton(buttonSize*3,0,buttonSize,BitmapFactory.decodeResource(getResources(), R.drawable.start)) {
            @Override
            void makeAction() {
                super.makeAction();
                typeBrush = Type.START;
            }
        };

        toFINISH = new MyButton(buttonSize*4,0,buttonSize,BitmapFactory.decodeResource(getResources(), R.drawable.finish)) {
            @Override
            void makeAction() {
                super.makeAction();
                typeBrush = Type.FINISH;
            }
        };
        insertButton = new MyButton(0,h-buttonSize*2,buttonSize*2,BitmapFactory.decodeResource(getResources(), R.drawable.insert)) {
            @Override
            void makeAction() {
                super.makeAction();

                MaskDBHelper mDBHelper = new MaskDBHelper(getContext());
                LevelDBHelper lDBHelper = new LevelDBHelper(getContext());

                SQLiteDatabase maskDatabase;
                try {
                    mDBHelper.updateDataBase();
                } catch (IOException mIOException) {
                    throw new Error("UnableToUpdateDatabase");
                }

                try {
                    maskDatabase = mDBHelper.getReadableDatabase();
                } catch (SQLException mSQLException) {
                    throw mSQLException;
                }
                SQLiteDatabase levelsDatabase = lDBHelper.getWritableDatabase();
                MapManager md = new MapManager(getContext(), maskDatabase, levelsDatabase, levelNumber ,levelStartX ,levelStartY, levelFinishX, levelFinishY );
                md.doInsetrion(encodedLevelMap);


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

                startEventMoveX = (int)x;
                startEventMoveY = (int)y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (moveMode){
                    if (startEventMoveX-x > cellSize*4 && currentX<encodedLevelMap[0].length-1-scaleW)
                        currentX++;
                    else if (startEventMoveX-x < (-1)*cellSize*4 && currentX>0)
                        currentX--;
                    if (startEventMoveY-y > cellSize*4 && currentY<encodedLevelMap.length-1 - scaleH )
                        currentY++;
                    else if (startEventMoveY-y < (-1)*cellSize*4 && currentY>0)
                        currentY--;
                }else
                    if (typeBrush != Type.START && typeBrush != Type.FINISH)
                        encodedLevelMap[cellY][cellX] = typeBrush;
                    else if (typeBrush == Type.START){
                        levelStartX = cellX;
                        levelStartY = cellY;
                        typeBrush = Type.WALL;
                        Toast.makeText(getContext(),"Start :"+cellX + " " + cellY, Toast.LENGTH_LONG).show();
                    }else if (typeBrush == Type.FINISH) {
                        levelFinishX = cellX;
                        levelFinishY = cellY;
                        typeBrush = Type.WALL;
                        Toast.makeText(getContext(), "Finish :" + cellX + " " + cellY, Toast.LENGTH_LONG).show();
                    }
                onUpdate();
                break;
            case MotionEvent.ACTION_UP:
                if (toBG.zone.contains((int)x,(int)y)){
                    toBG.makeAction();
                }else  if (toWALL.zone.contains((int)x,(int)y)){
                    toWALL.makeAction();
                }else  if (toWALLWT.zone.contains((int)x,(int)y)) {
                    toWALLWT.makeAction();
                }else  if (chngMode.zone.contains((int)x,(int)y)) {
                     chngMode.makeAction();
                }else  if (toSTART.zone.contains((int)x,(int)y)) {
                    toSTART.makeAction();
                }else  if (toFINISH.zone.contains((int)x,(int)y)) {
                    toFINISH.makeAction();
                }else  if (insertButton.zone.contains((int)x,(int)y)) {
                    insertButton.makeAction();
                }else if (!moveMode)
                    if (typeBrush != Type.START && typeBrush != Type.FINISH)
                        encodedLevelMap[cellY][cellX] = typeBrush;
                    else if (typeBrush == Type.START){
                        levelStartX = cellX;
                        levelStartY = cellY;
                        typeBrush = Type.WALL;
                        Toast.makeText(getContext(),"Start :"+cellX + " " + cellY, Toast.LENGTH_LONG).show();
                    }else if (typeBrush == Type.FINISH) {
                        levelFinishX = cellX;
                        levelFinishY = cellY;
                        typeBrush = Type.WALL;
                        Toast.makeText(getContext(), "Finish :" + cellX + " " + cellY, Toast.LENGTH_LONG).show();
                    }
                onUpdate();
        }

        return true;
    }

    void onUpdate(){
        invalidate();
    }


    enum Type{
        BACKGROUND, WALL, WALL_WITH_TORCH,
        START,FINISH,
        DOESNT_MATTER;
    }
}
