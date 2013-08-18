package com.hastagqq.app.util;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GPSTracker extends Service implements LocationListener{
	private static final String TAG = GPSTracker.class.getSimpleName();
	private final Context mContext;
	
	private boolean mIsGPSEnabled;
	private boolean mIsNetworkEnabled;
	private boolean mCanGetLocation;
	
	private Location mLocation;
	private double mLatitude;
	private double mLongitude;
	
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
	
	protected LocationManager mLocationManager;
	
	public GPSTracker(Context context) {
		mContext = context;
		getLocation();
	}
	
	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
    public Location getLocation() {
        try {
            mLocationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);
 
            // getting GPS status
            mIsGPSEnabled = mLocationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
 
            // getting network status
            mIsNetworkEnabled = mLocationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
 
            if (!mIsGPSEnabled && !mIsNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.mCanGetLocation = true;
                // First get location from Network Provider
                if (mIsNetworkEnabled) {
                    mLocationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (mLocationManager != null) {
                        mLocation = mLocationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (mLocation != null) {
                            mLatitude = mLocation.getLatitude();
                            mLongitude = mLocation.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (mIsGPSEnabled) {
                    if (mLocation == null) {
                        mLocationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (mLocationManager != null) {
                            mLocation = mLocationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (mLocation != null) {
                                mLatitude = mLocation.getLatitude();
                                mLongitude = mLocation.getLongitude();
                            }
                        }
                    }
                }
            }
 
        } catch (Exception e) {
            e.printStackTrace();
        }
 
        return mLocation;
    }
     
    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS(){
        if(mLocationManager != null){
            mLocationManager.removeUpdates(GPSTracker.this);
        }       
    }
     
    /**
     * Function to get latitude
     * */
    public double getLatitude(){
        if(mLocation != null){
            mLatitude = mLocation.getLatitude();
        }
         
        // return latitude
        return mLatitude;
    }
     
    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        if(mLocation != null){
            mLongitude = mLocation.getLongitude();
        }
         
        // return longitude
        return mLongitude;
    }
     
    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        return mCanGetLocation;
    }
     
    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
      
        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");
  
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
  
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });
  
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });
  
        // Showing Alert Message
        alertDialog.show();
    }
    
    public String getCity() {
    	Geocoder gcd = new Geocoder(mContext, Locale.getDefault());
    	List<Address> addresses;
    	String cityName = "The Dark Void";
    	
		try {
			addresses = gcd.getFromLocation(getLatitude(), getLongitude(), 1);
	    	if (addresses.size() > 0) {
	    	    cityName = addresses.get(0).getLocality();
	    	}
		} catch (IOException e) {
			Log.d(TAG, "::getCity() -- ERROR: " + e.getMessage());
		}

    	return cityName;
    }

}
