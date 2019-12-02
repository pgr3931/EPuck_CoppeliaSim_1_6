package at.fhv.dgr1992.differentialWheels;

import java.util.LinkedList;

public interface SensorObserver {
    void sensorValuesChanged(LinkedList<Sensor> sensors);
}
