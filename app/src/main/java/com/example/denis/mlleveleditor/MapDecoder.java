package com.example.denis.mlleveleditor;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedList;

/**
 * Created by denis on 14.07.17.
 */

public class MapDecoder {
    SQLiteDatabase masksDB;
    SQLiteDatabase levelsDB;

    int levelNum;

    int startX;
    int startY;

    int finishX;
    int finishY;


    private LinkedList<Mask> allMasks;

    MapDecoder(SQLiteDatabase mDB, SQLiteDatabase lDB, int n,int sX,int sY, int fX, int fY){
        masksDB = mDB;
        levelsDB = lDB;

        levelNum = n;

        startX = sX;
        startY = sY;

        finishX = fX;
        finishY = fY;

        allMasks = new LinkedList<Mask>();
        loadMasks();
    }

    void insertDecodedLevel(EditorView.Type[][] levelMap){
        ContentValues infoContentValues = new ContentValues();
        ContentValues mapContentValues = new ContentValues();

        infoContentValues.put(LevelDBHelper.KEY_ID,levelNum);
        infoContentValues.put(LevelDBHelper.KEY_LEVEL_SIZE_X,levelMap[0].length);
        infoContentValues.put(LevelDBHelper.KEY_LEVEL_SIZE_Y,levelMap.length);
        infoContentValues.put(LevelDBHelper.KEY_LEVEL_START_X,startX);
        infoContentValues.put(LevelDBHelper.KEY_LEVEL_START_Y,startY);
        infoContentValues.put(LevelDBHelper.KEY_LEVEL_FINISH_X,finishX);
        infoContentValues.put(LevelDBHelper.KEY_LEVEL_FINISH_Y,finishY);

        levelsDB.beginTransaction();

        levelsDB.insert(LevelDBHelper.TABLE_LEVELS_INFO,null,infoContentValues);
        infoContentValues.clear();

        for (int i = 0; i < levelMap.length ; i++) {
            for (int j = 0; j < levelMap[0].length ; j++) {
                if (i==0 || j ==0 || i==levelMap.length-1 || j == levelMap[0].length-1){
                    mapContentValues.put(LevelDBHelper.KEY_BLOCK_X,j);
                    mapContentValues.put(LevelDBHelper.KEY_BLOCK_Y,i);
                    mapContentValues.put(LevelDBHelper.KEY_BLOCK_TYPE,"WALL");
                    mapContentValues.put(LevelDBHelper.KEY_BLOCK_SHAPE,"DARKNESS");
                    mapContentValues.put(LevelDBHelper.KEY_IS_TORCH_ON_BLOCK,0);
                    mapContentValues.put(LevelDBHelper.KEY_NUM_LEVEL,levelNum);
                }else{
                    mapContentValues.put(LevelDBHelper.KEY_BLOCK_X,j);
                    mapContentValues.put(LevelDBHelper.KEY_BLOCK_Y,i);
                    if (levelMap[i][j] == EditorView.Type.WALL_WITH_TORCH){
                        mapContentValues.put(LevelDBHelper.KEY_BLOCK_TYPE,"WALL");
                    } else
                        mapContentValues.put(LevelDBHelper.KEY_BLOCK_TYPE,levelMap[i][j].toString());
                    if (levelMap[i][j] == EditorView.Type.BACKGROUND)
                        mapContentValues.put(LevelDBHelper.KEY_BLOCK_SHAPE,"NONE");
                    else
                        mapContentValues.put(LevelDBHelper.KEY_BLOCK_SHAPE,getStringShape(levelMap,j,i));
                    if (levelMap[i][j] == EditorView.Type.WALL_WITH_TORCH){
                        mapContentValues.put(LevelDBHelper.KEY_IS_TORCH_ON_BLOCK,1);
                    } else
                        mapContentValues.put(LevelDBHelper.KEY_IS_TORCH_ON_BLOCK,0);
                    mapContentValues.put(LevelDBHelper.KEY_NUM_LEVEL,levelNum);
                }

                levelsDB.insert(LevelDBHelper.TABLE_LEVEL_MAPS,null,mapContentValues);
                mapContentValues.clear();
            }
        }
        levelsDB.endTransaction();
    }

    private String getStringShape(EditorView.Type[][] levelMap,int x, int y){
        Mask bufMask = new Mask();
        for (int i = y-1, f = 0; f<3 ; i++, f++) {
            for (int j = x-1, k = 0; k < 3 ; j++, k++) {
                bufMask.mask[f][k] = levelMap[i][j];
            }
        }
        for (Mask e:allMasks){
            if (bufMask.equals(e))
                return e.shape;
        }
        return "DARKNESS";
    }

    void loadMasks(){
        Cursor dbCursor = masksDB.query(MaskDBHelper.TABLE_MASKS,null,null,null,null,null,null);
        dbCursor.moveToFirst();

        int shapeIndex;
        int codeIndex;

        String shape;
        String code;

        shapeIndex = dbCursor.getColumnIndex(MaskDBHelper.KEY_MASK_SHAPE);
        codeIndex = dbCursor.getColumnIndex(MaskDBHelper.KEY_MASK_CODE);

        do{
            shape = dbCursor.getString(shapeIndex);
            code = dbCursor.getString(codeIndex);

            allMasks.add(new Mask(shape,code));

        }while(dbCursor.moveToNext());

        dbCursor.close();
    }


    private class Mask{
        EditorView.Type[][] mask;
        String shape;

        Mask(String s, String code){
            mask = new EditorView.Type[3][3];
            shape = s;

            int f = 0;
            for (int i = 0; i<3 && f < code.length(); i++, f++){
                for (int j = 0; j < 3 && f < code.length(); j++, f++) {
                    switch (code.charAt(f)){
                        case 'D':
                            mask[i][j] = EditorView.Type.DOESNT_MATTER;
                            break;
                        case 'W':
                            mask[i][j] = EditorView.Type.WALL;
                            break;
                        case 'B':
                            mask[i][j] = EditorView.Type.BACKGROUND;
                            break;
                    }
                }
            }
        }

        Mask(){
            mask = new EditorView.Type[3][3];
        }

        @Override
        public boolean equals(Object obj) {
            Mask m2 = (Mask)obj;
            for (int i = 0; i <3; i++) {
                for (int j = 0; j <3; j++) {
                    if ((this.mask[i][j] != m2.mask[i][j]) && (m2.mask[i][j] != EditorView.Type.DOESNT_MATTER) && (this.mask[i][j] != EditorView.Type.WALL_WITH_TORCH && m2.mask[i][j] != EditorView.Type.WALL)){
                        return false;
                    }
                }
            }
            return true;
        }
    }
}
