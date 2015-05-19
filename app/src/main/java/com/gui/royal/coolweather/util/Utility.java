package com.gui.royal.coolweather.util;

import android.text.TextUtils;

import com.gui.royal.coolweather.db.CoolWeatherDB;
import com.gui.royal.coolweather.model.City;
import com.gui.royal.coolweather.model.County;
import com.gui.royal.coolweather.model.Province;

/**
 * 解析服务器返回的省市县的数据
 * Created by Jeremy on 2015/5/18.
 */
public class Utility {

    /**
     * 解析和处理返回的省级数据
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
}
