package com.themtgdeckgenius.sampleapplication.ui;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.themtgdeckgenius.sampleapplication.R;
import com.themtgdeckgenius.sampleapplication.SQLite.PermitLookupDatabaseHelper;
import com.themtgdeckgenius.sampleapplication.SQLite.PlateInfoDatabaseHelper;
import com.themtgdeckgenius.sampleapplication.SQLite.RepeatOffendersDatabaseHelper;
import com.themtgdeckgenius.sampleapplication.SQLite.tables.PermitLookupTable;
import com.themtgdeckgenius.sampleapplication.SQLite.tables.PlateInfoTable;
import com.themtgdeckgenius.sampleapplication.SQLite.tables.RepeatOffenderTable;
import com.themtgdeckgenius.sampleapplication.util.FileListInterface;
import com.themtgdeckgenius.sampleapplication.util.Options;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created by Michael Wheeler 7/14/2015
 * <p/>
 * Crossfading baised on tutorial from:
 * http://developer.android.com/training/animation/crossfade.html
 */

public class MainActivity
        extends Activity
        implements View.OnClickListener{

    private View mContentView;
    private View mLoadingView;
    private int mShortAnimationDuration;

    EditText mPlate;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContentView = findViewById(R.id.content);
        mLoadingView = findViewById(R.id.loading_spinner);
        Button mMButtonRepeatOffenders = (Button) findViewById(R.id.btnRepeatOffenders);
        Button mMButtonPermitSearch = (Button) findViewById(R.id.btnPermitSearch);
        Button mMButtonPlateInfo = (Button) findViewById(R.id.btnPlateInfoSearch);
        mPlate = (EditText) findViewById(R.id.plate);

        mMButtonRepeatOffenders.setOnClickListener(this);
        mMButtonPermitSearch.setOnClickListener(this);
        mMButtonPlateInfo.setOnClickListener(this);

        // Retrieve and cache the system's default "short" animation time.
        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        final AssetManager mAssetManager = getAssets();
        new Thread(new Runnable(){
            @Override
            public void run(){
                Properties properties = new Properties();
                final BufferedInputStream in;
                try{
                    in = new BufferedInputStream(mAssetManager.open(FileListInterface.OPTIONS_FILE));
                    properties.load(in);
                } catch(IOException e){
                    e.printStackTrace();
                }
                Options.set(properties, getApplicationContext());
            }
        }).start();

        new LoadDatabases().execute(null, null, null);

        if(Options.getOptions(Options.USES_REPEAT_OFFENDERS, true, getApplicationContext())){
            mMButtonRepeatOffenders.setVisibility(View.VISIBLE);
        }
        else{
            mMButtonRepeatOffenders.setVisibility(View.GONE);
        }
        if(Options.getOptions(Options.USES_PERMIT_LOOKUP, false, getApplicationContext())){
            mMButtonPermitSearch.setVisibility(View.VISIBLE);
        }
        else{
            mMButtonPermitSearch.setVisibility(View.GONE);
        }
        if(Options.getOptions(Options.USES_PLATE_INFO, true, getApplicationContext())){
            mMButtonPlateInfo.setVisibility(View.VISIBLE);
        }
        else{
            mMButtonPlateInfo.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_settings){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void crossfade(){

        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        mContentView.setAlpha(0f);
        mContentView.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        mContentView.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration)
                .setListener(null);

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        mLoadingView.animate()
                .alpha(0f)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter(){
                    @Override
                    public void onAnimationEnd(Animator animation){
                        mLoadingView.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.btnRepeatOffenders:
                checkRepeatOffenders(
                        mPlate.getText() == null ? "" : mPlate.getText().toString().trim(), Options.getOptions(Options.STATE, getApplicationContext()), this);
                break;
            case R.id.btnPermitSearch:
                checkPermits(
                        mPlate.getText() == null ? "" : mPlate.getText().toString().trim(), Options.getOptions(Options.STATE, getApplicationContext()), this);
                break;
            case R.id.btnPlateInfoSearch:
                checkForPlateInfo(
                        mPlate.getText() == null ? "" : mPlate.getText().toString().trim(), Options.getOptions(Options.STATE, getApplicationContext()), this);
                break;
        }
    }

    private class LoadDatabases
            extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... params){
            RepeatOffendersDatabaseHelper mRepeatOffendersDatabaseHelper = new RepeatOffendersDatabaseHelper(getApplicationContext());
            mRepeatOffendersDatabaseHelper.fillDatabase(getApplicationContext());
            PermitLookupDatabaseHelper mPermitLookupDatabaseHelper = new PermitLookupDatabaseHelper(getApplicationContext());
            mPermitLookupDatabaseHelper.fillDatabase(getApplicationContext());
            PlateInfoDatabaseHelper mPlateInfoDatabaseHelper = new PlateInfoDatabaseHelper(getApplicationContext());
            mPlateInfoDatabaseHelper.fillDatabase(getApplicationContext());
            return null;
        }

        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);
            crossfade();
        }
    }

    public void checkForPlateInfo(final String aPlate, final String aState, final Context context){
        PlateInfoDatabaseHelper mPlateInfoDatabaseHelper = new PlateInfoDatabaseHelper(context);
        SQLiteDatabase mPlateSQLiteDatabase = mPlateInfoDatabaseHelper.getReadableDatabase();
        Cursor mPlateInfoCursor = mPlateInfoDatabaseHelper.getPlateInfoCursor(mPlateSQLiteDatabase, aPlate, aState);
        PlateInfoTable mPlateInfoTable = new PlateInfoTable();
        if(mPlateInfoCursor.moveToFirst()){
            String vin = mPlateInfoCursor.getString(mPlateInfoCursor.getColumnIndex(mPlateInfoTable.getColumnName(3)));
            String make = mPlateInfoCursor.getString(mPlateInfoCursor.getColumnIndex(mPlateInfoTable.getColumnName(4)));
            String type = mPlateInfoCursor.getString(mPlateInfoCursor.getColumnIndex(mPlateInfoTable.getColumnName(5)));
            String color = mPlateInfoCursor.getString(mPlateInfoCursor.getColumnIndex(mPlateInfoTable.getColumnName(6)));
            final AlertDialog mAlertDialog = new AlertDialog.Builder(context).create();
            mAlertDialog.setTitle(R.string.plate_info_found);
            StringBuilder mStringBuilder = new StringBuilder();
            mStringBuilder.append("Vin: ").append(vin).append("\n");
            mStringBuilder.append("Make: ").append(make).append("\n");
            mStringBuilder.append("Model: ").append(type).append("\n");
            mStringBuilder.append("Color: ").append(color).append("\n");
            mAlertDialog.setMessage(mStringBuilder);
            mAlertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which){
                    mAlertDialog.dismiss();
                }
            });
            mAlertDialog.show();
        }
        else{
            final AlertDialog mAlertDialog = new AlertDialog.Builder(context).create();
            mAlertDialog.setTitle(R.string.no_plate_info_found);
            mAlertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which){
                    mAlertDialog.dismiss();
                }
            });
            mAlertDialog.show();
        }
    }

    public void checkPermits(final String aPlate, final String aState, final Context context){

        PermitLookupDatabaseHelper mPermitLookupDatabaseHelper = new PermitLookupDatabaseHelper(context);
        SQLiteDatabase mSQLitePermitDatabase = mPermitLookupDatabaseHelper.getReadableDatabase();
        PermitLookupTable mPermitLookupTable = new PermitLookupTable();
        StringBuilder mPermitStringBuilder = new StringBuilder();
        mPermitStringBuilder.append(aPlate);
        mPermitStringBuilder.append("-");
        mPermitStringBuilder.append(aState);
        mPermitStringBuilder.append(", ");
        Cursor mPermitCursor = mPermitLookupDatabaseHelper.getPermitCursor(mSQLitePermitDatabase, aPlate, aState);
        if(mPermitCursor.moveToFirst()){
            mPermitStringBuilder.append(mPermitCursor.getString(mPermitCursor.getColumnIndex(mPermitLookupTable.getColumnName(3))));
            mPermitStringBuilder.append(" - ");
            mPermitStringBuilder.append(mPermitCursor.getString(mPermitCursor.getColumnIndex(mPermitLookupTable.getColumnName(4))));
            final AlertDialog mAlertDialog = new AlertDialog.Builder(context).create();
            mAlertDialog.setTitle(R.string.permit_tital);
            mAlertDialog.setMessage(mPermitStringBuilder);
            mAlertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which){
                    mAlertDialog.dismiss();
                }
            });
            mAlertDialog.show();
        }
        else{
            final AlertDialog mAlertDialog = new AlertDialog.Builder(context).create();
            mAlertDialog.setTitle(R.string.no_permit_found);
            mAlertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which){
                    mAlertDialog.dismiss();
                }
            });
            mAlertDialog.show();
        }
    }

    public void checkRepeatOffenders(final String aPlate, final String aState, final Context context){
        RepeatOffendersDatabaseHelper mRepeatOffendersDatabaseHelper = new RepeatOffendersDatabaseHelper(context);
        SQLiteDatabase mSQLiteDatabase = mRepeatOffendersDatabaseHelper.getReadableDatabase();
        RepeatOffenderTable mRepeatOffenderTable = new RepeatOffenderTable();
        int numberOfViolations = mRepeatOffendersDatabaseHelper.getViolationsCount(mSQLiteDatabase, aState, aPlate);
        if(numberOfViolations > 0){
            Cursor allViolations = mRepeatOffendersDatabaseHelper.getViolationsCursor(mSQLiteDatabase, aState, aPlate);
            StringBuilder mStringBuilder = new StringBuilder();
            mStringBuilder.append(aPlate);
            mStringBuilder.append("-");
            mStringBuilder.append(aState);
            mStringBuilder.append(" has \n");
            for(int loop = 0; loop < numberOfViolations; loop++){
                allViolations.moveToPosition(loop);
                final String violationCode = allViolations.getString(allViolations.getColumnIndex(mRepeatOffenderTable.getColumnName(3)));
                final int warningsCount = mRepeatOffendersDatabaseHelper.getWarningsCount(mSQLiteDatabase, aState, aPlate, violationCode);
                final int violationsCount = mRepeatOffendersDatabaseHelper.getCitationsCount(mSQLiteDatabase, aState, aPlate, violationCode);
                if(warningsCount != 0){
                    mStringBuilder.append(warningsCount);
                    if(warningsCount == 1){
                        mStringBuilder.append(" warn");
                    }
                    else{
                        mStringBuilder.append(" warns");
                    }
                }
                if(violationsCount != 0){
                    if(warningsCount > 0){
                        mStringBuilder.append(", ");
                    }
                    mStringBuilder.append(violationsCount);
                    if(violationsCount == 1){
                        mStringBuilder.append(" cite ");
                    }
                    else{
                        mStringBuilder.append(" cites ");
                    }
                }
                mStringBuilder.append(" for ");
                mStringBuilder.append(violationCode);
                mStringBuilder.append("\n");
            }

            final AlertDialog mAlertDialog = new AlertDialog.Builder(context).create();
            mAlertDialog.setTitle("Violations");
            mAlertDialog.setMessage(mStringBuilder.toString());
            mAlertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which){
                    mAlertDialog.dismiss();
                }
            });
            mAlertDialog.show();
        }
        else{
            final AlertDialog mAlertDialog = new AlertDialog.Builder(context).create();
            mAlertDialog.setTitle("Violations");
            mAlertDialog.setMessage(context.getString(R.string.nothingFoundVios));
            mAlertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which){
                    mAlertDialog.dismiss();
                }
            });
            mAlertDialog.show();
        }
    }
}
