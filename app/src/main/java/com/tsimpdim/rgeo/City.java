package com.tsimpdim.rgeo;

public class City {

    private String cityName;
    private float latitude;
    private float longitude;
    private int population;
    private String countryName;
    private String language;
    private String currency;
    private String currencySymbol;
    private String currencyCode;
    private String timezone;


    public City(){}

    public City(String cityName, float latitude, float longitude, int population, String countryName,
                String language, String currency, String currencySymbol, String currencyCode, String timezone) {
        this.cityName = cityName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.population = population;
        this.countryName = countryName;
        this.language = language;
        this.currency = currency;
        this.currencySymbol = currencySymbol;
        this.currencyCode = currencyCode;
        this.timezone = timezone;
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
}
