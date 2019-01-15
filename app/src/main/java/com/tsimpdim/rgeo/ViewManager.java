package com.tsimpdim.rgeo;

import android.app.Activity;
import android.content.Context;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;


import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;

public class ViewManager {

    private Context ctx;

    private HashMap<String, TextView> tViews;

    // We save cityNameView separately from the rest of the tViews
    // That's because we want it to display instantly and without any sort of modification
    // e.g animation etc. etc.
    private TextView cityNameView;

    private AlphaAnimation fadeIn;
    private AlphaAnimation fadeOut;

    public ViewManager(Context context){
        ctx = context;

        cityNameView = ((Activity)ctx).findViewById(R.id.myImageViewText);

        tViews = new HashMap<>();
        tViews.put("ciPop", (TextView)((Activity)ctx).findViewById(R.id.population));
        tViews.put("coName", (TextView)((Activity)ctx).findViewById(R.id.country));
        tViews.put("coLang", (TextView)((Activity)ctx).findViewById(R.id.language));
        tViews.put("coCurr", (TextView)((Activity)ctx).findViewById(R.id.currency));
        tViews.put("coTmz", (TextView)((Activity)ctx).findViewById(R.id.timezone));
        tViews.put("ciTmp", (TextView)((Activity)ctx).findViewById(R.id.citytemp));
        tViews.put("ciHum", (TextView)((Activity)ctx).findViewById(R.id.cityhum));
        tViews.put("ciWs", (TextView)((Activity)ctx).findViewById(R.id.cityws));


        fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeOut = new AlphaAnimation( 1.0f, 0.0f);

        fadeIn.setDuration(200);
        fadeOut.setDuration(200);
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
        tViews.get("ciPop").setText(populationString);

        // Country name
        tViews.get("coName").setText(((Activity)ctx).getResources().getString(R.string.country_code, city.getCountryName(), city.getCountryCode()));

        // Timezone
        tViews.get("coTmz").setText(((Activity)ctx).getResources().getString(R.string.timezone, city.getTimezone()));

        // Currency
        tViews.get("coCurr").setText(((Activity)ctx).getResources().getString(R.string.currency, city.getCurrency(), city.getCurrencyCode(), city.getCurrencySymbol()));

        // Language
        tViews.get("coLang").setText(((Activity)ctx).getResources().getString(R.string.language, city.getLanguage()));

        // Temperature
        tViews.get("ciTmp").setText(((Activity)ctx).getResources().getString(R.string.citytemp, city.getTemperature()));

        // Humidity
        tViews.get("ciHum").setText(((Activity)ctx).getResources().getString(R.string.cityhum, city.getHumidity()));

        // Wind speed
        tViews.get("ciWs").setText(((Activity)ctx).getResources().getString(R.string.cityws, city.getWindSpeed()));

        fadeOutViews();
        fadeInViews();

    }

    public void fadeOutViews(){
        for (TextView tView : tViews.values()) {
            tView.clearAnimation();
            tView.startAnimation(fadeOut);
        }
    }

    public void fadeInViews(){
        for (TextView tView : tViews.values()) {
            tView.clearAnimation();
            tView.startAnimation(fadeIn);
        }
    }

}
