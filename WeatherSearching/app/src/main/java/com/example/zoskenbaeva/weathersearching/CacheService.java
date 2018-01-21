package com.example.zoskenbaeva.weathersearching;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.IBinder;

public class CacheService extends Service {
    SQLiteDatabase db;
    DHCache dbHelper = new DHCache(this);

    public CacheService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        deleteCache();
        stopSelf();
        return START_NOT_STICKY;
    }

    private void deleteCache() {
        db = dbHelper.getWritableDatabase();
        if(db != null){
            try{
                String deleteQuery = "DELETE FROM "+
                        DHCache.TB_NAME +
                        " WHERE w_time <= DATETIME('NOW', '-1 hours')";
                db.execSQL(deleteQuery);
            }catch(SQLiteException e){}
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm.set(
                alarm.RTC_WAKEUP,
                System.currentTimeMillis() + (1000 * 60 * 60), // One hour from now
                PendingIntent.getService(
                        this, 0,
                        new Intent(this, CacheService.class), 0)
        );
    }
}
