package frc.robot; 

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;

public class Autonomous {
    private MotorControllerGroup leftMotors, rightMotors; 
    public TalonFX shooter;
    private AHRS ahrs;

    private double driveOffset = 3;
    private double turnOffset = 12;
    private int timer = 0;
    private int second = 50;
    private double FEET = 8.50;   
    private double leftEncoderOffset = 0;
    private double rightEncoderOffset = 0;
    private double leftPower = 0.4, rightPower = leftPower;
    private final int visionrange = 2;
    
    private int autonomousStep = -1;
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
        Container.get().transferMotor1.set(0.33);
        Container.get().transferMotor2.set(-0.33);
    }

    public void transferOut() {
        Container.get().transferMotor1.set(-0.35);
        Container.get().transferMotor2.set(0.35);
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
        leftMotors.set(-leftPower / 2.75);
        rightMotors.set(rightPower / 2.75);
    }

    public void turnRight() {
        leftMotors.set(leftPower / 2.75);
        rightMotors.set(-rightPower / 2.75);
    }

    public void turnLeftSlow() {
        leftMotors.set(-leftPower / 3.5);
        rightMotors.set(rightPower / 3.5);
    }

    public void turnRightSlow() {
        leftMotors.set(leftPower / 3.5);
        rightMotors.set(-rightPower / 3.5);
    }

    public void driveStop() {
        leftMotors.set(0);
        rightMotors.set(0);
    }

    public void shoot() {
        shooter.set(ControlMode.PercentOutput, .30);
    }

    public void shootHigh() {
        shooter.set(ControlMode.PercentOutput, .59);
    }

    public void shooterStop() {
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

    public double[] updateVisionVals() {
        NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
        NetworkTableEntry tx = table.getEntry("tx");
        NetworkTableEntry ty = table.getEntry("ty");
        NetworkTableEntry ta = table.getEntry("ta");
        NetworkTableEntry tv = table.getEntry("tv");

        //read values
        double x = tx.getDouble(0.0);
        double y = ty.getDouble(0.0);
        double area = ta.getDouble(0.0);
        double v = tv.getDouble(0.0);
        
        //post to smart dashboard
        SmartDashboard.putNumber("LimelightX", x);
        SmartDashboard.putNumber("LimelightY", y);
        SmartDashboard.putNumber("LimelightArea", area);
        SmartDashboard.putNumber("LimelightTarget", v);

        double[] arr = {x, y, area, v};
        return arr;
    }

    public void aimToTarget() {
        double[] visionVals = updateVisionVals();

        // visionVals 0 is the X distance from the center of camera to target
        // visionVals 1 is the Y distance from the center of camera to target
        // visionVals 2 is the distance from camera to target (?)
        // visionVals 3 checks if there is a target in view

        if (visionVals[0] < -visionrange) turnLeftSlow();
        else if (visionVals[0] > visionrange) turnRightSlow();
        else driveStop();
    }

    public void reset(){
        driveStop();
        resetEncoders();
        shooterStop();
        intakeStop();
        Container.get().intakeExtendingMotor.set(0);
        timer = 0;
    }

    public void autonomousPeriodic() {
        SmartDashboard.putNumber("Absolute Direction", getAbsoluteDirection());
        if (autoType == "smartauto") {
            switch (autonomousStep) {
                case -1: {
                    reset();
                    autonomousStep++;
                    break;
                }
                case 0: {
                    //Vision
                    if (timer < second * 5.5) {
                        Container.get().intakeExtendingMotor.set(1);
                        timer++;
                    } else {
                        autonomousStep++;
                        reset();
                    }
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
                    //Vision
                    if (timer < second * 4.5) {
                        //aimToTarget();
                        shootHigh();
                        timer++;
                    } else {
                        autonomousStep++;
                        reset();
                    }
                    break;
                }
                case 5: {
                    if (timer < second * 1) {
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
                    if (timer < second * 0.5) {
                        transferOut();
                        shootHigh();
                        timer++;
                    } else {
                        autonomousStep++;
                        reset();
                    }
                    break;
                }
                case 7: {
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
                case 8: {
                    shooterStop();
                    break;
                }
            }
        }
    }
}