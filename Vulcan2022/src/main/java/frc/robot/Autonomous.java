package frc.robot; 

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
 
import javax.swing.TransferHandler.TransferSupport;

import com.revrobotics.RelativeEncoder;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.SerialPort;

import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;

enum autoStep {
    init,
    shoot,
    driveForward,
    turn;
};

public class Autonomous {
    private MotorControllerGroup leftMotors, rightMotors; 
    private RelativeEncoder leftEncoder, rightEncoder;
    //private int autonomousStep = -1;
    private double FEET = 8.50; // To go one FEET, the robot encoder has to read ~8.50 inches of the wheel
    private double leftDiff = 0, rightDiff = leftDiff;
    private double lastLeftDiff = 0, lastRightDiff = lastLeftDiff;
    autoStep autonomousStep = autoStep.init;

    public void autonomousInit() {
        //
    }
    /**
     * STEPS to 4 ball auto + 1 human player
     * 
     * 1. Intake on
     * 2. Forward 42"
     * 3. Turn 147.75 degrees
     * 
     * 
     * 
     * 
     * 1. Shoot preloaded ball
     * 2. Turn to ball with vision (approx 180 - x)
     * 3. Intake on
     * 4. Forward 42" (best if distance is caluclated with vision)
     * (ball picked up)
     * 5. Turn to ball with vision (approx 147.75°)
     * 6. Forward 120" (best if distance is calculated with vision)
     * (ball picked up)
     * 7. Turn to goal with vision (approx 120°)
     * 8. Shoot two balls into goal
     * 9. Turn to final ball (approx 170°) (at human player station)
     * 10. Forward 160" (Likely won't work, vision plz!!!!!)
     * (ball picked up)
     * 11. Backward the distance moved last step
     * 12. Aim to goal with vision
     * 13. Shoot
     * 
     * Human: Aimbot
     */
    
    public double getLeftEncoderDistance() {
        return Container.getInstance().getLeftInches();
    }
    public double getRightEncoderDistance() {
        return Container.getInstance().getRightInches();
    }

    public void calculateEncoderDiff() {
        leftDiff = getLeftEncoderDistance() - lastLeftDiff;
        rightDiff = getRightEncoderDistance() - lastRightDiff;

        lastLeftDiff = leftDiff;
        lastRightDiff = rightDiff;
    }

    public void move(double leftSpeed, double rightSpeed) {
        double offset = 0.05;

        calculateEncoderDiff();
        
        if (leftDiff > rightDiff) {
            leftSpeed -= offset;
            rightSpeed += offset;
        } else if (leftDiff < rightDiff) {
            leftSpeed += offset;
            rightSpeed -= offset;
        }

        leftMotors.set(leftSpeed);
        rightMotors.set(rightSpeed);
    }

    public float getabsoluteDirection() {
        return 0;//Container.getInstance().ahrs.getYaw();
    }

    public void autonomousPeriodic() {
        switch (autonomousStep) {
            case init: {
                
                break;
            }
            case shoot: {

                break;
            }
            case driveForward: {

                break;
            }
            case turn: {

                break;
            }
        };
    }
}
