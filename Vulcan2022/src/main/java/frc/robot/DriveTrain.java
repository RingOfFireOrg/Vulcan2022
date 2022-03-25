  
package frc.robot;

import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;

public class DriveTrain extends TeleopModule {

    MotorController leftMotors, rightMotors;
    LinearServo linear;
    private double driveCoefficient;
    private double motorCorrection;
    
    public DriveTrain() {
        rightMotors = new MotorControllerGroup(Container.get().frontRightMotor, Container.get().backRightMotor);
        leftMotors = new MotorControllerGroup(Container.get().frontLeftMotor, Container.get().backLeftMotor);
        linear = new LinearServo(0, 50, 5);
        driveCoefficient = .35;
        motorCorrection = 1;
    }   

    @Override
    public void teleopControl() {
        //double joystickInputSpeed = ControlSystems.get().leftstick.getY();
        
        double leftInputSpeed = ControlSystems.get().dGamepadLeftY();
        double rightInputSpeed = ControlSystems.get().dGamepadRightY();
        rightMotors.set(rightInputSpeed * driveCoefficient);
        leftMotors.set(leftInputSpeed * driveCoefficient * motorCorrection);
        
        if (ControlSystems.get().dGamepadA()) {
            driveCoefficient = .75;
        } else if (ControlSystems.get().dGamepadB()) {
            driveCoefficient = .35; 
        }
    }

    @Override
    public void teleopInit() { }
    public void periodic() { }
}
