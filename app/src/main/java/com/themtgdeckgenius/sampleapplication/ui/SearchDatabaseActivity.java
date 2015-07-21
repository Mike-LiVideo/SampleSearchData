package com.themtgdeckgenius.sampleapplication.ui;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.themtgdeckgenius.sampleapplication.R;
import com.themtgdeckgenius.sampleapplication.util.ButtonActions;
import com.themtgdeckgenius.sampleapplication.util.Options;

public class SearchDatabaseActivity
        extends Activity implements View.OnClickListener{

    private EditText mPlate;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_database);

        Button mMButtonRepeatOffenders = (Button) findViewById(R.id.btnRepeatOffenders);
        Button mMButtonPermitSearch = (Button) findViewById(R.id.btnPermitSearch);
        Button mMButtonPlateInfo = (Button) findViewById(R.id.btnPlateInfoSearch);
        mPlate = (EditText) findViewById(R.id.plate);

        mMButtonRepeatOffenders.setOnClickListener(this);
        mMButtonPermitSearch.setOnClickListener(this);
        mMButtonPlateInfo.setOnClickListener(this);

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
        getMenuInflater().inflate(R.menu.menu_search_database, menu);
        return true;
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.btnRepeatOffenders:
                ButtonActions.checkRepeatOffenders(
                        mPlate.getText() == null ? "" : mPlate.getText().toString().trim(), Options.getOptions(Options.STATE, getApplicationContext()), this);
                break;
            case R.id.btnPermitSearch:
                ButtonActions.checkPermits(
                        mPlate.getText() == null ? "" : mPlate.getText().toString().trim(), Options.getOptions(Options.STATE, getApplicationContext()), this);
                break;
            case R.id.btnPlateInfoSearch:
                ButtonActions.checkForPlateInfo(
                        mPlate.getText() == null ? "" : mPlate.getText().toString().trim(), Options.getOptions(Options.STATE, getApplicationContext()), this);
                break;
        }
    }
}
