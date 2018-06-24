package mush.com.kedial.touch;

import android.graphics.RectF;
import android.view.MotionEvent;

/**
 * Created by mush on 24/06/2018.
 */
public class ToggleTouchControl extends TouchControl {

    private static final long MIN_DELAY_NANOS = 1000000000/2; // 0.5 sec

    private boolean toggled;
    private boolean changed;
    private long lastToggleTime;

    public ToggleTouchControl(RectF area) {
        super(area);
        toggled = false;
        lastToggleTime = System.nanoTime();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean wasPressed = super.isPressed();
        changed = false;
        try {
            return super.onTouchEvent(event);
        } finally {
            long now = System.nanoTime();
            long delay = now - lastToggleTime;
            if (super.isPressed() && !wasPressed && delay > MIN_DELAY_NANOS) {
                toggled = !toggled;
                changed = true;
                lastToggleTime = now;
            }
        }
    }

    @Override
    public boolean isPressed() {
        return toggled;
    }

    public boolean isChanged() {
        return changed;
    }
}
