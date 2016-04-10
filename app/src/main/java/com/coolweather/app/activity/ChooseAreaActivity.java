package com.coolweather.app.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.app.R;
import com.coolweather.app.model.City;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class ChooseAreaActivity extends AppCompatActivity {
public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY=1;
    public static final int LEVEL_COUNTY=2;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private CoolWeatherDB coolWeatherDB;
    private ArrayAdapter<String> adapter;
    private List<String> dataList=new ArrayList<String>();
    private List<Province> provinceList;
    private List<City>cityList;
    private List<County>countyList;
    private Province selectedProvince;
    private City selectedCity;
    private int currentLevel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getBoolean("city_selected",false))
        {
            Intent intent=new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.choose_area);
        listView = (ListView) findViewById(R.id.list_view);
        titleText = (TextView) findViewById(R.id.title_text);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        coolWeatherDB = CoolWeatherDB.getInstance(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    querycities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                }
                else if(currentLevel==LEVEL_COUNTY)
                {

                    String countyName=countyList.get(position).getCountyName();/////////////////////////
                    Intent intent=new Intent(ChooseAreaActivity.this,WeatherActivity.class);
                    intent.putExtra("county_name",countyName);/////////////////////////////

                    startActivity(intent);
                    finish();
                }

            }
        });

        queryProvinces();
    }
private void queryProvinces()
{
    provinceList=coolWeatherDB.loadProvince();
    if(provinceList.size()>0)
    {
        dataList.clear();
        for(Province province:provinceList)
        {
            dataList.add(province.getProvinceName());
        }
        adapter.notifyDataSetChanged();
        titleText.setText("中国");
        currentLevel=LEVEL_PROVINCE;
    }
    else{
        queryFromServer(null,"province");
    }
}
    public void querycities() {
        cityList = coolWeatherDB.loadCities(selectedProvince.getId());
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        } else {
            queryFromServer(selectedProvince.getProvinceCode(), "city");
        }
    }
    public void queryCounties()
    {
        countyList=coolWeatherDB.loadCounties(selectedCity.getId());
        if(countyList.size()>0)
        {
            dataList.clear();
            for(County county:countyList)
            {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel=LEVEL_COUNTY;
        }
        else{
            queryFromServer(selectedCity.getCityCode(),"county");
        }
    }
    private void queryFromServer(final String code,final String type)
    {
        String address;
        if(!TextUtils.isEmpty(code))
        {
            address= "http://www.weather.com.cn/data/list3/city" + code + ".xml";

        }
        else { address = "http://www.weather.com.cn/data/list3/city.xml"; }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override

            public void onFinish(String response) {
                boolean result=false;
                if("province".equals(type)){
                    result= Utility.handleProvinceResponse(coolWeatherDB,response);
                }
                else if("city".equals(type))
                {
                    result=Utility.handleCityResponse(coolWeatherDB,response,selectedProvince.getId());
                }
                else if("county".equals(type))
                {
                    result=Utility.handleCountyRespose(coolWeatherDB,response,selectedCity.getId());
                }
                if(result)
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type))
                                queryProvinces();
                            else if("city".equals(type))
                                querycities();
                            else if("county".equals(type))
                                queryCounties();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }
    public void showProgressDialog()
    {
        if(progressDialog==null)
        {
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("正在加载.....");
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }

    public void closeProgressDialog()
    {
        if(progressDialog!=null)
            progressDialog.dismiss();
    }
    @Override
    public void onBackPressed()
    {
        if(currentLevel==LEVEL_COUNTY)
            querycities();
        else if(currentLevel==LEVEL_CITY)
            queryProvinces();
        else finish();


    }


    }

