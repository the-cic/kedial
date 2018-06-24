package mush.com.kedial.touch;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;

import mush.com.kedial.car.CarSimulation;

/**
 * Created by mush on 24/06/2018.
 */
public class TouchControls {

    public TouchControl accelerate;
    public TouchControl brake;
    public ToggleTouchControl gps;
    public ToggleTouchControl fps;

    private List<TouchControl> controls;

    private CarSimulation carSimulation;

    public interface GpsToggleListener {
        public void gpsToggled(boolean value);
    }

    public interface FpsToggleListener {
        public void fpsToggled(boolean value);
    }

    private GpsToggleListener gpsToggleListener;
    private FpsToggleListener fpsToggleListener;

    public TouchControls(CarSimulation carSimulation) {
        controls = new ArrayList<>();

        this.carSimulation = carSimulation;
        setup();
    }

    public void addControl(TouchControl control){
        controls.add(control);
    }

    public boolean onTouchEvent(MotionEvent event) {
        try {
            for (TouchControl control : controls) {
                if (control.onTouchEvent(event)) {
                    return true;
                }
            }
            return false;
        } finally {
            processTouch();
        }
    }

    public void draw(Canvas canvas) {
        for (TouchControl control : controls) {
            control.draw(canvas);
        }
    }

    private void processTouch() {
        if (accelerate.isPressed()) {
            carSimulation.accelerate();
        } else if (brake.isPressed()) {
            carSimulation.brake();
        } else {
            carSimulation.coast();
        }
        if (gps.isChanged()) {
            System.out.println("Changed to: " + gps.isPressed());
            if (gpsToggleListener != null) {
                gpsToggleListener.gpsToggled(gps.isPressed());
            }
        }
        if (fps.isChanged()) {
            if (fpsToggleListener != null) {
                fpsToggleListener.fpsToggled(fps.isPressed());
            }
        }
    }

    private void setup() {
        accelerate = new TouchControl(null);
        brake = new TouchControl(null);
        gps = new ToggleTouchControl(null);
        fps = new ToggleTouchControl(null);
        addControl(accelerate);
        addControl(brake);
        addControl(gps);
        addControl(fps);
    }

    public void onResize(int width, int height) {
        float button = Math.min(width * 0.2f, height * 0.2f);
        float margin = Math.min(width * 0.05f, height * 0.05f);
        float middle = height * 0.5f;

        accelerate.setArea(new RectF(width - button - margin, middle - margin * 0.5f - button, width - margin, middle - margin * 0.5f));
        brake.setArea(new RectF(width - button - margin, middle + margin * 0.5f, width - margin, middle + margin * 0.5f + button));

        gps.setArea(new RectF(margin, middle - button * 0.5f, margin + button, middle + button * 0.5f));

        fps.setArea(new RectF(margin, margin, margin + button, margin + button * 0.25f));
    }

    public void setGpsToggleListener(GpsToggleListener listener) {
        this.gpsToggleListener = listener;
    }

    public void setFpsToggleListener(FpsToggleListener listener) {
        this.fpsToggleListener = listener;
    }

    public void setGpsOn(boolean isOn) {
        accelerate.setVisible(!isOn);
        brake.setVisible(!isOn);
    }
}
