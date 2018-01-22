package com.example.zoskenbaeva.weathersearching;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by z.oskenbaeva on 19.01.2018.
 */

public class OWMUtils {
    public final static  String OWM_ICON_URL ="http://openweathermap.org/img/w/";//10d.png
    public final static  String OWM_API_KEY
            ="appid=";
    public final static  String OWM_API_BASE ="http://api.openweathermap.org/data/2.5/weather?";

    //&lat=52.3507849&lon=5.2647016
    public final static  String OWM_PARAMS_LAT ="lat=";
    public final static  String OWM_PARAMS_LNG ="lon=";
    List<WeatherInfo> wiList = new ArrayList<>();


    Context c = null;
    String curtoken = "";
    public OWMUtils(Context context, String token) {
        c = context;
        curtoken = token;
    }

    public List<WeatherInfo> requestToOWM(Map<String, String> strings) {
        for(Map.Entry<String, String> entry : strings.entrySet()){
            String[] parts = entry.getKey().split(" ");
            String stream = getWeatherFromOWM(parts[0].trim(), parts[1].trim());
            processOWMResult(stream, entry.getValue());
        }

        return wiList;
    }

    private String getWeatherFromOWM(String lat, String lng) {
        String request = OWM_API_BASE+OWM_API_KEY+c.getString(R.string.owmKey)
                + "&"+OWM_PARAMS_LAT + lat
                + "&"+OWM_PARAMS_LNG + lng;
        HTTPDataHandler hh = new HTTPDataHandler();
        String stream = hh.GetHTTPData(request);
        return stream;
    }


    private void processOWMResult(String result, String c_name) {
        WeatherInfo wi = parseOWMJson(result, c_name);
        wiList.add(wi);
    }

    private WeatherInfo parseOWMJson(String result, String c_name) {
        WeatherInfo wi = new WeatherInfo();
        try {
            JSONObject obj = new JSONObject(result);
            JSONObject main = obj.getJSONObject("main");
            DecimalFormat df = new DecimalFormat("#.#");
            double temp = Double.valueOf(df.format(main.getDouble("temp") - 273));
            int pressure = main.getInt("pressure");
            int humidity = main.getInt("humidity");
            JSONObject wind = obj.getJSONObject("wind");
            double w_speed = wind.getDouble("speed");
            JSONArray w_details = obj.getJSONArray("weather");
            for(int i=0; i < w_details.length();i++){
                JSONObject d = w_details.getJSONObject(i);
                wi.setRegionName(c_name);
                String description = d.getString("main")+"\n";
                description += "temp: "+temp+"\u2103,  humidity: "+humidity+"%, \nwind: "+w_speed+"m/s, "+pressure+"hpa";

                wi.setDescription(description);
                wi.setIconId(d.getString("icon"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return wi;
    }



    //01d.png  	01n.png  	clear sky
    //02d.png  	02n.png  	few clouds
    //03d.png  	03n.png  	scattered clouds
    //04d.png  	04n.png  	broken clouds
    //09d.png  	09n.png  	shower rain
    //10d.png  	10n.png  	rain
    //11d.png  	11n.png  	thunderstorm
    //13d.png  	13n.png  	snow
    //50d.png  	50n.png  	mist
}
