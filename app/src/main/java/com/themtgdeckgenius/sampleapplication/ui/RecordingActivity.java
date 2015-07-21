package com.themtgdeckgenius.sampleapplication.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.themtgdeckgenius.sampleapplication.R;
import com.themtgdeckgenius.sampleapplication.adapter.AudioAdapter;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

public class RecordingActivity
        extends Activity{

    private static final String LOGTAG = RecordingActivity.class.getSimpleName();
    AudioAdapter mAudioAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);
        ListView mRecordingsListView = (ListView) findViewById(R.id.recordingsList);
        mAudioAdapter = new AudioAdapter();
        mRecordingsListView.setAdapter(mAudioAdapter);
        Button mStartRecordButton = (Button) findViewById(R.id.btnStartRecord);
        mStartRecordButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recording, menu);
        return true;
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            final String sourceFile = getLastCaptureFilename();
            addToAdapter(sourceFile, data, true);
        }
    }

    protected void addToAdapter(String aSourceFile, final Intent aData, final boolean aDeleteSource){
        File source = new File(aSourceFile);
        if(!source.exists()){
            Uri fileUri = aData.getData();
            source = new File(source.getParentFile(), source.getName() + ".mp4");
            mAudioAdapter.add(source);
        }
    }

    protected void startMediaCapture(){
        File outputFile = createNewMediaFile();
        if(outputFile == null){
            Log.w(LOGTAG, "output file is null");
            return;
        }
        Intent intent = createCaptureIntent();
        startActivityForResult(intent, 1);
    }

    protected Intent createCaptureIntent(){
        Intent intent = new Intent();
        intent.setClass(this, VoiceCaptureActivity.class);
        return intent;
    }

    protected File createNewMediaFile(){
        File targetFile;
        final int maxCount = getItemMaxCount();

        Set<File> files = new HashSet<File>();
        File dir = new File(getWorkDir());
        final File[] listFiles = dir.listFiles(new FilenameFilter(){

            @Override
            public boolean accept(final File dir, final String filename){
                return filename.startsWith("Aud");
            }
        });
        for(File file : listFiles){
            files.add(new File(file.getParentFile(), FilenameUtils.getBaseName(file.getName())));
        }
        for(int i = 1; i <= maxCount; i++){
            String filename = MessageFormat.format("Aud{0}_0{1}", getArguments().getString(ARG_CITEID), i);
            targetFile = new File(getWorkDir(), filename);
            if(!files.contains(targetFile)){
                return targetFile;
            }
        }
        return null;
    }
}
