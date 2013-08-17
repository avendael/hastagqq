package com.hastagqq.app;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.hastagqq.app.util.Constants;
import com.hastagqq.app.util.GPSTracker;

import com.loopj.android.http.*;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {
	private static final String TAG = 
			MainActivity.class.getSimpleName();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        GPSTracker gpsTracker = new GPSTracker(MainActivity.this);
        Location location = gpsTracker.getLocation();
        Log.d(TAG, "::onCreate() -- " + location.getLatitude() + " - " + location.getLongitude());
        
        new GetAsyncTask().execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    private class PostAsyncTask extends AsyncTask<JSONObject, Integer, Double> {

        @Override
        protected Double doInBackground(JSONObject... params) {
            postData(params[0]);
            return null;
        }
        
        @Override
        protected void onPostExecute(Double result) {
            Log.d(TAG, "::onPostExecute() -- result = " + result);
        }
        
        @Override
        protected void onProgressUpdate(Integer... progress) {
            Log.d(TAG, "::onProgressUpdate() -- progress = " + progress);
        }
        
        public void postData(JSONObject data) {
            Log.d(TAG,"::postData() -- data = " + data.toString());
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(Constants.HOST + Constants.NEWS);
            
            try {
                
                /*JSONObject json = new JSONObject();
                try {
                    
                    json.put("title", "Bon Chon");
                    json.put("category", "Food");
                    json.put("location", "Makati City");
                    json.put("content", "Bonchon has the best fried chicken!!!!");
                    json.put("score", 0);
                    // PostAsyncTask().execute(json.toString());    
                } catch (JSONException e) {
                    Log.d(TAG, "::onCreate() -- ERROR: " + e.getMessage());
                }*/
                
                httpPost.setEntity(new StringEntity(data.toString()));
                HttpResponse response = httpClient.execute(httpPost);
                Log.d(TAG, "::postData() -- response = " + response.toString());
            } catch (IOException e) {
                Log.e(TAG, "::postData() -- ERROR: " + e.getMessage());
            }
            
        }
    }
    
    private class GetAsyncTask extends AsyncTask<String, Integer, Void> {

        @Override
        protected Void doInBackground(String... params) {
            getData();
            return null;
        }
        
        @Override
        protected void onPostExecute(Void voids) {
            Log.d(TAG, "HttpTask::onPostExecute() -- START");
         
            Log.d(TAG, "HttpTask::onPostExecute() -- END");
        }
        
        @Override
        protected void onProgressUpdate(Integer... progress) {
            
        }
        
        public void getData() {
            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("device_location", "Makati City");
            client.get(Constants.HOST + Constants.NEWS, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    Log.d(TAG, "::getData() -- response " + response);
                    
                }
            });
        }
    }
}
