package com.hastagqq.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.hastagqq.app.api.BasicApiResponse;
import com.hastagqq.app.model.DeviceInfo;
import com.hastagqq.app.util.Constants;
import com.hastagqq.app.util.GPSTracker;
import com.hastagqq.app.util.GsonUtil;
import com.hastagqq.app.util.HttpUtil;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

/**
 * @author avendael
 */
public class SplashActivity extends Activity {
    private static final String TAG = SplashActivity.class.getSimpleName();
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int TIMEOUT = 3000;
    public static final String PROPERTY_REG_ID = "registration_id";

    private String mRegId;
    private String mLocation;
    private GoogleCloudMessaging mGcm;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        GPSTracker gpsTracker = new GPSTracker(SplashActivity.this);
        mLocation = gpsTracker.getCity();
    }

    @Override
    protected void onResume() {
        super.onResume();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(TIMEOUT);

                    if (!checkPlayServices()) {
                        Toast.makeText(SplashActivity.this, R.string.err_no_gcm, Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        mGcm = GoogleCloudMessaging.getInstance(SplashActivity.this);
                        mContext = getApplicationContext();
                        mRegId = getRegistrationId(mContext);

                        if (mRegId.isEmpty()) {
                            registerInBackground();
                        } else {
                            startMainActivity();
                        }
                    }
                } catch (InterruptedException e) {
                    Log.e(TAG, "ERROR " + e);
                    finish();
                }
            }
        }).start();
    }

    private void startMainActivity() {
        Intent mainIntent = new Intent(this, MainActivity.class);

        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        startActivity(mainIntent);

        finish();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.e(TAG, "This device is not supported.");
                finish();
            }

            return false;
        }

        return true;
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences();
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }

        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private SharedPreferences getGCMPreferences() {
        return getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg;

                try {
                    if (mGcm == null) {
                        mGcm = GoogleCloudMessaging.getInstance(SplashActivity.this);
                    }

                    mRegId = mGcm.register(Constants.SENDER_ID);
                    msg = "Device registered, registration ID=" + mRegId;

                    sendRegistrationIdToBackend();
                    storeRegistrationId(SplashActivity.this, mRegId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }

                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                startMainActivity();
            }
        }.execute(null, null, null);
    }

    private void sendRegistrationIdToBackend() {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(Constants.HOST + Constants.DEVICE);
        Gson defaultGsonParser = GsonUtil.getDefaultGsonParser();
        DeviceInfo deviceInfo = new DeviceInfo(mRegId, mLocation);

        Log.d(TAG, "::sendRegistrationIdToBackend() -- payload " + defaultGsonParser.toJson(deviceInfo));
        try {
            httpPost.setEntity(new StringEntity(defaultGsonParser.toJson(deviceInfo)));
            HttpResponse response = httpClient.execute(httpPost);
            BasicApiResponse basicApiResponse = HttpUtil.parseBasicApiResponse(response);
            Log.d(TAG, "::sendRegistrationIdToBackend() -- " + defaultGsonParser.toJson(basicApiResponse));
        } catch (IOException e) {
            Log.e(TAG, "::postData() -- ERROR: " + e.getMessage());
        }
    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences();
        int appVersion = getAppVersion(context);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }
}
