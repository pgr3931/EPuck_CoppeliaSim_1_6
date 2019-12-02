package at.fhv.dgr1992.ePuck.ePuckVRep;

import at.fhv.dgr1992.ePuck.ePuckVRep.exceptions.StepSimNotPossibleException;
import at.fhv.dgr1992.ePuck.ePuckVRep.exceptions.SynchrounusModeNotActivatedException;
import at.fhv.dgr1992.exceptions.CameraNotEnabledException;
import at.fhv.dgr1992.exceptions.RobotFunctionCallException;
import at.fhv.dgr1992.exceptions.SensorNotEnabledException;
import at.fhv.dgr1992.exceptions.VelocityLimitException;
import coppelia.*;
import at.fhv.dgr1992.differentialWheels.*;
import at.fhv.dgr1992.ePuck.EPuck;

/**
 * The class "EPuckVRep" encapsulates the ePuck robot in a scene of the VRep simulator for a controller: it represents a proxy of the ePuck for the controller.
 */
public class EPuckVRep extends EPuck {

    private remoteApi _vrepRemote;
    private int _port;
    private String _ipAddress;
    private boolean _synchronous;
    private int _clientID;
    private String _signalName;
    private static double MAXVEL = (120 * Math.PI / 180); //to be verified on real ePuck

    private final Object lockAPI = new Object();

    /**
     * Creates a new instance of the EPuckVRep in asynchronous mode and with a max velocity of maxVel = (120 * Math.PI / 180).
     *
     * @param robotName Name of the robot inside VRep.
     * @param ipAddress IP- address of the computer where VRep is running.
     * @param port      Port the robot is mapped to.
     */
    public EPuckVRep(String robotName, String ipAddress, int port) {
        super(robotName, MAXVEL, 64, 64);
        init(robotName, ipAddress, port, false);
    }

    /**
     * Creates a new instance of the EPuckVRep with a max velocity of maxVel = (120 * Math.PI / 180).
     *
     * @param robotName   Name of the robot inside VRep.
     * @param ipAddress   IP- address of the computer where VRep is running.
     * @param port        Port the robot is mapped to.
     * @param synchronous Set to true to control the simulator in single steps.
     */
    public EPuckVRep(String robotName, String ipAddress, int port, boolean synchronous) {
        super(robotName, MAXVEL, 64, 64);
        init(robotName, ipAddress, port, synchronous);
    }

    /**
     * Creates a new instance of the EPuckVRep.
     *
     * @param robotName   Name of the robot inside VRep.
     * @param ipAddress   IP- address of the computer where VRep is running.
     * @param port        Port the robot is mapped to.
     * @param maxVelocity Maximum velocity that the ePuck can drive.
     * @param synchronous Set to true to control the simulator in single steps.
     */
    public EPuckVRep(String robotName, String ipAddress, int port, double maxVelocity, boolean synchronous) {
        super(robotName, maxVelocity, 64, 64);
        init(robotName, ipAddress, port, synchronous);
    }

    /**
     * Initialise the instance.
     *
     * @param robotName   Name of the robot inside VRep.
     * @param ipAddress   IP- address of the computer where VRep is running.
     * @param port        Port the robot is mapped to.
     * @param synchronous Set to true to control the simulator in single steps.
     */
    private void init(String robotName, String ipAddress, int port, boolean synchronous) {
        _ipAddress = ipAddress;
        _port = port;
        _signalName = "epuck" + port;
        _synchronous = false;
        _clientID = -1;
        _robotName = robotName;
        _vrepRemote = new remoteApi();
        _synchronous = synchronous;
    }

    //region connect and disconnect

    /**
     * Connect to VRep, trigger sensors and camera and set the speed to 0.
     *
     * @return Returns true if connect was successful.
     * @throws RobotFunctionCallException
     */
    @Override
    public boolean connect() throws RobotFunctionCallException {
        _clientID = _vrepRemote.simxStart(_ipAddress, _port, true, true, 5000, 5);

        if (_clientID != -1) {
            System.out.println("Connected to remote API server of Vrep with clientID: " + _clientID);
            _connected = true;

            //Trigger all sensors and the camera
            CharWA str = new CharWA("");
            //simxGetStringSignal requires a signal value, as _allSens and _camera doesn't need a value just send an empty string
            synchronized (lockAPI) {
                _vrepRemote.simxGetStringSignal(_clientID, _signalName + "_allSens", str, _vrepRemote.simx_opmode_streaming);
                _vrepRemote.simxGetStringSignal(_clientID, _signalName + "_camera", str, _vrepRemote.simx_opmode_streaming);
            }

            //Set the velocity to 0
            FloatWA myfloat = new FloatWA(2);
            myfloat.getArray()[0] = (float) 0.0;
            myfloat.getArray()[1] = (float) 0.0;
            //From the FloatWA we generate the CharWA
            str = new CharWA(myfloat.getCharArrayFromArray().toString());
            synchronized (lockAPI) {
                _vrepRemote.simxGetStringSignal(_clientID, _signalName + "_velocities", str, _vrepRemote.simx_opmode_streaming);
            }

            //Initialise the robot
            initRobotModel();
        } else {
            System.out.println("Failed connecting to remote API server of Vrep");
        }
        return _connected;
    }

    /**
     * Disconnect from VRep.
     */
    @Override
    public void disconnect() {
        if (_connected) {
            synchronized (lockAPI) {
                _vrepRemote.simxFinish(_clientID);
            }
        }
    }
    //endregion

    //region initialisation

    /**
     * Initialisation of the simulation. Getting wheel diameter and wheel distance from VRep and setting the max velocity.
     *
     * @throws RobotFunctionCallException
     */
    @Override
    protected void initRobotModel() throws RobotFunctionCallException {
        getWheelDiameterForRemote();
        getWheelDistanceForRemote();
        setMaxVelocityForRemote(_maxVel);
    }

    /**
     * Gets the wheel diameter of the robot.
     *
     * @throws RobotFunctionCallException
     */
    private void getWheelDiameterForRemote() throws RobotFunctionCallException {
        //Array for the result values
        FloatWA outFloat = new FloatWA(1);

        int returnCode = 0;
        synchronized (lockAPI) {
            //Get the wheel diameter from from VRep
            returnCode = _vrepRemote.simxCallScriptFunction(_clientID, _robotName, _vrepRemote.sim_scripttype_childscript, "getWheelDiameterForRemote", null, null, null, null, null, outFloat, null, null, _vrepRemote.simx_opmode_blocking);
        }

        if (returnCode == _vrepRemote.simx_return_ok) {
            _wheelDiameter = outFloat.getArray()[0];
        } else {
            VRepReturnCode[] vRepReturnCode = VRepReturnCode.defineReturnCode(returnCode);

            StringBuilder returnCodeDescriptions = new StringBuilder();
            for(VRepReturnCode errorCode: vRepReturnCode){
                returnCodeDescriptions.append(errorCode.getDetailDescription() + " ");
            }
            throw new RobotFunctionCallException("Getting wheel diameter failed. Return code msg from VRep: " + returnCodeDescriptions);
        }
    }

    /**
     * Gets the wheel distance of the robot.
     *
     * @throws RobotFunctionCallException
     */
    private void getWheelDistanceForRemote() throws RobotFunctionCallException {
        //Array for the result values
        FloatWA outFloat = new FloatWA(1);
        int returnCode = 0;
        synchronized (lockAPI) {
            //Get the wheel distance from VRep
            returnCode = _vrepRemote.simxCallScriptFunction(_clientID, _robotName, _vrepRemote.sim_scripttype_childscript, "getWheelDistanceForRemote", null, null, null, null, null, outFloat, null, null, _vrepRemote.simx_opmode_blocking);
        }

        if (returnCode == _vrepRemote.simx_return_ok) {
            _wheelDistance = outFloat.getArray()[0];
        } else {
            VRepReturnCode[] vRepReturnCode = VRepReturnCode.defineReturnCode(returnCode);

            StringBuilder returnCodeDescriptions = new StringBuilder();
            for(VRepReturnCode errorCode: vRepReturnCode){
                returnCodeDescriptions.append(errorCode.getDetailDescription() + " ");
            }
            throw new RobotFunctionCallException("Getting wheel distance failed. Return code msg from VRep: " + returnCodeDescriptions);
        }
    }

    /**
     * Sets the max velocity of the robot.
     *
     * @param maxVelocity The maximal velocity the robot can drive.
     * @throws RobotFunctionCallException
     */
    private void setMaxVelocityForRemote(double maxVelocity) throws RobotFunctionCallException {
        //Create array for the value to send and set it
        FloatWA inFloat = new FloatWA(1);
        inFloat.getArray()[0] = (float) maxVelocity;
        int returnCode = 0;
        synchronized (lockAPI) {
            //Set the max velocity
            returnCode = _vrepRemote.simxCallScriptFunction(_clientID, _robotName, _vrepRemote.sim_scripttype_childscript, "setMaxVelocityForRemote", null, inFloat, null, null, null, null, null, null, _vrepRemote.simx_opmode_blocking);
        }
        if (returnCode != _vrepRemote.simx_return_ok) {
            VRepReturnCode[] vRepReturnCode = VRepReturnCode.defineReturnCode(returnCode);

            StringBuilder returnCodeDescriptions = new StringBuilder();
            for(VRepReturnCode errorCode: vRepReturnCode){
                returnCodeDescriptions.append(errorCode.getDetailDescription() + " ");
            }
            throw new RobotFunctionCallException("Setting max velocity failed. Return code msg from VRep: " + returnCodeDescriptions);
        }
    }
    //endregion

    //region Updating sensors and camera

    /**
     * Get the proximity sensor values from VRep.
     *
     * @return Returns the new sensor values or the old if call to VRep was not successful.
     * @throws SensorNotEnabledException
     * @throws RobotFunctionCallException
     */
    @Override
    protected double[] refreshProximitySensorValues() throws SensorNotEnabledException, RobotFunctionCallException {
        if (!_proximitySensorEnabled) {
            throw new SensorNotEnabledException("Proximity sensor is not enabled");
        }

        FloatWA outFloat = new FloatWA(_numProximitySeonsors);
        int returnCode = 0;
        synchronized (lockAPI) {
            returnCode = _vrepRemote.simxCallScriptFunction(_clientID, _robotName, _vrepRemote.sim_scripttype_childscript, "getProxSensorsForRemote", null, null, null, null, null, outFloat, null, null, _vrepRemote.simx_opmode_blocking);
        }
        if (returnCode == _vrepRemote.simx_return_ok) {
            return floatArrayToDoubleArray(outFloat.getArray());
        } else {
            VRepReturnCode[] vRepReturnCode = VRepReturnCode.defineReturnCode(returnCode);

            StringBuilder returnCodeDescriptions = new StringBuilder();
            for(VRepReturnCode errorCode: vRepReturnCode){
                returnCodeDescriptions.append(errorCode.getDetailDescription() + " ");
            }
            throw new RobotFunctionCallException("Refreshing proximity sensor values failed. Return code msg from VRep: " + returnCodeDescriptions);
        }

    }

    /**
     * Get the light sensor values from VRep
     *
     * @return Returns the new sensor values or the old if call to VRep was not successful.
     * @throws SensorNotEnabledException
     */
    @Override
    protected double[] refreshLightSensorValues() throws SensorNotEnabledException {
        //TODO: implement when implemented in VRep
        throw new UnsupportedOperationException("light sensors for ePuck in VRep not implemented yet");
    }


    /**
     * Get the ground sensor values from VRep.
     *
     * @return the new sensor values.
     * @throws SensorNotEnabledException
     * @throws RobotFunctionCallException
     */
    @Override
    protected double[] refreshGroundSensorValues() throws SensorNotEnabledException, RobotFunctionCallException {
        if (!_groundSensorsEnabled) {
            throw new SensorNotEnabledException("Grounds sensor is not enabled");
        }

        FloatWA outFloat = new FloatWA(_numGroundSensors);
        int returnCode = 0;
        synchronized (lockAPI) {
            returnCode = _vrepRemote.simxCallScriptFunction(_clientID, _robotName, _vrepRemote.sim_scripttype_childscript, "getGroundSensorForRemote", null, null, null, null, null, outFloat, null, null, _vrepRemote.simx_opmode_blocking);
        }
        if (returnCode == _vrepRemote.simx_return_ok) {
            return floatArrayToDoubleArray(outFloat.getArray());
        } else {
            VRepReturnCode[] vRepReturnCode = VRepReturnCode.defineReturnCode(returnCode);

            StringBuilder returnCodeDescriptions = new StringBuilder();
            for(VRepReturnCode errorCode: vRepReturnCode){
                returnCodeDescriptions.append(errorCode.getDetailDescription() + " ");
            }
            throw new RobotFunctionCallException("Refreshing ground sensor values failed. Return code msg from VRep: " + returnCodeDescriptions);
        }

    }

    /**
     * Get the accelerometer values from VRep.
     *
     * @return Returns the new accelerometer values.
     * @throws SensorNotEnabledException
     * @throws RobotFunctionCallException
     */
    @Override
    protected Acceleration refreshAccelerometerValues() throws SensorNotEnabledException, RobotFunctionCallException {
        if (!_accelerometerEnabled) {
            throw new SensorNotEnabledException("Accelerometer is not enabled");
        }

        //Accelerometer delivers x,y and z acceleration
        FloatWA outFloat = new FloatWA(3);
        int returnCode = 0;
        synchronized (lockAPI) {
            returnCode = _vrepRemote.simxCallScriptFunction(_clientID, _robotName, _vrepRemote.sim_scripttype_childscript, "getAccelerometerForRemote", null, null, null, null, null, outFloat, null, null, _vrepRemote.simx_opmode_blocking);
        }
        if (returnCode == _vrepRemote.simx_return_ok) {
            return new Acceleration(new double[]{outFloat.getArray()[0], outFloat.getArray()[1], outFloat.getArray()[2]});
        } else {
            VRepReturnCode[] vRepReturnCode = VRepReturnCode.defineReturnCode(returnCode);

            StringBuilder returnCodeDescriptions = new StringBuilder();
            for(VRepReturnCode errorCode: vRepReturnCode){
                returnCodeDescriptions.append(errorCode.getDetailDescription() + " ");
            }
            throw new RobotFunctionCallException("Refreshing accelerometer value failed. Return code msg from VRep: " + returnCodeDescriptions);
        }
    }

    /**
     * Get the wheel encoding Values from VRep.
     *
     * @return Returns the new wheel encoding values.
     * @throws SensorNotEnabledException
     * @throws RobotFunctionCallException
     */
    @Override
    protected WheelEncode refreshWheelEncodingValues() throws SensorNotEnabledException, RobotFunctionCallException {
        if (!_wheelEncodingEnabled) {
            throw new SensorNotEnabledException("Wheel encoding is not enabled");
        }

        //Wheel encoding delivers left and right value
        FloatWA outFloat = new FloatWA(2);
        int returnCode = 0;
        synchronized (lockAPI) {
            returnCode = _vrepRemote.simxCallScriptFunction(_clientID, _robotName, _vrepRemote.sim_scripttype_childscript, "getWheelEncodingSensorForRemote", null, null, null, null, null, outFloat, null, null, _vrepRemote.simx_opmode_blocking);
        }
        if (returnCode == _vrepRemote.simx_return_ok) {
            return new WheelEncode(new double[]{outFloat.getArray()[0], outFloat.getArray()[1]});
        } else {
            VRepReturnCode[] vRepReturnCode = VRepReturnCode.defineReturnCode(returnCode);

            StringBuilder returnCodeDescriptions = new StringBuilder();
            for(VRepReturnCode errorCode: vRepReturnCode){
                returnCodeDescriptions.append(errorCode.getDetailDescription() + " ");
            }
            throw new RobotFunctionCallException("Refreshing wheel encoding value failed. Return code msg from VRep: " + returnCodeDescriptions);
        }
    }

    /**
     * Get the pose from VRep.
     *
     * @return Returns the current pose.
     * @throws SensorNotEnabledException
     * @throws RobotFunctionCallException
     */
    @Override
    protected Pose refreshPose() throws SensorNotEnabledException, RobotFunctionCallException {
        if (!_poseEnabled) {
            throw new SensorNotEnabledException("Pose is not enabled");
        }

        //Pose returns x-position,y-position and rotation around z axis
        FloatWA outFloat = new FloatWA(3);
        int returnCode = 0;
        synchronized (lockAPI) {
            //Request Pose
            returnCode = _vrepRemote.simxCallScriptFunction(_clientID, _robotName, _vrepRemote.sim_scripttype_childscript, "getPoseForRemote", null, null, null, null, null, outFloat, null, null, _vrepRemote.simx_opmode_blocking);
        }
        if (returnCode == _vrepRemote.simx_return_ok) {
            float[] floats = outFloat.getArray();
            Pose pose = new Pose(floatArrayToDoubleArray(floats));
            return pose;
        } else {
            VRepReturnCode[] vRepReturnCode = VRepReturnCode.defineReturnCode(returnCode);

            StringBuilder returnCodeDescriptions = new StringBuilder();
            for(VRepReturnCode errorCode: vRepReturnCode){
                returnCodeDescriptions.append(errorCode.getDetailDescription() + " ");
            }
            throw new RobotFunctionCallException("Refreshing pose failed. Return code msg from VRep: " + returnCodeDescriptions);
        }
    }


    /**
     * Get the camera image from VRep
     *
     * @return Returns the current image of the camera
     * @throws CameraNotEnabledException
     * @throws RobotFunctionCallException
     */
    @Override
    protected CameraImage refreshCameraImage() throws CameraNotEnabledException, RobotFunctionCallException {
        if (!_cameraEnabled) {
            throw new CameraNotEnabledException("Camera is not enabled");
        }

        //Array size: resolutionX*resolutionY*3
        FloatWA outFloat = new FloatWA(_imageWidth * _imageHeight * 3);
        int returnCode = 0;
        synchronized (lockAPI) {
            //Request the float values
            returnCode = _vrepRemote.simxCallScriptFunction(_clientID, _robotName, _vrepRemote.sim_scripttype_childscript, "getCameraSensorsForRemote", null, null, null, null, null, outFloat, null, null, _vrepRemote.simx_opmode_blocking);
        }
        if (returnCode == _vrepRemote.simx_return_ok) {
            //get the float arry of the return value
            float[] rgbFloatValues = outFloat.getArray();

            //Create a new image object and set each pixel
            CameraImage tmpImg = new CameraImage(_imageWidth, _imageHeight);
            for (int y = 0; y < _imageHeight; y++) {
                for (int x = 0; x < _imageWidth; x++) {
                    //The values from V-REP are between 0 and 1 so it is necessary to multiply with 255 to get the correct value
                    int r = (int) (rgbFloatValues[3 * (y * _imageWidth + x) + 0] * 255);
                    int g = (int) (rgbFloatValues[3 * (y * _imageWidth + x) + 1] * 255);
                    int b = (int) (rgbFloatValues[3 * (y * _imageWidth + x) + 2] * 255);
                    //Set the pixel with the corresponding colour. The image returned from V-REP is up-side-down so and with "(_imageHeight - 1) - y" this is corrected so the image has the correct orientation.
                    tmpImg.setPixel(x, (_imageHeight - 1) - y, r, g, b);
                }
            }

            return tmpImg;
        } else {
            VRepReturnCode[] vRepReturnCode = VRepReturnCode.defineReturnCode(returnCode);

            StringBuilder returnCodeDescriptions = new StringBuilder();
            for(VRepReturnCode errorCode: vRepReturnCode){
                returnCodeDescriptions.append(errorCode.getDetailDescription() + " ");
            }
            throw new RobotFunctionCallException("Refreshing camera image failed. Return code msg from VRep: " + returnCodeDescriptions);
        }
    }

    /**
     * Sets the VRep parameter image cycle
     *
     * @param imageCycle
     * @return true if the setting the value was successful
     * @throws RobotFunctionCallException
     */
    public boolean setImageCycle(int imageCycle) throws RobotFunctionCallException {

        //Create a int array and assign the imageCycle value
        IntWA outInt = new IntWA(1);
        outInt.getArray()[0] = imageCycle;
        int returnCode = 0;
        synchronized (lockAPI) {
            returnCode = _vrepRemote.simxCallScriptFunction(_clientID, _robotName, _vrepRemote.sim_scripttype_childscript, "setImageCycleForRemote", outInt, null, null, null, null, null, null, null, _vrepRemote.simx_opmode_blocking);
        }
        //Check if successful
        if (returnCode == _vrepRemote.simx_return_ok) {
            return true;
        } else {
            VRepReturnCode[] vRepReturnCode = VRepReturnCode.defineReturnCode(returnCode);

            StringBuilder returnCodeDescriptions = new StringBuilder();
            for(VRepReturnCode errorCode: vRepReturnCode){
                returnCodeDescriptions.append(errorCode.getDetailDescription() + " ");
            }
            throw new RobotFunctionCallException("setImageCycle failed. Return code msg from VRep: " + returnCodeDescriptions);
        }

    }

    /**
     * Read all non-camera sensors in one signal call, except for pose.
     *
     * @return true if reading the values was successful.
     * @throws RobotFunctionCallException
     */
    public boolean senseAllTogether() throws RobotFunctionCallException {

        //Create the array for the return values
        CharWA inCharWA = new CharWA(1);
        int returnCode = 0;
        synchronized (lockAPI) {
            returnCode = _vrepRemote.simxGetStringSignal(_clientID, _signalName + "_allSens", inCharWA, _vrepRemote.simx_opmode_buffer);
        }
        //Check if successful
        if (returnCode == _vrepRemote.simx_return_ok) {
            double[] values = getDoubleValuesFromCharWA(inCharWA);

            //Get all the needed values
            synchronized (_proximitySensorValues) {
                _proximitySensorValues = getValuesOfArray(values, 0, 7);
            }
            synchronized (_lightSensorValues) {
                _lightSensorValues = getValuesOfArray(values, 8, 15);
            }
            synchronized (_groundSensorValues) {
                _groundSensorValues = getValuesOfArray(values, 16, 18);
            }
            synchronized (_accelerometerValues) {
                _accelerometerValues = new Acceleration(getValuesOfArray(values, 19, 21));
            }
            synchronized (_wheelEncodeValues) {
                _wheelEncodeValues = new WheelEncode(getValuesOfArray(values, 22, 23));
            }

            return true;
        } else {
            VRepReturnCode[] vRepReturnCode = VRepReturnCode.defineReturnCode(returnCode);

            StringBuilder returnCodeDescriptions = new StringBuilder();
            for(VRepReturnCode errorCode: vRepReturnCode){
                returnCodeDescriptions.append(errorCode.getDetailDescription() + " ");
            }
            throw new RobotFunctionCallException("fastSensingOverSignal failed. Return code msg from VRep: " + returnCodeDescriptions);
        }
    }

    /**
     * Convert the float[] to a double[]
     *
     * @param floatArray
     * @return
     */
    private double[] floatArrayToDoubleArray(float[] floatArray) {
        double[] doubleArray = new double[floatArray.length];
        for (int i = 0; i < floatArray.length; i++) {
            doubleArray[i] = (double) floatArray[i];
        }
        return doubleArray;
    }

    /**
     * Extracts the double values from the CharWA
     *
     * @param valuesCharWA
     * @return
     */
    private double[] getDoubleValuesFromCharWA(CharWA valuesCharWA) {
        FloatWA floatWA = new FloatWA(1);
        floatWA.initArrayFromCharArray(valuesCharWA.getArray());
        float[] values = floatWA.getArray();
        return floatArrayToDoubleArray(values);
    }

    /**
     * Gets a specific range of values from the array
     *
     * @param values
     * @param startIndex Index of the first element that needs to be extracted
     * @param endIndex   Index of the last element that needs to be extracted
     * @return Array with the extracted values
     */
    private double[] getValuesOfArray(double[] values, int startIndex, int endIndex) {
        //Array to store the values
        double[] seperatedValues = new double[endIndex - startIndex + 1];

        //Get the values
        for (int i = startIndex; i <= endIndex; i++) {
            seperatedValues[i - startIndex] = values[i];
        }

        return seperatedValues;
    }

    //endregion Updating sensors and camera

    //region Simulation

    /**
     * To be called from a controller when we initialized with synchronous=True. Starts the simulation in synchronous mode to achieve exact simulation independent of the frame rate. One step is performed.
     *
     * @throws SynchrounusModeNotActivatedException
     * @throws RobotFunctionCallException
     * @throws StepSimNotPossibleException
     */
    public void startsim() throws SynchrounusModeNotActivatedException, RobotFunctionCallException, StepSimNotPossibleException {
        if (!_synchronous) {
            throw new SynchrounusModeNotActivatedException("Startsim requires the synchronous mode to have been set in the init method");
        }
        synchronized (lockAPI) {
            int returnCode = _vrepRemote.simxStartSimulation(_clientID, _vrepRemote.simx_opmode_blocking);
            if (returnCode != _vrepRemote.simx_return_ok) {
                VRepReturnCode[] vRepReturnCode = VRepReturnCode.defineReturnCode(returnCode);

                StringBuilder returnCodeDescriptions = new StringBuilder();
                for(VRepReturnCode errorCode: vRepReturnCode){
                    returnCodeDescriptions.append(errorCode.getDetailDescription() + " ");
                }
                throw new RobotFunctionCallException("SimxStartSimulation failed. Return code msg from VRep: " + returnCodeDescriptions);
            }

            returnCode = _vrepRemote.simxSynchronous(_clientID, true);
            if (returnCode != _vrepRemote.simx_return_ok) {
                VRepReturnCode[] vRepReturnCode = VRepReturnCode.defineReturnCode(returnCode);

                StringBuilder returnCodeDescriptions = new StringBuilder();
                for(VRepReturnCode errorCode: vRepReturnCode){
                    returnCodeDescriptions.append(errorCode.getDetailDescription() + " ");
                }
                throw new RobotFunctionCallException("SimxSynchronous failed. Return code msg from VRep: " + returnCodeDescriptions);
            }
            stepsim(1);
        }
    }

    /**
     * Perform the specified number of steps in the simulator. Requires the instance to be initialized in synchronous mode.
     *
     * @param steps number of steps to simulate
     * @throws SynchrounusModeNotActivatedException
     * @throws StepSimNotPossibleException
     * @throws RobotFunctionCallException
     */
    public void stepsim(int steps) throws SynchrounusModeNotActivatedException, StepSimNotPossibleException, RobotFunctionCallException {
        if (!_synchronous) {
            throw new SynchrounusModeNotActivatedException("Startsim requires the synchronous mode to have been set in the init method.");
        }
        synchronized (lockAPI) {
            if (_hasOwnCameraThread || _hasOwnSensingThread) {
                throw new StepSimNotPossibleException("Stepsim is incompatible with sensing or camera threads.");
            }

            int returnCode = 0;
            for (int i = 0; i < steps; i++) {
                returnCode = _vrepRemote.simxSynchronousTrigger(_clientID);
                if (returnCode != _vrepRemote.simx_return_ok) {
                    VRepReturnCode[] vRepReturnCode = VRepReturnCode.defineReturnCode(returnCode);

                    StringBuilder returnCodeDescriptions = new StringBuilder();
                    for(VRepReturnCode errorCode: vRepReturnCode){
                        returnCodeDescriptions.append(errorCode.getDetailDescription() + " ");
                    }
                    throw new RobotFunctionCallException("Triggering single simulation step(SimxSynchronousTrigger) failed. Return code msg from VRep: " + returnCodeDescriptions);
                }
            }
        }
    }
    //endregion

    /**
     * Set the motor speed for the left and right motor
     *
     *
     * @param speed@return true = successful set the motor speeds; false = setting motor speeds failed
     * @throws VelocityLimitException
     * @throws RobotFunctionCallException
     */
    @Override
    public boolean setMotorSpeeds(Speed speed) throws VelocityLimitException, RobotFunctionCallException {
        //Check if the given speeds don't exceed the limits
        //changed 19.10.:HV
  //      if (Math.abs(speed.getLeft()) > _maxVel || Math.abs(speed.getLeft()) > _maxVel) {
   //         throw new VelocityLimitException("velocity: " + speed.getLeft() + ", " + speed.getLeft() +"-" + _maxVel + " => velocity <= " + _maxVel);
  //      }

        //Store the new motor speeds
        if (speed.getLeft() != _motorSpeed.getLeft() || speed.getRight() != _motorSpeed.getRight()) {
            _motorSpeed = speed;
        }

        //setVelocitiesForRemote float[] --> [0]= left , [1] = right
        FloatWA speedFloatWA = new FloatWA(2);
        float[] speedFloats = speedFloatWA.getArray();
        speedFloats[0] = (float) speed.getLeft();
        speedFloats[1] = (float) speed.getRight();

        speedFloats[0] =  Math.max(Math.min((float)speedFloats[0], (float)_maxVel), (float)-_maxVel);
        speedFloats[1] =  Math.max(Math.min((float)speedFloats[1], (float)_maxVel), (float)-_maxVel);

        int returnCode = 0;
        synchronized (lockAPI) {
            //Send command to VRep
            returnCode = _vrepRemote.simxCallScriptFunction(_clientID, _robotName, _vrepRemote.sim_scripttype_childscript, "setVelocitiesForRemote", null, speedFloatWA, null, null, null, null, null, null, _vrepRemote.simx_opmode_blocking);
        }
        //Check if successful
        if (returnCode == _vrepRemote.simx_return_ok) {
            return true;
        } else {
            VRepReturnCode[] vRepReturnCode = VRepReturnCode.defineReturnCode(returnCode);

            StringBuilder returnCodeDescriptions = new StringBuilder();
            for(VRepReturnCode errorCode: vRepReturnCode){
                returnCodeDescriptions.append(errorCode.getDetailDescription() + " ");
            }
            throw new RobotFunctionCallException("setMotorSpeeds left=" + speed.getLeft() + " right=" + speed.getRight() + " failed. Return code msg from VRep: " + returnCodeDescriptions);
        }

    }
}
