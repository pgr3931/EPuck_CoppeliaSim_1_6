package at.fhv.dgr1992.differentialWheels;

/**
 * Acceleration of the robot
 */
public class Acceleration {
    private double[] _acceleration;

    /**
     * Construct new acceleration with the given array
     * @param acceleration Array with the accelerations
     */
    public Acceleration(double[] acceleration){
        _acceleration = acceleration;
    }

    /**
     * Get the acceleration on the x-axis.
     * @return x-axis acceleration
     */
    public double getX(){
        return _acceleration[0];
    }

    /**
     * Get the acceleration on the y-axis
     * @return y-axis acceleration
     */
    public double getY(){
        return _acceleration[1];
    }

    /**
     * Get the acceleration on the z-axis
     * @return z-axis acceleration
     */
    public double getZ(){
        return _acceleration[2];
    }
}