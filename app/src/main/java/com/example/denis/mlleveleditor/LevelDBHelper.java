package com.example.denis.mlleveleditor;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by denis on 14.07.17.
 */

public class LevelDBHelper extends SQLiteOpenHelper {
    static final String TABLE_LEVELS_INFO = "levelInfo";
    static final String TABLE_PLAYER_INFO = "playerInfo";
    static final String TABLE_LEVEL_MAPS = "levelMaps";

    static final String KEY_ID = "_id";

    static final String KEY_LEVEL_SIZE_X = "level_size_x";
    static final String KEY_LEVEL_SIZE_Y = "level_size_y";
    static final String KEY_LEVEL_START_X = "level_start_x";
    static final String KEY_LEVEL_START_Y = "level_start_y";
    static final String KEY_LEVEL_FINISH_X = "level_finish_x";
    static final String KEY_LEVEL_FINISH_Y = "level_finish_y";

    static final String KEY_BLOCK_X = "block_x";
    static final String KEY_BLOCK_Y = "block_y";
    static final String KEY_BLOCK_TYPE = "block_type";
    static final String KEY_BLOCK_SHAPE = "block_shape";
    static final String KEY_IS_TORCH_ON_BLOCK = "is_torch_on_block";  //Integer 1/0
    static final String KEY_NUM_LEVEL = "num_level";

    private static String DB_NAME = "gameplay.db";
    private static String DB_PATH = "";
    private static final int DB_VERSION = 1;

    public LevelDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableLevels = "CREATE TABLE " + TABLE_LEVELS_INFO +
                "(" + KEY_ID + " INTEGER PRIMARY KEY, " +
                KEY_LEVEL_SIZE_X + " INTEGER NOT NULL, " +
                KEY_LEVEL_SIZE_Y + " INTEGER NOT NULL, " +
                KEY_LEVEL_START_X + " INTEGER NOT NULL, " +
                KEY_LEVEL_START_Y + " INTEGER NOT NULL, " +
                KEY_LEVEL_FINISH_X + " INTEGER NOT NULL, " +
                KEY_LEVEL_FINISH_Y + " INTEGER NOR NULL)";

        String createTableBlocks = "CREATE TABLE " + TABLE_LEVEL_MAPS +
                "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                KEY_BLOCK_X + " INTEGER NOT NULL, "+
                KEY_BLOCK_Y + " INTEGER NOT NULL, "+
                KEY_BLOCK_TYPE + " TEXT NOT NULL, "+
                KEY_BLOCK_SHAPE + " TEXT NOT NULL, "+
                KEY_IS_TORCH_ON_BLOCK + " INTEGER NOT NULL, "+
                KEY_NUM_LEVEL + " INTEGER NOT NULL)";

        db.execSQL(createTableBlocks);
        db.execSQL(createTableLevels);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LEVEL_MAPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LEVELS_INFO);

        onCreate(db);
    }
}
