  
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
    LinearServo linear;
    private RelativeEncoder rightEncoder;
    private double driveCoefficient;
    private double motorCorrection; // leftEncoder;
    
    public DriveTrain() {
        rightMotors = new MotorControllerGroup(Container.getInstance().frontRightMotor, Container.getInstance().backRightMotor);
        leftMotors = new MotorControllerGroup(Container.getInstance().frontLeftMotor, Container.getInstance().backLeftMotor);
        linear = new LinearServo(0, 50, 5);
        driveCoefficient = .2;
        motorCorrection = 1;
    }   

    @Override
    public void teleopControl() {
        //double joystickInputSpeed = ControlSystems.getInstance().leftstick.getY();
        
        double leftInputSpeed = ControlSystems.getInstance().dGamepadLeftY();
        double rightInputSpeed = ControlSystems.getInstance().dGamepadRightY();
        rightMotors.set(rightInputSpeed * driveCoefficient);
        leftMotors.set(leftInputSpeed * driveCoefficient * motorCorrection);
        /*if (ControlSystems.getInstance().mGamepadX()){
            linear.setPosition(50);
        } else {
            linear.setPosition(0);
        }*/
        if (ControlSystems.getInstance().dGamepadA()) {
            driveCoefficient = .8;
        } else if (ControlSystems.getInstance().dGamepadB()) {
            driveCoefficient = .2; 
        }
    }

    @Override
    public void teleopInit() { }
    public void periodic() { }
}
