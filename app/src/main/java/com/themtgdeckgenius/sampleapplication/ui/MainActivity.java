package com.themtgdeckgenius.sampleapplication.ui;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.themtgdeckgenius.sampleapplication.R;
import com.themtgdeckgenius.sampleapplication.SQLite.PermitLookupDatabaseHelper;
import com.themtgdeckgenius.sampleapplication.SQLite.PlateInfoDatabaseHelper;
import com.themtgdeckgenius.sampleapplication.SQLite.RepeatOffendersDatabaseHelper;
import com.themtgdeckgenius.sampleapplication.util.FileListInterface;
import com.themtgdeckgenius.sampleapplication.util.Options;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Michael Wheeler 7/14/2015
 * <p/>
 * Crossfading based on tutorial from:
 * http://developer.android.com/training/animation/crossfade.html
 */

public class MainActivity
        extends Activity
        implements View.OnClickListener{

    private View mContentView;
    private View mLoadingView;
    private int mShortAnimationDuration;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContentView = findViewById(R.id.content);
        mLoadingView = findViewById(R.id.loading_spinner);

        Button mSearchButton = (Button) findViewById(R.id.btnSearch);
        Button mRecordButton = (Button) findViewById(R.id.btnRecord);

        mSearchButton.setOnClickListener(this);
        mRecordButton.setOnClickListener(this);

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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
        Intent mIntent = new Intent();
        switch(v.getId()){
            case R.id.btnSearch:
                mIntent.setClass(getApplicationContext(), SearchDatabaseActivity.class);
                break;
            case R.id.btnRecord:
                mIntent.setClass(getApplicationContext(), VoiceCaptureActivity.class);
                break;
        }
        startActivity(mIntent);
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
}
