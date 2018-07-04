package mush.com.kedial;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.List;

/**
 * Created by mush on 24/06/2018.
 */
public class GpsManager implements GpsStatus.Listener, LocationListener {

    private static final String TAG = GpsManager.class.getSimpleName();

    public interface GpsManagerDelegate {
        public void onGpsSpeed(Float metersPerSecond);
        public void onGpsOn();
        public void onGpsOff();
    }

    private static final int gpsMinTime = 500;
    private static final int gpsMinDistance = 0;

    private LocationManager locationManager;
    private GpsManagerDelegate delegate;

    public void startListening(Activity activity) {
        Log.i(TAG, "Starting listening");

        if (locationManager == null) {
            locationManager = (LocationManager)activity.getSystemService(Context.LOCATION_SERVICE);
        }

        final Criteria criteria = new Criteria();

        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setSpeedRequired(true);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        final String bestProvider = locationManager.getBestProvider(criteria, true);

        try {
            Log.i(TAG, "checking self permission");
            if (ContextCompat.checkSelfPermission(activity.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                Log.i(TAG, "not granted, requesting permission");
                ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
                Log.i(TAG, "ok?");
            } else {
                Log.i(TAG, "granted");
            }

            if (bestProvider != null && bestProvider.length() > 0) {
                Log.i(TAG, "best provider: " + bestProvider);
                locationManager.requestLocationUpdates(bestProvider, gpsMinTime,
                        gpsMinDistance, this);
            }
            else {
                final List<String> providers = locationManager.getProviders(true);
                for (final String provider : providers)
                {
                    Log.i(TAG, "provider: " + provider);
                    locationManager.requestLocationUpdates(provider, gpsMinTime,
                            gpsMinDistance, this);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void stopListening() {
        Log.i(TAG, "Stopping listening");
        try
        {
            if (locationManager != null) {
                Log.i(TAG, "remove updates");
                locationManager.removeUpdates(this);
            }
            locationManager = null;
        }
        catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }

    public boolean checkGpsEnabled() {
        if (locationManager != null) {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } else {
            return false;
        }
    }

    @Override
    public void onGpsStatusChanged(int event) {
        Log.i(TAG, "status changed: " + event);
        switch (event) {
            case GpsStatus.GPS_EVENT_STARTED:
                delegate.onGpsOn();
                break;
            case GpsStatus.GPS_EVENT_STOPPED:
                delegate.onGpsOff();
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "location changed: " + location);

        delegate.onGpsSpeed(location.getSpeed());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i(TAG, "location status changed: " + status);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i(TAG, "location on provider enabled: " + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i(TAG, "location on provider disabled: " + provider);
    }

    public void setDelegate(GpsManagerDelegate delegate) {
        this.delegate = delegate;
    }
}
