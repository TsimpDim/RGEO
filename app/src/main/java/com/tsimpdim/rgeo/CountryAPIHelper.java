package com.tsimpdim.rgeo;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class CountryAPIHelper {

    private final String callURL;
    private final RequestQueue queue;
    private final Context ctx;

    public CountryAPIHelper(String url, Context context){
        ctx = context;
        callURL = url;
        queue = Volley.newRequestQueue(ctx);
    }

    public void getResponse(final VolleyCallback callback){

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
            (Request.Method.GET, callURL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    callback.onSuccess(response);
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Toast.makeText(ctx, "Could not get country name", Toast.LENGTH_SHORT).show();
                }
            });

        // Access the RequestQueue through your singleton class.
        queue.add(jsonObjectRequest);
    }

}