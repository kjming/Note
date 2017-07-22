package com.example.kjming.note7;

/**
 * Created by Kjming on 4/19/2017.
 */
import android.graphics.Color;
public enum Colors {

    LIGHTGREY("#D3D3D3"), BLUE("#33B5E5"), PURPLE("#AA66CC"),GREEN("#99CC00"), ORANGE("#FFBB33"), RED("#FF4444");

    private String mCode;

    private Colors(String code) {
        mCode = code;
    }

    public String getCode() {
        return mCode;
    }

    public int parseColor() {
        return Color.parseColor(mCode);
    }

}
