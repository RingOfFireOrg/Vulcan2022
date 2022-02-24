package frc.robot;

import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;

public class ControlSystems {
    
    private static ControlSystems thetrueControlSystem;
    Joystick rightstick;
    Joystick leftstick;
    Joystick manipulatorStick;
    XboxController driverController;
    XboxController manipulatorController;

    public ControlSystems() {
        rightstick = new Joystick(0);
        leftstick = new Joystick(1);
        //manipulatorStick = new Joystick(2);
        manipulatorController = new XboxController(2);
        //driverController = new XboxController(3);
    }
    public double rightSpeed() {
        if (Math.abs(rightstick.getY()) < .1) {
            return 0;
        }
        return rightstick.getY();
    } 
    public double leftSpeed() {
        if (Math.abs(leftstick.getY()) < .1) {
            return 0;
        }
        return leftstick.getY();
    }
    public double manipulatorStickSpeed() {
        return manipulatorStick.getY();
    }
    public double manipulatorStickTwist() {
        return manipulatorStick.getTwist();
    }
    public Boolean manipulatorStickTrigger() {
        return manipulatorStick.getTrigger();
    }
    public double mGamepadLeftY() {
        return manipulatorController.getRawAxis(RobotMap.MANIPULATOR_LEFT_JOYSTICK_Y);
    }
    public double mGamepadRightY() {
        return manipulatorController.getRawAxis(3);
    }
    public double dGamepadLeftY() {
        return driverController.getRawAxis(RobotMap.MANIPULATOR_LEFT_JOYSTICK_Y);
    }
    public double dGamepadRightY() {
        return driverController.getRawAxis(RobotMap.MANIPULATOR_RIGHT_JOYSTICK_Y);
    }
    public double gamepadLeftTrigger() {
        return manipulatorController.getRawAxis(RobotMap.MANIPULATOR_LEFT_TRIGGER); //nice
    }
    public double gamepadRightTrigger() {
        return manipulatorController.getRawAxis(RobotMap.MANIPULATOR_RIGHT_TRIGGER);
    }
    public static ControlSystems getInstance() {
        if (thetrueControlSystem != null) {
            return thetrueControlSystem;
        }                   
        else {
            thetrueControlSystem = new ControlSystems();
            return thetrueControlSystem;
        }
    }
}