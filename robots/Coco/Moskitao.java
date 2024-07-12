package Coco;
import robocode.*;
import java.awt.Color;
import static robocode.util.Utils.normalRelativeAngleDegrees;
// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html

/**
 */
public class Moskitao extends AdvancedRobot
{
    boolean movingForward;
    /**
     */
    public void run() {
   	 // Initialization of the robot should be put here

   	 // After trying out your robot, try uncommenting the import at the top,
   	 // and the next line:

   	 // setColors(Color.red,Color.blue,Color.green); // body,gun,radar
    	setColors(Color.green,Color.blue,Color.green);
   	 // Robot main loop
   	 while(true) {
   		 // Replace the next 4 lines with any behavior you would like
   		 setAhead(20000);
   		 movingForward = true;
   		 setTurnRight(80);
   		 waitFor(new TurnCompleteCondition(this));
   		 setTurnLeft(90);
   		 turnGunRight(180);
   		 waitFor(new TurnCompleteCondition(this));
   		 setTurnRight(90);
   		 turnGunRight(180);
   		 
   	 }
    }

    /**
     * onScannedRobot: What to do when you see another robot
     */
    public void onScannedRobot(ScannedRobotEvent e) {
   	 // Replace the next line with any behavior you would like
	 
   	 double absoluteBearing = getHeading() + e.getBearing();
   	 double bearingFromGun = normalRelativeAngleDegrees(absoluteBearing - getGunHeading());
   	 
    if (Math.abs(bearingFromGun) <= 3) {
   		 turnGunRight(bearingFromGun);
   		 // We check gun heat here, because calling fire()
   		 // uses a turn, which could cause us to lose track
   		 // of the other robot.
   		 if (getGunHeat() == 0) {
   			 fire(Math.min(3 - Math.abs(bearingFromGun), getEnergy() - .1));
   		 }
   	 }
   		 else {
   		 turnGunRight(bearingFromGun);
   	 }
   	 if (bearingFromGun == 0) {
   		 scan();
   	 }
    }

    /**
     * onHitByBullet: What to do when you're hit by a bullet
     */
//    public void onHitByBullet(HitByBulletEvent e) {
   	 // Replace the next line with any behavior you would like
   	 //back(100);
   	 //setTurnLeft(90);
    //}
    
    /**
     * onHitWall: What to do when you hit a wall
     */
    public void onHitWall(HitWallEvent e) {
   	 // Replace the next line with any behavior you would like
   	 reverseDirection();
    }    
   	 public void reverseDirection() {
   	 if (movingForward) {
   		 setBack(2000);
   		 movingForward = false;
   	 } else {
   		 setAhead(2000);
   		 movingForward = true;
   	 }
    }
   	 public void onHitRobot(HitRobotEvent e) {
   	 // If we're moving the other robot, reverse!
   	 if (e.isMyFault()) {
   		 reverseDirection();
   	 }
    }
   	 public void onWin(WinEvent e) {
   	 // Victory dance
   	 turnRight(36000);
    }
}