package com.coolweather.app.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coolweather.app.R;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import org.w3c.dom.Text;

import java.net.URLEncoder;

public class WeatherActivity extends AppCompatActivity implements Button.OnClickListener{
    private LinearLayout weatherInfoLayout;
    private TextView cityNameText;
    private TextView publishText;
    private TextView weatherDespText;
    private TextView tempText;
    private TextView currentDateText;
    private Button switchCity;
    private Button refreshWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);
        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        cityNameText = (TextView) findViewById(R.id.city_name);
        publishText = (TextView) findViewById(R.id.publish_text);
        weatherDespText = (TextView) findViewById(R.id.weather_desp);
        tempText = (TextView) findViewById(R.id.temp);
        currentDateText = (TextView) findViewById(R.id.current_date);
        switchCity = (Button) findViewById(R.id.switch_city);
        refreshWeather = (Button) findViewById(R.id.refresh_weather);
        String countyName=getIntent().getStringExtra("county_name");
        if(!TextUtils.isEmpty(countyName))
        {
            publishText.setText("同步中......");
            weatherInfoLayout.setVisibility(View.VISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryweatherCode(countyName);
        }
        else
        {
            showWeather(countyName);
        }
        switchCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);

    }
    @Override
    public void onClick(View v)
    {/*
        switch(v.getId())
        {
            case R.id.switch_city:
                Intent intent=new Intent(this,ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh_weather:
                publishText.setText("同步中....");
                SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
                String weatherCode=prefs.getString("weather_code","");
                if(!TextUtils.isEmpty(weatherCode))
                {
                    queryWeatherInfo(weatherCode);
                }
                break;
            default:break;
        }
        */
    }
    private void queryweatherCode(String  countyName)
    {String address="";
        try {
         address = "http://api.map.baidu.com/telematics/v3/weather?location=" + URLEncoder.encode(countyName, "utf-8") + "&output=json&ak=GArYjKexiZ3mDDpmvDjyb4Sf";
    }
    catch(Exception e)
    {
        e.printStackTrace();
    }
        queryFromServer(address,countyName);
    }
   
    private void queryFromServer(final String address,final String  name)
    {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                    Utility.handleWeatherResponse(WeatherActivity.this, response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather(name);
                        }
                    });

            }


            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败...");
                    }
                });

            }
        });
    }
    private void showWeather(String name)
    {
        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(prefs.getString("city_name", ""));
        tempText.setText(prefs.getString("temp", ""));
        weatherDespText.setText(prefs.getString("weather_desp", ""));
        publishText.setText(prefs.getString("publish_time", ""));
        currentDateText.setText("pm2.5: "+prefs.getInt("pm",0));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
    }
}
