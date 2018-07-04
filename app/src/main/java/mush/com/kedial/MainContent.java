package mush.com.kedial;

import android.app.Activity;
import android.util.Log;

import mush.com.kedial.car.Car;
import mush.com.kedial.car.CarSimulation;
import mush.com.kedial.car.GpsCar;

/**
 * Created by mush on 26/06/2018.
 */
public class MainContent implements GpsManager.GpsManagerDelegate {

    private static MainContent instance;

    private GpsManager gpsManager;

    public int targetFps;
    private GpsCar gpsCar;
    private CarSimulation carSimulation;
    private Car car;
    private boolean gpsToggled;
    private boolean gpsEnabled;
    private boolean gpsActive;

    private MainContent() {
        Log.i("content", "new MainContent");

        gpsManager = new GpsManager();
        gpsManager.setDelegate(this);

        gpsCar = new GpsCar();
        carSimulation = new CarSimulation();

        targetFps = 30;

        gpsDisable();
    }

    public static MainContent get() {
        if (instance == null) {
            instance = new MainContent();
        }
        return instance;
    }

    public void toggleGps(Activity activity) {
        gpsToggled = !gpsToggled;
        if (gpsToggled) {
            gpsManager.startListening(activity);
            Log.i("content", "gps enabled: " + gpsManager.checkGpsEnabled());
            gpsEnable();
        } else {
            gpsManager.stopListening();
            gpsDisable();
        }
    }

    public boolean isGpsToggled() {
        return gpsToggled;
    }

    public boolean isGpsActive() {
        return gpsActive;
    }

    public boolean isGpsEnabled() {
        return gpsEnabled;
    }

    private void gpsEnable() {
        car = gpsCar;
        gpsCar.reset();
        gpsEnabled = true;
    }

    private void gpsDisable() {
        car = carSimulation;
        gpsEnabled = false;
    }

    @Override
    public void onGpsSpeed(Float metersPerSecond) {
        gpsCar.setSpeedFromGps(metersPerSecond);
    }

    @Override
    public void onGpsOn() {
        gpsActive = true;
    }

    @Override
    public void onGpsOff() {
        gpsActive = false;
    }

    public CarSimulation getCarSimulation() {
        return carSimulation;
    }

    public Car getCar() {
        return car;
    }
}
