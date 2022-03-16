package frc.robot;
import java.util.ResourceBundle.Control;

import com.revrobotics.CANSparkMax;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Turret extends TeleopModule {
    
    CANSparkMax turret;
    private final int visionrange = 2;
    private final double turretMotorSpeed = 0.2;

    public void teleopInit() {
        turret = Container.get().turretMotor;
    }

    public void teleopControl() {
       if (ControlSystems.get().dGamepadRightBumper()) {
        aimToTarget();
    }
        //Container.get().turretMotor.set(ControlSystems.get().mGamepadLeftY() * .2);
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

        double speed = visionVals[0] * 0.01;

        speed = Math.min(0.4, speed);

        if (visionVals[0] < -visionrange || visionVals[0] > visionrange) 
            Container.get().turretMotor.set(speed);
        else if(visionVals[3] == 1) 
            Container.get().turretMotor.set(0);
    }
}