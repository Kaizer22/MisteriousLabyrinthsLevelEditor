package com.example.denis.mlleveleditor;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //showMeDatabase();
    }

    public void toEditor(View v) {
        EditText x = (EditText) findViewById(R.id.x);
        EditText y = (EditText) findViewById(R.id.y);
        EditText num = (EditText) findViewById(R.id.number);
        Log.d("AAAAAAAAAAAAAAA", x.getText().toString());
        int lWidth = Integer.decode(x.getText().toString());
        int lHeight = Integer.decode(y.getText().toString());
        int number = Integer.decode(num.getText().toString());
        EditorView ev = new EditorView(this, lWidth, lHeight, number);


        setContentView(ev);
    }

    public void upgradeDatabase(View v) {
        LevelDBHelper ldbh = new LevelDBHelper(this);
        SQLiteDatabase ldb = ldbh.getWritableDatabase();
        ldbh.onUpgrade(ldb, 1, 1);
    }

    public void deleteChoosenLevel(View v) {
        EditText num = (EditText) findViewById(R.id.number);
        int number = Integer.decode(num.getText().toString());
        LevelDBHelper ldbh = new LevelDBHelper(this);
        SQLiteDatabase ldb = ldbh.getWritableDatabase();
        ldb.delete(LevelDBHelper.TABLE_LEVELS_INFO, LevelDBHelper.KEY_ID + " = " + number,null);
        ldb.delete(LevelDBHelper.TABLE_LEVEL_MAPS, LevelDBHelper.KEY_NUM_LEVEL + " = " + number,null);
    }

    private void showMeDatabase(){
        LevelDBHelper ldbh = new LevelDBHelper(this);
        SQLiteDatabase database = ldbh.getReadableDatabase();





        String selection;

        int levelIDIndex;
        int levelSizeXIndex;
        int levelSizeYIndex;
        int levelStartXIndex;
        int levelStartYIndex;
        int levelFinishXIndex;
        int levelFinishYIndex;

        int levelID;
        int levelSizeX;
        int levelSizeY;
        int levelStartX;
        int levelStartY;
        int levelFinishX;
        int levelFinishY;

        int blockXIndex;
        int blockYIndex;
        int blockTypeIndex;
        int blockShapeIndex;
        int blockIsTorchOnItIndex;
        int blockLevelIndex;

        int blockX;
        int blockY;
        String blockType;
        String blockShape;
        int blockIsTorchOnIt;
        int blockLevel;



        Cursor cursor = database.query(LevelDBHelper.TABLE_LEVELS_INFO,null,null,null,null,null,null);
        Cursor blocksCursor;
        cursor.moveToFirst();

        levelIDIndex = cursor.getColumnIndex(LevelDBHelper.KEY_ID);
        levelSizeXIndex = cursor.getColumnIndex(LevelDBHelper.KEY_LEVEL_SIZE_X);
        levelSizeYIndex = cursor.getColumnIndex(LevelDBHelper.KEY_LEVEL_SIZE_Y);
        levelStartXIndex = cursor.getColumnIndex(LevelDBHelper.KEY_LEVEL_START_X);
        levelStartYIndex = cursor.getColumnIndex(LevelDBHelper.KEY_LEVEL_START_Y);
        levelFinishXIndex = cursor.getColumnIndex(LevelDBHelper.KEY_LEVEL_FINISH_X);
        levelFinishYIndex = cursor.getColumnIndex(LevelDBHelper.KEY_LEVEL_FINISH_Y);


        do {
            cursor = database.query(LevelDBHelper.TABLE_LEVELS_INFO,null,null,null,null,null,null);
            cursor.moveToFirst();
            levelID = cursor.getInt(levelIDIndex);
            levelSizeX = cursor.getInt(levelSizeXIndex);
            levelSizeY = cursor.getInt(levelSizeYIndex);
            levelStartX = cursor.getInt(levelStartXIndex);
            levelStartY = cursor.getInt(levelStartYIndex);
            levelFinishX = cursor.getInt(levelFinishXIndex);
            levelFinishY = cursor.getInt(levelFinishYIndex);

            Log.d("Level"+levelID,"Size: " + levelSizeX + " : " + levelSizeY + "St" + levelStartX + ":" + levelStartY + "Fn" + levelFinishX + ":" +levelFinishY);


            selection = LevelDBHelper.KEY_NUM_LEVEL + " = " + levelID;

            blocksCursor = database.query(LevelDBHelper.TABLE_LEVEL_MAPS,null,selection,null,null,null,null);
            blocksCursor.moveToFirst();

            blockXIndex = blocksCursor.getColumnIndex(LevelDBHelper.KEY_BLOCK_X);
            blockYIndex = blocksCursor.getColumnIndex(LevelDBHelper.KEY_BLOCK_Y);
            blockTypeIndex = blocksCursor.getColumnIndex(LevelDBHelper.KEY_BLOCK_TYPE);
            blockShapeIndex = blocksCursor.getColumnIndex(LevelDBHelper.KEY_BLOCK_SHAPE);
            blockIsTorchOnItIndex = blocksCursor.getColumnIndex(LevelDBHelper.KEY_IS_TORCH_ON_BLOCK);
            blockLevelIndex = blocksCursor.getColumnIndex(LevelDBHelper.KEY_NUM_LEVEL);


            do{
                blockX = blocksCursor.getInt(blockXIndex);
                blockY = blocksCursor.getInt(blockYIndex);
                blockType = blocksCursor.getString(blockTypeIndex);
                blockShape = blocksCursor.getString(blockShapeIndex);
                blockIsTorchOnIt = blocksCursor.getInt(blockIsTorchOnItIndex);
                blockLevel = blocksCursor.getInt(blockLevelIndex);
                Log.d("Block " + blockX + ":" + blockY, blockType + " " + blockShape + " Torch: " + blockIsTorchOnIt + " LEVEL : " + blockLevel);

            }while (blocksCursor.moveToNext());


        }while (cursor.moveToNext());

        cursor.close();
        blocksCursor.close();
    }
}

