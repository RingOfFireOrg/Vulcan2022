package frc.robot; 

import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
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
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Vision extends TeleopModule {
    
    private MotorControllerGroup leftMotors, rightMotors;
    public TalonFX shooter;
    public CANSparkMax transferMotor1;
    public CANSparkMax transferMotor2;
    
    //Vision vars
    private final int visionrange = 2;
    private final double turnSpeed = 0.16;
    private final double maxTurnSpeed = 0.3;
    private final double limelightMountAngle = 25.0; //Gotta measure this...
    private final double limelightHeightInches = 20.0; //Gotta measure this... (Inches)
    private final double goalHeightInches = 60.0; //Gotta measure this... Goal to floor (Inches)
    private final double targetDistance = 80.0; //Gotta measure... target dist from robot to goal (Inches)
    private final double distanceRange = 5.0; //Margin of error (Inches)

    //Shooter & Turret vars
    private final double lowShooterSpeed = 0.3;
    private final double highShooterSpeed = 0.65;
    private final double second = 20;
    private final double startTransferDelay = second * 3;
    private final double transferSpeed = 0.5;
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
    }

    public void teleopControl() {
        if (ControlSystems.get().dGamepadRightBumper()) aimAndMoveToTarget();
    }

    public double[] updateVisionVals() {
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
        double angleToGoalDegrees = limelightMountAngle + y;
        double angleToGoalRadians = angleToGoalDegrees * (3.14159 / 180.0);
        double limelightToGoalDistance = (goalHeightInches - limelightHeightInches) / Math.tan(angleToGoalRadians);
        
        //post to smart dashboard
        SmartDashboard.putNumber("LimelightX", x);
        SmartDashboard.putNumber("LimelightY", y);
        SmartDashboard.putNumber("LimelightArea", area);
        SmartDashboard.putNumber("LimelightTarget", targets);

        double[] arr = {x, y, limelightToGoalDistance, targets};
        return arr;
    }

    public void aimAndMoveToTarget() {
        //Just turn
        double[] visionVals = updateVisionVals();

        double degreesX = visionVals[0]; //-29.8 to 29.8
        double degreesY = visionVals[1]; //-24.85 to 24.85 (STATES COMPETITION)
        double distanceToTarget = visionVals[2];
        double targetInVision = visionVals[3];

        double steering_adjust = 0;
        double speedDivideCoef = turnSpeed * 29.8;
        double additionalSteeringAdjust = 0;
        double min = 0.05;
        double max = maxTurnSpeed;

        //Calculate steering_adjust
        if (targetInVision == 0) {
            //No target, turn robot until it finds one
            steering_adjust = 0.3;
        } else {
            if (degreesX < -visionrange) {
                //Target to the left
                steering_adjust = -turnSpeed * (degreesX / speedDivideCoef) - min;
            }
            else if (degreesX > visionrange) {
                //Target to the right
                steering_adjust = turnSpeed * (degreesX / speedDivideCoef) + min;
            }
            if (distanceToTarget < targetDistance - distanceRange) {
                //Robot too close to target, drive backwards!
                additionalSteeringAdjust -= 0.15;
            }
            else if (distanceToTarget > targetDistance + distanceRange) {
                //Robot too far from target, drive forwards!
                additionalSteeringAdjust += 0.15;
            }
        }
        
        //Clamp speed to -0.3, 0.3
        steering_adjust = Math.min(Math.max(steering_adjust, min), max);

        //Resolve
        leftMotors.set(steering_adjust + additionalSteeringAdjust);
        rightMotors.set(-steering_adjust + additionalSteeringAdjust);
    }

    public void everythingBagel() {
        //Aim to target, drive to desired position, auto run shooter and transfer

        double[] visionVals = updateVisionVals();

        double degreesX = visionVals[0]; //-29.8 to 29.8
        double degreesY = visionVals[1]; //-24.85 to 24.85 (STATES COMPETITION)
        double distanceToTarget = visionVals[2];
        double targetInVision = visionVals[3];

        double steering_adjust = 0;
        double speedDivideCoef = turnSpeed * 29.8;
        double additionalSteeringAdjust = 0;
        double min = 0.05;
        double max = maxTurnSpeed;

        boolean inDesiredAngle = false;
        boolean inDesiredPosition = false;

        //Calculate steering_adjust
        if (targetInVision == 0) {
            //No target, turn robot until it finds one
            steering_adjust = 0.3;
        } else {
            if (degreesX < -visionrange) {
                //Target to the left
                steering_adjust = -turnSpeed * (degreesX / speedDivideCoef) - min;
            }
            else if (degreesX > visionrange) {
                //Target to the right
                steering_adjust = turnSpeed * (degreesX / speedDivideCoef) + min;
            } else {
                inDesiredAngle = true;
            }

            if (distanceToTarget < targetDistance - distanceRange) {
                //Robot too close to target, drive backwards!
                additionalSteeringAdjust -= 0.15;
            }
            else if (distanceToTarget > targetDistance + distanceRange) {
                //Robot too far from target, drive forwards!
                additionalSteeringAdjust += 0.15;
            }
            else {
                //Achieved desired position!
                inDesiredPosition = true;
            }
        }
        
        //Clamp speed to -0.3, 0.3
        steering_adjust = Math.min(Math.max(steering_adjust, min), max);

        //Resolve
        leftMotors.set(steering_adjust + additionalSteeringAdjust);
        rightMotors.set(-steering_adjust + additionalSteeringAdjust);

        double shooterSpeed = highShooterSpeed;
        if (inDesiredPosition && inDesiredAngle) {
            startTransferTimer++;
            if (startTransferTimer > startTransferDelay) {
                transferMotor1.set(transferSpeed);
                transferMotor2.set(-transferSpeed);
            } else {
                transferMotor1.set(0);
                transferMotor2.set(0);
            } 
        } else {
            startTransferTimer = 0;
        }

        Container.get().shooter.set(ControlMode.PercentOutput, shooterSpeed);
    }

    public void periodic() {}
}