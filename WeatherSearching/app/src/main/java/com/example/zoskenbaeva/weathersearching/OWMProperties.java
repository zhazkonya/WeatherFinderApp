package com.example.zoskenbaeva.weathersearching;

/**
 * Created by z.oskenbaeva on 19.01.2018.
 */

public class OWMProperties {
    public final static  String OWM_ICON_URL ="http://openweathermap.org/img/w/";//10d.png
    public final static  String OWM_API_KEY
            = "appid=334740fed34c64cbdb860013770983bd";
    public final static  String OWM_API_BASE ="http://api.openweathermap.org/data/2.5/weather?";

    //&lat=52.3507849&lon=5.2647016
    public final static  String OWM_PARAMS_LAT ="lat=";
    public final static  String OWM_PARAMS_LNG ="lon=";

    public final static String OWM_REQUEST_ID = "3";

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
