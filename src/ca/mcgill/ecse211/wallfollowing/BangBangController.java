package ca.mcgill.ecse211.wallfollowing;

import lejos.hardware.motor.*;

public class BangBangController implements UltrasonicController {

  private final int bandCenter;
  private final int bandwidth;
  private final int motorLow;
  private final int motorHigh;
  private int distance;

  public BangBangController(int bandCenter, int bandwidth, int motorLow, int motorHigh) {
    // Default Constructor
    this.bandCenter = bandCenter;
    this.bandwidth = bandwidth;
    this.motorLow = motorLow;
    this.motorHigh = motorHigh;
    WallFollowingLab.leftMotor.setSpeed(motorHigh); // Start robot moving forward
    WallFollowingLab.rightMotor.setSpeed(motorHigh);
    WallFollowingLab.leftMotor.forward();
    WallFollowingLab.rightMotor.forward();
  }

  @Override
  public void processUSData(int distance) {
    this.distance = distance;
    // TODO: process a movement based on the us distance passed in (BANG-BANG style)
    
    int error = this.distance - bandCenter;
    
    //If the error is within the limits, continue forward
    if (Math.abs(error) <= bandwidth) {
    		WallFollowingLab.leftMotor.setSpeed(motorHigh);
    		WallFollowingLab.rightMotor.setSpeed(motorHigh);
    		WallFollowingLab.leftMotor.forward();
    		WallFollowingLab.rightMotor.forward();
    }
     //If the error is negative, move farther from the wall (right turn)
    else if (error < 0) {
    		//An even more negative error means that there is a convex corner, requiring a bigger adjustment
    		if (error < -3) {
    			WallFollowingLab.leftMotor.setSpeed(motorLow);
        		WallFollowingLab.rightMotor.setSpeed(motorLow);
        		WallFollowingLab.leftMotor.forward();
        		WallFollowingLab.rightMotor.backward();
    		}
    		else {
    			WallFollowingLab.leftMotor.setSpeed(motorHigh);
        		WallFollowingLab.rightMotor.setSpeed(motorLow);
        		WallFollowingLab.leftMotor.forward();
        		WallFollowingLab.rightMotor.forward();
    		}
    }
    
    //A positive error means we need to move closer to the wal
    else if (error > 0) {
    		WallFollowingLab.leftMotor.setSpeed(motorLow);
		WallFollowingLab.rightMotor.setSpeed(motorHigh);
		WallFollowingLab.leftMotor.forward();
		WallFollowingLab.rightMotor.forward();
    }
    
  }

  @Override
  public int readUSDistance() {
    return this.distance;
  }
}
