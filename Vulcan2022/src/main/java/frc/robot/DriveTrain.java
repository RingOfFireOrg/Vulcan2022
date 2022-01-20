  
package frc.robot;

import com.ctre.phoenix.sensors.CANCoder;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.motorcontrol.VictorSP;
import frc.robot.RobotMap;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class DriveTrain extends TeleopModule {

    MotorController leftMotors, rightMotors;
    private RelativeEncoder leftEncoder, rightEncoder;

    VictorSP jeremyMotor;
    VictorSP jeremyMotor2;

    public DriveTrain() {
        rightMotors = new MotorControllerGroup(Container.getInstance().frontRightMotor, Container.getInstance().backRightMotor);
        leftMotors = new MotorControllerGroup(Container.getInstance().frontLeftMotor, Container.getInstance().backLeftMotor);
        jeremyMotor = Container.getInstance().jeremyMotor;
        jeremyMotor2 = Container.getInstance().jeremyMotor2;
    }

    @Override
    public void teleopControl() {
        /**
         * NEEDS TO BE ALTERED
         * 
         * Must use encoders to alter motor speeds for driving straight
         */
        
        double joystickInputSpeed = ControlSystems.getInstance().leftstick.getY();
        
        if (jeremyMotor != null) {
            jeremyMotor.set(joystickInputSpeed);
        }
        
        if (jeremyMotor2 != null) {
            jeremyMotor2.set(-joystickInputSpeed);
        }
        
        //double leftInputSpeed = ControlSystems.getInstance().dGamepadLeftY();
        //double rightInputSpeed = ControlSystems.getInstance().dGamepadRightY();
        //rightMotors.set(rightInputSpeed);
        //leftMotors.set(leftInputSpeed);
    }

    @Override
    public void teleopInit() { }
    public void periodic() { }
}
