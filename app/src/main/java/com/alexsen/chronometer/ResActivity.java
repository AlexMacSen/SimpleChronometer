package com.alexsen.chronometer;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.alexsen.chronometer.db.ChronoDbHelper;

public class ResActivity extends Activity {

    private ListView listLv;

    // create an object to create and manage database versions
    SQLiteDatabase db = ChronometerApp.getDatabaseHelper().getReadableDatabase();
    Cursor cursor = db.query(ChronoDbHelper.TABLE_NAME, null, null, null, null, null, null);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res);


        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(ChronoDbHelper.KEY_ID);
            int timerIndex = cursor.getColumnIndex(ChronoDbHelper.VALUE);
            do {
                Log.d(ChronometerActivity.LOG_TAG, "ID = " + cursor.getInt(idIndex) +
                        ", value = " + cursor.getString(timerIndex));

            } while (cursor.moveToNext());
        } else
            Log.d(ChronometerActivity.LOG_TAG, "0 rows");

        // create a data adapter and attach it to the list

        ListAdapter mAdapter = new SimpleCursorAdapter(this,
                R.layout.item, cursor,
                new String[]{ChronoDbHelper.KEY_ID, ChronoDbHelper.VALUE},
                new int[]{R.id.idCount, R.id.value});

        listLv = (ListView) findViewById(R.id.listLv);
        listLv.setAdapter(mAdapter);

    }

    @Override
    protected void onDestroy() {
        // close cursor
        cursor.close();
        // destroy second activity
        super.onDestroy();
        Log.d(ChronometerActivity.LOG_TAG, "--- Second activity is destroyed ---");
    }
}
