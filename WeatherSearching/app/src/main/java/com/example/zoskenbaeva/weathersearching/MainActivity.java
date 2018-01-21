package com.example.zoskenbaeva.weathersearching;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.etLocationName) EditText etLocationName;
    @BindView(R.id.lvLocationsAndWeatherInfo)
    RecyclerView lvLocationsAndWeatherInfo;
    private Disposable gMapDisposable;
    private Disposable owmDisposable;
    public SQLiteDatabase db;
    List<WeatherInfo> curWIList = new ArrayList<>();
    WeatherInfoAdapter wia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        startService(new Intent(this, CacheService.class));

        GridLayoutManager glm = new GridLayoutManager(this,1);
        lvLocationsAndWeatherInfo.setLayoutManager(glm);
        wia = new WeatherInfoAdapter(curWIList);
        lvLocationsAndWeatherInfo.setAdapter(wia);

        initCacheDB();
        fetchCacheData();

    }

    private void initCacheDB() {
        DHCache dbHelper = new DHCache(this);
        db = dbHelper.getWritableDatabase();
    }

    public void fetchCacheData() {
        if(db != null){
            Cursor c = db.rawQuery("select * from "+DHCache.TB_NAME+"" +
                    " ORDER BY w_time DESC;", null);
            String token = "";
            int counter = 0;
            List<WeatherInfo> wiList = new ArrayList<>();
            if(c.moveToFirst()){
                do{
                    String tok = c.getString(2);
                    if(counter != 0 && !tok.equals(token)){
                        break;
                    }
                    token = tok;
                    WeatherInfo wi = new WeatherInfo();
                    wi.setRegionName(c.getString(3));
                    wi.setDescription(c.getString(4));
                    wi.setIconId(c.getString(5));
                    wiList.add(wi);
                    counter ++;
                } while(c.moveToNext());
            }
            //etLocationName.setText(token);
            WeatherInfoAdapter wia = new WeatherInfoAdapter(wiList);
            lvLocationsAndWeatherInfo.setAdapter(wia);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try{
            if (gMapDisposable !=null && !gMapDisposable.isDisposed()) {
                gMapDisposable.dispose();
            }
            if (owmDisposable!=null && !owmDisposable.isDisposed()) {
                owmDisposable.dispose();
            }
        }catch(Exception e){}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            if (gMapDisposable != null && !gMapDisposable.isDisposed()) {
                gMapDisposable.dispose();
            }
            if (owmDisposable!=null && !owmDisposable.isDisposed()) {
                owmDisposable.dispose();
            }
        }catch(Exception e){}
    }

    @OnTextChanged(R.id.etLocationName)
    protected void onLocationNameChanged(CharSequence text) {
        if(etLocationName.getText().length() >= 2){
            GMAPUtils gmapObj = new GMAPUtils(this, etLocationName.getText().toString());
            Observable<Map<String, String>> placesObservable =
                    Observable.fromCallable(() ->
                            gmapObj.requestToGmap(etLocationName.getText().toString()));
            gMapDisposable = placesObservable.
                    subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe(strings -> getWeatherFromOWM(strings, gmapObj.curtoken));
        }
    }

    private void getWeatherFromOWM(Map<String, String> coorToPlaces, String token) {
        OWMUtils owmObj = new OWMUtils(this, token);

        Observable<List<WeatherInfo>> owmObservable =
                Observable.fromCallable(() ->
                        owmObj.requestToOWM(coorToPlaces));
        owmDisposable = owmObservable.
                observeOn(AndroidSchedulers.mainThread()).
                subscribeOn(Schedulers.io()).
                subscribe(wiList -> {
                    fillLocationsAndWeatherInfo(wiList,  owmObj.curtoken);
                });

    }

    public void fillLocationsAndWeatherInfo(List<WeatherInfo>  wiList, String token) {
        //WeatherInfoAdapter wia = new WeatherInfoAdapter(wiList);
        //lvLocationsAndWeatherInfo.setAdapter(wia);
        wia.swapItems(wiList);
        cacheResults(wiList, token);
    }

    private void cacheResults(List<WeatherInfo>  wiList, String token) {
        for(WeatherInfo wi : wiList){
            db.execSQL("INSERT INTO "+DHCache.TB_NAME +
                    " (w_token," +
                    " w_result ," +
                    " w_data , "+
                    " w_icon)" +
                    "VALUES\n (" +
                    " \""+token +"\"," +
                    " \""+wi.getRegionName()+"\"," +
                    " \""+wi.getDescription()+"\"," +
                    " \""+wi.getIconId()+"\" );");
        }
        fetchCacheData();
    }


}
