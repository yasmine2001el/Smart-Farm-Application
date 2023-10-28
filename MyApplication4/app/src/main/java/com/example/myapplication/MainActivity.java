package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {


    private TextView textViewDecision;
    private TextView textViewRainfall;
    private TextView textViewPotassium;
    private TextView textViewNitrogen;
    private TextView textViewPhosphore;
    private TextView textViewPh;
    private TextView textViewHumidity;
    private TextView textViewTemperature;

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String ENDPOINT_URL = "http://100.95.146.121:5000/ml";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewDecision = findViewById(R.id.textView57);
        textViewRainfall = findViewById(R.id.textView54);
        textViewPotassium = findViewById(R.id.textView50);
        textViewPh = findViewById(R.id.textView52);
        textViewHumidity = findViewById(R.id.textView53);
        textViewTemperature = findViewById(R.id.textView51);
        textViewNitrogen = findViewById(R.id.textView48);
        textViewPhosphore = findViewById(R.id.textView49);

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Create a JSON object request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, ENDPOINT_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Extract the necessary data
                            JSONObject dataObject = response.getJSONObject("data");
                            double potassium = dataObject.getDouble("K");
                            double nitrogen = dataObject.getDouble("N");
                            double phosphorus = dataObject.getDouble("P");
                            double humidity = dataObject.getDouble("humidity");
                            double pH = dataObject.getDouble("ph");
                            double temperature = dataObject.getDouble("temperature");
                            double waterSensor = dataObject.getDouble("water_sensor");
                            String decision = response.getString("decision");

                            textViewDecision.setText(decision);
                            textViewRainfall.setText(String.valueOf(potassium));
                            textViewPotassium.setText(String.valueOf(potassium));
                            textViewNitrogen.setText(String.valueOf(nitrogen));
                            textViewPhosphore.setText(String.valueOf(phosphorus));
                            textViewPh.setText(String.valueOf(pH));
                            textViewHumidity.setText(String.valueOf(humidity));
                            textViewTemperature.setText(String.valueOf(temperature));
                            int statusCode = response.getInt("statusCode");
                            Log.d(TAG, "Response code: " + statusCode);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error: " + error.getMessage());
                    }
                });

        // Add the request to the request queue
        requestQueue.add(jsonObjectRequest);

    }

}
