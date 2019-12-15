package solution.proportional;

import solution.common.BasicRobot;

/**
 * Proportional solution to follow a wall
 */
public class ProportionalWallFollowController extends BasicRobot {

    private void startBehavior() {
        startEPuck();
        boolean wallFound = false;
        boolean positioned = false;
        boolean repositioned;
        while (epuck.isConnected()) {
            try {
                // Drives forward until the wall is in front of the robot
                if (!wallFound)
                    driveForward();

                epuck.senseAllTogether();
                double[] distVector = epuck.getProximitySensorValues();
                // Changes the state to wallFound
                if (!wallFound && (distVector[FRONT_LEFT] < 0.05 || distVector[FRONT_RIGHT] < 0.05))
                    wallFound = true;

                // Positions the robot, so that its left side is directed towards the wall
                if (wallFound && !positioned) {
                    if (distVector[FRONT_LEFT] < 0.05 || distVector[LEFT_FRONT] < 0.012)
                        turnRight();
                    else
                        positioned = true;
                }

                if (positioned) {
                    // Repositions the robot if a corner is reached
                    if (distVector[FRONT_LEFT] < 0.05 || distVector[FRONT_RIGHT] < 0.05) {
                        repositioned = false;
                        turnRight();
                    } else
                        repositioned = true;

                    if(repositioned) {
                        double leftSensors = distVector[FRONT_LEFT] + distVector[LEFT_FRONT] + distVector[LEFT] + distVector[BACK_LEFT];

                        double leftSpeed = -52 * Math.pow(leftSensors, 2) + MAX_SPEED;
                        double rightSpeed = 52 * Math.pow(leftSensors, 2); 

                        setSpeeds(leftSpeed, rightSpeed);
                    }
                }

                Thread.sleep(5);
            } catch (Exception e) {
                epuck.disconnect();
            }
        }
    }

    public static void main(String[] args) {
        ProportionalWallFollowController controller = new ProportionalWallFollowController();
        controller.startBehavior();
    }
}
