package com.example.hp.coolweather;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

class Location{

    private String location;

    public Location(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {
    private List<Location> locations;

    public LocationAdapter(List<Location> locations) {
        this.locations = locations;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_cityName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_cityName = itemView.findViewById(R.id.tv_cityName);//为对象赋值
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        ViewHolder holder;
        View view;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        view = inflater.inflate(R.layout.location_item,viewGroup,false);
        holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.tv_cityName.setText(locations.get(position).getLocation());
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

}
