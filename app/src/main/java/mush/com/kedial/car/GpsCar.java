package mush.com.kedial.car;

/**
 * Created by mush on 24/06/2018.
 */
public class GpsCar implements Car {

    private static final float MPS_TO_KPH = 3.6f;

    private double avgSpeed;
    private double speedSample;
    private double acceleration;

    public GpsCar() {
        reset();
    }

    @Override
    public double getSpeed() {
        return avgSpeed;
    }

    public void setSpeedFromGps(float metersPerSecond) {
        speedSample = metersPerSecond * MPS_TO_KPH;
        System.out.println("gps speed: " + metersPerSecond + " m/s = " + speedSample + " km/h");
    }

    public void reset() {
        speedSample = 0;
        avgSpeed = 0;
        acceleration = 0;
    }

    @Override
    public void update(double secondsPerFrame) {
        double impulse = (speedSample - avgSpeed) * 0.20;
        acceleration += impulse * secondsPerFrame;
        acceleration -= acceleration * 10.0 * secondsPerFrame;
        avgSpeed += acceleration * secondsPerFrame * 60;
        if (avgSpeed < 0) {
            avgSpeed = 0;
            acceleration = 0;
        }
    }

}
