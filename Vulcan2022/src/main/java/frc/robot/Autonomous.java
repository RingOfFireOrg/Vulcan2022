package frc.robot; 

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
 
import javax.swing.TransferHandler.TransferSupport;

import com.revrobotics.RelativeEncoder;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.SerialPort;

import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;

public class Autonomous {
    private MotorControllerGroup leftMotors, rightMotors; 
    private RelativeEncoder leftEncoder, rightEncoder;
    private double leftEncoderOffset = 0, rightEncoderOffset = 0;
    private int autonomousStep = 0;
    private double FEET = 8.50;
    private double leftDiff = 0, rightDiff = 0;
    double leftPower = 0.2, rightPower = leftPower;
    double prevLeftEncoderDistance = 0, prevRightEncoderDistance = 0;
    private double driveOffset = 0;
    private int readjustDriveMotorCount = 0;

    //autoStep autonomousStep = autoStep.init;

    public void autonomousInit() {
        rightMotors = new MotorControllerGroup(Container.get().frontRightMotor,
                Container.get().backRightMotor);
        leftMotors = new MotorControllerGroup(Container.get().frontLeftMotor,
                Container.get().backLeftMotor);

        rightMotors.setInverted(true);
        leftMotors.setInverted(true);
    }
    
    public double getLeftEncoderDistance() {
        return -(Container.get().getLeftInches() - leftEncoderOffset);
    }
    public double getRightEncoderDistance() {
        return -(Container.get().getRightInches() - rightEncoderOffset);
    }

    public void resetEncoders() {
        leftEncoderOffset = Container.get().getLeftInches();
        rightEncoderOffset = Container.get().getRightInches();
    }

    public void intakeIn() {
        Container.get().intakeMotor.set(1);
    }

    public void intakeOut() {
        Container.get().intakeMotor.set(-1);
    }

    public void transferIn() {

    }
    
    public void driveForward() {
        // double offset = 0.003;
        // readjustDriveMotorCount++;

        // if (readjustDriveMotorCount > 30) {
        //     readjustDriveMotorCount = 0;
        //     double leftEncoderDistance = getLeftEncoderDistance();
        //     double rightEncoderDistance = getRightEncoderDistance();

        //     double leftDiff = leftEncoderDistance - prevLeftEncoderDistance;
        //     double rightDiff = rightEncoderDistance - prevRightEncoderDistance;

        //     prevLeftEncoderDistance = leftEncoderDistance;
        //     prevRightEncoderDistance = rightEncoderDistance;

        //     // if left rotated more than right, slow down left & speed up right, else opposite
        //     if (leftDiff > rightDiff) {
        //         leftPower = leftPower - offset;
        //         rightPower = rightPower + offset;
        //     }
        //     else if (leftDiff < rightDiff) {
        //         leftPower = leftPower + offset;
        //         rightPower = rightPower - offset;
        //     }
        // }

        leftMotors.set(0.2);
        rightMotors.set(0.2);
    }

    public void driveBackward() {
        leftMotors.set(-0.2);
        rightMotors.set(-0.2);
    }

    public void turnLeft() {
        leftMotors.set(-0.2);
        rightMotors.set(0.2);
    }

    public void turnRight() {
        leftMotors.set(0.2);
        rightMotors.set(-0.2);
    }

    public void driveStop() {
        leftMotors.set(0);
        rightMotors.set(0);
    }

    public float getAbsoluteDirection() {
        return  Container.get().ahrs.getYaw();
    }

    public void reset(){
        driveStop();
        resetEncoders();
    }

    /**
     * 1. 
     */

    public void autonomousPeriodic() {
        System.out.print(getLeftEncoderDistance());
        switch (autonomousStep) {
            case 0: {
                reset();
                autonomousStep++;
                break;
            }
            case 1: {
                if (getLeftEncoderDistance() > FEET * 3) {
                    autonomousStep++;
                    reset();
                } else {
                    driveForward();
                }
                break;
            }
            case 2: {
                if (getLeftEncoderDistance() < -FEET * 3) {
                    autonomousStep++;
                    reset();
                } else {
                    driveBackward();
                }
                break;
            }
            case 3: {
                if (getAbsoluteDirection() > -90) {
                    autonomousStep++;
                    reset();
                } else {
                    turnRight();
                }
                break;
            }
            case 4: {
                if (getAbsoluteDirection() > -90) {
                    autonomousStep++;
                    reset();
                } else {
                    turnLeft();
                }
                break;
            }
            case 5: {
                if (getAbsoluteDirection() < 0) {
                    autonomousStep++;
                    reset();
                } else {
                    turnRight();
                }
                break;
            }
            case 6: {
                break;
            }
        };
    }
}