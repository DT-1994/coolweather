package com.coolweather.app.model;

/**
 * Created by DT on 2016/4/7.
 */
public class County {
    private int id;
    private String countyName;
    private String countyCode;

    int cityId;
    public int getId(){return id;}
    public String getCountyName(){return countyName;}
    public String getCountyCode(){return countyCode;}

    int getCityId(){return cityId;}
    public void setId(int id){this.id=id;}
    public void setCountyName(String countyName){this.countyName=countyName;}
    public void setCountyCode(String countyCode){this.countyCode=countyCode;}

    public void setCityId(int cityId){this.cityId=cityId;}
}
