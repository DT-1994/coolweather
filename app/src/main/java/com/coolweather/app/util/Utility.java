package com.coolweather.app.util;

import android.text.TextUtils;

import com.coolweather.app.model.City;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

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
}
