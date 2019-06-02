package com.example.healthy.Databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ReminderDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "reminder";
    private static final int DATABASE_VERSION = 4;
    public static final String COLUMN_ID = "_id";
    public static final String TITLE = "title";
    public static final String TYPE = "type";
    public static final String TABLE_NAME = "tasks";
    public static final String DETAIL = "detail";
    public static final String DATE = "date";
    public static final String TIME = "time";

    public ReminderDatabase(Context context) {
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createDB = "create table if not exists " + TABLE_NAME + " ( "
                + COLUMN_ID + " integer primary key autoincrement, "
                + TITLE + " text, "
                + DETAIL + " text, "
                + TYPE + " text, "
                + TIME + " text, "
                + DATE + " text)";
        db.execSQL(createDB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + TABLE_NAME);
        onCreate(db);
    }
}
