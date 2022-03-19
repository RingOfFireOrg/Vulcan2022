package frc.robot; 

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
 
import javax.swing.TransferHandler.TransferSupport;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.SerialPort;

import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;

public class Autonomous {
    private MotorControllerGroup leftMotors, rightMotors; 
    public TalonFX shooter;
    //private CANSparkMax turret;
    private AHRS ahrs;

    private double driveOffset = 3;
    private double turnOffset = 12;
    private int timer = 0;
    private int second = 20;
    private double FEET = 8.50;   
    private double leftEncoderOffset = 0;
    private double rightEncoderOffset = 0;
    private double leftPower = 0.4, rightPower = leftPower;
    
    private int autonomousStep = 0;
    private String autoType = "smartauto";

    public void autonomousInit() {
        rightMotors = new MotorControllerGroup(
            Container.get().frontRightMotor,
            Container.get().backRightMotor
        );
        leftMotors = new MotorControllerGroup(
            Container.get().frontLeftMotor,
            Container.get().backLeftMotor
        );

        rightMotors.setInverted(true);
        leftMotors.setInverted(true);

        shooter = Container.get().shooter;
        ahrs = Container.get().ahrs;
        //turret = Container.get().turretMotor;
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

    public void intakeStop() {
        Container.get().intakeMotor.set(0);
    }

    public void transferIn() {
        Container.get().transferMotor1.set(0.6);
        Container.get().transferMotor2.set(-0.6);
    }

    public void transferOut() {
        Container.get().transferMotor1.set(-0.6);
        Container.get().transferMotor2.set(0.6);
    }

    public void transferStop() {
        Container.get().transferMotor1.set(0);
        Container.get().transferMotor2.set(0);
    }
    
    public void driveForward() {
        leftMotors.set(leftPower);
        rightMotors.set(rightPower);
    }

    public void driveBackward() {
        leftMotors.set(-leftPower);
        rightMotors.set(-rightPower);
    }

    public void turnLeft() {
        leftMotors.set(-leftPower / 2.5);
        rightMotors.set(rightPower / 2.5);
    }

    public void turnRight() {
        leftMotors.set(leftPower / 2.5);
        rightMotors.set(-rightPower / 2.5);
    }

    public void driveStop() {
        leftMotors.set(0);
        rightMotors.set(0);
    }

    public void shoot() {
        shooter.set(ControlMode.PercentOutput, .30);
    }

    public void shootHigh() {
        shooter.set(ControlMode.PercentOutput, .65);
    }

    public void shooterStop() {
        shooter.set(ControlMode.PercentOutput, 0);
    }

    // public void turretRight() {
    //     turret.set(0.15);
    // }

    // public void turretLeft() {
    //     turret.set(-0.15);
    // }

    // public void turretStop() {
    //     turret.set(0);
    // }

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

    public void drive(String direction, double amount) {
        if (direction == "forward") {
            if (getLeftEncoderDistance() < amount - driveOffset) {
                driveForward();
            } else {
                autonomousStep++;
                reset();
            }
        } else if (direction == "backward") {
            if (getLeftEncoderDistance() > amount + driveOffset) {
                driveBackward();
            } else {
                autonomousStep++;
                reset();
            }
        }
    }

    public void reset(){
        driveStop();
        resetEncoders();
        shooterStop();
        //turretStop();
        intakeStop();
        timer = 0;
    }

    public void autonomousPeriodic() {
        SmartDashboard.putNumber("Absolute Direction", getAbsoluteDirection());
        if (autoType == "dumbauto") {
            switch (autonomousStep) {
                case 0: {
                    reset();
                    autonomousStep++;
                    break;
                }
                case 1: {
                    intakeIn();
                    drive("forward", FEET * 8.34);
                    break; 
                }
                case 2: {
                    turn("right", 180);
                    break;
                }
                case 3: {
                    intakeIn();
                    drive("forward", FEET * 14.51);
                    break; 
                }
                case 4: {
                    //Start shooter
                    intakeStop();
                    if (timer < second * 2.5) {
                        shoot();
                        timer++;
                    } else {
                        autonomousStep++;
                        reset();
                    }
                    break;
                }
                case 5: {
                    if (timer < second * 4.5) {
                        transferIn();
                        shoot();
                        timer++;
                    } else {
                        autonomousStep++;
                        reset();
                    }
                    break;
                }
                case 6: {
                    shooterStop();
                    break;
                }
            }
        } 
        else if (autoType == "smartauto") {
            switch (autonomousStep) {
                case 0: {
                    reset();
                    autonomousStep++;
                    break;
                }
                case 1: {
                    intakeIn();
                    drive("forward", FEET * 8.34);
                    break; 
                }
                case 2: {
                    turn("right", 180);
                    break;
                }
                case 3: {
                    intakeIn();
                    drive("forward", FEET * 5.51/*14.51*/);
                    break; 
                }
                case 4: {
                    //Start shooter
                    intakeStop();
                    if (timer < second * 3) {
                        shootHigh();
                        timer++;
                    } else {
                        autonomousStep++;
                        reset();
                    }
                    break;
                }
                case 5: {
                    if (timer < second * 4.5) {
                        transferIn();
                        shootHigh();
                        timer++;
                    } else {
                        autonomousStep++;
                        reset();
                    }
                    break;
                }
                case 6: {
                    shooterStop();
                    break;
                }
            }
        }
        
        /*else {
            switch (autonomousStep) {
                case 0: {
                    reset(); 
                    autonomousStep++;
                    break;
                }
                case 1: {
                    //shoot 1 ball
                    if (timer < second * 2.5) {
                        shoot();
                        timer++;
                    } else if (timer < second * 3.5) {
                        shoot();
                        transferIn();
                        timer++;
                    } else {
                        autonomousStep++;
                        reset();
                    }
                    break;
                }
                case 2: {
                    //turn the robot 90 degrees
                    turn("right", 90);
                    break;
                }
                case 3: {
                    intakeIn();
                    drive("forward", FEET * 9.34);
                    break;
                }
                case 4: {
                    //turn("left", 122.25);
                    if (getAbsoluteDirection() < 180 - turnOffset) {
                        turnRight();
                    } else if (getAbsoluteDirection() < -122.25 + turnOffset) {
                        turnRight();
                    } else {
                        reset();
                    }

                    if (timer < second) {
                        turretRight();
                        timer++;
                    }

                    break;
                }
                case 5: {
                    drive("forward", FEET * 14.2);
                    intakeIn();
                    break;
                }
                case 6: {
                    turn("right", 12.96);
                    break;
                }
                case 7: {
                    drive("forward", FEET * 3);
                    break;
                }
                case 8: {
                    //shoot 2 balls
                    if (timer < second * 2.5) {
                        shoot();
                        timer++;
                    } else if (timer < second * 4.5) {
                        shoot();
                        transferIn();
                        timer++;
                    } else {
                        autonomousStep++;
                        reset();
                    }
                    break;
                }
                default: {
                    break;
                }
            };
        } */
    }
}