package com.example.zoskenbaeva.weathersearching;

import android.content.Context;
import android.os.SystemClock;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by z.oskenbaeva on 19.01.2018.
 */

public class GMAPUtils {
    //"input=Vict&types=geocode&language=fr&key=AIzaSyBNiFTBo1qMncbxXrrNDuLY7hea1PqDUS4";

    Context c = null;
    String curtoken = "";
    public final static  String GMAP_AUTOCOM_API_BASE
            ="https://maps.googleapis.com/maps/api/place/autocomplete/json?";
    public final static  String GMAP_AUTOCOM_PARAMS =
            "language=en&types=(cities)&components=country:kz&input=";

    public final static  String GMAP_PLACE_API_BASE
            ="https://maps.googleapis.com/maps/api/place/details/json?";
    public final static  String GMAP_PLACE_PARAMS
            ="placeid=";

    public final static  String GMAP_KEY
            = "key=AIzaSyBNiFTBo1qMncbxXrrNDuLY7hea1PqDUS4";
    Map<String, String> coorToCity = new HashMap<>();

    public GMAPUtils(Context context, String token) {
        c = context;
        curtoken = token;
    }

    public Map<String, String> requestToGmap( String token) {
        SystemClock.sleep(300);

        String autocomplete_request =
                GMAP_AUTOCOM_API_BASE + GMAP_AUTOCOM_PARAMS
                        + token
                        + "&"+ GMAP_KEY;

        HTTPDataHandler hh = new HTTPDataHandler();
        String stream = hh.GetHTTPData(autocomplete_request);
        processPLacesData(stream);
        return coorToCity;
    }

    private void processPLacesData(String result) {
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

    private void getPlaceDetails(String p_id, String city_name) {
        String placedeatil_request =
                GMAP_PLACE_API_BASE+GMAP_KEY
                        +"&"+GMAP_PLACE_PARAMS+p_id;

        HTTPDataHandler hh = new HTTPDataHandler();
        String stream = hh.GetHTTPData(placedeatil_request);
        parsePlaceDeatilJson(stream, city_name);
    }

    private void parsePlaceDeatilJson(String result, String cityName) {
        String[] details = new String[3];
        try {
            JSONObject obj = new JSONObject(result);
            JSONObject placeobj = obj.getJSONObject("result");
            details[0] = placeobj.getString("formatted_address");
            JSONObject p_location = placeobj.getJSONObject("geometry").getJSONObject("location");
            details[1] = p_location.getString("lat");
            details[2] = p_location.getString("lng");
            coorToCity.put(details[1]+" " + details[2], cityName.replace(", Kazakhstan",""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
