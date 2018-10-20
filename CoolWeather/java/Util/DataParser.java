package Util;

import android.text.TextUtils;
import android.util.Log;

import com.example.hp.coolweather.data.Weather;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import com.example.hp.coolweather.data.City;
import com.example.hp.coolweather.data.County;
import com.example.hp.coolweather.data.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DataParser {

    private static Gson gson = new Gson();

    public static boolean parseProvinceData(String s) {
        if(TextUtils.isEmpty(s))
            return false;
        s = s.replaceAll("id","provinceCode");
        s = s.replaceAll("name","provinceName");
        List<Province> list = gson.fromJson(s, new TypeToken<List<Province>>() {
        }.getType());
        for (Province province : list) {
            province.save();
        }
        return true;
    }

    public static boolean parseCityData(String s, int provinceId) {
        if(TextUtils.isEmpty(s))
            return false;
        s = s.replaceAll("id","cityCode");
        s = s.replaceAll("name","cityName");
        List<City> list = gson.fromJson(s, new TypeToken<List<City>>() {
        }.getType());
        for (City city : list) {
            city.setProvinceId(provinceId);
            city.save();
        }
        return true;
    }

    public static boolean parseCountyData(String s, int cityId) {
        if(TextUtils.isEmpty(s))
            return false;
        s = s.replaceAll("id","countyCode");
        s = s.replaceAll("name","countyName");
        List<County> list = gson.fromJson(s, new TypeToken<List<County>>() {
        }.getType());
        for (County county : list) {
            county.setCityId(cityId);
            county.save();
        }
        return true;
    }

    public static Weather parseWeatherData(String s){
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weather = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weather,Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
