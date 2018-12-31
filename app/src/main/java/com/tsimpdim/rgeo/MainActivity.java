package com.tsimpdim.rgeo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity{

    private MapView map = null;
    private ViewManager viewMan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        viewMan = new ViewManager(MainActivity.this);

        final TextView mainTextView = (TextView) findViewById(R.id.myImageViewText);
        mainTextView.setOnTouchListener(new OnSwipeTouchListener(this){
            @Override
            public void onSwipeLeft() {
                processNewCity();
            }
        });

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        processNewCity();

    }

    /**
     * Generates new info and updates the UI
     * @return city id
     */
    private long processNewCity(){

        final City currCity = new City();

        // Get Cursor
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Cursor rndCityCur = dbHelper.getRndCity();

        // Set up map
        Toast.makeText(this, "Loading map...", Toast.LENGTH_SHORT).show();

        currCity.setLatitude(rndCityCur.getFloat(rndCityCur.getColumnIndex("latitude")));
        currCity.setLongitude(rndCityCur.getFloat(rndCityCur.getColumnIndex("longitude")));

        IMapController mapController = map.getController();
        mapController.setZoom(13);
        GeoPoint startPoint = new GeoPoint(currCity.getLatitude(), currCity.getLongitude());
        mapController.setCenter(startPoint);


        // Get city name
        currCity.setCityName(rndCityCur.getString(rndCityCur.getColumnIndex("name")));

        // Get population
        currCity.setPopulation(rndCityCur.getInt(rndCityCur.getColumnIndex("population"))); // Actual population from DB

        // Get country code
        currCity.setCountryCode(rndCityCur.getString(rndCityCur.getColumnIndex("countrycode")));

        CountryAPIHelper api = new CountryAPIHelper("https://restcountries.eu/rest/v2/alpha/" + currCity.getCountryCode() + "?fields=name;timezones;currencies;languages", currCity, this);
        api.getResponse(new VolleyCallback() {
            @Override
            public void onSuccess(City city, JSONObject result) {
                try{
                    // Country name
                    city.setCountryName(result.getString("name"));

                    // Timezone
                    JSONArray timezones = result.getJSONArray("timezones");
                    city.setTimezone(timezones.getString(0));

                    // Currency
                    JSONArray currencyArr = result.getJSONArray("currencies");
                    JSONObject currencyObj = currencyArr.getJSONObject(0);
                    city.setCurrency(currencyObj.getString("name"));
                    city.setCurrencySymbol(currencyObj.getString("symbol"));
                    city.setCurrencyCode(currencyObj.getString("code"));

                    // Language
                    JSONArray languages = result.getJSONArray("languages");
                    JSONObject languageObj = languages.getJSONObject(0);
                    city.setLanguage(languageObj.getString("name"));

                    viewMan.setViewsFromCity(city);
                }catch(JSONException e){
                    Toast.makeText(MainActivity.this, "An error occured", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rndCityCur.getLong(rndCityCur.getColumnIndex("_id"));
    }

    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onPause(){
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    public void toggleContents(View v){
        map.setVisibility(map.isShown() ? View.GONE: View.VISIBLE );
    }

}
