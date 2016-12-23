package com.alexsen.chronometer;

import android.app.Application;

import com.alexsen.chronometer.db.ChronoDbHelper;

public class ChronometerApp extends Application {

    private static ChronometerApp application;
    private static ChronoDbHelper dbHelper;

    {
        application = this;
    }

    public static synchronized ChronoDbHelper getDatabaseHelper() {
        if (dbHelper == null) {
            dbHelper = new ChronoDbHelper(application);
        }
        return dbHelper;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
