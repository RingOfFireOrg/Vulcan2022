package frc.robot;

import edu.wpi.first.wpilibj.motorcontrol.VictorSP;

public class Intake {
      
    public VictorSP intakeMotor;
    private double intakeSpeed = 1;

    public void teleopInit() {
        intakeMotor = Container.get().intakeMotor;
    }

    public void teleopControl() {
        if (ControlSystems.get().mGamepadA()) { //In
            intakeMotor.set(intakeSpeed);
        } else if (ControlSystems.get().mGamepadB()) { //Out
            intakeMotor.set(-intakeSpeed);
        } else {
            intakeMotor.set(0);
        }
    }
}