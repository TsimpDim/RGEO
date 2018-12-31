package com.tsimpdim.rgeo;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class ViewManager {

    private Context ctx;
    private TextView cityNameView;
    private TextView cityPopView;
    private TextView countryCodeView;
    private TextView countryLangView;
    private TextView countryCurrView;
    private TextView countryTmzView;

    public ViewManager(Context context){
        ctx = context;
        cityNameView = ((Activity)ctx).findViewById(R.id.myImageViewText);
        cityPopView = ((Activity)ctx).findViewById(R.id.population);
        countryCodeView = ((Activity)ctx).findViewById(R.id.country);
        countryLangView = ((Activity)ctx).findViewById(R.id.language);
        countryCurrView = ((Activity)ctx).findViewById(R.id.currency);
        countryTmzView = ((Activity)ctx).findViewById(R.id.timezone);
    }

    public void setViewsFromCity(City city){

        // Set city name
        try {
            cityNameView.setText(new String(city.getCityName().getBytes("ISO-8859-1"), "UTF-8"));
        }catch(UnsupportedEncodingException e){
            cityNameView.setText(city.getCityName());
            Toast.makeText(ctx,"Text might be shown incorrectly", Toast.LENGTH_LONG).show();
        }

        // Set population
        // Set proper pattern on numbers -> separate thousands/decimals etc. with spaces
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols();
        formatSymbols.setDecimalSeparator(' ');
        formatSymbols.setGroupingSeparator(' ');
        formatter.setDecimalFormatSymbols(formatSymbols);

        // Build & set final population string
        String populationString = ((Activity)ctx).getResources().getString(R.string.city_population, formatter.format(city.getPopulation())); // Final string from strings.xml placeholder
        cityPopView.setText(populationString);

        // Country name
        countryCodeView.setText(((Activity)ctx).getResources().getString(R.string.country_code, city.getCountryName(), city.getCountryCode()));

        // Timezone
        countryTmzView.setText(((Activity)ctx).getResources().getString(R.string.timezone, city.getTimezone()));

        // Currency
        countryCurrView.setText(((Activity)ctx).getResources().getString(R.string.currency, city.getCurrency(), city.getCurrencyCode(), city.getCurrencySymbol()));

        // Language
        countryLangView.setText(((Activity)ctx).getResources().getString(R.string.language, city.getLanguage()));

    }

}
