package com.tsimpdim.rgeo;

import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

public class City {

    private String cityName;
    private float latitude;
    private float longitude;
    private int population;
    private String countryName;
    private String countryCode;
    private String language;
    private String currency;
    private String currencySymbol;
    private String currencyCode;
    private String timezone;

    private ViewManager viewMan;
    private DatabaseHelper dbHelper;
    private MapView map;

    private Context mainCtx;


    public City(String cityName, float latitude, float longitude, int population, String countryName, String countryCode,
                String language, String currency, String currencySymbol, String currencyCode, String timezone) {
        this.cityName = cityName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.population = population;
        this.countryName = countryName;
        this.countryCode = countryCode;
        this.language = language;
        this.currency = currency;
        this.currencySymbol = currencySymbol;
        this.currencyCode = currencyCode;
        this.timezone = timezone;
    }

    public City(Context ctx, MapView mapView, DatabaseHelper dbHelper, ViewManager viewMan){
        this.dbHelper = dbHelper;
        this.viewMan = viewMan;
        map = mapView;
        mainCtx = ctx;

        processNewCity();
    }


    /**
     * Generates new random city
     * @return City object
     */
    private City processNewCity(){

        // Get random City from DB
        Cursor rndCityCur = dbHelper.getRndCity();

        // Get Latitude & Longitude
        setLatitude(rndCityCur.getFloat(rndCityCur.getColumnIndex("latitude")));
        setLongitude(rndCityCur.getFloat(rndCityCur.getColumnIndex("longitude")));

        // Show location on map
        IMapController mapController = map.getController();
        mapController.setZoom(13);
        GeoPoint startPoint = new GeoPoint(getLatitude(), getLongitude());
        mapController.setCenter(startPoint);

        // Get city name
        setCityName(rndCityCur.getString(rndCityCur.getColumnIndex("name")));

        // Get population
        setPopulation(rndCityCur.getInt(rndCityCur.getColumnIndex("population"))); // Actual population from DB

        // Get country code
        setCountryCode(rndCityCur.getString(rndCityCur.getColumnIndex("countrycode")));

        CountryAPIHelper api = new CountryAPIHelper("https://restcountries.eu/rest/v2/alpha/" + getCountryCode() + "?fields=name;timezones;currencies;languages", this, mainCtx);
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
                    Toast.makeText(mainCtx, "Error on city name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return this;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
