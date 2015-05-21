package com.gui.royal.coolweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.gui.royal.coolweather.db.CoolWeatherDB;
import com.gui.royal.coolweather.model.City;
import com.gui.royal.coolweather.model.County;
import com.gui.royal.coolweather.model.Province;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 解析服务器返回的省市县的数据
 * Created by Jeremy on 2015/5/18.
 */
public class Utility {

    /**
     * 解析和处理返回的省级数据
     *
     * 解析JSON数据
     */
    public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB, String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] allProvinces = response.split(",");
            if (allProvinces != null && allProvinces.length > 0) {
                for (String p : allProvinces) {
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    //将解析出来的数据存储到Province表中
                    coolWeatherDB.saveProvince(province);
                }
                return  true;
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     */
    public static boolean handleCitiesResponse (CoolWeatherDB coolWeatherDB, String respose, int provinceId) {
        if (!TextUtils.isEmpty(respose)) {
            String[] allCities = respose.split(",");
            if (allCities != null && allCities.length > 0) {
                for (String c : allCities) {
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    //将解析出来的数据存储到City表中
                    coolWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的县级数据
     */
    public static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB, String respose, int cityId) {
        if (!TextUtils.isEmpty(respose)) {
            String[] allCounties = respose.split(",");
            if (allCounties != null && allCounties.length > 0) {
                for (String c : allCounties) {
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    //将解析出来的数据存储到County表中
                    coolWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析JSON数据，并将解析出的数据存储到本地
     * {"weatherinfo":
     *      {"city":"昆山","cityid":"101190404","temp1":"21.C","temp2":"9.C","weather":"多云转小雨","img1":"d1.gif","img2":"n7.gif","ptime":"11:00"}
     * }
     */
    public static void handleWeatherResponse(Context context, String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
            String cityName = weatherInfo.getString("city");//"city":"昆山"
            String weatherCode = weatherInfo.getString("cityid");//"cityid":"101190404"
            String temp1 = weatherInfo.getString("temp1");//"temp1":"21.C"
            String temp2 = weatherInfo.getString("temp2");//"temp2":"9.C"
            String weatherDesp = weatherInfo.getString("weather");//"weather":"多云转小雨"
            String publishTime = weatherInfo.getString("ptime");//"ptime":"11:00"
            saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, weatherDesp, publishTime);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * 将解析出的天气信息存储到SharePreference文件中
     * @param context 上下文
     * @param cityName 城市名
     * @param weatherCode 城市ID
     * @param temp1 高温
     * @param temp2 低温
     * @param weatherDesp 天气描述
     * @param publishTime 更新时间
     */
    private static void saveWeatherInfo(Context context, String cityName, String weatherCode, String temp1, String temp2, String weatherDesp, String publishTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA); //设置日期格式
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();//获取可操作的实例
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", cityName);
        editor.putString("weather_code", weatherCode);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("weather_desp", weatherDesp);
        editor.putString("publish_time", publishTime);
        editor.putString("current_date", sdf.format(new Date()));
        editor.apply();
    }

}
