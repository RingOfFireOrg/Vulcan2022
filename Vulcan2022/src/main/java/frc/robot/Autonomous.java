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
    turn,
    resetAll,
};

public class Autonomous {
    private MotorControllerGroup leftMotors, rightMotors; 
    private RelativeEncoder leftEncoder, rightEncoder;
    private double leftEncoderOffset = 0, rightEncoderOffset = 0;
    private int autonomousStep = 0;
    private double FEET = 8.50; // To go one FEET, the robot encoder has to read ~8.50 inches of the wheel
    private double leftDiff = 0, rightDiff = 0;
    double leftPower = 0.2, rightPower = leftPower;
    double prevLeftEncoderDistance = 0, prevRightEncoderDistance = 0;
    private double driveOffset = 0;

    //autoStep autonomousStep = autoStep.init;

    public void autonomousInit() {
        rightMotors = new MotorControllerGroup(Container.get().frontRightMotor,
                Container.get().backRightMotor);
        leftMotors = new MotorControllerGroup(Container.get().frontLeftMotor,
                Container.get().backLeftMotor);

        //rightMotors.setInverted(true);
        leftMotors.setInverted(true);
    }
    /**
     * STEPS to 4 ball auto + 1 human player
    NEW
     * 1. Intake on
     * 2. Forward 42"
     * 3. Turn 147.75 degrees

    OLD
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
    
    HUMAN PLAYER feeds 
     * Human: Aimbot
     */
    
    public double getLeftEncoderDistance() {
        return Container.get().getLeftInches() - leftEncoderOffset;
    }
    public double getRightEncoderDistance() {
        return Container.get().getRightInches() - rightEncoderOffset;
    }

    public void resetEncoders() {
        leftEncoderOffset = Container.get().getLeftInches();
        rightEncoderOffset = Container.get().getRightInches();
    }
    
    public void driveForward() {
        double offset = 0.03;

        double leftEncoderDistance = getLeftEncoderDistance();
        double rightEncoderDistance = getRightEncoderDistance();

        double leftDiff = leftEncoderDistance - prevLeftEncoderDistance;
        double rightDiff = rightEncoderDistance - prevRightEncoderDistance;

        prevLeftEncoderDistance = leftEncoderDistance;
        prevRightEncoderDistance = rightEncoderDistance;

        // if left rotated more than right, slow down left & speed up right, else opposite
        if (leftDiff > rightDiff) {
            leftPower = leftPower - offset;
            rightPower = rightPower + offset;
        }
        else if (leftDiff < rightDiff) {
            leftPower = leftPower + offset;
            rightPower = rightPower - offset;
        }

        leftMotors.set(leftPower);
        rightMotors.set(rightPower);

        System.out.println(leftDiff);
        System.out.println(rightDiff);
        System.out.println(leftPower);
        System.out.println(rightPower);
        System.out.println(System.lineSeparator());
    }

    public float getabsoluteDirection() {
        return 0;//Container.get().ahrs.getYaw();
    }

    public void autonomousPeriodic() {
        switch (autonomousStep) {
            case 0: {
                resetEncoders();
                autonomousStep++;
                break;
            }
            case 1: {
                driveForward();
                break;
            }
        };
    }
}