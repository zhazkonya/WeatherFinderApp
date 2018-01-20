package com.example.zoskenbaeva.weathersearching;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import java.util.List;

/**
 * Created by z.oskenbaeva on 19.01.2018.
 */

public class WeatherInfoAdapter extends RecyclerView.Adapter<WeatherInfoAdapter.WeatherInfoViewHolder> {
    List<WeatherInfo> regionsList;
    public WeatherInfoAdapter(List<WeatherInfo> regions) {
        regionsList = regions;
    }

    @Override
    public WeatherInfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_info,
                parent, false);
        WeatherInfoViewHolder wivh = new WeatherInfoViewHolder(v);
        return wivh;
    }

    @Override
    public void onBindViewHolder(WeatherInfoViewHolder holder, int position) {
        holder.tvRegionName.setText(regionsList.get(position).getRegionName());
        holder.tvDescription.setText(regionsList.get(position).getDescription());
        Ion.with(holder.itemView.getContext())
                .load(OWMProperties.OWM_ICON_URL
                        +regionsList.get(position).getIconId()+".png")
                .withBitmap()
                .intoImageView(holder.ivIcon);

    }

    @Override
    public int getItemCount() {
        return regionsList.size();
    }

    public static class WeatherInfoViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        CardView dvRegion;
        TextView tvRegionName;
        TextView tvDescription;
        ImageView ivIcon;

        public WeatherInfoViewHolder(View v) {
            super(v);
            dvRegion = (CardView)v.findViewById(R.id.cv_region);
            tvRegionName = (TextView) v.findViewById(R.id.region_name);
            tvDescription = (TextView) v.findViewById(R.id.description);
            ivIcon = (ImageView) v.findViewById(R.id.weather_icon);
        }
    }

}
