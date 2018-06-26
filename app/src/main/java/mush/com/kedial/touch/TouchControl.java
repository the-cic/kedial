package mush.com.kedial.touch;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;

import java.util.Collection;

/**
 * Created by mush on 24/06/2018.
 */
public class TouchControl {

    public interface TouchControlDelegate {
        public void onPress(TouchControl control);
    }

    private RectF area;
    private boolean pressed;
    private boolean visible;
    private Paint paint;
    private TouchControlDelegate delegate;

    public TouchControl(RectF area) {
        this.area = area;
        visible = true;
        pressed = false;

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
    }

    public void setDelegate(TouchControlDelegate delegate) {
        this.delegate = delegate;
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean wasPressed = pressed;

        try {
            if (area != null && visible && area.contains(event.getX(), event.getY())) {

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    pressed = false;
                } else {
                    pressed = true;
                }

                return true;
            } else {
                pressed = false;
                return false;
            }
        } finally {
            if (delegate != null && pressed != wasPressed) {
                if (pressed) {
                    delegate.onPress(this);
                }
            }
        }
    }

    public void setArea(RectF area) {
        this.area = area;
    }

    public RectF getArea() {
        return area;
    }

    public boolean isPressed() {
        return pressed;
    }

    public void setVisible(boolean visible1){
        this.visible = visible1;
    }

    public void draw(Canvas canvas) {
        if (area != null && visible) {
            if (isPressed()) {
                paint.setColor(Color.WHITE);
            } else {
                paint.setColor(Color.GRAY);
            }
            canvas.drawRoundRect(area, 10, 10, paint);
        }
    }

}
