package mush.com.kedial;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import mush.com.kedial.car.Car;
import mush.com.kedial.car.CarSimulation;
import mush.com.kedial.car.GpsCar;
import mush.com.kedial.touch.TouchControls;

/**
 * Created by mush on 22/06/2018.
 */
public class MainSurfaceView extends SurfaceView implements SurfaceHolder.Callback, GpsManager.GpsManagerListener, TouchControls.FpsToggleListener {

    private Paint fpsPaint;
    private DrawThread drawThread;
    private DialRenderer dialRenderer;
    private GpsCar gpsCar;
    private CarSimulation carSimulation;
    private Car car;
    private TouchControls touchControls;

    public MainSurfaceView(MainActivity mainActivity) {
        super(mainActivity);

        System.out.println("Main surface view");
        getHolder().addCallback(this);

        setFocusable(true);

        dialRenderer = new DialRenderer();
        gpsCar = new GpsCar();
        carSimulation = new CarSimulation();
        touchControls = new TouchControls(carSimulation);
        touchControls.setGpsToggleListener(mainActivity);
        touchControls.setFpsToggleListener(this);

        onGpsOff();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        System.out.println("Surface Created");
        if (drawThread == null) {
            drawThread = new DrawThread(getHolder(), this);
            drawThread.setName("Draw Thread");
            drawThread.start();
//            drawThread.setTargetFps(60);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        touchControls.onResize(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        System.out.println("Surface Destroyed");

        boolean retry = true;
        while (retry) {
            try {
                System.out.println("set running false");
                drawThread.stopRunning();
                System.out.println("join");
                drawThread.join();
                retry = false;
                System.out.println("running false and thread joined");

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        drawThread = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean b = super.onTouchEvent(event);

        touchControls.onTouchEvent(event);

        return true;
    }

    @Override
    public void draw(Canvas canvas) {
        if (canvas == null) {
            System.out.println("Canvas is null!");
            return;
        }
        super.draw(canvas);

        dialRenderer.draw(canvas);
        touchControls.draw(canvas);

        drawFps(canvas);
    }

    public void update(double secondsPerFrame) {
        car.update(secondsPerFrame);
        dialRenderer.update(car.getSpeed(), secondsPerFrame);
    }

    private void drawFps(Canvas canvas) {
        double fpsDropPerc = drawThread.getAverageFpsDropPercent();
        Paint paint = getFpsPaint();

        if (fpsDropPerc > 0.6) {
            paint.setColor(Color.RED);
        } else if (fpsDropPerc > 0.3) {
            paint.setColor(Color.YELLOW);
        } else {
            paint.setColor(Color.GREEN);
        }

//        canvas.drawText((int)drawThread.getAverageFps() + " FPS, drop: " + fpsDropPerc, 10, 20, paint);
        canvas.drawText((int) drawThread.getAverageFps() + " FPS", 10, 20, paint);
   }

    private Paint getFpsPaint() {
        if (fpsPaint == null) {
            Typeface fpsTypeface = Typeface.create("sans-serif", Typeface.BOLD);
            fpsPaint = new Paint();

            fpsPaint.setColor(Color.RED);
            fpsPaint.setStyle(Paint.Style.FILL);
            fpsPaint.setTextSize(20);
            fpsPaint.setTypeface(fpsTypeface);
        }

        return fpsPaint;
    }

    @Override
    public void onGpsSpeed(Float metersPerSecond) {
        gpsCar.setSpeedFromGps(metersPerSecond);
    }

    @Override
    public void onGpsOn() {
        car = gpsCar;
        gpsCar.reset();
        touchControls.setGpsOn(true);
    }

    @Override
    public void onGpsOff() {
        car = carSimulation;
        touchControls.setGpsOn(false);
    }

    @Override
    public void fpsToggled(boolean value) {
        drawThread.setTargetFps(value ? 60 : 30);
    }
}
