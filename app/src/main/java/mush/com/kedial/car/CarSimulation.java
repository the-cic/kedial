package mush.com.kedial.car;

/**
 * Created by mush on 23/06/2018.
 */
public class CarSimulation implements Car {

    private static final double ACCELERATION_MAX_FORCE = 30;
    private static final double BRAKE_FORCE = 30;
    private static final double MAX_VELOCITY = 150;

    private double speed;
    private double acceleration;
    private int mode;

    public CarSimulation() {
        speed = 0;
        mode = 0;
    }

    @Override
    public double getSpeed() {
        return speed;
    }

    public void accelerate() {
        mode = 1;
    }

    public void brake() {
        mode = -1;
    }

    public void coast() {
        mode = 0;
    }

    @Override
    public void update(double seconds) {
        if (mode > 0) {
            accelerate(seconds);
        } else if (mode < 0) {
            brake(seconds);
        } else {
            coast(seconds);
        }
    }

    private void accelerate(double seconds) {
        double resistance = speed / MAX_VELOCITY;
        double targetAcceleration = ACCELERATION_MAX_FORCE * (1 - resistance);
        acceleration += (targetAcceleration - acceleration) * 0.05;

        speed += acceleration * seconds;

        if (speed < 0) {
            speed = 0;
        }
    }

    private void brake(double seconds) {
        double targetAcceleration = -BRAKE_FORCE;
        acceleration += (targetAcceleration - acceleration) * 0.5;

        speed += acceleration * seconds;

        if (speed < 0) {
            speed = 0;
        }
    }

    private void coast(double seconds) {
        acceleration *= 0.95;

        speed += acceleration * seconds;

        if (speed < 0) {
            speed = 0;
        }
    }

}
