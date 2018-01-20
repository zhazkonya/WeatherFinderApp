package com.example.zoskenbaeva.weathersearching;

import java.util.List;

/**
 * Created by z.oskenbaeva on 19.01.2018.
 */

public class WeatherInfo {
    String regionName;
    String description;
    String iconId;

    WeatherInfo(){
        regionName = "";
        description = "";
        iconId="";
    }
    WeatherInfo(String rName, String d, String i_ic){
        regionName = rName;
        description = d;
        iconId=i_ic;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconId() {
        return iconId;
    }

    public void setIconId(String iconId) {
        this.iconId = iconId;
    }


}
