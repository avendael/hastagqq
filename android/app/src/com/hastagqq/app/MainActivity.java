package com.hastagqq.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.hastagqq.app.api.BasicApiResponse;
import com.hastagqq.app.api.GetNewsApiResponse;
import com.hastagqq.app.api.NewsApiClient;
import com.hastagqq.app.db.NewsDal;
import com.hastagqq.app.model.DeviceInfo;
import com.hastagqq.app.model.News;
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
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class MainActivity extends FragmentActivity implements NewsApiClient.GetCallback,
        NewsApiClient.CreateCallback {
	private static final String TAG = MainActivity.class.getSimpleName();
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";

    private String mRegId;
    private String mLocation;
    private GoogleCloudMessaging mGcm;
    private Context mContext;
    private NewsListFragment mNewsListFragment;
    private NewsDal mNewsDal;
    private List<News> mNewsItems;
    private String mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNewsDal = new NewsDal(this);

        if (!checkPlayServices()) {
            Toast.makeText(this, R.string.err_no_gcm, Toast.LENGTH_LONG).show();
            finish();

            return;
        } else {
            mGcm = GoogleCloudMessaging.getInstance(this);
            mContext = getApplicationContext();
            mRegId = getRegistrationId(mContext);

            if (mRegId.isEmpty()) {
                registerInBackground();
            }
        }

        showNewsListFragment();
        Log.d(TAG, "::onCreate() -- " + mLocation);
    }

    @Override
    protected void onResume() {
        super.onResume();

        GPSTracker gpsTracker = new GPSTracker(MainActivity.this);
        mLocation = gpsTracker.getCity();

        try {
            mNewsDal.open();
            mNewsDal.deleteAllNews();
            NewsApiClient.getNews(mLocation, this);
        } catch (SQLException e) {
            Log.e(TAG, "ERROR: " + e);
        }

        if (!checkPlayServices()) {
            Toast.makeText(this, R.string.err_no_gcm, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mNewsDal.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_create_news:
                showCreateNewsFragment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onGetNewsComplete(GetNewsApiResponse apiResponse) {
        Log.d(TAG, "::onGetNewsComplete() -- START");
        Log.d(TAG, "::onGetNewsComplete() -- " + apiResponse);
        final List<News> newsItems = apiResponse.getNewsItems();
        Log.d(TAG, "::onGetNewsComplete() -- newsItems size = " + newsItems.size());

        mNewsItems = newsItems;

        Collections.reverse(newsItems);

        if (mCurrentFragment != null && mCurrentFragment.equals("list"))
            mNewsListFragment.onNewsAvailable(newsItems);

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!newsItems.isEmpty()) {
                    for (News news : newsItems) {
                        Log.d(TAG, "::onGetNewsComplete() -- location = " + news.getLocation());
                        mNewsDal.createNews(news);
                    }
                }
            }
        });

        Log.d(TAG, "::onGetNewsComplete() -- END");
    }

    @Override
    public void onCreateNewsComplete(BasicApiResponse apiResponse) {
        Log.d(TAG, "::onCreateNewsComplete() -- START");
        Log.d(TAG, "::onCreateNewsComplete() -- " + apiResponse);
        onBackPressed();
//        getSupportFragmentManager().popBackStack();
        Log.d(TAG, "::onCreateNewsComplete() -- END");
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mCurrentFragment != null && mCurrentFragment.equals("create")
                && mNewsListFragment != null && mNewsListFragment.isVisible()) {
            NewsApiClient.getNews(mLocation, this);
            showNewsListFragment();
        }
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg;

                try {
                    if (mGcm == null) {
                        mGcm = GoogleCloudMessaging.getInstance(MainActivity.this);
                    }

                    mRegId = mGcm.register(Constants.SENDER_ID);
                    msg = "Device registered, registration ID=" + mRegId;

                    sendRegistrationIdToBackend();
                    storeRegistrationId(MainActivity.this, mRegId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }

                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                // TODO
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

    private void showNewsListFragment() {
        mCurrentFragment = "list";
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        mNewsListFragment = new NewsListFragment();
        ft.replace(R.id.fl_fragment_container, mNewsListFragment, NewsListFragment.TAG_FRAGMENT);
        ft.commit();
    }

    private void showCreateNewsFragment() {
        mCurrentFragment = "create";
        CreateNewsFragment createNewsFragment = new CreateNewsFragment();
        Bundle args = new Bundle();

        args.putString(CreateNewsFragment.EXTRAS_LOCATION, mLocation);
        Log.d(TAG, "::showCreateNewsFragment() mLocation " + mLocation);
        createNewsFragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.addToBackStack(null);
        transaction.replace(R.id.fl_fragment_container,
                createNewsFragment).commit();
    }
}
