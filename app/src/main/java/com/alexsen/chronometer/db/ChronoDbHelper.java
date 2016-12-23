package com.alexsen.chronometer.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ChronoDbHelper extends SQLiteOpenHelper {

    // constats for table
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "chrono.db";
    public static final String TABLE_NAME = "saveTimer";

    public static final String KEY_ID = "_id";
    public static final String VALUE = "valueTimer";
    private static final String TAG = ChronoDbHelper.class.getSimpleName();

    public ChronoDbHelper(Context context) {

        // superclass constructor
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        // create a table with fields
        Log.d(TAG, "--- onCreate database ---");

        db.execSQL("CREATE TABLE " + TABLE_NAME + "(" + KEY_ID
                + " INTEGER PRIMARY KEY," + VALUE + " TEXT" + ")");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // delete the old table with the fields
        db.execSQL("drop table if existts" + TABLE_NAME);
        Log.d(TAG, "--- drop database ---");
        onCreate(db);

    }

}
