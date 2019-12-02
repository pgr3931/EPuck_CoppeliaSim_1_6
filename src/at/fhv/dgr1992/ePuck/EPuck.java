package at.fhv.dgr1992.ePuck;

import at.fhv.dgr1992.differentialWheels.*;
import at.fhv.dgr1992.exceptions.CameraNotEnabledException;
import at.fhv.dgr1992.exceptions.RobotFunctionCallException;
import at.fhv.dgr1992.exceptions.SensorNotEnabledException;

import java.util.LinkedList;
import java.util.Timer;

/**
 * Abstract representation of the robot e-Puck
 */
public abstract class EPuck extends DifferentialWheels {

    protected boolean _hasOwnCameraThread;
    /**
     * time in ms
     */
    protected long _cameraCycleTime;
    protected boolean _hasOwnSensingThread;
    /**
     * time in ms
     */
    protected long _sensorCycleTime;
    protected boolean _senseAllTogetherEnabled;
    protected Timer _cameraImageRefreshTimer;
    protected Timer _sensorValueRefreshTimer;
    protected int _imageWidth;
    protected int _imageHeight;
    protected CameraImage _cameraImage;

    public EPuck(String robotName, double maxVel, int imageWidth, int imageHeight) {
        //Values set are the values from the robot
        super(robotName, 0.0425, 0.0623, maxVel, 8, 8, 3);

        _hasOwnCameraThread = false;
        _hasOwnSensingThread = false;
        _senseAllTogetherEnabled = false;

        _cameraImageRefreshTimer = new Timer();
        _sensorValueRefreshTimer = new Timer();

        _cameraCycleTime = 500;
        _sensorCycleTime = 90;

        _imageWidth = imageWidth;
        _imageHeight = imageHeight;
        _cameraImage = new CameraImage(imageWidth,imageHeight);
    }

    protected abstract double[] refreshProximitySensorValues() throws SensorNotEnabledException, RobotFunctionCallException;

    protected abstract double[] refreshLightSensorValues() throws SensorNotEnabledException, RobotFunctionCallException;

    protected abstract double[] refreshGroundSensorValues() throws SensorNotEnabledException, RobotFunctionCallException;

    protected abstract Acceleration refreshAccelerometerValues() throws SensorNotEnabledException, RobotFunctionCallException;

    protected abstract WheelEncode refreshWheelEncodingValues() throws SensorNotEnabledException, RobotFunctionCallException;

    protected abstract Pose refreshPose() throws SensorNotEnabledException, RobotFunctionCallException;

    protected abstract CameraImage refreshCameraImage() throws CameraNotEnabledException, RobotFunctionCallException;

    protected abstract boolean senseAllTogether() throws RobotFunctionCallException;

    public void setSenseAllTogether() {
        _senseAllTogetherEnabled = true;
    }

    protected void setCameraImage(CameraImage image){
        synchronized (_cameraImage){
            _cameraImage = image;
        }
    }

    public void createImageThread() {
        if (!_hasOwnCameraThread) {
            _cameraImageRefreshTimer.schedule(new CameraImageRefreshTask(this), 0, _cameraCycleTime);
            _hasOwnCameraThread = true;
        }
    }

    public void stopImageThread() {
        if (_hasOwnCameraThread) {
            _cameraImageRefreshTimer.cancel();
            _cameraImageRefreshTimer.purge();
            _hasOwnCameraThread = false;
        }
    }

    protected void refreshSensorValues() {
        if (_senseAllTogetherEnabled) {
            refreshSensorValuesAllTogether();
        } else {
            refreshSensorValuesIndividual();
        }
    }

    private void refreshSensorValuesAllTogether() {
        try {
            senseAllTogether();
        } catch (RobotFunctionCallException e) {
            e.printStackTrace();
        }
    }

    private void refreshSensorValuesIndividual() {
        LinkedList<Sensor> updatedSensors = new LinkedList<Sensor>();

        if (_proximitySensorEnabled) {
            try {
                synchronized (_proximitySensorValues) {
                    _proximitySensorValues = refreshProximitySensorValues();
                }
                updatedSensors.add(Sensor.Proximity);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (_lightSensorsEnabled) {
            try {
                synchronized (_lightSensorValues) {
                    _lightSensorValues = refreshLightSensorValues();
                }
                updatedSensors.add(Sensor.Light);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (_groundSensorsEnabled) {
            try {
                synchronized (_groundSensorValues) {
                    _groundSensorValues = refreshGroundSensorValues();
                }
                updatedSensors.add(Sensor.Ground);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (_accelerometerEnabled) {
            try {
                synchronized (_accelerometerValues) {
                    _accelerometerValues = refreshAccelerometerValues();
                }
                updatedSensors.add(Sensor.Accelerometer);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (_wheelEncodingEnabled) {
            try {
                synchronized (_wheelEncodeValues) {
                    _wheelEncodeValues = refreshWheelEncodingValues();
                }
                updatedSensors.add(Sensor.WheelEncoding);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (_poseEnabled) {
            try {
                synchronized (_pose) {
                    _pose = refreshPose();
                }
                updatedSensors.add(Sensor.Pose);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (updatedSensors.size() > 0) {
            notifySensorObservers(updatedSensors);
        }
    }

    public void createSensingThread() {
        if (!_hasOwnSensingThread) {
            _sensorValueRefreshTimer.schedule(new SensorValueRefreshTask(this), 0, _sensorCycleTime);
            _hasOwnSensingThread = true;
        }
    }

    public void stopSensingThread() {
        _sensorValueRefreshTimer.cancel();
        _sensorValueRefreshTimer.purge();
        _hasOwnSensingThread = false;
    }


    @Override
    public double[] getProximitySensorValues() throws RobotFunctionCallException, SensorNotEnabledException {
        synchronized (_proximitySensorValues) {
            if (!_hasOwnSensingThread && !_senseAllTogetherEnabled) {
                _proximitySensorValues = refreshProximitySensorValues();
            }

            return _proximitySensorValues;
        }
    }

    @Override
    public double[] getLightSensorValues() throws RobotFunctionCallException, SensorNotEnabledException {
        synchronized (_lightSensorValues) {
            if (!_hasOwnSensingThread && !_senseAllTogetherEnabled) {
                _lightSensorValues = refreshLightSensorValues();
            }
            return _lightSensorValues;
        }
    }

    @Override
    public double[] getGroundSensorValues() throws RobotFunctionCallException, SensorNotEnabledException {
        synchronized (_groundSensorValues) {
            if (!_hasOwnSensingThread && !_senseAllTogetherEnabled) {
                _groundSensorValues = refreshGroundSensorValues();
            }
            return _groundSensorValues;
        }
    }

    @Override
    public Acceleration getAccelerometerValues() throws RobotFunctionCallException, SensorNotEnabledException {
        synchronized (_accelerometerValues) {
            if (!_hasOwnSensingThread && !_senseAllTogetherEnabled) {
                _accelerometerValues = refreshAccelerometerValues();
            }
            return _accelerometerValues;
        }
    }

    @Override
    public WheelEncode getWheelEncodingValues() throws RobotFunctionCallException, SensorNotEnabledException {
        synchronized (_wheelEncodeValues) {
            if (!_hasOwnSensingThread && !_senseAllTogetherEnabled) {
                _wheelEncodeValues = refreshWheelEncodingValues();
            }
            return _wheelEncodeValues;
        }
    }

    @Override
    public Pose getPose() throws RobotFunctionCallException, SensorNotEnabledException {
        synchronized (_pose) {
            if ((_hasOwnSensingThread && _senseAllTogetherEnabled) || !_hasOwnSensingThread) {
                _pose = refreshPose();
            }
            return _pose;
        }
    }

    @Override
    public CameraImage getCameraImage() throws CameraNotEnabledException, RobotFunctionCallException {
        synchronized (_cameraImage) {
            if (!_hasOwnCameraThread) {
                _cameraImage = refreshCameraImage();
            }
            return _cameraImage;
        }
    }
}
