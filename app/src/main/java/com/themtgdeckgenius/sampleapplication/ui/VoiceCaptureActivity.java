package com.themtgdeckgenius.sampleapplication.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.themtgdeckgenius.sampleapplication.R;
import com.themtgdeckgenius.sampleapplication.util.Options;

import java.io.File;
import java.io.IOException;

public class VoiceCaptureActivity
        extends Activity{

    MediaRecorder mMediaRecorder;
    private Context activityContext;
    private Button mRecordButton;
    private Button mStopButton;
    private ProgressBar mAmplitudeProgressBar;
    private TextView mRecordingStatusTextView;
    private TextView mCountDownTimer;
    private int mMaxRecordTime;
    private String audioRecording;
    private Thread mAmplitudeThread;
    private final String title = "tempRecording.mp4";
    private boolean running;
    private static final String LOGTAG = VoiceCaptureActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_record);
        activityContext = this;
        mRecordButton = (Button) findViewById(R.id.btnRecord);
        mStopButton = (Button) findViewById(R.id.btnStop);
        mAmplitudeProgressBar = (ProgressBar) findViewById(R.id.pbAmplitudeProgressBar);
        mRecordingStatusTextView = (TextView) findViewById(R.id.recordingStatus);
        mCountDownTimer = (TextView) findViewById(R.id.countDown);


        audioRecording = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tempRecording.mp4";
        mMaxRecordTime = Options.getOptions(Options.RECORD_MAX_TIME, 30, getApplicationContext()) * 1000;
        mCountDownTimer.setText("" + (mMaxRecordTime / 1000));
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mMediaRecorder.setOutputFile(audioRecording);
        final CountDownTimer mTimerThread = new CountDownTimer(mMaxRecordTime, 1000){
            @Override
            public void onTick(long millisUntilFinished){
                mCountDownTimer.setText("" + (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish(){
                mStopButton.callOnClick();
            }
        };

        mRecordButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                try{
                    mMediaRecorder.prepare();
                    mMediaRecorder.start();
                } catch(IllegalStateException | IOException e){
                    e.printStackTrace();
                }

                mAmplitudeThread = createAmpThread();
                if(mAmplitudeThread.isInterrupted()){
                    mAmplitudeThread.run();
                }else {
                    mAmplitudeThread.start();
                }

                mTimerThread.start();

                mRecordingStatusTextView.setText(R.string.recording_in_progress);
                mRecordingStatusTextView.setTextColor(getResources().getColor(R.color.basic_green));
                mRecordButton.setVisibility(View.GONE);
                mStopButton.setVisibility(View.VISIBLE);

            }
        });

        mStopButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mMediaRecorder.stop();
                mMediaRecorder.reset();
                mMediaRecorder.release();
                mMediaRecorder = null;


                mAmplitudeThread.interrupt();
                mAmplitudeThread = null;
                mTimerThread.cancel();

                mRecordingStatusTextView.setText(getString(R.string.recording_complete));
                mRecordingStatusTextView.setTextColor(getResources().getColor(R.color.basic_blue));

                final AlertDialog mAlertDialog = new AlertDialog.Builder(activityContext).create();
                mAlertDialog.setTitle("Recording Successful");
                mAlertDialog.setMessage("What would you like to do with the recording?");
                mAlertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel Recording", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        if(getParent() == null){
                            setResult(Activity.RESULT_CANCELED);
                        }
                        else{
                            getParent().setResult(Activity.RESULT_CANCELED);
                        }
                        mAlertDialog.dismiss();
                        finish();
                    }
                });
                mAlertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Record New", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        mCountDownTimer.setText("" + (mMaxRecordTime / 1000));
                        resetMediaRecorder();
                        running = false;
                        mAlertDialog.dismiss();
                    }
                });
                mAlertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Save Recording", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        mAlertDialog.dismiss();
                        setResult();
                    }
                });
                mAlertDialog.show();
                mStopButton.setVisibility(View.INVISIBLE);
                mRecordButton.setVisibility(View.VISIBLE);
            }
        });

//        play.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) throws IllegalArgumentException, SecurityException, IllegalStateException{
//                MediaPlayer m = new MediaPlayer();
//
//                try{
//                    m.setDataSource(outputFile);
//                } catch(IOException e){
//                    e.printStackTrace();
//                }
//
//                try{
//                    m.prepare();
//                } catch(IOException e){
//                    e.printStackTrace();
//                }
//
//                m.start();
//                Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_LONG).show();
//            }
//        });
    }

    private void setResult(){
        Intent mIntent = new Intent();
        File audioFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/tempRecording.mp4");
        mIntent.setData(Uri.fromFile(audioFile));
        if(getParent() == null){
            setResult(Activity.RESULT_OK, mIntent);
            finish();
        }
        else{
            getParent().setResult(Activity.RESULT_OK, mIntent);
            finish();
        }
    }

    private void resetMediaRecorder(){
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mMediaRecorder.setOutputFile(audioRecording);
    }

    public Thread createAmpThread(){
        return new Thread(new Runnable(){
            @Override
            public void run(){
                //noinspection InfiniteLoopStatement
                while(true){
                    try{
                        Thread.sleep(250);
                    } catch(InterruptedException e){
                        return;
                    }
                    if(mMediaRecorder != null){
                        final int amplitude = mMediaRecorder.getMaxAmplitude();
                        mAmplitudeProgressBar.post(new Runnable(){
                            @Override
                            public void run(){
                                mAmplitudeProgressBar.setProgress(amplitude);
                            }
                        });
                        if(!running){ return; }
                    }
                }
            }
        });
    }
}

