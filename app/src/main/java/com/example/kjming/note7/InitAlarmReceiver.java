package com.example.kjming.note7;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.List;

public class InitAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        NoteDatabase noteDatabase =new NoteDatabase(context.getApplicationContext());
        List<Item> items = noteDatabase.getAll();
        long current = Calendar.getInstance().getTimeInMillis();

        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        for (Item item:items) {
            long alarm = item.getAlarmDatetime();
            if(alarm==0||alarm<=current) {
                continue;
            }
            Intent alarmIntent = new Intent(context,AlarmReceiver.class);
            alarmIntent.putExtra("id",item.getId());
            PendingIntent pi = PendingIntent.getBroadcast(context,(int)item.getId(),
                    alarmIntent,PendingIntent.FLAG_ONE_SHOT);

            am.set(AlarmManager.RTC_WAKEUP,item.getAlarmDatetime(),pi);
            android.util.Log.d("tag", "pendingIntent");
        }


    }
}
