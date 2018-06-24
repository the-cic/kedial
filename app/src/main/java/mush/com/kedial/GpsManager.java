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

import java.util.List;

/**
 * Created by mush on 24/06/2018.
 */
public class GpsManager implements GpsStatus.Listener, LocationListener {

    public interface GpsManagerListener {
        public void onGpsSpeed(Float metersPerSecond);
        public void onGpsOn();
        public void onGpsOff();
    }

    private static final int gpsMinTime = 500;
    private static final int gpsMinDistance = 0;

    private LocationManager locationManager;
    private GpsManagerListener listener;

    public void startListening(Activity activity) {
        System.out.println("Starting listening");

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
            System.out.println("checking self permission");
            if (ContextCompat.checkSelfPermission(activity.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                System.out.println("not granted, requesting permission");
                ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
                System.out.println("ok?");
            } else {
                System.out.println("granted");
            }

            if (bestProvider != null && bestProvider.length() > 0) {
                System.out.println("bestprovider: " + bestProvider);
                locationManager.requestLocationUpdates(bestProvider, gpsMinTime,
                        gpsMinDistance, this);
            }
            else {
                final List<String> providers = locationManager.getProviders(true);
                for (final String provider : providers)
                {
                    System.out.println("provider: " + provider);
                    locationManager.requestLocationUpdates(provider, gpsMinTime,
                            gpsMinDistance, this);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void stopListening() {
        System.out.println("Stopping listening");
        try
        {
            if (locationManager != null) {
                System.out.println("remove updates");
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
        System.out.println("status changed: " + event);
    }

    @Override
    public void onLocationChanged(Location location) {
        System.out.println("location changed: " + location);

        listener.onGpsSpeed(location.getSpeed());

//        listener.onGpsSpeed((float) (Math.random() * 30));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        System.out.println("location status changed: " + status);
    }

    @Override
    public void onProviderEnabled(String provider) {
        System.out.println("location on provider enabled: " + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        System.out.println("location on provider disabled: " + provider);
    }

    public void setListener(GpsManagerListener listener) {
        this.listener = listener;
    }
}
