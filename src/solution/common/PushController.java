package solution.common;

import at.fhv.dgr1992.differentialWheels.CameraImage;
import at.fhv.dgr1992.exceptions.CameraNotEnabledException;
import at.fhv.dgr1992.exceptions.RobotFunctionCallException;
import at.fhv.dgr1992.exceptions.VelocityLimitException;

/**
 * Functionality for both PushControllers
 */
public class PushController extends BasicRobot {

    /**
     * Turns the robot clockwise until the puck is found
     */
    protected boolean findPuck() throws CameraNotEnabledException, RobotFunctionCallException, VelocityLimitException {
        CameraImage image = epuck.getCameraImage();
        if (image.getPixel(32, 32).getBlue() > 100 && image.getPixel(32, 32).getRed() < 100) {
            stopDriving();
            return true;
        } else {
            turnRight();
            return false;
        }
    }
}
