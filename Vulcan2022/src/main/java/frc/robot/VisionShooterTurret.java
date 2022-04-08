package frc.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;

import edu.wpi.first.wpilibj.motorcontrol.VictorSP;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class VisionShooterTurret {
    // Subsystems
    public TalonFX shooter;
    public CANSparkMax transferMotor1, transferMotor2;
    public VictorSP intakeMotor;
    CANSparkMax turret;
    DigitalInput leftLimitSwitch, rightLimitSwitch;
    RelativeEncoder turretEncoder;

    // Important Vision Vars
    private final double visionrange = 1.5;
    private final double targetHeight = 14.3; // limelight target ty value

    // Shooter
    private final double lowShooterSpeed = 0.3;
    private final double highShooterSpeed = 0.56;
    private final double second = 20;
    private double shooter_running_time = 0;
    private double shooter_velocity = 0;

    // Transfer
    private final double startTransferDelay = second * 3.5;
    private final double transferSpeed = 0.4;
    public boolean reverseTransfer = false;
    private double startTransferTimer = 0;
    public double reverseTransferTimer = 0;

    // Turret
    private final double turretEncoderRange = 12.5;
    private final double turretErrorRange = 1;

    // Intake
    private double intakeSpeed = 1;

    public void teleopInit() {
        // Get all subsystems from Container.java
        transferMotor1 = Container.get().transferMotor1;
        transferMotor2 = Container.get().transferMotor2;
        shooter = Container.get().shooter;
        intakeMotor = Container.get().intakeMotor;
        turret = Container.get().turretMotor;
        leftLimitSwitch = Container.get().leftLimitSwitch;
        rightLimitSwitch = Container.get().rightLimitSwitch;
        turretEncoder = Container.get().turretEncoder;
    }

    public void teleopControl() {
        // Update reverse transfer timer for auto shoot
        if (reverseTransfer) {
            reverseTransferTimer++;
            if (reverseTransferTimer >= second * 0.75) {
                reverseTransfer = false;
            }
        }

        // Teleoperated manipulator controls
        if (Controllers.get().mGamepadRightBumper()) {
            // Turn turret to target and shoot
            turretAndShootToTarget();
        } else {
            // Center turret back to facing forward
            if (turretEncoder.getPosition() < -turretErrorRange)
                turret.set(0.05);
            else if (turretEncoder.getPosition() > turretErrorRange)
                turret.set(-0.05);
            else
                turret.set(0);

            // Reset shooter timer for auto shooting with vision
            shooter_running_time = 0;

            // Enable manipulator shooter control
            shooterControl();
        }
    }

    public double[] getVisionVals() {
        // https://docs.limelightvision.io/en/latest/networktables_api.html
        NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
        NetworkTableEntry tx = table.getEntry("tx");
        NetworkTableEntry ty = table.getEntry("ty");
        NetworkTableEntry ta = table.getEntry("ta");
        NetworkTableEntry tv = table.getEntry("tv");

        // Horizontal Offset From Crosshair To Target (-29.8 to 29.8deg)
        double x = tx.getDouble(0.0);

        // Vertical Offset From Crosshair To Target (-24.85 to 24.85deg)
        double y = ty.getDouble(0.0);

        // Target Area (0% of image to 100% of image)
        double area = ta.getDouble(0.0);

        // Whether the limelight has any valid targets (0 or 1)
        double targets = tv.getDouble(0.0);

        // post to smart dashboard
        SmartDashboard.putNumber("LimelightX", x);
        SmartDashboard.putNumber("LimelightY", y);
        SmartDashboard.putNumber("LimelightArea", area);
        SmartDashboard.putNumber("LimelightTarget", targets);

        SmartDashboard.putBoolean("In range and pointed at target", Math.abs(x) < 0.04);
        SmartDashboard.putBoolean("In range", targets == 1);

        double[] arr = { x, y, targets };
        return arr;
    }

    // Shooter control w/ Manipulator Controller!
    public void shooterControl() {
        // Create shooter speed variable
        double shooterSpeed = 0;

        // Manipulator Triggers for high and low shooter
        if (Controllers.get().mGamepadRightTrigger() > 0.1) {
            shooterSpeed = highShooterSpeed;
        } else if (Controllers.get().mGamepadLeftTrigger() > 0.1) {
            shooterSpeed = lowShooterSpeed;
        }

        SmartDashboard.putNumber("Shooter Velocity", shooter.getSelectedSensorVelocity());
        if (Controllers.get().mGamepadLeftBumper() == true) {
            // Auto low shooter
            shooterSpeed = lowShooterSpeed;
            startTransferTimer++;

            if (startTransferTimer > startTransferDelay) {
                // Get shooter velocity
                double current_shooter_velocity = shooter.getSelectedSensorVelocity();

                double past_shooter_velocity = shooter_velocity;
                shooter_velocity = current_shooter_velocity;

                if (past_shooter_velocity != 0 && past_shooter_velocity - shooter_velocity > 50) {
                    // Ball went though!
                    reverseTransfer = true;
                }

                //Run transfer
                if (reverseTransfer) {
                    transferOut();
                } else {
                    transferIn();
                }

                intakeMotor.set(intakeSpeed);
            } else {
                transferStop();
                intakeMotor.set(0);
                shooter_velocity = 0;
                reverseTransfer = false;
            }
        } else {
            startTransferTimer = 0;
        }

        // Set shooter speed
        shooter.set(ControlMode.PercentOutput, shooterSpeed);
    }

    // TMethod to turn turret to target
    public void turretToTarget() {
        // Read vision values
        double[] visionVals = getVisionVals();

        // Get horizontal Offset From Crosshair To Target (-29.8 to 29.8deg)
        double tx = visionVals[0];

        // Turret speed
        double turret_speed = 0;

        if (tx < -visionrange)
            turret_speed = 0.1;
        if (tx > visionrange)
            turret_speed = -0.1;

        // Clamp speed w/ encoder
        if (turretEncoder.getPosition() > turretEncoderRange)
            turret_speed = Math.min(turret_speed, 0); // Only negative speeds

        if (turretEncoder.getPosition() < -turretEncoderRange)
            turret_speed = Math.max(turret_speed, 0); // Only postive speeds

        // Stop turret if in range
        if (Math.abs(tx) < visionrange)
            turret_speed = 0;

        // Set turret motor to turret speed
        turret.set(turret_speed);
    }

    // Method to aim turret to hub and auto-shoot with varying speeds
    public void turretAndShootToTarget() {
        // Read vision values
        double[] visionVals = getVisionVals();

        // Get horizontal Offset From Crosshair To Target (-29.8 to 29.8deg)
        double tx = visionVals[0];

        // Turret speed
        double turret_speed = 0;

        if (tx < -visionrange)
            turret_speed = 0.1;
        if (tx > visionrange)
            turret_speed = -0.1;

        // Clamp speed w/ encoder
        if (turretEncoder.getPosition() > turretEncoderRange)
            turret_speed = Math.min(turret_speed, 0); // Only negative speeds

        if (turretEncoder.getPosition() < -turretEncoderRange)
            turret_speed = Math.max(turret_speed, 0); // Only postive speeds

        // Stop turret if in range
        if (Math.abs(tx) < visionrange)
            turret_speed = 0;

        // Set turret motor to turret speed
        turret.set(turret_speed);

        // Shooter - Calculate speed
        double ty = visionVals[1]; // -24.85 to 24.85 deg
        double shooter_speed_adjust = (ty - targetHeight) * 0.004; // Calculate
        double shooter_speed = highShooterSpeed + shooter_speed_adjust;

        // Set shooter motor to shooter speed
        shooter.set(ControlMode.PercentOutput, highShooterSpeed/*shooter_speed*/);
        shooter_running_time++;

        // Run transfer if the shooter has ran for long enough
        if (shooter_running_time > startTransferDelay) {
            // Get shooter velocity
            double current_shooter_velocity = shooter.getSelectedSensorVelocity();

            double past_shooter_velocity = shooter_velocity;
            shooter_velocity = current_shooter_velocity;

            if (past_shooter_velocity != 0 && past_shooter_velocity - shooter_velocity > 50) {
                // Ball went though!
                reverseTransfer = true;
            }

            //Reverse transfer
            if (reverseTransfer) {
                transferOut();
            } else {
                transferIn();
            }

            intakeMotor.set(intakeSpeed);
        } else {
            transferStop();
            shooter_velocity = 0;
            reverseTransfer = false;
        }
    }

    public void transferIn() {
        transferMotor1.set(transferSpeed);
        transferMotor2.set(-transferSpeed);
    }

    public void transferOut() {
        transferMotor1.set(-transferSpeed);
        transferMotor2.set(transferSpeed);
    }

    public void transferStop() {
        transferMotor1.set(0);
        transferMotor2.set(0);
    }
}