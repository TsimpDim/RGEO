package com.tsimpdim.rgeo;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;

public class MainActivity extends AppCompatActivity{

    private MapView map = null;
    private DatabaseHelper dbHelper;
    private ViewManager viewMan;
    private City prevCity;
    private City currCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        dbHelper = new DatabaseHelper(this);
        viewMan = new ViewManager(MainActivity.this);

        // Set up Map
        map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        final TextView mainTextView = (TextView) findViewById(R.id.myImageViewText);
        mainTextView.setOnTouchListener(new OnSwipeTouchListener(this){
            @Override
            public void onSwipeLeft() {
                prevCity = currCity;
                currCity = new City(ctx, map, dbHelper, viewMan);
            }

            @Override
            public void onSwipeRight(){
                viewMan.setViewsFromCity(prevCity);
            }
        });

        currCity = new City(ctx, map, dbHelper, viewMan);
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

    public void toggleMap(View v){
        map.setVisibility(map.isShown() ? View.GONE: View.VISIBLE );
    }

    public void toggleWeather(View v){
        LinearLayout weatherPanel = (LinearLayout) findViewById(R.id.weathercont);
        weatherPanel.setVisibility(weatherPanel.isShown() ? View.GONE: View.VISIBLE );
    }

}
