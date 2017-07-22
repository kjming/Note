package com.example.kjming.note7;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

/**
 * Created by Kjming on 5/30/2017.
 */

public class PictureActivity extends Activity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_picture);

        ImageView pictureView = (ImageView)findViewById(R.id.pictureView);

        Intent intent = getIntent();
        String pictureName = intent.getStringExtra("pictureName");
        if(pictureName!=null) {
            FileUtil.fileToImageView(pictureName,pictureView);
        }
    }

    public void clickPicture(View view) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        }else {
            finish();
        }
    }

}
