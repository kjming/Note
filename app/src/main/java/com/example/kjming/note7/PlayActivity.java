package com.example.kjming.note7;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

/**
 * Created by Kjming on 5/9/2017.
 */

public class PlayActivity extends Activity {
    private MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        Intent intent = getIntent();
        String fileName = intent.getStringExtra("fileName");
        Uri uri = Uri.parse(fileName);
        mMediaPlayer = MediaPlayer.create(this,uri);
    }

    @Override
    protected void onStop() {
        if(mMediaPlayer.isPlaying()){
            mMediaPlayer.stop();
        }
        mMediaPlayer.release();
        super.onStop();
    }

    public void onSubmit(View view) {
        finish();
    }

    public void clickPlay(View view) {
        mMediaPlayer.start();
    }

    public void clickPause(View view) {
        mMediaPlayer.pause();
    }

    public void clickStop(View view) {
        if(mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        mMediaPlayer.seekTo(0);
    }


}
