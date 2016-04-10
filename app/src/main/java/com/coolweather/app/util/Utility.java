package com.coolweather.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.coolweather.app.model.City;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by DT on 2016/4/7.
 */
public class Utility {
    public static synchronized boolean handleProvinceResponse(CoolWeatherDB coolweatherdb, String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] allProvince = response.split(",");
            if (allProvince != null && allProvince.length > 0) {
                for (String p : allProvince) {
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceName(array[1]);
                    province.setProvinceCode(array[0]);
                    coolweatherdb.saveProvince(province);
                }

                return true;
            }
        }
        return false;
    }

    public static synchronized boolean handleCityResponse(CoolWeatherDB coolWeatherDB, String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCity = response.split(",");
            if (allCity != null && allCity.length > 0) {
                for (String c : allCity) {
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    coolWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }
    public static synchronized boolean handleCountyRespose(CoolWeatherDB coolWeatherDB,String response,int cityId)
    {
        if(!TextUtils.isEmpty(response))
        {
            String[] allCounty=response.split(",");
            if(allCounty!=null&&allCounty.length>0)
            {
                for(String p:allCounty)
                {
                    String[] array=p.split("\\|");
                        County county=new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    coolWeatherDB.saveCounty(county);
                }
            }
            return true;
        }
        return false;
    }
    public static void handleWeatherResponse(Context context,String response) {
        try {
            JSONObject jsonObject=new JSONObject(response);

            JSONArray result=jsonObject.getJSONArray("results");
            JSONObject result0=result.getJSONObject(0);
            String cityName=result0.getString("currentCity");
            int pmTwoPointFive;
            if(result0.getString("pm25").isEmpty()){
                pmTwoPointFive = 0;
            }else{
                pmTwoPointFive = result0.getInt("pm25");
            }
            JSONArray weather_data=result0.getJSONArray("weather_data");
            JSONObject jsonObject1=weather_data.getJSONObject(0);
            String data=jsonObject1.getString("date");
            String weather_desp=jsonObject1.getString("weather");
            String temp=jsonObject1.getString("temperature");
            saveWeatherInfo(context,cityName,temp,weather_desp,data,pmTwoPointFive);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }













     /*
        try{

            JSONObject jsonObject=new JSONObject(response);
            JSONObject weatherInfo=jsonObject.getJSONObject("results");
            String cityName=weatherInfo.getString("currentCity");
            String weatherCode=weatherInfo.getString("cityid");
            String temp1=weatherInfo.getString("temp1");
            String temp2=weatherInfo.getString("temp2");
            String weatherDesp=weatherInfo.getString("weather");
            String publishTime=weatherInfo.getString("ptime");
            saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, weatherDesp, publishTime); }
        catch (JSONException e) { e.printStackTrace(); }*/
        public static void saveWeatherInfo(Context context,String cityName, String temp, String weatherDesp, String publishTime,int pm)
        {
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
            SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(context).edit();
            editor.putBoolean("city_selected", true);
            editor.putInt("pm",pm);
            editor.putString("city_name", cityName);
            editor.putString("temp", temp);
            editor.putString("weather_desp", weatherDesp);
            editor.putString("publish_time", publishTime);
            editor.putString("current_date", sdf.format(new Date()));
            editor.commit();
        }

}






