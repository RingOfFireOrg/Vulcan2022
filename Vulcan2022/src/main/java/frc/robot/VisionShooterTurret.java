package frc.robot; 

import edu.wpi.first.wpilibj.DigitalInput;
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
    public CANSparkMax transferMotor1, transferMotor2;
    public VictorSP intakeMotor;
    CANSparkMax turret;
    DigitalInput leftLimitSwitch, rightLimitSwitch;
    
    //Important Vision Vars
    private final double visionrange = 1.5;
    private final double target_height = 21; //Tape to limelight's crosshair percent

    //Shooter, Turret, and Intake vars
    private final double lowShooterSpeed = 0.3;
    private final double highShooterSpeed = 0.57;
    private final double second = 20;
    private final double startTransferDelay = second * 3;
    private final double transferSpeed = 0.4;
    private double startTransferTimer = 0;
    private double intakeSpeed = 1;
    private boolean kill_turret = false;

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
        leftLimitSwitch = Container.get().leftLimitSwitch;
        rightLimitSwitch = Container.get().rightLimitSwitch;
    }

    public void teleopControl() {
        //Shooter Control
        shooterControl();

        //Driver vision turn and position
        if (Controllers.get().dGamepadLeftBumper() == true) {
            turretToTarget();
            //turretAndShootToTarget();
        } else {
            turret.set(0);
        }

        if (kill_turret) turret.set(0);
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

        //post to smart dashboard
        SmartDashboard.putNumber("LimelightX", x);
        SmartDashboard.putNumber("LimelightY", y);
        SmartDashboard.putNumber("LimelightArea", area);
        SmartDashboard.putNumber("LimelightTarget", targets);

        double[] arr = {x, y, targets};
        return arr;
    }

    public void shooterControl() {
        double shooterSpeed = 0;
        if (Controllers.get().mGamepadRightTrigger() > 0.1) {
            shooterSpeed = highShooterSpeed;
        } else if (Controllers.get().mGamepadLeftTrigger() > 0.1) {
            shooterSpeed = lowShooterSpeed;
        }

        if (Controllers.get().mGamepadLeftBumper() == true) {
            shooterSpeed = lowShooterSpeed;
            startTransferTimer++;

            if (startTransferTimer > startTransferDelay) {
                transferIn();
                intakeMotor.set(intakeSpeed);
            } else {
                transferStop();
                intakeMotor.set(0);
            }
        } else if (Controllers.get().mGamepadRightBumper() == true) {
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

        shooter.set(ControlMode.PercentOutput, shooterSpeed);
    }

    public void turretToTarget() {
        //Read vision values
        double[] visionVals = getVisionVals();

        //If no target, exit function
        if (visionVals[2] == 0) return;

        //Turret speed
        double tx = visionVals[0]; //Horizontal Offset From Crosshair To Target (-29.8 to 29.8deg)
        double turret_speed = tx / 40; //Turret speed

        //Clamp speed between -0.1 and 0.1
        turret_speed = Math.min(Math.max(turret_speed, -0.1), 0.1);

        //Clamp speed w/ limit switches
        if (rightLimitSwitch.get()) {
            turret_speed = Math.min(turret_speed, 0); //Only negative speeds
        }
        if (leftLimitSwitch.get()) {
            turret_speed = Math.max(turret_speed, 0); //Only postive speeds
        }

        //Set turret motor to turret speed
        turret.set(turret_speed);
    }

    public void turretAndShootToTarget() {
        //Read vision values
        double[] visionVals = getVisionVals();

        //If no target, exit function
        if (visionVals[2] == 0) return;
        
        //Turret speed
        double tx = visionVals[0]; // +-29.8
        double turret_speed = tx / 40; //Change! ADJUST!

        //Clamp speed between -0.1 and 0.1
        turret_speed = Math.min(Math.max(turret_speed, -0.1), 0.1);

        //Clamp speed w/ limit switches
        if (rightLimitSwitch.get()) {
            turret_speed = Math.min(turret_speed, 0); //Only negative speeds
        }
        if (leftLimitSwitch.get()) {
            turret_speed = Math.max(turret_speed, 0); //Only postive speeds
        }

        //Set turret motor to turret speed
        turret.set(turret_speed);

        //Shooter - If the turret isn't facing the target then exit function
        if (Math.abs(turret_speed) > 0.04) return;
        
        double ty = -visionVals[1]; // +-24.85
        double target_ty = target_height; //base, -21.xx (ADJUST ADJUST CHANGE IT);
        double shooter_base_speed = highShooterSpeed; //Working speed from target_height location
        double shooter_adjust_rate = 0.05; //How sensitive shooter speed is to distance
        double shooter_speed_adjust = ((ty - target_ty) * -1) / shooter_adjust_rate; //Calculate

        //Set shooter motor to shooter speed
        double shooter_speed = shooter_base_speed + shooter_speed_adjust;
        shooter.set(ControlMode.PercentOutput, shooter_speed);
    }
    
    public void turnToTarget() {
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

    public void turnToTargetAndDrive() {
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

        double distance_adjust = KpDistance * (distance_error - target_height);

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

        double distance_adjust = KpDistance * (distance_error - target_height);
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