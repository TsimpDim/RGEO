package com.tsimpdim.rgeo;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        TextView mainTextView = (TextView) findViewById(R.id.myImageViewText);
        processNewCity(mainTextView);


    }

    private void processNewCity(TextView curTextView){

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Cursor rndCityCur = dbHelper.getRndCity();
        String rndCityName = rndCityCur.getString(rndCityCur.getColumnIndex("name"));

        try {
            curTextView.setText(new String(rndCityName.getBytes("ISO-8859-1"), "UTF-8"));
        }catch(UnsupportedEncodingException e){
            curTextView.setText(rndCityName);
            Toast.makeText(this,"Text might be shown incorrectly", Toast.LENGTH_LONG);
        }

    }

}
