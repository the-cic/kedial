package mush.com.kedial;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import mush.com.kedial.touch.TouchControl;
import mush.com.kedial.touch.TouchControls;

/**
 * Created by mush on 22/06/2018.
 */
public class MainSurfaceView extends SurfaceView implements SurfaceHolder.Callback, TouchControl.TouchControlDelegate, MainContent.MainContentDelegate {

    private Paint fpsPaint;
    private DrawThread drawThread;
    private DialRenderer dialRenderer;
    private TouchControls touchControls;
    private MainContent content;

    public MainSurfaceView(MainActivity mainActivity) {
        super(mainActivity);

        content = MainContent.get();

        Log.i("view", "new MainSurfaceView");
        getHolder().addCallback(this);

        setFocusable(true);

        dialRenderer = new DialRenderer();
        touchControls = new TouchControls(content.getCarSimulation());
        touchControls.setGpsToggleDelegate(content);
        touchControls.setFpsToggleDelegate(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i("view", "Surface Created");

        content.setDelegate(this);
        content.checkGpsToggled();

        if (drawThread == null) {
            drawThread = new DrawThread(getHolder(), this);
            drawThread.setName("Draw Thread");
            drawThread.start();
            drawThread.setTargetFps(content.targetFps);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i("view", "Surface Changed");
        touchControls.onResize(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i("view", "Surface Destroyed");

        content.setDelegate(null);

        boolean retry = true;
        while (retry) {
            try {
                Log.i("view", "set running false");
                drawThread.stopRunning();
                System.out.println("join");
                drawThread.join();
                retry = false;
                Log.i("view", "running false and thread joined");

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
            Log.i("view", "Canvas is null!");
            return;
        }
        super.draw(canvas);

        dialRenderer.draw(canvas);
        touchControls.draw(canvas);

        drawLabels(canvas);
    }

    public void update(double secondsPerFrame) {
        content.getCar().update(secondsPerFrame);
        dialRenderer.update(content.getCar().getSpeed(), secondsPerFrame);
    }

    private void drawLabels(Canvas canvas) {
        double fpsDropPerc = drawThread.getAverageFpsDropPercent();
        Paint paint = getFpsPaint();

        if (fpsDropPerc > 0.6) {
            paint.setColor(Color.RED);
        } else if (fpsDropPerc > 0.3) {
            paint.setColor(Color.YELLOW);
        } else {
            paint.setColor(Color.GREEN);
        }

        RectF area = touchControls.fps.getArea();
        canvas.drawText((int) drawThread.getAverageFps() + " FPS", area.left + 7, area.centerY() + 7, paint);

        if (content.isGpsToggled()) {
            paint.setColor(Color.WHITE);
        } else {
            paint.setColor(Color.GRAY);
        }

        area = touchControls.gps.getArea();
        canvas.drawText("GPS", area.centerX() - 20, area.centerY() + 7, paint);
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
    public void onPress(TouchControl control) {
        int fps = content.targetFps / 2;
        if (fps < 15) {
            fps = 60;
        }
        content.targetFps = fps;
        drawThread.setTargetFps(content.targetFps);
    }

    @Override
    public void onGpsEnabled(boolean enabled) {
        Log.i("view", "gps enabled:"+enabled);
        touchControls.setGpsOn(enabled);
    }

    @Override
    public void onGpsActive(boolean active) {
        Log.i("view", "gps active:"+active);
    }
}
