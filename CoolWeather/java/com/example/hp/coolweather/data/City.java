package com.example.hp.coolweather.data;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.LitePalSupport;

public class City extends LitePalSupport {
    private int id;
    private int provinceId;
    //@SerializedName("id")
    private String cityCode;
    //@SerializedName("name")
    private String cityName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

}
