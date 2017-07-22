package com.example.kjming.note7;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2017/5/6.
 */

public class FileUtil {
    public static final String APP_DIR = "androidtutorial";

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static File getPublicAlbumStorageDir(String albumName) {
        File pictures = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File file = new File(pictures, albumName);
        if (!file.mkdir()) {
            Log.e("getAlbumStorageDir", "Directory not creates");
        }
        return file;
    }

    public static File getAlbumStorageDir(Context context, String albumName) {
        File pictures = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File file = new File(pictures, albumName);
        if (!file.mkdir()) {
            Log.e("getAlbumStorageDir", "Directory not created");
        }
        return file;
    }

    public static File getExternalStorageDir(String dir) {
        File result = new File(Environment.getExternalStorageDirectory(), dir);
        if (!isExternalStorageWritable()) {
            return null;
        }
        if (!result.exists() && !result.mkdir()) {
            return null;
        }
        return result;
    }

    public static void fileToImageView(String fileName, ImageView imageView) {
        if (new File(fileName).exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(fileName);
            imageView.setImageBitmap(bitmap);
        } else {
            Log.e("fileToImageView", fileName + "not found");
        }
    }

    public static String getUniqueFileName() {
    SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
    return date.format(new Date());
    }
}
