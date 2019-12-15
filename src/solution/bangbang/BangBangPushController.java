package solution.bangbang;

import solution.common.PushController;

/**
 * Bang Bang solution to push a puck
 */
public class BangBangPushController extends PushController {

    private void startBehavior() {
        startEPuck();
        epuck.enableCamera();
        boolean puckFound = false;
        while (epuck.isConnected()) {
            try {
                // Finds the puck if it isn't already in the robot's field of view
                if (!puckFound)
                    puckFound = findPuck();

                // Balances the puck
                if (puckFound) {
                    epuck.senseAllTogether();
                    double[] distVector = epuck.getProximitySensorValues();

                    // Puck shifts the the right
                    if (distVector[FRONT_LEFT] > distVector[FRONT_RIGHT])
                        turnRight();
                    // Puck shifts to the left
                    else if (distVector[FRONT_LEFT] < distVector[FRONT_RIGHT])
                        turnLeft();
                    else
                        driveForward();
                }

                Thread.sleep(20);
            } catch (Exception e) {
                epuck.disconnect();
            }
        }

    }

    public static void main(String[] args) {
        BangBangPushController controller = new BangBangPushController();
        controller.startBehavior();
    }
}
