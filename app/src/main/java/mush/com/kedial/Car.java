package mush.com.kedial;

/**
 * Created by mush on 23/06/2018.
 */
public class Car {

    private static final double ACCELERATION_MAX_FORCE = 30;
    private static final double BRAKE_FORCE = 30;
    private static final double[] GEAR_MAX_VELOCITY = new double[]{20, 40, 60, 85, 130};
    private static final double MAX_VELOCITY = 150;
    private static final double GEAR_SHIFT_FACTOR = 0.85;

    private double velocity;
    private double acceleration;

    public Car() {
        velocity = 0;
    }

    public double getVelocity() {
        return velocity;
    }

    public void accelerate(double seconds) {
        /*
        double topSpeed = getTopSpeed();
        double remainingSpeed = topSpeed - velocity;
        if (remainingSpeed < 0){
            remainingSpeed = 0;
        }
        double targetAcceleration = (remainingSpeed / topSpeed) * ACCELERATION_MAX_FORCE;
        */
//        double remainingVelocity = MAX_VELOCITY - velocity;
        double resistance = velocity / MAX_VELOCITY; // 0 .. 1
        double targetAcceleration = ACCELERATION_MAX_FORCE * (1 - resistance);
        acceleration += (targetAcceleration - acceleration) * 0.05;

        velocity += acceleration * seconds;

        if (velocity < 0) {
            velocity = 0;
        }
    }

    public void brake(double seconds) {
        double targetAcceleration = -BRAKE_FORCE;
        acceleration += (targetAcceleration - acceleration) * 0.5;

        velocity += acceleration * seconds;

        if (velocity < 0) {
            velocity = 0;
        }
    }

    public void coast(double seconds) {
        acceleration *= 0.95;

        velocity += acceleration * seconds;

        if (velocity < 0) {
            velocity = 0;
        }
    }

    private double getTopSpeed() {
        for (double gearTopSpeed : GEAR_MAX_VELOCITY) {
            double shiftSpeed = gearTopSpeed * GEAR_SHIFT_FACTOR;
            if (velocity < shiftSpeed) {
                return gearTopSpeed;
            }
        }
        return GEAR_MAX_VELOCITY[GEAR_MAX_VELOCITY.length - 1];
    }
}
