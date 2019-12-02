package at.fhv.dgr1992.ePuck;

import java.util.TimerTask;

/**
 * This task refreshes all the sensor values
 */
public class SensorValueRefreshTask extends TimerTask{

    private EPuck _ePuck;

    /**
     * Constructs new Task
     * @param ePuck Instance of the ePuck of which the sensor values need to be refreshed
     */
    public SensorValueRefreshTask(EPuck ePuck){
        _ePuck = ePuck;
    }

    @Override
    public void run() {
        _ePuck.refreshSensorValues();
    }
}
