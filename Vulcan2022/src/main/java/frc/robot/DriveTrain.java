
package frc.robot;

import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;

public class DriveTrain {

    MotorController leftMotors, rightMotors;
    LinearServo linear;
    private double driveCoefficient;

    public DriveTrain() {
        rightMotors = new MotorControllerGroup(Container.get().frontRightMotor, Container.get().backRightMotor);
        leftMotors = new MotorControllerGroup(Container.get().frontLeftMotor, Container.get().backLeftMotor);
        linear = new LinearServo(0, 50, 5);
        driveCoefficient = .35;
    }

    public void teleopControl() {
        // double joystickInputSpeed = Controllers.get().leftstick.getY();

        double leftInputSpeed = Controllers.get().dGamepadLeftY();
        double rightInputSpeed = Controllers.get().dGamepadRightY();
        if (Controllers.get().dGamepadA()) {
            driveCoefficient = .4;
        } else if (Controllers.get().dGamepadB()) {
            driveCoefficient = .2;
        }

        rightMotors.set(rightInputSpeed * driveCoefficient);
        leftMotors.set(leftInputSpeed * driveCoefficient);
    }
}
