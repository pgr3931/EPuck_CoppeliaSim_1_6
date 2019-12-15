package solution.proportional;

import at.fhv.dgr1992.differentialWheels.CameraImage;
import solution.common.DoorController;

/**
 * Proportional solution to stop right before a door
 */
public class ProportionalDoorProxController extends DoorController {

    private void startBehavior() {
        startEPuck();
        epuck.enableCamera();
        boolean doorFound = false;
        boolean doorInFront = false;
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

                    // Changes the state to doorInFront
                    epuck.senseAllTogether();
                    double[] distVector = epuck.getProximitySensorValues();
                    if (distVector[FRONT_RIGHT] < 0.05 || distVector[FRONT_LEFT] < 0.05)
                        doorInFront = true;

                    // Same as in the DoorController
                    if (!doorInFront)
                        setSpeeds(leftSpeed, rightSpeed);
                    else
                        // If the door is in front of the robot, the speeds decelerate until a proximity of 0.03 is reached
                        setSpeeds(leftSpeed * (distVector[FRONT_LEFT] - 0.03), rightSpeed * (distVector[FRONT_RIGHT] - 0.03));
                }

                Thread.sleep(20);
            } catch (Exception e) {
                epuck.disconnect();
            }
        }
    }

    public static void main(String[] args) {
        ProportionalDoorProxController controller = new ProportionalDoorProxController();
        controller.startBehavior();
    }

}
