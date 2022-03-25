package frc.robot; 

import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
 
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.revrobotics.CANSparkMax;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class VisionAndShooter extends TeleopModule {
    
    private MotorControllerGroup leftMotors, rightMotors;
    public TalonFX shooter;
    public CANSparkMax transferMotor1;
    public CANSparkMax transferMotor2;
    
    //Vision vars (keep these just in case...)
    private final double visionrange = 1.5;
    // private final double turnSpeed = 0.16;
    // private final double maxTurnSpeed = 0.1;
    // private final double limelightMountAngle = 25.0; //Gotta measure this... (NOT IMPORTANT RN)
    // private final double limelightHeightInches = 35.0; //Gotta measure this... (Inches)
    // private final double goalHeightInches = 102.8; //Middle of target to floor (Inches)
    // private final double targetDistance = 80.0; //Gotta measure... target dist from robot to goal (Inches)
    // private final double targetYAngle = 5.0; //GOTTA FINE TUNE THIS
    // private final double distanceRange = 5.0; //Margin of error (Inches)
    // private final double targetYAngleError = 1.0; //Marge of angle error (Inches)
    //we need to measure all of these at the forge today rn they are just guesstimate lmao

    //Shooter & Turret vars
    private final double lowShooterSpeed = 0.3;
    private final double highShooterSpeed = 0.65;
    private final double second = 20;
    private final double startTransferDelay = second * 3;
    private final double transferSpeed = 0.4;
    private double startTransferTimer = 0;

    public void teleopInit() {
        rightMotors = new MotorControllerGroup(
            Container.get().frontRightMotor,
            Container.get().backRightMotor
        );
        leftMotors = new MotorControllerGroup(
            Container.get().frontLeftMotor,
            Container.get().backLeftMotor
        );
        transferMotor1 = Container.get().transferMotor1;
        transferMotor2 = Container.get().transferMotor2;
        shooter = Container.get().shooter;
    }

    public void teleopControl() {
        //Manipulator shooter
        double shooterSpeed = 0;
        if (ControlSystems.get().mGamepadRightTrigger() > 0.1) {
            shooterSpeed = highShooterSpeed;
        } else if (ControlSystems.get().mGamepadLeftTrigger() > 0.1) {
            shooterSpeed = lowShooterSpeed;
        }

        if (ControlSystems.get().mGamepadLeftBumper() == true) {
            shooterSpeed = lowShooterSpeed;
            startTransferTimer++;

            if (startTransferTimer > startTransferDelay) {
                transferIn();
            } else {
                transferStop();
            }
        } else if (ControlSystems.get().mGamepadRightBumper() == true) {
            shooterSpeed = highShooterSpeed;
            startTransferTimer++;
            
            if (startTransferTimer > startTransferDelay) {
                transferIn();
            } else {
                transferStop();
            }
        } else {
            startTransferTimer = 0;
        }

        //Driver vision turn and position
        if (ControlSystems.get().dGamepadLeftBumper() == true) {
            aimToTargetAndDrive();
            //everythingBagel();
            //aimToTargetAndDriveOld();
            //everythingBagelOld();
        }

        Container.get().shooter.set(ControlMode.PercentOutput, shooterSpeed);
    }

    public double[] getVisionVals() {
        // https://docs.limelightvision.io/en/latest/networktables_api.html
        NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
        NetworkTableEntry tx = table.getEntry("tx");
        NetworkTableEntry ty = table.getEntry("ty");
        NetworkTableEntry ta = table.getEntry("ta");
        NetworkTableEntry tv = table.getEntry("tv");

        //Horizontal Offset From Crosshair To Target (-29.8 to 29.8deg)
        double x = tx.getDouble(0.0);

        //Vertical Offset From Crosshair To Target (-24.85 to 24.85deg)
        double y = ty.getDouble(0.0); 

        //Target Area (0% of image to 100% of image)
        double area = ta.getDouble(0.0);

        //Whether the limelight has any valid targets (0 or 1)
        double targets = tv.getDouble(0.0);

        //Distance
        // double angleToGoalDegrees = limelightMountAngle + y;
        // double angleToGoalRadians = angleToGoalDegrees * (3.14159 / 180.0);
        // double limelightToGoalDistance = (goalHeightInches - limelightHeightInches) / Math.tan(angleToGoalRadians);
        
        //post to smart dashboard
        SmartDashboard.putNumber("LimelightX", x);
        SmartDashboard.putNumber("LimelightY", y);
        SmartDashboard.putNumber("LimelightArea", area);
        SmartDashboard.putNumber("LimelightTarget", targets);

        double[] arr = {x, y};
        return arr;
    }

    
    public void aimToTarget() {
        double[] visionVals = getVisionVals();
        double KpAim = -0.07f;
        double min_aim = 0.05f;

        double tx = visionVals[0];

        double heading_error = -tx;
        double steering_adjust = 0.0f;

        if (tx > visionrange) {
            steering_adjust = KpAim*heading_error - min_aim;
        }
        else if (tx < -visionrange) {
            steering_adjust = KpAim*heading_error + min_aim;
        }

        leftMotors.set(steering_adjust);
        rightMotors.set(-steering_adjust);
    }

    public void aimToTargetAndDrive() {
        double[] visionVals = getVisionVals();
        double KpAim = -0.07f;
        double KpDistance = -0.07f;
        double min_aim = 0.05f;

        double tx = visionVals[0];
        double ty = visionVals[1];

        double heading_error = -tx;
        double distance_error = -ty;
        double steering_adjust = 0.0f;

        if (tx > visionrange) {
            steering_adjust = KpAim*heading_error - min_aim;
        }
        else if (tx < -visionrange) {
            steering_adjust = KpAim*heading_error + min_aim;
        }

        double distance_adjust = KpDistance * distance_error;

        leftMotors.set(steering_adjust + distance_adjust);
        rightMotors.set(-steering_adjust + distance_adjust);
    }

    public void everythingBagel() {
        double[] visionVals = getVisionVals();
        double KpAim = -0.07f;
        double KpDistance = -0.07f;
        double min_aim = 0.05f;

        double tx = visionVals[0];
        double ty = visionVals[1];

        double heading_error = -tx;
        double distance_error = -ty;
        double steering_adjust = 0.0f;

        boolean inDesiredPosition = false;
        boolean inDesiredAngle = false;

        if (tx > visionrange) {
            steering_adjust = KpAim*heading_error - min_aim;
        }
        else if (tx < -visionrange) {
            steering_adjust = KpAim*heading_error + min_aim;
        }
        else {
            inDesiredAngle = true;
        }

        double distance_adjust = KpDistance * distance_error;
        if (distance_adjust < 0.05) {
            inDesiredPosition = true;
        }

        leftMotors.set(steering_adjust + distance_adjust);
        rightMotors.set(-steering_adjust + distance_adjust);

        double shooterSpeed = highShooterSpeed;
        if (inDesiredPosition && inDesiredAngle) {
            startTransferTimer++;
            if (startTransferTimer > startTransferDelay) {
                transferIn();
            } else {
                transferStop();
            } 
        } else {
            startTransferTimer = 0;
        }

        Container.get().shooter.set(ControlMode.PercentOutput, shooterSpeed);
    }

    public void transferIn() {
        transferMotor1.set(transferSpeed);
        transferMotor2.set(-transferSpeed);
    }

    public void transferStop() {
        transferMotor1.set(0);
        transferMotor2.set(0);
    }

    public void periodic() {}
}