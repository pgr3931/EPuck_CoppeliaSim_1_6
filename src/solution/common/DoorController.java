package solution.common;

import at.fhv.dgr1992.differentialWheels.CameraImage;
import at.fhv.dgr1992.exceptions.RobotFunctionCallException;
import at.fhv.dgr1992.exceptions.VelocityLimitException;

/**
 * Functionalities for all DoorControllers
 */
public class DoorController extends BasicRobot {

    /**
     * Turns the robot clockwise until the door is found
     */
    protected boolean findDoor(CameraImage image) throws RobotFunctionCallException, VelocityLimitException {
        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 64; j++) {
                if (image.getPixel(j, i).getRed() == 0) {
                    stopDriving();
                    return true;
                }
            }
        }
        turnRight();
        return false;
    }

    /**
     * Returns the left and right side of the door as pixel positions
     */
    protected int[] getLeftRightPos(CameraImage image) {
        int[] result = new int[2];
        boolean found = false;
        int y = 0; // y-position of the first door-pixel

        // Finds the left side of the door and saves the height of y
        for (int i = 0; i < 64 && !found; i++) {
            for (int j = 0; j < 64 && !found; j++) {
                if (image.getPixel(j, i).getRed() == 0) {
                    y = i;
                    result[0] = j;
                    found = true;
                }
            }
        }

        // Finds the right side of the door
        for (int i = 63; i >= 0; i--) {
            if (image.getPixel(i, y).getRed() == 0) {
                result[1] = i;
                break;
            }
        }

        return result;
    }

    protected int getCenter(CameraImage image){
        int[] leftRightPos = getLeftRightPos(image);
        return leftRightPos[0] + (leftRightPos[1] - leftRightPos[0]) / 2;
    }
}
