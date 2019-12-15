package solution.common;

import at.fhv.dgr1992.differentialWheels.Speed;
import at.fhv.dgr1992.ePuck.ePuckVRep.EPuckVRep;
import at.fhv.dgr1992.exceptions.RobotFunctionCallException;
import at.fhv.dgr1992.exceptions.VelocityLimitException;

/**
 * Basic robot functions class every controller derives from
 */
public class BasicRobot {
    protected final double MIN_SPEED = 0; // min. motor speed
    protected final double MAX_SPEED = 120 * Math.PI / 180; // max. motor speed

    //Sensor positions
    protected final int LEFT = 0;
    protected final int LEFT_FRONT = 1;
    protected final int FRONT_LEFT = 2;
    protected final int FRONT_RIGHT = 3;
    protected final int RIGHT_FRONT = 4;
    protected final int RIGHT = 5;
    protected final int BACK_RIGHT = 6;
    protected final int BACK_LEFT = 7;

    protected EPuckVRep epuck; // robot

    /**
     * Starts and connects the robot and enables all sensors
     */
    protected void startEPuck() {
        try {
            epuck = new EPuckVRep("ePuck", "127.0.0.1", 19999, false);
            if (!epuck.isConnected()) {
                epuck.connect();
            }
            epuck.enableAllSensors();
            epuck.setSenseAllTogether();
            epuck.setMotorSpeeds(new Speed(0, 0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Robot turns clockwise
     */
    protected void turnRight() throws RobotFunctionCallException, VelocityLimitException {
        epuck.setMotorSpeeds(new Speed(MAX_SPEED, MIN_SPEED));
    }

    /**
     * Robot turns counterclockwise
     */
    protected void turnLeft() throws RobotFunctionCallException, VelocityLimitException {
        epuck.setMotorSpeeds(new Speed(MIN_SPEED, MAX_SPEED));
    }

    /**
     * Robot drives forward
     */
    protected void driveForward() throws RobotFunctionCallException, VelocityLimitException {
        epuck.setMotorSpeeds(new Speed(MAX_SPEED, MAX_SPEED));
    }

    /**
     * Robot stops driving
     */
    protected void stopDriving() throws RobotFunctionCallException, VelocityLimitException {
        epuck.setMotorSpeeds(new Speed(MIN_SPEED, MIN_SPEED));
    }

    /**
     * Sets the speed for the wheels
     */
    protected void setSpeeds(double left, double right) throws RobotFunctionCallException, VelocityLimitException {
        epuck.setMotorSpeeds(new Speed(left, right));
    }
}
