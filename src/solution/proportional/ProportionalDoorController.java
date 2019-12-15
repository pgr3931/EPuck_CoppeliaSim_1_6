package solution.proportional;

import at.fhv.dgr1992.differentialWheels.CameraImage;
import solution.common.DoorController;

/**
 * Proportional solution to crash into a door
 */
public class ProportionalDoorController extends DoorController {

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

                // Sets the motor speeds depending on the left and right edge of the door
                if (doorFound) {
                    int center = getCenter(image);
                    double leftSpeed = -0.00053 * Math.pow(center - 63, 2) + MAX_SPEED;
                    double rightSpeed = -0.00053 * Math.pow(center, 2) + MAX_SPEED;
                    setSpeeds(leftSpeed, rightSpeed);
                }

                Thread.sleep(20);
            } catch (Exception e) {
                epuck.disconnect();
            }
        }
    }

    public static void main(String[] args) {
        ProportionalDoorController controller = new ProportionalDoorController();
        controller.startBehavior();
    }
}
