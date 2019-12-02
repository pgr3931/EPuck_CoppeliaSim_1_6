package at.fhv.dgr1992.differentialWheels;

/**
 * The position in x and y coordinates and its orientation.
 */
public class Pose {
    private double[] _pose;

    /**
     * Construct new pose
     * @param pose [0]=x, [1]=y and [2]=theta
     */
    public Pose(double[] pose){
        _pose = pose;
    }

    /**
     * Get the x coordinates
     * @return Position in the x coordinates
     */
    public double getX(){
        return _pose[0];
    }

    /**
     * Get the y coordinates
     * @return Position in the y coordinates
     */
    public double getY(){
        return _pose[1];
    }

    /**
     * Get the orientation
     * @return Orientation
     */
    public double getTheta(){
        return _pose[2];
    }
}
