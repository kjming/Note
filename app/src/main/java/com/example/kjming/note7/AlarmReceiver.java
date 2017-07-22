package com.example.kjming.note7;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.io.File;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        long id = intent.getLongExtra("id",0);
        if(id!=0) {
            sendNotify(context,id);
            android.util.Log.d("Stupid", "sendNotify");
        }
        android.util.Log.d("Stupid", "description");
    }

    public void sendNotify(Context context,long id) {
        NoteDatabase itemData =new NoteDatabase(context.getApplicationContext());
        Item item = itemData.get(id);
        File file = new File(FileUtil.getExternalStorageDir(FileUtil.APP_DIR),"p"+item.getFileName()+".jpg");
        boolean isPicture = (item.getFileName()!=null&&item.getFileName().length()>0&&file.exists());
        NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(isPicture) {
            Notification.Builder builder =new Notification.Builder(context);
            builder.setSmallIcon(android.R.drawable.star_on)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle(context.getString(R.string.app_name));
            Notification.BigPictureStyle bigPictureStyle = new Notification.BigPictureStyle();
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            bigPictureStyle.bigPicture(bitmap).setSummaryText(item.getTitle());
            builder.setStyle(bigPictureStyle);
            nm.notify((int)item.getId(),builder.build());
            android.util.Log.d("tag", "picture");
        }else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setSmallIcon(android.R.drawable.star_big_on)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentTitle(item.getTitle());
            nm.notify((int)item.getId(),builder.build());
            android.util.Log.d("tag", "noPicture");
        }
    }
}
