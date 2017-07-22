package com.example.kjming.note7;

/**
 * Created by Kjming on 4/19/2017.
 */
import android.graphics.Color;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;
public class Item implements Serializable{
    private long mId;
    private long mDatetime;
    private Colors mColor;
    private String mTitle;
    private String mContent;
    private String mFileName;
    private String mRecFileName;
    private double mLatitude;
    private double mLongitude;
    private long mLastModify;
    private boolean mSelected;
    private long mAlarmDatetime;

    public Item() {
        mTitle = "";
        mContent = "";
        mColor = Colors.LIGHTGREY;
    }

    public Item(long id, long datetime, Colors color,String title,String content,String fileName,
                String recFileName,double latitude,double longitude,long lastModify) {

        mId = id;
        mDatetime = datetime;
        mColor = color;
        mTitle = title;
        mContent = content;
        mFileName = fileName;
        mRecFileName = recFileName;
        mLatitude = latitude;
        mLongitude = longitude;
        mLastModify = lastModify;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public long getDatetime() {
        return mDatetime;
    }

    public String getLocaleDatetime() {
        return String.format(Locale.getDefault(), "%tF  %<tR", new Date(mDatetime));
    }

    public String getLocaleDate() {
        return String.format(Locale.getDefault(), "%tF", new Date(mDatetime));
    }

    public String getLocaleTime() {
        return String.format(Locale.getDefault(), "%tR", new Date(mDatetime));
    }

    public void setDatetime(long datetime) {
        mDatetime = datetime;
    }

    public Colors getColor() {
        return mColor;
    }

    public void setColor(Colors color) {
        mColor = color;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getFileName() {
        return mFileName;
    }

    public void setFileName(String fileName) {
        mFileName = fileName;
    }

    public String getRecFileName() {
        return mRecFileName;
    }

    public void setRecFileName(String recFileName) {
        mRecFileName = recFileName;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public long getLastModify() {
        return mLastModify;
    }

    public void setLastModify(long lastModify) {
        mLastModify = lastModify;
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setSelected(boolean selected) {
        mSelected = selected;
    }

    public long getAlarmDatetime() {
        return mAlarmDatetime;
    }

    public void setAlarmDatetime (long alarmDatetime) {
        mAlarmDatetime = alarmDatetime;
    }





}
