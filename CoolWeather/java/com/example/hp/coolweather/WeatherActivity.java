package com.example.hp.coolweather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hp.coolweather.data.Weather;

import java.io.IOException;

import Util.DataParser;
import Util.HttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    protected DrawerLayout drawerLayout;

    private ImageView iv_bingPic;

    protected SwipeRefreshLayout swipe_refresh;
    private ScrollView sv_weather;

    private String weather_id;

    private TextView tv_location;
    private TextView tv_updateTime;
    private TextView tv_degree;
    private TextView tv_weatherInfo;
    private LinearLayout forecast_layout;
    private TextView tv_aqi;
    private TextView tv_pm25;
    private TextView tv_comfort;
    private TextView tv_washCar;
    private TextView tv_sport;
    private Button btn_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.module_activity_weather);
        drawerLayout = findViewById(R.id.layout_drawer);

        iv_bingPic = findViewById(R.id.iv_bingPic);
        swipe_refresh = findViewById(R.id.swipe_refresh);
        sv_weather = findViewById(R.id.sv_weather);

        swipe_refresh.setColorSchemeResources(R.color.colorPrimary);

        tv_location = findViewById(R.id.tv_location);
        tv_updateTime = findViewById(R.id.tv_updateTime);
        tv_degree = findViewById(R.id.tv_degree);
        tv_weatherInfo = findViewById(R.id.tv_weatherInfo);
        forecast_layout = findViewById(R.id.layout_forecast);
        tv_aqi = findViewById(R.id.tv_aqi);
        tv_pm25 = findViewById(R.id.tv_pm2_5);
        tv_comfort = findViewById(R.id.tv_comfort);
        tv_washCar = findViewById(R.id.tv_washCar);
        tv_sport = findViewById(R.id.tv_sport);
        btn_location = findViewById(R.id.btn_location);

        btn_location.setOnClickListener((view)->drawerLayout.openDrawer(GravityCompat.START));

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        String bingPic = prefs.getString("bingPic",null);
        if(bingPic!=null)
            Glide.with(this).load(bingPic).into(iv_bingPic);
        else
            loadBingPic();
        if (weatherString != null) {//有缓存
            Weather weather = DataParser.parseWeatherData(weatherString);
            weather_id = weather.getBasic().getCid();
            showWeatherInfo(weather);
        } else {//无缓存
            weather_id = getIntent().getStringExtra("weather_id");
            sv_weather.setVisibility(View.INVISIBLE);
            requestWeather(weather_id);
        }
        swipe_refresh.setOnRefreshListener(()->requestWeather(weather_id));
    }

    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Looper.prepare();
                Toast.makeText(WeatherActivity.this,"噫，图片加载出了点问题"
                        ,Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.
                        getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bingPic",responseText);
                editor.apply();
                runOnUiThread(()->Glide.with(WeatherActivity.this)
                        .load(responseText).into(iv_bingPic));
            }
        });
    }

    private void showWeatherInfo(Weather weather) {
        String cityName = weather.getBasic().getCity();
        String updateTime = weather.getBasic().getUpdate().getLoc();
        String degree = weather.getNow().getTmp()+"℃";
        String weatherInfo = weather.getNow().getCond().getTxt();
        tv_location.setText(cityName);
        tv_updateTime.setText(updateTime);
        tv_degree.setText(degree);
        tv_weatherInfo.setText(weatherInfo);
        forecast_layout.removeAllViews();
        for(Weather.DailyForecastBean forecast :weather.getDaily_forecast()){
            View view = LayoutInflater.from(this)
                    .inflate(R.layout.forecast_item, forecast_layout,false);
            TextView tv_date = view.findViewById(R.id.tv_date);
            TextView tv_info = view.findViewById(R.id.tv_info);
            TextView tv_maxText = view.findViewById(R.id.tv_maxText);
            TextView tv_minText = view.findViewById(R.id.tv_minText);
            tv_date.setText(forecast.getDate());
            tv_info.setText(forecast.getCond().getTxt_d());
            tv_maxText.setText(forecast.getTmp().getMax()+"℃");
            tv_minText.setText(forecast.getTmp().getMin()+"℃");
            forecast_layout.addView(view);
        }
        if(weather.getAqi().getCity().getAqi()!=null){
            tv_aqi.setText(weather.getAqi().getCity().getAqi());
            tv_pm25.setText(weather.getAqi().getCity().getPm25());
        }
        String comfort = "舒适度: " + weather.getSuggestion().getComf().getTxt();
        String carWash = "洗车指数: " + weather.getSuggestion().getCw().getTxt();
        String sport = "运动建议: " + weather.getSuggestion().getSport().getTxt();
        tv_comfort.setText(comfort);
        tv_washCar.setText(carWash);
        tv_sport.setText(sport);
        sv_weather.setVisibility(View.VISIBLE);
    }

    protected void requestWeather(String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" +
                weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";
        Log.e("ERROR!",weatherUrl);
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(WeatherActivity.this,
                        "获取天气信息失败", Toast.LENGTH_SHORT).show());
                swipe_refresh.setRefreshing(false);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                Weather weather = DataParser.parseWeatherData(responseText);
                runOnUiThread(() -> {
                    if(weather!=null&&"ok".equals(weather.getStatus())){
                        SharedPreferences.Editor editor = PreferenceManager
                                .getDefaultSharedPreferences(WeatherActivity.this).edit();
                        editor.putString("weather",responseText);
                        weather_id = weather.getBasic().getCid();
                        editor.apply();
                        showWeatherInfo(weather);
                    }else{
                        Log.e("WeatherActivity",weather == null?"weather is null":"weather is not null");
                        Toast.makeText(WeatherActivity.this,
                                "服务器错误,获取天气信息失败", Toast.LENGTH_SHORT).show();
                    }
                    swipe_refresh.setRefreshing(false);
                });
            }
        });
        loadBingPic();
    }
}
