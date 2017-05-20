package com.alexsen.chronometer;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.alexsen.chronometer.db.ChronoDbHelper;
import com.alexsen.chronometer.utils.ToastUtils;
import com.alexsen.dialogfragmetns.Fragment1;

public class ChronometerActivity extends Activity implements OnClickListener {

    public static final String LOG_TAG = "myLogs";

    private Chronometer mChronometer;

    // constants for the chronometer
    public static final String IS_TIMER_STOPPED = "stopTimer";
    public static final String START_TIME = "startTime";
    public static final String BASE_TIME = "baseTime";
    public static final String ELAPSED_TIME = "elapsedTime";
    public static final String DELTA_TIME = "deltaTime";

    // constants for the contextMenu
    public static final int MENU_VIEW = 101;
    public static final int MENU_CLEAR = 102;
    public static final int MENU_EXIT = 103;

    boolean eventStop = true;
    long elapsedTime, startTime, deltaTime;

    private ImageButton buttonStart;
    private ImageButton buttonStop;
    private ImageButton buttonReset;
    private ImageButton buttonSave;

    LinearLayout linearLayout;

    Fragment1 dialogFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        linearLayout = (LinearLayout) findViewById(R.id.root);
        registerForContextMenu(linearLayout);

        buttonStart = (ImageButton) findViewById(R.id.button_start);
        buttonStop = (ImageButton) findViewById(R.id.button_stop);
        buttonReset = (ImageButton) findViewById(R.id.button_reset);
        buttonSave = (ImageButton) findViewById(R.id.save_results);
        mChronometer = (Chronometer) findViewById(R.id.chronometer);

        buttonStart.setOnClickListener(this);
        buttonStop.setOnClickListener(this);
        buttonReset.setOnClickListener(this);
        buttonSave.setOnClickListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState) {
        // save current variable eventStop
        saveInstanceState.putBoolean(IS_TIMER_STOPPED, eventStop);
        // Save start time
        saveInstanceState.putLong(START_TIME, startTime);
        // Save current base time
        saveInstanceState.putLong(BASE_TIME, mChronometer.getBase());
        // save elapsed time
        saveInstanceState.putLong(ELAPSED_TIME, elapsedTime);
        // save delta time
        saveInstanceState.putLong(DELTA_TIME, deltaTime);
        // call the superclass
        super.onSaveInstanceState(saveInstanceState);
        Log.d(LOG_TAG, START_TIME);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {

        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        eventStop = savedInstanceState.getBoolean(IS_TIMER_STOPPED);
        startTime = savedInstanceState.getLong(START_TIME);
        elapsedTime = savedInstanceState.getLong(ELAPSED_TIME);
        deltaTime = savedInstanceState.getLong(DELTA_TIME);
        if (eventStop) {
            if (elapsedTime > 0l) {
                long newBaseTime = SystemClock.elapsedRealtime() - elapsedTime;
                mChronometer.setBase(newBaseTime);
            }
            Log.d(LOG_TAG, ELAPSED_TIME);
        } else {
            long baseTime = savedInstanceState.getLong(BASE_TIME);
            mChronometer.setBase(baseTime);
            mChronometer.start();
            Log.d(LOG_TAG, START_TIME);
        }

    }

    @Override

    public void onClick(View v) {

        // create an object for the data
        ContentValues values = new ContentValues();
        // connect to the database
        SQLiteDatabase db = ChronometerApp.getDatabaseHelper().getWritableDatabase();

        switch (v.getId()) {
            case R.id.button_start:
                //Start time (Only for save info about start time in database);
                startTime = System.currentTimeMillis();
                //Set new base time;
                if (elapsedTime > 0l) {
                    long newBaseTime = SystemClock.elapsedRealtime() - elapsedTime;
                    mChronometer.setBase(newBaseTime);
                } else {
                    mChronometer.setBase(SystemClock.elapsedRealtime());
                }
                mChronometer.start();
                buttonStart.setClickable(false);
                eventStop = false;
                ToastUtils.showToast(this, R.string.info_start);
                break;
            case R.id.button_stop:
                buttonStart.setClickable(true);
                mChronometer.stop();
                // If the stop button is pressed the variable eventStop is assigned a value true
                if (!eventStop) {
                    eventStop = true;
                    elapsedTime = SystemClock.elapsedRealtime() - mChronometer.getBase();
                    startTime = 0l;
                    ToastUtils.showToast(this, R.string.info_stop);
                }
                break;
            case R.id.button_reset:
                mChronometer.setBase(SystemClock.elapsedRealtime());
                startTime = System.currentTimeMillis();
                elapsedTime = 0l;
                ToastUtils.showToast(this, R.string.info_reset);
                break;
            case R.id.save_results:
                Log.d(LOG_TAG, "--- Insert in mytable: ---");
                // prepare data to be inserted in the form of steam: the name of the column - value
                long rowID;
                if (eventStop) {
                    deltaTime = elapsedTime;
                } else {
                    deltaTime = System.currentTimeMillis() - startTime + elapsedTime;
                }
                String time = String.format("%02d:%02d:%02d", deltaTime / 3600000,
                        (deltaTime % 3600000) / 60000, (deltaTime % 60000) / 1000);
                db.beginTransaction();
                try {
                    values.put(ChronoDbHelper.VALUE, time);
                    // insert a record and get her ID
                    rowID = db.insert(ChronoDbHelper.TABLE_NAME, null, values);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                Log.d(LOG_TAG, "row inserted, ID = " + rowID);
                ToastUtils.showToast(this, R.string.info_save);
                break;
        }

    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, MENU_VIEW, 0, R.string.view);
        menu.add(0, MENU_CLEAR, 0, R.string.clear);
        menu.add(0, MENU_EXIT, 0, R.string.exit);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        // connect to the database
        SQLiteDatabase db = ChronometerApp.getDatabaseHelper().getWritableDatabase();
        switch (id) {
            case MENU_VIEW:
                ToastUtils.showToast(this, R.string.info_view);
                doCursor();
                break;

            case MENU_CLEAR:
                Log.d(LOG_TAG, "--- Clear mytable: ---");
                // delete all recording
                int clearCount = db.delete(ChronoDbHelper.TABLE_NAME, null, null);
                Log.d(LOG_TAG, "deleted rows count = " + clearCount);
                ToastUtils.showToast(this, R.string.info_clear);
                break;

            case MENU_EXIT:
                dialogFragment = new Fragment1(
                        getString(R.string.menu_exit));
                dialogFragment.show(getFragmentManager(), "dialog");
                break;
        }

        return super.onContextItemSelected(item);
    }
    public void onBackPressed (){
        dialogFragment = new Fragment1(
                getString(R.string.menu_exit));
        dialogFragment.show(getFragmentManager(), "dialog");
    }
    public void doPositiveClick() {
        // close the connection to the database
        ChronometerApp.getDatabaseHelper().close();
        // close application
        finish();
    }

    public void doNegativeClick() {
        // return to Activity
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chronometer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // connect to the database
        SQLiteDatabase db = ChronometerApp.getDatabaseHelper().getWritableDatabase();
        int id = item.getItemId();
        switch (id) {
            case R.id.view_results:
                ToastUtils.showToast(this, R.string.info_view);
                doCursor();
                break;
            case R.id.clear_results:
                Log.d(LOG_TAG, "--- Clear mytable: ---");
                // delete all recording
                int clearCount = db.delete(ChronoDbHelper.TABLE_NAME, null, null);
                Log.d(LOG_TAG, "deleted rows count = " + clearCount);
                ToastUtils.showToast(this, R.string.info_clear);
                break;
            case R.id.button_exit:
                dialogFragment = new Fragment1(
                        getString(R.string.menu_exit));
                dialogFragment.show(getFragmentManager(), "dialog");
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void doCursor() {

        Intent intent = new Intent(this, ResActivity.class);
        startActivity(intent);
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
