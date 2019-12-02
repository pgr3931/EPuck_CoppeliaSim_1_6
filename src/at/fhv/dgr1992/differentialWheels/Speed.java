package at.fhv.dgr1992.differentialWheels;

/**
 * Speed holds the speed of the left and the right motor.
 */
public class Speed {
    private double _left;
    private double _right;

    /**
     * Construct new Speed
     * @param left speed in rad/sec of left motor
     * @param right speed in rad/sec of left motor
     */
    public Speed(double left, double right){
        _left = left;
        _right = right;
    }

    /**
     * Get the speed of the right motor.
     * @return Speed of the right motor in rad/sec
     */
    public double getRight(){
        return _right;
    }

    /**
     * Get the speed of the left value.
     * @return Speed of the left motor in rad/sec
     */
    public double getLeft(){
        return _left;
    }
}
