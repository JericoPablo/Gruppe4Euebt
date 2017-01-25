package com.hsrt.euebt;

/**
 * Created by Johannes on 11.12.2016.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * A helper class for managing the database's structure.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_TRAININGS = "trainings";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String TABLE_TRAINING_EXTRAS = "extras";
    public static final String COLUMN_EXTRA_NAME = "name";
    public static final String COLUMN_EXTRA_TYPE = "type";
    public static final String COLUMN_EXTRA_CONTENT = "content";

    private static final String DATABASE_NAME = "euebt.db";
    private static final int DATABASE_VERSION = 3;
    private static final String TRAININGS_TABLE_CREATE = "create table " + TABLE_TRAININGS + "( " + COLUMN_NAME + " text not null, " + COLUMN_TIMESTAMP + " integer not null, " + COLUMN_LOCATION + " text not null, " +COLUMN_LATITUDE + " text not null, "+COLUMN_LONGITUDE + " text not null );";
    private static final String EXTRAS_TABLE_CREATE = "create table " + TABLE_TRAINING_EXTRAS + "( " + COLUMN_EXTRA_NAME + " text not null, " + COLUMN_EXTRA_TYPE + " text not null, " + COLUMN_EXTRA_CONTENT + " text not null );";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(TRAININGS_TABLE_CREATE);
        database.execSQL(EXTRAS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ": Resetting database.");
        db.execSQL("drop table if exists " + TABLE_TRAININGS);
        onCreate(db);
    }
}