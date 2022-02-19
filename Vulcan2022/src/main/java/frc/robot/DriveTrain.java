  
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
    private RelativeEncoder rightEncoder; // leftEncoder;
    
    public DriveTrain() {
        rightMotors = new MotorControllerGroup(Container.getInstance().frontRightMotor, Container.getInstance().backRightMotor);
        leftMotors = new MotorControllerGroup(Container.getInstance().frontLeftMotor, Container.getInstance().backLeftMotor);
    }   

    @Override
    public void teleopControl() {
        double joystickInputSpeed = ControlSystems.getInstance().leftstick.getY();
        
        //double leftInputSpeed = ControlSystems.getInstance().dGamepadLeftY();
        //double rightInputSpeed = ControlSystems.getInstance().dGamepadRightY();
        //rightMotors.set(rightInputSpeed);
        //leftMotors.set(leftInputSpeed);
    }

    @Override
    public void teleopInit() { }
    public void periodic() { }
}
