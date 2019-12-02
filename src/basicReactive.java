//package at.fhv.dgr1992.simpletest;

import at.fhv.dgr1992.differentialWheels.Acceleration;
import at.fhv.dgr1992.differentialWheels.Speed;
import at.fhv.dgr1992.ePuck.ePuckVRep.EPuckVRep;
import at.fhv.dgr1992.differentialWheels.CameraImage;
import at.fhv.dgr1992.differentialWheels.CameraImagePixel;
import at.fhv.dgr1992.ePuck.ePuckVRep.exceptions.StepSimNotPossibleException;
import at.fhv.dgr1992.ePuck.ePuckVRep.exceptions.SynchrounusModeNotActivatedException;
import at.fhv.dgr1992.exceptions.RobotFunctionCallException;
import at.fhv.dgr1992.exceptions.SensorNotEnabledException;
import at.fhv.dgr1992.exceptions.VelocityLimitException;
import org.apache.commons.math3.linear.*;
import java.util.Arrays;


public class basicReactive {

	int resolX = 64, resolY = 64;
    double maxVel = 120.0 * java.lang.Math.PI / 180.0;  // 4/3 of a full wheel turn
    double noDetectionDistance = 0.05;

    //the next three declarations are just example code, not used in this behavior
    double[][] proportionalMatrixData = new double[][]{{0, 0, 0, 0},{0, 0, 0, 0}};
    RealMatrix proportionalMatrix = MatrixUtils.createRealMatrix(proportionalMatrixData);
    double[] baseVelocity = new double[]{maxVel / 6.0, maxVel / 6.0};



    boolean vectorGreater(double[] v1, double[] v2) {
    	//compare two vector element-wise
        if (v1.length != v2.length) {
            System.err.println("tries to compare two vectors of different lengths");
            System.exit(1);
        }
        for (int i=0; i < v1.length; i++)
            if (v1[i] <= v2[i])
                return false;
        return true;
    }



    void startBehavior()  {
    	
    	boolean synchron = true;
    	
        EPuckVRep epuck = new EPuckVRep("ePuck","127.0.0.1",19999, synchron);
        
        try {
            if (!epuck.isConnected()) {
                epuck.connect();
            }
            epuck.enableAllSensors();
            //epuck.enableCamera();
            //epuck.enablePose();   //in all exercises, you are not allowed to use this sensor 
            epuck.setSenseAllTogether();
            epuck.setMotorSpeeds(new Speed(0, 0));
            if (synchron)
            	epuck.startsim();
            int stepCounter = 0;

            //CameraImage image = new CameraImage(resolX,resolY);

            while(epuck.isConnected()) {
                stepCounter += 1;
                //boolean newImage = false;
                epuck.senseAllTogether();
                double[] distVector = epuck.getProximitySensorValues();
                double[] lightVector = epuck.getLightSensorValues();
                //Acceleration acceleration = epuck.getAccelerometerValues();

                /*
                if (stepCounter%4 == 0) {
                    try {
                        image = epuck.getCameraImage();
                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }
                    newImage = true;
                }
				*/
                
                if (vectorGreater(Arrays.copyOfRange(distVector, 0, 6), new double[]{0.25 * noDetectionDistance, 0.25 * noDetectionDistance, 0.25 * noDetectionDistance, 0.25 * noDetectionDistance,
                		                                                            0.25 * noDetectionDistance, 0.25 * noDetectionDistance})) 
                    // nothing in front, go ahead approaching a box
                      epuck.setMotorSpeeds(new Speed(maxVel, maxVel));
                else  {
                   // Obstacle in front
                	if (distVector[0] + distVector[1] + distVector[2] < distVector[3] +distVector[4] + distVector[5])  
                		//turn clockwise
                		epuck.setMotorSpeeds(new Speed(maxVel/2.0, -maxVel/2.0));
                	else
                		//turn counterclockwise
               		 	epuck.setMotorSpeeds(new Speed(-maxVel/2.0, +maxVel/2.0));
                }

                if (synchron)
                	epuck.stepsim(1);
				else
					Thread.sleep(50);
					
            }


        } catch (VelocityLimitException e) {
            e.printStackTrace();
        } catch (RobotFunctionCallException e) {
            e.printStackTrace();
        } catch (SensorNotEnabledException e) {
            e.printStackTrace();
        } catch (SynchrounusModeNotActivatedException e) {
            e.printStackTrace();
        } catch (StepSimNotPossibleException e) {
            e.printStackTrace();
    	} catch (InterruptedException e) {
    		e.printStackTrace();
    	}
    }

    public static void main(String[] args) {
        basicReactive pbcontroller = new basicReactive();
        pbcontroller.startBehavior();
    }

}
