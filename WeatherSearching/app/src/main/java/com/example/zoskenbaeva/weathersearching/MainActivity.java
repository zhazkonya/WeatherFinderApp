package com.example.zoskenbaeva.weathersearching;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.EditText;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;

import static com.example.zoskenbaeva.weathersearching.GMAPProperties.*;
import static com.example.zoskenbaeva.weathersearching.OWMProperties.*;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.etLocationName) EditText etLocationName;
    @BindView(R.id.lvLocationsAndWeatherInfo)
    RecyclerView lvLocationsAndWeatherInfo;
    List<WeatherInfo> wiList;
    final String TAG="WEATHER_PROJECT";
    Map<String, String> coorToCity = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        GridLayoutManager glm = new GridLayoutManager(this,1);
        lvLocationsAndWeatherInfo.setLayoutManager(glm);
    }

    @OnTextChanged(R.id.etLocationName)
    protected void onLocationNameChanged(CharSequence text) {
        if(etLocationName.getText().length() >= 2){
            wiList = new ArrayList<WeatherInfo>();
            search_city();
        }
    }

    private void search_city() {
        String autocomplete_request =
                GMAP_AUTOCOM_API_BASE + GMAP_AUTOCOM_PARAMS
                       + etLocationName.getText().toString().trim()
                       + "&"+ GMAP_KEY;
        Ion.with(this)
                .load(autocomplete_request)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        processData(result);
                    }
                });
    }

    private void processData(String result) {
        Map<String, String> regionsList = parseGmapJSON(result);

        for(Map.Entry<String, String> entry : regionsList.entrySet()){
            getPlaceDetails(entry.getKey(), entry.getValue());
        }
    }

    private Map<String, String> parseGmapJSON(String result) {
        Map<String, String> pidsToCities = new HashMap<>();
        try {
            String cities = "";
            JSONObject obj = new JSONObject(result);
            JSONArray predictions = obj.getJSONArray("predictions");
            for(int i=0; i < predictions.length(); i++){
                JSONObject pred = predictions.getJSONObject(i);
                String l = pred.getString("description");
                String place_id = pred.getString("place_id");
                pidsToCities.put(place_id, l);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return pidsToCities;
    }

    String city="";
    private void getPlaceDetails(String p_id, String city_name) {
        String placedeatil_request =
                GMAP_PLACE_API_BASE+GMAP_KEY
                +"&"+GMAP_PLACE_PARAMS+p_id;
        city = city_name;
        new ProcessJSON().execute(placedeatil_request, GMAP_PLACE_ID ,city_name);
        /*Ion.with(this)
                .load(placedeatil_request)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        processResult(result, city);
                    }
                });*/

    }

    private void processResult(String result, String c_name) {
        String[] details = parsePlaceDeatilJson(result);
        coorToCity.put( details[1]+" " + details[2], c_name.replace(", Kazakhstan",""));
        getWeatherFromOWM( details[1],  details[2]);
    }

    private String[] parsePlaceDeatilJson(String result) {
        String[] details = new String[3];
        try {
            JSONObject obj = new JSONObject(result);
            JSONObject placeobj = obj.getJSONObject("result");
            details[0] = placeobj.getString("formatted_address");
            JSONObject p_location = placeobj.getJSONObject("geometry").getJSONObject("location");
            details[1] = p_location.getString("lat");
            details[2] = p_location.getString("lng");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return details;
    }

    private void getWeatherFromOWM(String lat, String lng) {
        String request = OWM_API_BASE+OWM_API_KEY
                + "&"+OWM_PARAMS_LAT + lat
                + "&"+OWM_PARAMS_LNG + lng;

        new ProcessJSON().execute(request, OWM_REQUEST_ID, lat +" "+ lng);
        /*Ion.with(this)
                .load(request)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        processOWMResult(result);
                    }
                });*/
    }

    private void processOWMResult(String result, String c_name) {
        WeatherInfo wi = parseOWMJson(result, c_name);
        wiList.add(wi);
        fillLocationsAndWeatherInfo();
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

    public void fillLocationsAndWeatherInfo() {
        WeatherInfoAdapter wia = new WeatherInfoAdapter(wiList);
        lvLocationsAndWeatherInfo.setAdapter(wia);
    }

    private class ProcessJSON extends AsyncTask<String, Void, String> {
        String c_name="";
        String lat_lng = "";
        protected String doInBackground(String... strings){
            String stream = null;
            String urlString = strings[0];
            if(strings[1].equals(GMAP_PLACE_ID))
                c_name = strings[2];
            else if(strings[1].equals(OWM_REQUEST_ID))
                lat_lng = strings[2];
            HTTPDataHandler hh = new HTTPDataHandler();
            stream = hh.GetHTTPData(urlString);

            // Return the data from specified url
            return stream;
        }

        protected void onPostExecute(String stream) {
            if(!c_name.trim().equals("")) {
                Log.d(TAG,"len1: " + stream);
                Log.d(TAG, "len2: " + c_name);
                processResult(stream, c_name);
            }else if(!lat_lng.trim().equals("")){
                Log.d(TAG,"len1: " + stream);
                Log.d(TAG,"len2: " + coorToCity.get(lat_lng));
                Log.d(TAG,"len3: " + lat_lng);
                processOWMResult(stream, coorToCity.get(lat_lng));
            }


        }
    }

}
