package com.tsimpdim.rgeo;

import android.content.Context;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;


import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.io.UnsupportedEncodingException;


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
                processNewCity(mainTextView);
            }
        });

        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        processNewCity(mainTextView);

    }

    /**
     * Generates new info and updates the UI
     * @param curTextView
     * @return city id
     */
    private long processNewCity(TextView curTextView){

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Cursor rndCityCur = dbHelper.getRndCity();
        String rndCityName = rndCityCur.getString(rndCityCur.getColumnIndex("name"));

        try {
            curTextView.setText(new String(rndCityName.getBytes("ISO-8859-1"), "UTF-8"));
        }catch(UnsupportedEncodingException e){
            curTextView.setText(rndCityName);
            Toast.makeText(this,"Text might be shown incorrectly", Toast.LENGTH_LONG).show();
        }

        Toast.makeText(this, "Loading map...", Toast.LENGTH_SHORT).show();

        float latitude = rndCityCur.getFloat(rndCityCur.getColumnIndex("latitude"));
        float longitude = rndCityCur.getFloat(rndCityCur.getColumnIndex("longitude"));


        IMapController mapController = map.getController();
        mapController.setZoom(13);
        GeoPoint startPoint = new GeoPoint(latitude, longitude);
        mapController.setCenter(startPoint);

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

}
