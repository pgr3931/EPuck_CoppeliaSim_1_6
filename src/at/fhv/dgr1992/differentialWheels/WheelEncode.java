package at.fhv.dgr1992.differentialWheels;

/**
 * Holds the value of the wheel encoding sensor
 */
public class WheelEncode {
    private double[] _wheelEncoding;

    /**
     * Construct new wheel encoder
     * @param wheelEncoding value of the wheel encoder
     */
    public WheelEncode(double[] wheelEncoding){
        _wheelEncoding = wheelEncoding;
    }

    /**
     * Get the value for the left wheel
     * @return Wheel encoding value in radians
     */
    public double getLeft(){
        return _wheelEncoding[0];
    }

    /**
     * Get the value for the right wheel
     * @return Wheel encoding value in radians
     */
    public double getRight(){
        return _wheelEncoding[1];
    }
}
