package ca.mcgill.ecse211.wallfollowing;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class PController implements UltrasonicController {

  /* Constants */
  private static final int MOTOR_SPEED = 200;
  private static final int FILTER_OUT = 60;
  
  //This is the proportion that we want the error to account for
  private static final double proportionConstant = 1.8;
  
  private static final int MAX_CORRECTION = 50;

  private final int bandCenter;
  private final int bandWidth;
  private int distance;
  private int filterControl;

  public PController(int bandCenter, int bandwidth) {
    this.bandCenter = bandCenter;
    this.bandWidth = bandwidth;
    this.filterControl = 0;

//This section of code was removed because it caused the motors to start running before the sensor was activated, leading to error
//    WallFollowingLab.leftMotor.setSpeed(MOTOR_SPEED); // Initalize motor rolling forward
//    WallFollowingLab.rightMotor.setSpeed(MOTOR_SPEED);
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

    // TODO: process a movement based on the us distance passed in (P style)
    int change;
    int error = this.distance - bandCenter;
    
    if (Math.abs(error) <= bandWidth) {
		WallFollowingLab.leftMotor.setSpeed(MOTOR_SPEED);
		WallFollowingLab.rightMotor.setSpeed(MOTOR_SPEED);
		WallFollowingLab.leftMotor.forward();
		WallFollowingLab.rightMotor.forward();
}
    //If the error is negative, move farther from the wall (right turn)
    else if (error < 0) {
		//An even more negative error means that there is a convex corner, requiring a bigger adjustment
		if (error < - this.bandWidth) {
			WallFollowingLab.leftMotor.setSpeed(MOTOR_SPEED);
    			WallFollowingLab.rightMotor.setSpeed(MOTOR_SPEED);
    			WallFollowingLab.leftMotor.forward();
    			WallFollowingLab.rightMotor.backward();
		}
		else {
			change = calcGain(error);
			WallFollowingLab.leftMotor.setSpeed(MOTOR_SPEED + change);
    			WallFollowingLab.rightMotor.setSpeed(MOTOR_SPEED - change);
    			WallFollowingLab.leftMotor.forward();
    			WallFollowingLab.rightMotor.forward();
		}
}

    //A positive error means we need to move closer to the wal
    else if (error > 0) {
    		change = calcGain(error);
    		WallFollowingLab.leftMotor.setSpeed(MOTOR_SPEED - change);
    		WallFollowingLab.rightMotor.setSpeed(MOTOR_SPEED + change);
    		WallFollowingLab.leftMotor.forward();
    		WallFollowingLab.rightMotor.forward();
    }
  }


  @Override
  public int readUSDistance() {
    return this.distance;
  }
  
  public int calcGain(int error) {
	  int change;
	  
	  error = Math.abs(error);
	  
	  //The change is based on the error and the proportion constant
	  change = (int)(proportionConstant*(double)(error));
	  
	  //This is so that the speed correction will never exceed the original speed of the motors, so that 
	  //turns are not too drastic
	  if  (change >= MOTOR_SPEED || change >= MAX_CORRECTION) {
		  change = MAX_CORRECTION;
	  }
	  
	  return change;
  }

}
