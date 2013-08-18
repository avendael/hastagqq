package com.hastagqq.app;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;

import com.hastagqq.app.api.BasicApiResponse;
import com.hastagqq.app.api.GetNewsApiResponse;
import com.hastagqq.app.api.NewsApiClient;
import com.hastagqq.app.util.GPSTracker;

public class MainActivity extends FragmentActivity implements NewsApiClient.GetCallback,
        NewsApiClient.CreateCallback {
	private static final String TAG = MainActivity.class.getSimpleName();

    private String mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GPSTracker gpsTracker = new GPSTracker(MainActivity.this);
        mLocation = gpsTracker.getCity();
        Location location = gpsTracker.getLocation();

        Log.d(TAG, "::onCreate() -- " + location.getLatitude() + " - " + location.getLongitude() + " - " + mLocation);

        showCreateNewsFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onGetNewsComplete(GetNewsApiResponse apiResponse) {
        Log.d(TAG, "::onGetNewsComplete() -- START");
        Log.d(TAG, "::onGetNewsComplete() -- " + apiResponse);
        Log.d(TAG, "::onGetNewsComplete() -- END");
    }

    @Override
    public void onCreateNewsComplete(BasicApiResponse apiResponse) {
        Log.d(TAG, "::onCreateNewsComplete() -- START");
        Log.d(TAG, "::onCreateNewsComplete() -- " + apiResponse);
        Log.d(TAG, "::onCreateNewsComplete() -- END");
    }

    private void showCreateNewsFragment() {
        CreateNewsFragment createNewsFragment = new CreateNewsFragment();
        Bundle args = new Bundle();

        args.putString(CreateNewsFragment.EXTRAS_LOCATION, mLocation);
        createNewsFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment_container,
                createNewsFragment).commit();
    }
}
