package mush.com.kedial;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import mush.com.kedial.touch.TouchControls;

/**
 * Created by mush on 22/06/2018.
 */
public class MainActivity extends Activity {

    private MainSurfaceView mainView;

    public MainActivity() {
        super();
        Log.i("main", "new MainActivity");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("main", "onCreate");

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (mainView == null) {
            mainView = new MainSurfaceView(this);
        }

        setContentView(mainView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("main", "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("main", "onResume");
    }

}
