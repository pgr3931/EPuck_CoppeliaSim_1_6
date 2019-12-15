package solution.bangbang;

import at.fhv.dgr1992.differentialWheels.CameraImage;
import solution.common.DoorController;

/**
 * Bang Bang solution to crash into a door
 */
public class BangBangDoorController extends DoorController {

    private void startBehavior() {
        startEPuck();
        epuck.enableCamera();
        boolean doorFound = false;
        while (epuck.isConnected()) {
            try {
                CameraImage image = epuck.getCameraImage();
                // Finds the door if it isn't already in the robot's field of view
                if (!doorFound)
                    doorFound = findDoor(image);

                if (doorFound) {
                    // Calculates the center of the door
                    int center = getCenter(image);

                    // Steers depending on the door's center-position
                    if (center < 31)
                        turnLeft();
                    else if (center > 33)
                        turnRight();
                    else
                        driveForward();
                }
            } catch (Exception e) {
                epuck.disconnect();
            }
        }
    }

    public static void main(String[] args) {
        BangBangDoorController controller = new BangBangDoorController();
        controller.startBehavior();
    }
}
