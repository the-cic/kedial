package mush.com.kedial;

import android.util.Log;

import mush.com.kedial.car.Car;
import mush.com.kedial.car.CarSimulation;
import mush.com.kedial.car.GpsCar;
import mush.com.kedial.touch.TouchControl;

/**
 * Created by mush on 26/06/2018.
 */
public class MainContent implements TouchControl.TouchControlDelegate, GpsManager.GpsManagerListener {

    public interface MainContentDelegate {
        public void onGpsEnabled(boolean enabled);
        public void onGpsActive(boolean active);
    }

    private static MainContent instance;

    private GpsManager gpsManager;
    private MainActivity activity;
    private MainContentDelegate delegate;

    public int targetFps;
    private GpsCar gpsCar;
    private CarSimulation carSimulation;
    private Car car;
    private boolean gpsToggled;

    private MainContent() {
        Log.i("content", "new MainContent");

        gpsManager = new GpsManager();
        gpsManager.setListener(this);

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

    public void setActivity(MainActivity activity) {
        Log.i("content", "set activity:" + activity);
        this.activity = activity;
    }

    public void setDelegate(MainContentDelegate delegate) {
        Log.i("content", "set delegate:" + delegate);
        this.delegate = delegate;
    }

    @Override
    public void onPress(TouchControl control) {
        if (this.activity == null) {
            return;
        }
        gpsToggled = !gpsToggled;
        if (gpsToggled) {
            gpsManager.startListening(this.activity);
            Log.i("content", "gps enabled: " + gpsManager.checkGpsEnabled());
            gpsEnable();
        } else {
            gpsManager.stopListening();
            gpsDisable();
        }
    }

    public void checkGpsToggled() {
        if (delegate != null) {
            delegate.onGpsEnabled(gpsToggled);
        }
    }

    public boolean isGpsToggled() {
        return gpsToggled;
    }

    public void gpsEnable() {
        car = gpsCar;
        gpsCar.reset();
        if (delegate != null) {
            delegate.onGpsEnabled(true);
        }
    }

    public void gpsDisable() {
        car = carSimulation;
        if (delegate != null) {
            delegate.onGpsEnabled(false);
        }
    }

    @Override
    public void onGpsSpeed(Float metersPerSecond) {
        gpsCar.setSpeedFromGps(metersPerSecond);
    }

    @Override
    public void onGpsOn() {
        if (delegate != null) {
            delegate.onGpsActive(true);
        }
    }

    @Override
    public void onGpsOff() {
        if (delegate != null) {
            delegate.onGpsActive(false);
        }
    }

    public CarSimulation getCarSimulation() {
        return carSimulation;
    }

    public Car getCar() {
        return car;
    }
}
