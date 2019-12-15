package solution.proportional;

import solution.common.PushController;

public class ProportionalPushController extends PushController {

    private void startBehavior() {
        startEPuck();
        epuck.enableCamera();
        boolean puckFound = false;
        while (epuck.isConnected()) {
            try {
                // Finds the puck if it isn't already in the robot's field of view
                if (!puckFound)
                    puckFound = findPuck();

                // Sets the motor speeds to a %-value of the front sensors
                if (puckFound) {
                    epuck.senseAllTogether();
                    double[] distVector = epuck.getProximitySensorValues();
                    //setSpeeds((distVector[FRONT_LEFT] + 0.05) * 10 * MAX_SPEED, (distVector[FRONT_RIGHT] + 0.05) * 10 * MAX_SPEED);
                    setSpeeds(distVector[FRONT_LEFT] * 20 + 1, distVector[FRONT_RIGHT] * 20 + 1);
                }

                Thread.sleep(5);
            } catch (Exception e) {
                epuck.disconnect();
            }
        }

    }

    public static void main(String[] args) {
        ProportionalPushController controller = new ProportionalPushController();
        controller.startBehavior();
    }
}
