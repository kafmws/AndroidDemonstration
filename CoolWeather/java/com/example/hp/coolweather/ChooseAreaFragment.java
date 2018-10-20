package com.example.hp.coolweather;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.hp.coolweather.data.City;
import com.example.hp.coolweather.data.County;
import com.example.hp.coolweather.data.Province;

import Util.DataParser;
import Util.HttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {
    private static final int LEVEL_PROVINCE = 0;
    private static final int LEVEL_CITY = 1;
    private static final int LEVEL_COUNTY = 2;

    private ProgressBar progressBar;
    private TextView tv_title;
    private Button btn_back;
    private RecyclerView rv_location;
    private RecyclerView.Adapter adapter;
    private List<Location> dataList = new ArrayList<>();
    private List<Province> provinces;//省列表
    private List<City> cities;//市列表
    private List<County> counties;//县列表
    private Province selectedProvince;
    private City selectedCity;
    private County selectedCounty;
    private int currentLevel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        tv_title = view.findViewById(R.id.tv_title);
        btn_back = view.findViewById(R.id.btn_back);
        rv_location = view.findViewById(R.id.rv_location);
        adapter = new LocationAdapter(dataList);
        //queryProvinces();
        rv_location.setAdapter(adapter);
        rv_location.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        rv_location.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        rv_location.addOnItemTouchListener(new SimpleRecyclerViewItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinces.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cities.get(position);
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {
                    String weatherId = counties.get(position).getWeather_id();
                    if (getActivity() instanceof MainActivity) {
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id", weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    }else if(getActivity() instanceof WeatherActivity){
                        WeatherActivity activity = (WeatherActivity) getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.swipe_refresh.setRefreshing(true);
                        activity.requestWeather(weatherId);
                    }
                }
            }
        }));
        btn_back.setOnClickListener(v -> {
            if (currentLevel == LEVEL_COUNTY) {
                queryCities();
            } else if (currentLevel == LEVEL_CITY) {
                queryProvinces();
            }
        });
        queryProvinces();
    }

    //查询省   先本地再服务器
    private void queryProvinces() {
        tv_title.setText("中国");
        btn_back.setVisibility(View.GONE);
        provinces = LitePal.findAll(Province.class);
        if (provinces.size() > 0) {
            dataList.clear();
            for (Province province : provinces)
                dataList.add(new Location(province.getProvinceName()));
            adapter.notifyDataSetChanged();
            rv_location.smoothScrollToPosition(0);
            currentLevel = LEVEL_PROVINCE;
        } else {//从网络请求数据
            queryFromInternet("http:/guolin.tech/api/china/", "province");
        }
//        for (Location location : dataList){
//            Log.d("Exception:::",location.getLocation() == null?"null":"不是null");
//        }
//        if(dataList.isEmpty()){
//            Log.d("Exception:::","空的");
//        }
    }

    //查询市   先本地再服务器
    private void queryCities() {
        tv_title.setText(selectedProvince.getProvinceName());
        btn_back.setVisibility(View.VISIBLE);
        cities = LitePal.where("provinceid = ?", String.valueOf(selectedProvince.getId())).find(City.class);
        if (cities.size() > 0) {
            dataList.clear();
            for (City city : cities)
                dataList.add(new Location(city.getCityName()));
            adapter.notifyDataSetChanged();
            rv_location.smoothScrollToPosition(0);
            currentLevel = LEVEL_CITY;
        } else {//从网络请求数据
            String provinceCode = selectedProvince.getProvinceCode();
            queryFromInternet("http:/guolin.tech/api/china/" + provinceCode, "city");
        }
    }

    //查询县   先本地再服务器
    private void queryCounties() {
        tv_title.setText(selectedCity.getCityName());
        btn_back.setVisibility(View.VISIBLE);
        counties = LitePal.where("cityid = ?", String.valueOf(selectedCity.getId())).find(County.class);
        if (counties.size() > 0) {
            dataList.clear();
            for (County county : counties)
                dataList.add(new Location(county.getCountyName()));
            adapter.notifyDataSetChanged();
            rv_location.smoothScrollToPosition(0);
            currentLevel = LEVEL_COUNTY;
        } else {//从网络请求数据
            String provinceCode = selectedProvince.getProvinceCode();
            String cityCode = selectedCity.getCityCode();
            queryFromInternet("http:/guolin.tech/api/china/" +
                    provinceCode + "/" + cityCode, "county");
        }
    }

    //从服务器查询省市县数据
    private void queryFromInternet(String address, final String type) {
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), type + "数据加载失败", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                switch (type) {
                    case "province":
                        result = DataParser.parseProvinceData(responseText);
                        break;
                    case "city":
                        result = DataParser.parseCityData(responseText, selectedProvince.getId());
                        break;
                    case "county":
                        result = DataParser.parseCountyData(responseText, selectedCity.getId());
                        break;
                }
                if (result) {
                    getActivity().runOnUiThread(() -> {
                        switch (type) {
                            case "province":
                                queryProvinces();
                                break;
                            case "city":
                                queryCities();
                                break;
                            case "county":
                                queryCounties();
                                break;
                        }
                    });
                }
            }
        });
    }

    //显示进度条
    private void showProgressBar() {
        if (progressBar == null) {
            progressBar = new ProgressBar(getActivity());
        }
    }

    //关闭进度条
    private void closeProgressBar() {
        if (progressBar != null) {

        }
    }

}


class SimpleRecyclerViewItemClickListener extends
        RecyclerView.SimpleOnItemTouchListener {
    private OnItemClickListener mListener;
    private GestureDetectorCompat mGestureDetector;

    public SimpleRecyclerViewItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        if (mGestureDetector == null) {
            initGestureDetector(rv);
        }
        if (mGestureDetector.onTouchEvent(e)) { // 把事件交给GestureDetector处理
            return true;
        } else {
            return false;
        }
    }

    /**
     * 初始化GestureDetector
     */
    private void initGestureDetector(final RecyclerView recyclerView) {
        mGestureDetector = new GestureDetectorCompat(recyclerView.getContext(), new GestureDetector.SimpleOnGestureListener() { // 这里选择SimpleOnGestureListener实现类，可以根据需要选择重写的方法

            /**
             * 单击事件
             */
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (childView != null && mListener != null) {
                    mListener.onItemClick(childView, recyclerView.getChildLayoutPosition(childView));
                    return true;
                }
                return false;
            }
        });
    }
}