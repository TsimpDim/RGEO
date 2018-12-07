package com.tsimpdim.rgeo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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

    MapView map = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

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

        // Get Cursor
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Cursor rndCityCur = dbHelper.getRndCity();

        // Get city name
        String rndCityName = rndCityCur.getString(rndCityCur.getColumnIndex("name"));
        TextView cityNameView = (TextView) findViewById(R.id.myImageViewText);

        try {
            cityNameView.setText(new String(rndCityName.getBytes("ISO-8859-1"), "UTF-8"));
        }catch(UnsupportedEncodingException e){
            cityNameView.setText(rndCityName);
            Toast.makeText(this,"Text might be shown incorrectly", Toast.LENGTH_LONG).show();
        }

        // Set up map
        Toast.makeText(this, "Loading map...", Toast.LENGTH_SHORT).show();

        float latitude = rndCityCur.getFloat(rndCityCur.getColumnIndex("latitude"));
        float longitude = rndCityCur.getFloat(rndCityCur.getColumnIndex("longitude"));


        IMapController mapController = map.getController();
        mapController.setZoom(13);
        GeoPoint startPoint = new GeoPoint(latitude, longitude);
        mapController.setCenter(startPoint);

        // Get population
        int population = rndCityCur.getInt(rndCityCur.getColumnIndex("population")); // Actual population from DB
        TextView cityPopView = (TextView) findViewById(R.id.population); // Population TextView

        // Set proper pattern on numbers -> separate thousands/decimals etc. with spaces
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols();
        formatSymbols.setDecimalSeparator(' ');
        formatSymbols.setGroupingSeparator(' ');
        formatter.setDecimalFormatSymbols(formatSymbols);

        // Build & set final population string
        String populationString = getResources().getString(R.string.city_population, formatter.format(population)); // Final string from strings.xml placeholder
        cityPopView.setText(populationString);

        // Get & Set country name, code, timezone, currency, language
        final TextView countryCodeView = (TextView) findViewById(R.id.country);
        final TextView countryLangView = (TextView) findViewById(R.id.language);
        final TextView countryCurrView = (TextView) findViewById(R.id.currency);
        final TextView countryTmzView = (TextView) findViewById(R.id.timezone);

        final String cc = rndCityCur.getString(rndCityCur.getColumnIndex("countrycode"));

        CountryAPIHelper api = new CountryAPIHelper("https://restcountries.eu/rest/v2/alpha/" + cc + "?fields=name;timezones;currencies;languages", this);
        api.getResponse(new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try{
                    // Country name
                    String country = result.getString("name");
                    countryCodeView.setText(getResources().getString(R.string.country_code, country, cc));

                    // Timezone
                    JSONArray timezones = result.getJSONArray("timezones");
                    String timezone = timezones.getString(0);
                    countryTmzView.setText(getResources().getString(R.string.timezone, timezone));

                    // Currency
                    JSONArray currencyArr = result.getJSONArray("currencies");
                    JSONObject currencyObj = currencyArr.getJSONObject(0);
                    String currency = currencyObj.getString("name");
                    String symbol = currencyObj.getString("symbol");
                    String code = currencyObj.getString("code");
                    countryCurrView.setText(getResources().getString(R.string.currency, currency, code, symbol));

                    // Language
                    JSONArray languages = result.getJSONArray("languages");
                    JSONObject languageObj = languages.getJSONObject(0);
                    String language = languageObj.getString("name");
                    countryLangView.setText(getResources().getString(R.string.language, language));

                }catch(JSONException e){
                    String country = " ";
                    countryCodeView.setText(getResources().getString(R.string.country_code, country, cc));
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
