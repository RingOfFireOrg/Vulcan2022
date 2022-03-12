package frc.robot; 

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
 
import javax.swing.TransferHandler.TransferSupport;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.kauailabs.navx.frc.AHRS;
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
    private double turnOffset = 8;
    public TalonFX shooter;
    private AHRS ahrs;
    private int timer = 0;
    
    //autoStep autonomousStep = autoStep.init;

    public void autonomousInit() {
        rightMotors = new MotorControllerGroup(Container.get().frontRightMotor,
                Container.get().backRightMotor);
        leftMotors = new MotorControllerGroup(Container.get().frontLeftMotor,
                Container.get().backLeftMotor);

        rightMotors.setInverted(true);
        leftMotors.setInverted(true);

        shooter = Container.get().shooter;
        ahrs = Container.get().ahrs;
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

    public void shoot() {
        shooter.set(ControlMode.PercentOutput, .75);
    }

    public void stopShooter() {
        shooter.set(ControlMode.PercentOutput, 0);
    }

    public float getAbsoluteDirection() {
        return ahrs.getYaw();
    }

    public void turn(String direction, double amount) {
        if (direction == "right") {
            if (getAbsoluteDirection() < amount - turnOffset) {
                turnRight();
            } else {
                autonomousStep++;
                reset();
            }
        } else if (direction == "left") {
            if (getAbsoluteDirection() > amount + turnOffset) {
                turnLeft();
            } else {
                autonomousStep++;
                reset();
            }
        }
    }

    public void reset(){
        driveStop();
        resetEncoders();
        stopShooter();
        ahrs.zeroYaw();
        timer = 0;
    }

    public void autonomousPeriodic() {
        SmartDashboard.putNumber("Absolute Direction", getAbsoluteDirection());
        switch (autonomousStep) {
            case 0: {
                reset();
                autonomousStep = 400;
                break;
            }
            case 1: {
                //shoot 1 ball
                if (timer < 60) {
                    transferIn();
                    shoot();
                    timer++;
                } else {
                    autonomousStep++;
                    reset();
                }
                break;
            }
            case 2: {
                //turn robot 122.25 degrees
                turn("right", 122.25);
                break;
            }
            case 400: {
                if (getAbsoluteDirection() < 90 - turnOffset) {
                    turnRight();
                } else {
                    autonomousStep++;
                    reset();
                }
                break;
            }
            case 401: {
                if (getAbsoluteDirection() > -90 + turnOffset) {
                    turnLeft();
                } else {
                    autonomousStep++;
                    reset();
                }
                break;
            }
            case 402: {
                if (getAbsoluteDirection() < 0 - turnOffset) {
                    turnRight();
                } else {
                    autonomousStep++;
                    reset();
                }
                break;
            }
            case 6: {
                break;
            }
        };
    }
}