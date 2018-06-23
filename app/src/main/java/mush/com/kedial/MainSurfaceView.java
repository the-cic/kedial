package mush.com.kedial;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.text.MessageFormat;

/**
 * Created by mush on 22/06/2018.
 */
public class MainSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private Paint fpsPaint;
    private DrawThread drawThread;
    private DialRenderer dialRenderer;
    private Car car;
    private int dir = 0;

    public MainSurfaceView(Context context) {
        super(context);

        System.out.println("Main surface view");
        getHolder().addCallback(this);

        setFocusable(true);

        dialRenderer = new DialRenderer();
        car = new Car();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        System.out.println("Surface Created");
        if (drawThread == null) {
            drawThread = new DrawThread(getHolder(), this);
            drawThread.setName("Draw Thread");
            drawThread.start();
            drawThread.setTargetFps(60);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

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

//        System.out.println(MessageFormat.format("on touch event:{0}", event));
        if (event.getX() > getWidth() / 2) {
//            System.out.println("Right");
            if (event.getAction() == MotionEvent.ACTION_UP) {
//                System.out.println("Finished");
                dir = 0;
            } else {
                if (event.getY() < getHeight() * 0.4) {
                    dir = 1;
                } else if (event.getY() > getHeight() * 0.6) {
                    dir = -1;
                } else {
                    dir = 0;
                }
            }
        }

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

        drawFps(canvas);
    }

    public void update(double secondsPerFrame) {
        if (dir > 0) {
            car.accelerate(secondsPerFrame);
//            if (car.getVelocity() > 120) {
//                dir = -1;
//            }
        } else if (dir < 0) {
            car.brake(secondsPerFrame);
//            if (car.getVelocity() < 1) {
//                dir = 1;
//            }
        } else {
            car.coast(secondsPerFrame);
        }
        dialRenderer.update(car.getVelocity(), secondsPerFrame);
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
        canvas.drawText((int)drawThread.getAverageFps() + " FPS", 10, 20, paint);
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
}
