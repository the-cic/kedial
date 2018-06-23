package mush.com.kedial;

import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by mush on 22/06/2018.
 */
public class DrawThread extends Thread {

    private static final long NANOS_PER_MILLISECOND = 1000000;
    private static final long NANOS_PER_SECOND = 1000 * NANOS_PER_MILLISECOND;

    private SurfaceHolder surfaceHolder;
    private MainSurfaceView surfaceView;
    private boolean running;
    private int targetFps;
    private long targetNanosPerFrame;
    private double averageFps;

    public DrawThread(SurfaceHolder holder, MainSurfaceView view) {
        super();
        surfaceHolder = holder;
        surfaceView = view;
        setTargetFps(30);
    }

    @Override
    public void run() {
        System.out.println("thread run started");
        running = true;

        long frameBeginTime;
        long nanosSinceLastUpdate;
        long lastFrameBeginTime = System.nanoTime();
        long remainingFrameNanos;
        long waitMillis;

        int framesCount = 0;
        long framesTime = 0;

        while (running) {
            // 1. how much time since last updateAndDraw?
            frameBeginTime = System.nanoTime();
            nanosSinceLastUpdate = frameBeginTime - lastFrameBeginTime;
            lastFrameBeginTime = frameBeginTime;

            // 2. updateAndDraw with time since last updateAndDraw
            updateAndDraw(nanosSinceLastUpdate);

            // 3. how much time is left to wait
            remainingFrameNanos = targetNanosPerFrame - (System.nanoTime() - frameBeginTime);

            waitMillis = remainingFrameNanos > 0
                    ? remainingFrameNanos / NANOS_PER_MILLISECOND
                    : 1;

            // 4. Wait
            try {
                Thread.sleep(waitMillis);
            } catch (InterruptedException e) {
                System.out.println("Interrupted");
            }

            // Calculate average FPS every full second
            framesCount++;
            framesTime += nanosSinceLastUpdate;

            if (framesCount >= targetFps) {
                averageFps = NANOS_PER_SECOND / (framesTime / framesCount);
                framesCount = 0;
                framesTime = 0;
            }
        }

        System.out.println("thread run finished");
    }

    public void stopRunning() {
        running = false;
        this.interrupt();
    }

    private void updateAndDraw(long nanosSinceLastUpdate) {
        Canvas canvas = null;
        double secondsPerFrame = (double) nanosSinceLastUpdate / NANOS_PER_SECOND;

        try {
            canvas = this.surfaceHolder.lockCanvas();
            synchronized (surfaceHolder) {
                //this.gameContentView.update(elapsedSeconds);
                //this.gameContentView.draw(canvas);
                this.surfaceView.update(secondsPerFrame);
                this.surfaceView.draw(canvas);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (canvas != null) {
                try {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setTargetFps(int fps) {
        targetFps = fps;
        targetNanosPerFrame = NANOS_PER_SECOND / targetFps;
    }

    public double getAverageFps() {
        return averageFps;
    }

    public double getAverageFpsDropPercent() {
        // 50 = 60 - 10
        // 20 = 60 - 40
        // 1 = 60 - 59
        double dropFps = targetFps - averageFps;
        return dropFps / targetFps;
    }
}
