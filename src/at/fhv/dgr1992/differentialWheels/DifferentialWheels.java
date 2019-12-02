package at.fhv.dgr1992.differentialWheels;

import at.fhv.dgr1992.exceptions.CameraNotEnabledException;
import at.fhv.dgr1992.exceptions.RobotFunctionCallException;
import at.fhv.dgr1992.exceptions.SensorNotEnabledException;
import at.fhv.dgr1992.exceptions.VelocityLimitException;

import java.util.LinkedList;

/**
 * Abstract representation of a robot with two wheels
 */
public abstract class DifferentialWheels{

    protected String _robotName;
    protected double _wheelDiameter;
    protected double _wheelDistance;
    protected boolean _proximitySensorEnabled = false;
    protected int _numProximitySeonsors;
    protected int[] _enabledProximitySensors;
    protected double[] _proximitySensorValues;
    protected boolean _lightSensorsEnabled = false;
    protected int _numLightSensors;
    protected int[] _enabledLightSensors;
    protected double[] _lightSensorValues;
    protected boolean _groundSensorsEnabled = false;
    protected double[] _groundSensorValues;
    protected int _numGroundSensors;
    protected boolean _accelerometerEnabled = false;
    protected Acceleration _accelerometerValues;
    protected boolean _wheelEncodingEnabled = false;
    protected WheelEncode _wheelEncodeValues;
    protected boolean _poseEnabled = false;
    protected Pose _pose;
    protected boolean _cameraEnabled = false;
    protected boolean _connected = false;
    protected Speed _motorSpeed;
    protected double _maxVel;

    private LinkedList<SensorObserver> _sensorObservers;
    private LinkedList<CameraImageObserver> _cameraImageObservers;

    public DifferentialWheels(String robotName, double wheelDiameter, double wheelDistance, double maxVel,int numProximitySensors, int numLightSensors, int numGroundSensors){
        _robotName = robotName;
        _wheelDiameter = wheelDiameter;
        _wheelDistance = wheelDistance;
        _maxVel = maxVel;
        _numProximitySeonsors = numProximitySensors;
        _numLightSensors = numLightSensors;
        _numGroundSensors = numGroundSensors;
        _pose = new Pose(new double[3]);

        _enabledProximitySensors = new int[_numProximitySeonsors];
        _proximitySensorValues = new double[_numProximitySeonsors];
        _enabledLightSensors = new int[_numLightSensors];
        _lightSensorValues = new double[_numLightSensors];
        _groundSensorValues = new double[3];
        _accelerometerValues = new Acceleration(new double[3]);
        _wheelEncodeValues = new WheelEncode(new double[2]);

        _sensorObservers = new LinkedList<SensorObserver>();
        _cameraImageObservers = new LinkedList<CameraImageObserver>();

        _motorSpeed = new Speed(0,0);
    }

    protected abstract boolean connect() throws RobotFunctionCallException;
    protected abstract void disconnect();
    protected abstract void initRobotModel() throws RobotFunctionCallException;
    public abstract double[] getProximitySensorValues() throws SensorNotEnabledException, RobotFunctionCallException;
    public abstract double[] getLightSensorValues() throws SensorNotEnabledException, RobotFunctionCallException;
    public abstract double[] getGroundSensorValues() throws SensorNotEnabledException, RobotFunctionCallException;
    public abstract Acceleration getAccelerometerValues() throws SensorNotEnabledException, RobotFunctionCallException;
    public abstract WheelEncode getWheelEncodingValues() throws SensorNotEnabledException, RobotFunctionCallException;
    public abstract Pose getPose() throws SensorNotEnabledException, RobotFunctionCallException;
    public abstract CameraImage getCameraImage() throws CameraNotEnabledException, RobotFunctionCallException;
    public abstract boolean setMotorSpeeds(Speed speed) throws VelocityLimitException, RobotFunctionCallException;


    /**
     * Enable the accelerometer, all light sensors, all proximity sensors, ground sensors and the wheel encoding of the robot.
     */
    public void enableAllSensors(){
        enableAccelerometer();
        enableAllLightSensors();
        enableAllProximitySensors();
        enableWheelEncoding();
        enableGroundSensors();
    }

    /**
     * Enable all proximity sensors of the robot.
     */
    public void enableAllProximitySensors(){
        _enabledProximitySensors = new int[_numProximitySeonsors];
        for(int i = 0; i < _numProximitySeonsors; i++){
            _enabledProximitySensors[i] = i;
        }
        _proximitySensorEnabled = true;
    }

    /**
     * Enable specific proximity sensors of the robot.
     * @param sensorIDs IDs of the sensors to be enabled.
     * @throws Exception
     */
    public void enableProximitySensors(int[] sensorIDs) throws Exception{
        if(sensorIDs.length < _numProximitySeonsors){
            _enabledProximitySensors = new int[sensorIDs.length];
            for(int i = 0; i < sensorIDs.length; i++){
                _enabledProximitySensors[i] = sensorIDs[i];
            }
            _proximitySensorEnabled = true;
        } else {
            throw new Exception("Number of sensors larger then actually available sensors");
        }
    }

    /**
     * Enable all light sensors of the robot.
     */
    public void enableAllLightSensors(){
        _enabledLightSensors = new int[_numLightSensors];
        for(int i = 0; i < _numLightSensors; i++){
            _enabledLightSensors[i] = i;
        }
        _lightSensorsEnabled = true;
    }

    /**
     * Enable specific light sensors of the robot.
     * @param sensorIDs IDs of the sensors to enable
     * @throws Exception
     */
    public void enableLightSensors(int[] sensorIDs) throws Exception{
        if(sensorIDs.length < _numLightSensors) {
            _enabledLightSensors = new int[_numLightSensors];
            for (int i = 0; i < _numLightSensors; i++) {
                _enabledLightSensors[i] = i;
            }
            _lightSensorsEnabled = true;
        } else {
            throw new Exception("Number of sensors larger then actually available sensors");
        }
    }

    /**
     * Enable the accelerometer of the robot.
     */
    public void enableAccelerometer(){
        _accelerometerEnabled = true;
    }

    /**
     * Enable the wheel encoding of the robot.
     */
    public void enableWheelEncoding(){
        _wheelEncodingEnabled = true;
    }

    /**
     * Enable the ground sensors of the robot.
     */
    public void enableGroundSensors(){
        _groundSensorsEnabled = true;
    }

    /**
     * Enable pose of the robot.
     */
    public void enablePose(){
        _poseEnabled = true;
    }

    /**
     * Enable the camera of the robot.
     */
    public void enableCamera(){
        _cameraEnabled = true;
    }

    /**
     * Check if connected to the robot.
     * @return true = connected to the robot
     */
    public boolean isConnected(){
            return _connected;
    }

    /**
     * Get the current motor speed of the robot.
     * @return
     */
    public Speed getMotorSpeed(){
        return _motorSpeed;
    }

    /**
     * Get the maximum velocity that the robot can make.
     * @return
     */
    public double getMaxVelocity(){
        return _maxVel;
    }

    /**
     * Register as observer for the sensor values to get updates.
     * @param observer
     */
    public void registerSensorObserver(SensorObserver observer){
        _sensorObservers.add(observer);
    }

    /**
     * Unregister from the observer list.
     * @param observer
     */
    public void unregisterSensorObserver(SensorObserver observer){
        _sensorObservers.remove(observer);
    }

    /**
     * Notify all observers about a the change of sensor values by giving them a list of all sensors that have changed.
     * @param sensors
     */
    protected void notifySensorObservers(LinkedList<Sensor> sensors){
        for (SensorObserver observer: _sensorObservers) {
            observer.sensorValuesChanged(sensors);
        }
    }

    /**
     * Register as observer for camera image to get updates.
     * @param observer
     */
    public void registerCameraImageObserver(CameraImageObserver observer){
        _cameraImageObservers.add(observer);
    }

    /**
     * Unregister from the observer list.
     * @param observer
     */
    public void unregisterCameraImageObserver(CameraImageObserver observer){
        _cameraImageObservers.remove(observer);
    }

    /**
     * Notify all observers that the camera image has changed by sending them the current image.
     * @param cameraImage
     */
    protected void notifyCameraImageObservers(CameraImage cameraImage){
        for (CameraImageObserver observer: _cameraImageObservers) {
            observer.cameraImageChanged(cameraImage);
        }
    }
}
