package com.example.zoskenbaeva.weathersearching;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import java.util.List;

/**
 * Created by zhaziraoskenbayeva on 21/01/18.
 */

public class PlacesDiffCallback extends DiffUtil.Callback {

    List<WeatherInfo> oldList, newList;

    public PlacesDiffCallback(List<WeatherInfo> oldList, List<WeatherInfo> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList == null ? 0 : oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList == null ? 0 : newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getRegionName()
                .equals(newList.get(newItemPosition).getRegionName());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getDescription()
                .equals(newList.get(newItemPosition).getDescription());
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        // Implement method if you're going to use ItemAnimator
        return super.getChangePayload(oldItemPosition, newItemPosition);

    }
}
