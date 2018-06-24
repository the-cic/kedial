package mush.com.kedial;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import mush.com.kedial.touch.TouchControls;

/**
 * Created by mush on 22/06/2018.
 */
public class MainActivity extends Activity implements TouchControls.GpsToggleListener {

    private MainSurfaceView mainView;
    private GpsManager gpsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.println("on Create");

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        if (mainView == null) {
            mainView = new MainSurfaceView(this);
        }

        setContentView(mainView);

        if (gpsManager == null) {
            gpsManager = new GpsManager();
            gpsManager.setListener(mainView);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("onResume");
    }

    @Override
    public void gpsToggled(boolean toOn) {
        if (gpsManager != null) {
            if (toOn) {
                gpsManager.startListening(this);
                System.out.println("gps enabled: " + gpsManager.checkGpsEnabled());
                mainView.onGpsOn();
            } else {
                gpsManager.stopListening();
                mainView.onGpsOff();
            }
        }
    }
}
