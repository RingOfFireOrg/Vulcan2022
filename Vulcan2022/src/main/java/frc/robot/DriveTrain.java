
package frc.robot;

import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveTrain {

    MotorController leftMotors, rightMotors;
    LinearServo linear;
    private double driveCoefficient;
    private double leftDriveMultiplier = 1;
    private double rightDriveMultiplier = 1;
    private boolean backBtnPressed = false;
    private boolean startBtnPressed = false;

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
            driveCoefficient = .75;
        } else if (Controllers.get().dGamepadB()) {
            driveCoefficient = .35;
        }

        if (Controllers.get().dGamepadBack() && backBtnPressed == false) {
            backBtnPressed = true;
            leftDriveMultiplier *= -1;
        } else if (!Controllers.get().dGamepadBack()){
            backBtnPressed = false;
        }

        if (Controllers.get().dGamepadStart() && startBtnPressed == false) {
            startBtnPressed = true;
            rightDriveMultiplier *= -1;
        } else if (!Controllers.get().dGamepadStart()){
            startBtnPressed = false;
        }

        SmartDashboard.putNumber("Right Drive Mult", rightDriveMultiplier);

        rightMotors.set(rightDriveMultiplier * rightInputSpeed * driveCoefficient);
        leftMotors.set(leftDriveMultiplier * leftInputSpeed * driveCoefficient);
    }
}
