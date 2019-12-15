package solution.bangbang;

import solution.common.BasicRobot;

/**
 * Bang Bang solution to follow a wall
 */
public class BangBangWallFollowController extends BasicRobot {

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

                // Follows the wall
                if (positioned) {

                    // Repositions the robot if a corner is reached
                    if (distVector[FRONT_LEFT] < 0.05 || distVector[FRONT_RIGHT] < 0.05) {
                        repositioned = false;
                        turnRight();
                    } else
                        repositioned = true;

                    if (repositioned) {
                        // Turns right if the robots gets too close to the wall
                        if (distVector[LEFT_FRONT] < 0.012 || distVector[LEFT] < 0.015)
                            turnRight();
                        // Turns left if the robot gets too far away from the wall
                        else if (distVector[LEFT_FRONT] > 0.04)
                            turnLeft();

                        // Drives forward if the robot is positioned correctly
                        if (distVector[LEFT_FRONT] > 0.012 && distVector[LEFT_FRONT] < 0.04)
                            driveForward();
                    }
                }

                Thread.sleep(5);
            } catch (Exception e) {
                epuck.disconnect();
            }
        }

    }

    public static void main(String[] args) {
        BangBangWallFollowController controller = new BangBangWallFollowController();
        controller.startBehavior();
    }
}
