package solution.bangbang;

import at.fhv.dgr1992.differentialWheels.CameraImage;
import solution.common.DoorController;

/**
 * Bang Bang solution to stop right before the door
 */
public class BangBangDoorProxController extends DoorController {

    private void startBehavior() {
        startEPuck();
        epuck.enableCamera();
        boolean doorFound = false;
        boolean keepGoing = true;
        while (epuck.isConnected() && keepGoing) {
            try {
                CameraImage image = epuck.getCameraImage();
                // Finds the door if it isn't already in the robot's field of view
                if (!doorFound)
                    doorFound = findDoor(image);


                if (doorFound) {
                    // Calculates the center of the door
                    int center = getCenter(image);

                    // Stops the robot right before crashing into the door
                    epuck.senseAllTogether();
                    double[] distVector = epuck.getProximitySensorValues();
                    if (distVector[FRONT_LEFT] < 0.05 || distVector[FRONT_RIGHT] < 0.05) {
                        stopDriving();
                        keepGoing = false;
                    }

                    // Steers the robot depending on the door's center-position
                    if (keepGoing) {
                        if (center < 31)
                            turnLeft();
                        else if (center > 33)
                            turnRight();
                        else
                            driveForward();
                    }
                }

                Thread.sleep(20);
            } catch (Exception e) {
                epuck.disconnect();
            }
        }
    }


    public static void main(String[] args) {
        BangBangDoorProxController controller = new BangBangDoorProxController();
        controller.startBehavior();
    }
}
