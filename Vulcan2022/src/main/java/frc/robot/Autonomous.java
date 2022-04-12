package frc.robot; 

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;

public class Autonomous {
    private MotorControllerGroup leftMotors, rightMotors; 
    public TalonFX shooter;
    private AHRS ahrs;
    CANSparkMax turret;
    RelativeEncoder turretEncoder;

    private double driveOffset = 3;
    private double turnOffset = 12;
    private int timer = 0;
    private int second = 50;
    private double FEET = 8.50;   
    private double leftEncoderOffset = 0;
    private double rightEncoderOffset = 0;
    private double leftPower = 0.4, rightPower = leftPower;
    private final int visionrange = 2;
    private double shooter_velocity = 0;
    public boolean reverseTransfer = false;
    public double reverseTransferTimer = 0;
    private final double turretEncoderRange = 12.5;
    private boolean dontReverseTransfer = true;
    
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

        turret = Container.get().turretMotor;
        turretEncoder = Container.get().turretEncoder;
    }

    //encoder distances 1 Adding a new comment asd
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
        shooter.set(ControlMode.PercentOutput, .56);
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

    public double[] getVisionVals() {
        NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
        double tx = table.getEntry("tx").getDouble(0.0);
        double ty = table.getEntry("ty").getDouble(0.0);
        
        double[] arr = {tx, ty};
        return arr;
    }

    public void turretToTarget() {
        // Read vision values
        double[] visionVals = getVisionVals();

        // Get horizontal Offset From Crosshair To Target (-29.8 to 29.8deg)
        double tx = visionVals[0];

        // Turret speed
        double turret_speed = 0;

        if (tx < -visionrange)
            turret_speed = -0.1;
        if (tx > visionrange)
            turret_speed = 0.1;

        // Clamp speed w/ encoder
        if (turretEncoder.getPosition() > turretEncoderRange)
            turret_speed = Math.min(turret_speed, 0); // Only negative speeds

        if (turretEncoder.getPosition() < -turretEncoderRange)
            turret_speed = Math.max(turret_speed, 0); // Only postive speeds

        // Stop turret if in target is in range
        if (Math.abs(tx) < visionrange)
            turret_speed = 0;

        // Set turret motor to turret speed
        turret.set(turret_speed);
    }

    public void turretStop() {
        turret.set(0);
    }

    public void reset(){
        driveStop();
        resetEncoders();
        turretStop();
        intakeStop();
        Container.get().intakeExtendingMotor.set(0);
        timer = 0;
    }

    public void autonomousPeriodic() {
        SmartDashboard.putNumber("Absolute Direction", getAbsoluteDirection());
        if (autoType == "smartauto") {
            switch (autonomousStep) {
                //Shoot two balls into high goal
                case -1: {
                    reset();
                    autonomousStep++;
                    break;
                }
                case 0: {
                    //Vision
                    if (timer < second * 1.5) {
                        Container.get().intakeExtendingMotor.set(1);
                        timer++;
                    } else {
                        autonomousStep++;
                        reset();
                    }
                    break;
                }
                case 1: {
                    if (timer < second * 1) {
                        Container.get().intakeExtendingMotor.set(1);
                        timer++;
                    }
                    intakeIn();
                    drive("forward", FEET * 8.34);
                    break; 
                }
                case 2: {
                    turn("right", 180);
                    intakeIn();
                    shootHigh();
                    break;
                }
                case 3: {
                    intakeIn();
                    shootHigh();
                    drive("forward", FEET * 5.51/*14.51*/);
                    break; 
                }
                case 4: {
                    //Vision
                    if (timer < second * 1.5) {
                        turretToTarget();
                        shootHigh();
                        timer++;
                    } else {
                        autonomousStep++;
                        reset();
                    }
                    break;
                }
                case 5: {
                    shootHigh();
                    intakeIn();
                    turretToTarget();

                    if (reverseTransfer) {
                        reverseTransferTimer++;
            
                        if (reverseTransferTimer >= second * 0.75) {
                            reverseTransfer = false;
                        }
                    }

                    // Save old shooter velocity
                    double past_shooter_velocity = shooter_velocity;

                    // Get current shooter velocity
                    shooter_velocity = shooter.getSelectedSensorVelocity();

                    // If the shooter velocity went down by 50 then reverse transfer
                    if (dontReverseTransfer == true && past_shooter_velocity - shooter_velocity > 50) {
                        reverseTransfer = true;
                        dontReverseTransfer = false;
                    }

                    // Run transfer
                    if (reverseTransfer) {
                        transferOut();
                    } else {
                        transferIn();
                    }

                    if (timer < second * 6) {
                        timer++;
                    } else {
                        autonomousStep++;
                        reset();
                    }

                    break;
                }
                case 6: {
                    shooterStop();
                    transferStop();
                    intakeStop();
                    break;
                }
            }
        } else if (autoType == "evilauto") {
            //Shoots one preloaded ball into high and intakes an opposing color ball
            switch (autonomousStep) {
                case -1: {
                    reset();
                    autonomousStep++;
                    break;
                }
                case 0: {
                    //Vision
                    if (timer < second * 1.5) {
                        Container.get().intakeExtendingMotor.set(1);
                        timer++;
                    } else {
                        autonomousStep++;
                        reset();
                    }
                    break;
                }
                case 1: {
                    if (timer < second * 1) {
                        Container.get().intakeExtendingMotor.set(1);
                        timer++;
                    }
                    intakeIn();
                    drive("forward", FEET * 8.34);
                    break; 
                }
                case 2: {
                    turn("right", 180);
                    intakeIn();
                    shootHigh();
                    break;
                }
                case 3: {
                    intakeIn();
                    shootHigh();
                    drive("forward", FEET * 5.51/*14.51*/);
                    break; 
                }
                case 4: {
                    //Vision
                    if (timer < second * 1) {
                        turretToTarget();
                        shootHigh();
                        timer++;
                    } else {
                        autonomousStep++;
                        reset();
                    }
                    break;
                }
                case 5: {
                    shootHigh();
                    intakeIn();
                    turretToTarget();

                    if (reverseTransfer) {
                        reverseTransferTimer++;
            
                        if (reverseTransferTimer >= second * 0.75) {
                            reverseTransfer = false;
                            autonomousStep++;
                            break;
                        }
                    }

                    // Save old shooter velocity
                    double past_shooter_velocity = shooter_velocity;

                    // Get current shooter velocity
                    shooter_velocity = shooter.getSelectedSensorVelocity();

                    // If the shooter velocity went down by 50 then reverse transfer
                    if (dontReverseTransfer == true && past_shooter_velocity - shooter_velocity > 50) {
                        reverseTransfer = true;
                        dontReverseTransfer = false;
                    }

                    // Run transfer
                    if (reverseTransfer) {
                        transferOut();
                    } else {
                        transferIn();
                    }

                    if (timer < second * 6) {
                        timer++;
                    } else {
                        autonomousStep++;
                        reset();
                    }

                    break;
                }
                case 6: {
                    shooterStop();
                    transferStop();
                    intakeStop();
                    break;
                }
            }
        }
    }
}
