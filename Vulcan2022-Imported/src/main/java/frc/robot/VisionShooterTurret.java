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
    private final double lowShooterSpeed = 0.35;
    private final double highShooterSpeed = 0.45;
    private final double second = 20;
    private double shooter_running_time = 0;
    private double shooter_velocity = 0;

    // Transfer
    private final double startTransferDelay = second * 3.75;
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
        // Update reverse transfer timer for auto shootings
        if (reverseTransfer) {
            reverseTransferTimer++;

            if (reverseTransferTimer >= second * 0.75) {
                reverseTransfer = false;
            }
        }

        // Teleoperated manipulator controls
        if (Controllers.get().mGamepadRightBumper()) {
            // Turn turret to target and shoot with vision
            turretAndShootToTarget();
        } else {
            // Enable manipulator shooter control
            shooterControl();

            // Rotate turret back to facing forward
            if (turretEncoder.getPosition() < -turretErrorRange)
                turret.set(0.05);
            else if (turretEncoder.getPosition() > turretErrorRange)
                turret.set(-0.05);
            else
                turret.set(0);

            // Reset shooter timer for auto shooting with vision
            shooter_running_time = 0;
        }
    }

    public double[] getVisionVals() {
        // https://docs.limelightvision.io/en/latest/networktables_api.html
        NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");

        // Horizontal Offset From Crosshair To Target (-29.8 to 29.8deg)
        double x = table.getEntry("tx").getDouble(0.0);

        // Vertical Offset From Crosshair To Target (-24.85 to 24.85deg)
        double y = table.getEntry("ty").getDouble(0.0);

        // post to smart dashboard
        SmartDashboard.putNumber("LimelightX", x);
        SmartDashboard.putNumber("LimelightY", y);

        double[] arr = { x, y };
        return arr;
    }

    // Shooter control w/ Manipulator Controller!
    public void shooterControl() {
        // Create shooter speed variable
        double shooterSpeed = 0;

        // Manipulator Triggers for high and low shooter
        if (Controllers.get().mGamepadRightTrigger() > 0.1) {
            // High shooter
            shooterSpeed = highShooterSpeed;
        } else if (Controllers.get().mGamepadLeftTrigger() > 0.1) {
            // Low shooter
            shooterSpeed = lowShooterSpeed;
        }

        // Put shooter velocity into smart dashboard
        SmartDashboard.putNumber("Shooter Velocity", shooter.getSelectedSensorVelocity());

        // Auto low shooter
        if (Controllers.get().mGamepadLeftBumper() == true) {
            shooterSpeed = lowShooterSpeed;
            startTransferTimer++;

            if (startTransferTimer > startTransferDelay) {
                // Save old shooter velocity
                double past_shooter_velocity = shooter_velocity;

                // Get current shooter velocity
                shooter_velocity = shooter.getSelectedSensorVelocity();

                // If the shooter velocity went down by 50 then reverse transfer
                if (past_shooter_velocity != 0 && past_shooter_velocity - shooter_velocity > 50) {
                    reverseTransfer = true;
                }

                // Run transfer
                if (reverseTransfer) {
                    transferOut();
                } else {
                    transferIn();
                }

                // Run intake
                intakeMotor.set(intakeSpeed);
            } else {
                // Reset everything
                transferStop();
                intakeMotor.set(0);
                shooter_velocity = 0;
                reverseTransfer = false;
            }
        } else {
            // Reset auto transfer timer for next time
            startTransferTimer = 0;
        }

        // Set shooter speed
        shooter.set(ControlMode.PercentOutput, shooterSpeed);
    }

    // Method to turn turret to target
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

    // Method to aim turret to hub and auto-shoot with varying speeds
    public void turretAndShootToTarget() {
        // Read vision values
        double[] visionVals = getVisionVals();

        // Get Horizontal Offset From Crosshair To Target (-29.8 to 29.8deg)
        double tx = visionVals[0];

        // Get Vertical Offset From Crosshair To Target (-24.85 to 24.85deg)
        double ty = visionVals[1];

        // Turret speed
        double turret_speed = 0;

        if (tx < -visionrange)
            turret_speed = -0.1;
        if (tx > visionrange)
            turret_speed = 0.1;

        // Clamp turret speed w/ encoder
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
        double shooter_speed = highShooterSpeed + ((ty - targetHeight) / 250);

        // Set shooter motor to shooter speed
        shooter.set(ControlMode.PercentOutput, highShooterSpeed/* shooter_speed */);

        // Increment shooter running time for auto transfer
        shooter_running_time++;

        // Run transfer if the shooter has ran for long enough
        if (shooter_running_time > startTransferDelay) {
            // Save old shooter velocity
            double past_shooter_velocity = shooter_velocity;

            // Get current shooter velocity
            shooter_velocity = shooter.getSelectedSensorVelocity();

            // If the shooter velocity went down by 50 then reverse transfer
            if (past_shooter_velocity != 0 && past_shooter_velocity - shooter_velocity > 50) {
                reverseTransfer = true;
            }

            // Run transfer
            if (reverseTransfer) {
                transferOut();
            } else {
                transferIn();
            }

            intakeMotor.set(intakeSpeed);
        } else {
            // Reset vars and stop transfer
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