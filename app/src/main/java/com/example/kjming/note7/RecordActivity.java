package com.example.kjming.note7;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import java.io.IOException;

/**
 * Created by Kjming on 5/8/2017.
 */

public class RecordActivity extends Activity{
    private ImageButton mRecordbutton;
    private boolean mIsRecording = false;
    private ProgressBar mRecordVolumn;
    private MyRecoder mMyrecoder;
    private String mFilename;
    private static int mFrequency = 4410;
    private static int mCodingRate = AudioFormat.ENCODING_PCM_16BIT;
    private static int mChannelConfig = AudioFormat.CHANNEL_IN_MONO;
    private static int mRecBufSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        processViews();
        Intent intent = getIntent();
        mFilename = intent.getStringExtra("mFilename");
    }

    public void onSubmit(View view) {
        if (mIsRecording) {
            mMyrecoder.stop();
        }

        if(view.getId() == R.id.record_ok) {
            Intent result = getIntent();
            setResult(Activity.RESULT_OK,result);
        }
        finish();
    }

    private void processViews() {
        mRecordbutton = (ImageButton)findViewById(R.id.record_button);
        mRecordVolumn = (ProgressBar)findViewById(R.id.record_volume);
        setProgressBarIndeterminate(false);

        mRecordbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickRecord(v);
            }
        });
    }

    public void clickRecord(View view) {
        mIsRecording = !mIsRecording;
        if(mIsRecording) {
            mRecordbutton.setImageResource(R.drawable.record_red_icon);
            mMyrecoder = new MyRecoder(mFilename);
            mMyrecoder.start();
            new MicLevelTask().execute();
        }else {
            mRecordbutton.setImageResource(R.drawable.record_dark_icon);
            mRecordVolumn.setProgress(0);
            mMyrecoder.stop();
        }
    }

    private class MicLevelTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... args) {
            while (mIsRecording) {
                publishProgress();
                try {
                    Thread.sleep(200);
                }catch (InterruptedException e) {
                    Log.d("RecordActivity",e.toString());
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            mRecordVolumn.setProgress((int)mMyrecoder.getAmplitudeEMA());
        }
    }

    private class MyRecoder {
        private static final double EMA_FILTER = 0.6;
        private MediaRecorder recorder = null;
        private double mEMA = 0.0;
        private String output;

        MyRecoder(String output) {
            this.output = output;
        }

        public void start(){
            if(recorder ==null) {
                recorder = new MediaRecorder();
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                recorder.setOutputFile(output);
            }
            try {
                recorder.prepare();
            }catch (IOException e) {
                Log.d("Phillip", output + " ><>< " + e.toString());
            }

            recorder.start();
            mEMA =0.0;
        }

        public void stop() {
            if(recorder!=null) {
                recorder.stop();
                recorder.release();
                recorder = null;
            }
        }

        public double getAmplitude() {
            if(recorder != null) {
                return (recorder.getMaxAmplitude()/2700.0);
            }else {
                return 0;
            }
        }

        public double getAmplitudeEMA() {
            double amp = getAmplitude();
            mEMA = EMA_FILTER*amp + (1.0-EMA_FILTER)*mEMA;
            return mEMA;
        }
    }
}
