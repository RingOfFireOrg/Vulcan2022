package frc.robot; 

import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
 
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.revrobotics.CANSparkMax;
import edu.wpi.first.wpilibj.motorcontrol.VictorSP;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class VisionShooterTurret extends TeleopModule {
    
    private MotorControllerGroup leftMotors, rightMotors;
    public TalonFX shooter;
    public CANSparkMax transferMotor1;
    public CANSparkMax transferMotor2;
    public VictorSP intakeMotor;
    CANSparkMax turret;
    
    //Vision vars (keep these just in case...)
    private final double visionrange = 1.5;
    private final double target_height = -21; //Tape to limelight's crosshair percent
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

    //Shooter, Turret, and Intake vars
    private final double lowShooterSpeed = 0.3;
    private final double highShooterSpeed = 0.57;
    private final double second = 20;
    private final double startTransferDelay = second * 3;
    private final double transferSpeed = 0.4;
    private double startTransferTimer = 0;
    private double intakeSpeed = 1;

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
        intakeMotor = Container.get().intakeMotor;
        turret = Container.get().turretMotor;
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
                intakeMotor.set(intakeSpeed);
            } else {
                transferStop();
                intakeMotor.set(0);
            }
        } else if (ControlSystems.get().mGamepadRightBumper() == true) {
            shooterSpeed = highShooterSpeed;
            startTransferTimer++;
            
            if (startTransferTimer > startTransferDelay) {
                transferIn();
                intakeMotor.set(intakeSpeed);
            } else {
                transferStop();
                intakeMotor.set(0);
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

        shooter.set(ControlMode.PercentOutput, shooterSpeed);
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

    public void turretToTarget() {
        //Read vision values
        double[] visionVals = getVisionVals();

        //Turret
        double tx = visionVals[0]; // +-29.8
        double turret_speed = tx / 10; //Adjust!

        turret_speed = Math.min(0.4, turret_speed);
        turret_speed = Math.max(-0.4, turret_speed);

        turret.set(turret_speed);
    }

    public void turretAndShootToTarget() {
        //Read vision values
        double[] visionVals = getVisionVals();
        
        //Turret
        double tx = visionVals[0]; // +-29.8
        double turret_speed = tx / 10; //Adjust! ADJUST!

        turret_speed = Math.min(0.4, turret_speed);
        turret_speed = Math.max(-0.4, turret_speed);
        
        turret.set(turret_speed);

        //Shooter
        if (Math.abs(turret_speed) > 0.04) return;
        
        double ty = -visionVals[1]; // +-24.85
        double target_ty = -target_height; //base, -21.xx (Adjust);
        double shooter_base_speed = highShooterSpeed; //Working and tested speed from base
        double shooter_adjust_rate = 0.05; //How sensitive shooter is to distance
        double shooter_speed_adjust = ((ty - target_ty) * -1) / shooter_adjust_rate;

        shooter.set(ControlMode.PercentOutput, shooter_base_speed + shooter_speed_adjust);
    }
    
    public void aimToTarget() {
        double[] visionVals = getVisionVals();
        double KpAim = 0.07f;
        double min_aim = 0.05f;

        double tx = visionVals[0];

        double heading_error = tx;
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
        double KpAim = 0.07f;
        double KpDistance = 0.07f;
        double min_aim = 0.05f;

        double tx = visionVals[0];
        double ty = visionVals[1];

        double heading_error = tx;
        double distance_error = ty;
        double steering_adjust = 0.0f;

        if (tx > visionrange) {
            steering_adjust = KpAim*heading_error - min_aim;
        }
        else if (tx < -visionrange) {
            steering_adjust = KpAim*heading_error + min_aim;
        }

        double distance_adjust = KpDistance * (distance_error + target_height);

        double leftSpeed = steering_adjust + distance_adjust;
        double rightSpeed = -steering_adjust + distance_adjust;

        if (leftSpeed < -0.8 || rightSpeed < -0.8 || leftSpeed > 0.8 || rightSpeed > 0.8) {
            leftSpeed /= 1.2;
            rightSpeed /= 1.2;
        }

        leftSpeed /= 1.4;
        rightSpeed /= 1.4;

        leftMotors.set(leftSpeed);
        rightMotors.set(rightSpeed);
    }

    public void everythingBagel() {
        double[] visionVals = getVisionVals();
        double KpAim = -0.07f;
        double KpDistance = 0.07f;
        double min_aim = 0.05f;

        double tx = visionVals[0];
        double ty = visionVals[1];

        double heading_error = -tx;
        double distance_error = ty;
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

        double distance_adjust = KpDistance * (distance_error + target_height);
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

        shooter.set(ControlMode.PercentOutput, shooterSpeed);
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