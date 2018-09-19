package ca.mcgill.ecse211.wallfollowing;

import lejos.hardware.motor.*;

public class BangBangController implements UltrasonicController {

  private final int bandCenter;
  private final int bandwidth;
  private final int motorLow;
  private final int motorHigh;
  private int leftMotorSpeed;
  private int rightMotorSpeed;
  private int distance;
  private int filterControl;
  private static final int FILTER_OUT = 60;

  public BangBangController(int bandCenter, int bandwidth, int motorLow, int motorHigh) {
    // Default Constructor
    this.bandCenter = bandCenter;
    this.bandwidth = bandwidth;
    this.motorLow = motorLow;
    this.motorHigh = motorHigh;
    this.leftMotorSpeed = 0;
    this.leftMotorSpeed = 0;
    this.filterControl = 0;
 
//This section of code was removed because it caused the motors to start running before the sensor was activated, leading to error
//    WallFollowingLab.leftMotor.setSpeed(motorHigh); // Start robot moving forward
//    WallFollowingLab.rightMotor.setSpeed(motorHigh);
//    WallFollowingLab.leftMotor.forward();
//    WallFollowingLab.rightMotor.forward();
  }

  @Override
  public void processUSData(int distance) {
    
    // rudimentary filter - toss out invalid samples corresponding to null
    // signal.
    // (n.b. this was not included in the Bang-bang controller, but easily
    // could have).
    //
    if (distance >= 255 && filterControl < FILTER_OUT) {
      // bad value, do not set the distance var, however do increment the
      // filter value
      filterControl++;
    } else if (distance >= 255) {
      // We have repeated large values, so there must actually be nothing
      // there: leave the distance alone
      this.distance = distance;
    } else {
      // distance went below 255: reset filter and leave
      // distance alone.
      filterControl = 0;
      this.distance = distance;
    }
    // TODO: process a movement based on the us distance passed in (BANG-BANG style)
    
    int error = this.distance - bandCenter;
    
    //If the error is within the limits, continue forward
    if (Math.abs(error) <= bandwidth) {
    		WallFollowingLab.leftMotor.setSpeed(motorHigh);
    		WallFollowingLab.rightMotor.setSpeed(motorHigh);
    		this.leftMotorSpeed = motorHigh;
    		this.rightMotorSpeed = motorHigh;
    		WallFollowingLab.leftMotor.forward();
    		WallFollowingLab.rightMotor.forward();
    }
     //If the error is negative, move farther from the wall (right turn)
    else if (error < 0) {
    		//An even more negative error means that there is a convex corner, requiring a bigger adjustment
    		if (error < -5) {
    			WallFollowingLab.leftMotor.setSpeed(motorLow);
        		WallFollowingLab.rightMotor.setSpeed(motorLow);
        		this.leftMotorSpeed = motorLow;
        		this.rightMotorSpeed = motorLow;
        		
        		WallFollowingLab.leftMotor.forward();
        		WallFollowingLab.rightMotor.backward();
    		}
    		else {
    			WallFollowingLab.leftMotor.setSpeed(motorHigh);
        		WallFollowingLab.rightMotor.setSpeed(motorLow);
        		
        		this.leftMotorSpeed = motorHigh;
        		this.rightMotorSpeed = motorLow;
        		WallFollowingLab.leftMotor.forward();
        		WallFollowingLab.rightMotor.forward();
    		}
    }
    
    //A positive error means we need to move closer to the wal
    else if (error > 0) {
    		WallFollowingLab.leftMotor.setSpeed(motorLow);
		WallFollowingLab.rightMotor.setSpeed(motorHigh);
		this.leftMotorSpeed = motorLow;
		this.rightMotorSpeed = motorHigh;
		
		WallFollowingLab.leftMotor.forward();
		WallFollowingLab.rightMotor.forward();
    }
    
  }

  @Override
  public String readUSLSpeed() {
	  return "Left motor: " + this.leftMotorSpeed;
  }
  
  @Override
  public String readUSRSpeed() {
	  return "Right motor: " + this.rightMotorSpeed;
  }
  
  @Override
  public int readUSDistance() {
    return this.distance;
  }
}
