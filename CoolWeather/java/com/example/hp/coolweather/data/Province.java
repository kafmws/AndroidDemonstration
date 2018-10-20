package com.example.hp.coolweather.data;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.LitePalSupport;

public class Province extends LitePalSupport {
    private int id;

    //@SerializedName("id")
    private String provinceCode;

    //@SerializedName("name")
    private String provinceName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }
}
