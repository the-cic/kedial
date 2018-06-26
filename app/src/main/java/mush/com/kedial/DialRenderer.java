package mush.com.kedial;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Log;

/**
 * Created by mush on 22/06/2018.
 */
public class DialRenderer {

    private final static double ANGLE_0 = 1.25 * Math.PI;
    private final static double ANGLE_100 = 0.5 * Math.PI;
    private final static double ANGLE_200 = -1.25 * Math.PI;
    private final static double ANGLE_SPAN_100 = ANGLE_100 - ANGLE_0;

    private final static int textSize = 30;
    private final static int lcdTextSize = 40;

    private Paint bgPaint;
    private Paint hilightPaint;
    private Paint textPaint;
    private Paint linePaint;
    private Paint needlePaint;
    private Paint needleHeadPaint;
    private Paint lcdBgPaint;
    private Paint lcdTextPaint;
    private Paint barPaint;
    private Paint topBarPaint;

    private double inputValue;
    private double displayValue;
    private double secondsSinceLcdUpdate;

    private final PointF center = new PointF(0, 0);
    private final float radius = 220;

    public DialRenderer() {
        bgPaint = fillPaint(0xff333333);

        linePaint = strokePaint(Color.WHITE);
        hilightPaint = strokePaint(0xff808080);
        hilightPaint.setStrokeWidth(1);

        needlePaint = strokePaint(0xffff8010);
        needlePaint.setStrokeWidth(10);

        barPaint = strokePaint(0xffff8010);
        barPaint.setStrokeWidth(4);

        topBarPaint = strokePaint(0xffff8010);
        topBarPaint.setStrokeWidth(2);

        needleHeadPaint = fillPaint(0xff151515);

        textPaint = fillPaint(Color.WHITE);
        Typeface fpsTypeface = Typeface.create("sans-serif", Typeface.BOLD);
        textPaint.setTextSize(textSize);
        textPaint.setTypeface(fpsTypeface);

        lcdTextPaint = new Paint(textPaint);
        lcdTextPaint.setColor(0xffff8010);
        lcdTextPaint.setTextSize(lcdTextSize);

        lcdBgPaint = fillPaint(0xff502000);

        inputValue = 0;
        displayValue = 0;
        secondsSinceLcdUpdate = 0;
    }

    public void update(double value, double secondsPerFrame) {
        inputValue = value;
        secondsSinceLcdUpdate += secondsPerFrame;

        if (inputValue == 0 || secondsSinceLcdUpdate > 0.25) {
            displayValue = inputValue;
            secondsSinceLcdUpdate = 0;
        }
    }

    public void draw(Canvas canvas) {
        DrawFilter filter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG);
        canvas.setDrawFilter(filter);

        int sw = canvas.getWidth();
        int sh = canvas.getHeight();

        PointF screenCenter = new PointF(sw / 2, sh / 2);
        int screenRadius = (int) (Math.min(sw, sh) * 0.4);

        float screenScale = screenRadius / radius;

        canvas.save();

        canvas.translate(screenCenter.x, screenCenter.y);
        canvas.scale(screenScale, screenScale);

        drawFrame(canvas);
        drawTicks(canvas);
        drawLcd(canvas);
        drawEnergyGraph(canvas);
        drawNeedle(canvas);

        canvas.restore();
    }

    private void drawFrame(Canvas canvas) {
        canvas.drawCircle(center.x, center.y, (float) (radius * 1.05), bgPaint);
        RectF oval = new RectF((float)(center.x - radius), (float)(center.y - radius), (float)(center.x + radius), (float)(center.y + radius));
        canvas.drawArc(oval, 180 - 45, 270, false, linePaint);
    }

    private void drawLcd(Canvas canvas) {
        double lcdW = 0.2;
        double lcdT = 0.6 - 0.3;
        double textT = 0.8 - 0.3;
        double lcdB = 0.85 - 0.3;
        canvas.drawRect(center.x - (float) (radius * lcdW), center.y + (float) (radius * lcdT), center.x + (float) (radius * lcdW), center.y + (float) (radius * lcdB), lcdBgPaint);

        int speed = (int) displayValue;
        String speedText = "" + speed;
        canvas.drawText(speedText, center.x - (float) (speedText.length() - 1.2) * (lcdTextSize / 2), (float) (center.y + radius * textT), lcdTextPaint);
    }

    private void drawEnergyGraph(Canvas canvas) {
        float x0 = center.x - radius * 0.2f;
        float y0 = center.y + radius * 0.95f;

        double valueIndex = inputValue / 10;
        double valueRemainder = valueIndex % 1.0;

        for (int i = 1; i <= 15; i++) {
            // 35
            // 10 : 1
            // 20 : 2
            // 30 : 3 = 3 * 1
            // 4 : 2 = 4 * 0.5, 0.5 = (35 - 30) / 10

            if (valueIndex > i -1) {
                int barCount = valueIndex > i
                        ? i
                        : (int)(i * valueRemainder) ;

                float x = x0 + (i - 1) * 6;

                for (int j = 0; j < barCount; j++) {
                    boolean top = (j == i - 1);
                    float y = y0 - j * 5;
                    if (top) {
                        y += 1f;
                    }
                    canvas.drawLine(x, y, x + 5, y, top ? topBarPaint : barPaint);
                }
            }
        }
    }

    private void drawNeedle(Canvas canvas) {
        double needleAngle = angleForValue(inputValue);
        double headRadius = radius * 0.2;
        PointF needleTip = point(center, needleAngle, radius * 0.95);

        canvas.drawLine(center.x, center.y, needleTip.x, needleTip.y, needlePaint);
        canvas.drawCircle(center.x, center.y, (float) headRadius, needleHeadPaint);

        headRadius *= 0.95;
        RectF oval = new RectF((float)(center.x - headRadius), (float)(center.y - headRadius), (float)(center.x + headRadius), (float)(center.y + headRadius));
        canvas.drawArc(oval, 280, 60, false, hilightPaint);
    }

    private void drawTicks(Canvas canvas) {
        drawTickForValue(canvas, center, 0, radius, 0.6, null, 0.8);
        drawTickForRawValue(canvas, center, 5, radius);
        drawTickForRawValue(canvas, center, 15, radius);
        for (int i = 10; i <= 200; i += 10) {
            drawTickForRawValue(canvas, center, i, radius);
        }
    }

    private void drawTickForRawValue(Canvas canvas, PointF center, int rawValue, double radius) {
        double length = 1.05;
        if (rawValue % 20 == 0) {
            length = 1.1;
        }

        double square = squareValue(rawValue);
        if (square < 200) {
            String sqText = rawValue == 0 || square > 10 ? "" + rawValue : null;
            drawTickForValue(canvas, center, square, radius, 1 - (length - 1), sqText, 0.8);
        }
    }

    private void drawTickForValue(Canvas canvas, PointF center, double value, double radius, double length, String text, double textFactor) {
        double angle = angleForValue(value);
        drawTick(canvas, center, angle, radius, length, text, textFactor);
    }

    private void drawTick(Canvas canvas, PointF center, double angle, double radius, double lengthFactor, String text, double textFactor) {
        PointF p1 = point(center, angle, radius);
        PointF p2 = point(center, angle, radius * lengthFactor);
        canvas.drawLine(p1.x, p1.y, p2.x, p2.y, linePaint);
        if (text != null) {
            PointF p3 = point(center, angle, radius * textFactor);
            canvas.drawText(text, p3.x - text.length() * (textSize / 4), p3.y + (textSize / 2), textPaint);
        }
    }

    private double angleForValue(double value) {
        double percent = value / 100.0;
        return ANGLE_0 + percent * ANGLE_SPAN_100;
    }

    private double squareValue(double linearValue) {
        // 0: 0 = 0, 50 = ?
        // 50: 0 -> 0, 50 -> 50, 70 -> 100, 100 -> 140
        // v: v * (i / v)^2
        double baseValue = inputValue > 3 ? inputValue : 3;
        double value = baseValue * Math.pow(linearValue / baseValue, 2);
        return value;
    }

    private PointF point(PointF center, double angle, double radius) {
        return new PointF((float)(center.x + Math.cos(angle) * radius), (float)(center.y - Math.sin(angle) * radius));
    }

    private Paint strokePaint(int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        return paint;
    }

    private Paint fillPaint(int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        return paint;
    }

}
