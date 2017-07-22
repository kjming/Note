package com.example.kjming.note7;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;


public class ColorActivity extends AppCompatActivity {
    private LinearLayout mColorGallery;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color);

        processViews();

        ColorListener listener = new ColorListener();

        for (Colors c :Colors.values()) {//Colors c :Colors.values()
            Button button = new Button(this);
            button.setId(c.parseColor());
            LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(128,128);
            layout.setMargins(6,6,6,6);
            button.setLayoutParams(layout);
            button.setBackgroundColor(c.parseColor());
            button.setOnClickListener(listener);
            mColorGallery.addView(button);
        }


    }

    private void processViews () {
        mColorGallery = (LinearLayout) findViewById(R.id.color_gallery);
    }

    private class ColorListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            String action=ColorActivity.this.getIntent().getAction();
            if (action != null && action.equals("com.example.kjming.note7.CHOOSE_COLOR")) {
                SharedPreferences.Editor editor =
                        PreferenceManager.getDefaultSharedPreferences(ColorActivity.this).edit();
                editor.putInt("DEFAULT_COLOR",view.getId());
                editor.apply();
                finish();
            }else {
                Intent result = getIntent();
                result.putExtra("colorId", view.getId());
                setResult(Activity.RESULT_OK, result);
                finish();
            }
        }
    }


}
