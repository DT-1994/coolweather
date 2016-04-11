package com.coolweather.app.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.coolweather.app.receiver.AutoUpdateReceiver;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import java.net.URLEncoder;

/**
 * Created by DT on 2016/4/11.
 */
public class AutoUpdateService extends Service {
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent,int flags,int startId)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();
        AlarmManager manager=(AlarmManager)getSystemService(ALARM_SERVICE);
        int eHour=8*60*60*1000;
        long triggerAtTime= SystemClock.elapsedRealtime()+eHour;
        Intent i=new Intent(this,AutoUpdateReceiver.class);
        PendingIntent pi=PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent,flags,startId);


    }
    private void updateWeather()
    {
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String countyName=prefs.getString("city_name", "");
        String address="";
        try {
            address = "http://api.map.baidu.com/telematics/v3/weather?location=" + URLEncoder.encode(countyName, "utf-8") + "&output=json&ak=GArYjKexiZ3mDDpmvDjyb4Sf";
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(AutoUpdateService.this,response);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();

            }
        });
    }
}
