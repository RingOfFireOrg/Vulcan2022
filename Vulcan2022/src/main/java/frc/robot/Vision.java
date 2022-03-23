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

    private final int visionrange = 2;
    private final double turnSpeed = 0.16;

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
        if (ControlSystems.get().dGamepadRightBumper()) {
            aimToTarget();
        }
    }

    public void turnLeft() {
        leftMotors.set(-turnSpeed);
        rightMotors.set(turnSpeed);
    }

    public void turnRight() {
        leftMotors.set(turnSpeed);
        rightMotors.set(-turnSpeed);
    }

    public void stop() {
        leftMotors.set(0);
        rightMotors.set(0);
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

        if (visionVals[0] < -visionrange) turnLeft();
        else if (visionVals[0] > visionrange) turnRight();
        else stop();
    }

    public void periodic() {}
}