  
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
    LinearServo linear;
    LinearServo linear2;
    
    public DriveTrain() {
        //rightMotors = new MotorControllerGroup(Container.getInstance().frontRightMotor, Container.getInstance().backRightMotor);
        //leftMotors = new MotorControllerGroup(Container.getInstance().frontLeftMotor, Container.getInstance().backLeftMotor);
        linear = new LinearServo(9, 50, 5);
        linear2 = new LinearServo(8, 50, 5);
    }   

    @Override
    public void teleopControl() {
        double joystickInputSpeed = ControlSystems.getInstance().leftstick.getY();
        
        //double leftInputSpeed = ControlSystems.getInstance().dGamepadLeftY();
        //double rightInputSpeed = ControlSystems.getInstance().dGamepadRightY();
        //rightMotors.set(rightInputSpeed);
        //leftMotors.set(leftInputSpeed);
        if (ControlSystems.getInstance().mGamepadX()){
            linear.setPosition(50);
    } else {
        linear.setPosition(0);
    }

    if (ControlSystems.getInstance().mGamepadX()){
        linear2.setPosition(50);
} else {
    linear2.setPosition(0);
}

if (ControlSystems.getInstance().mGamepadA()){
    Container.getInstance().winchMotor.set(1);
} else if (ControlSystems.getInstance().mGamepadB()) {
    Container.getInstance().winchMotor.set(-1);
} else {
    Container.getInstance().winchMotor.set(0);
}

if (ControlSystems.getInstance().mGamepadLB()){
    Container.getInstance().winchMotorTwo.set(1);
} else if (ControlSystems.getInstance().mGamepadRB()) {
    Container.getInstance().winchMotorTwo.set(-1);
} else {
    Container.getInstance().winchMotorTwo.set(0);
}


    }

    

    @Override
    public void teleopInit() { }
    public void periodic() { }
}
