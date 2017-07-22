package com.example.kjming.note7;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Kjming on 5/3/2017.
 */

public class MyDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "noteData.db";
    public static final int VERSION = 2;
    private static SQLiteDatabase database;

    public MyDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,int version) {
        super(context,name,factory,version);
    }

    public static SQLiteDatabase getDatabase(Context context) {
        if (database == null||!database.isOpen()) {
            database = new MyDBHelper(context,DATABASE_NAME,null,VERSION).getWritableDatabase();
        }
        return database;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(NoteDatabase.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NoteDatabase.TABLE_NAME);
        onCreate(db);
    }




}
