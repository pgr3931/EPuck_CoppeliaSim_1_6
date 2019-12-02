package at.fhv.dgr1992.ePuck;

import at.fhv.dgr1992.differentialWheels.CameraImage;
import at.fhv.dgr1992.exceptions.CameraNotEnabledException;
import at.fhv.dgr1992.exceptions.RobotFunctionCallException;

import java.util.TimerTask;

/**
 * This tasks refreshes the camera image.
 */
public class CameraImageRefreshTask extends TimerTask {

    private EPuck _ePuck;

    /**
     * Constructs new task
     * @param ePuck Instance of the ePuck of which the camera image needs to refreshed
     */
    public CameraImageRefreshTask(EPuck ePuck){
        _ePuck = ePuck;
    }

    @Override
    public void run() {
        try {
            CameraImage img = _ePuck.refreshCameraImage();
            _ePuck.setCameraImage(img);
        } catch (CameraNotEnabledException e) {
            e.printStackTrace();
        } catch (RobotFunctionCallException e){
            e.printStackTrace();
        }
    }
}
