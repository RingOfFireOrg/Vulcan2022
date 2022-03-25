package frc.robot;

import edu.wpi.first.wpilibj.motorcontrol.VictorSP;

public class Intake {
      
    public VictorSP intakeMotor;
    private double intakeSpeed = 1;

    public void teleopInit() {
        intakeMotor = Container.get().intakeMotor;
    }

    public void teleopControl() {
        Container.get().intakeMotor.set(ControlSystems.get().mGamepadA()?intakeSpeed:ControlSystems.get().mGamepadB()?-intakeSpeed:0);

        // if (ControlSystems.get().mGamepadA()) { //In
        //     Container.get().intakeMotor.set(intakeSpeed);
        // } else if (ControlSystems.get().mGamepadB()) { //Out
        //     Container.get().intakeMotor.set(-intakeSpeed);
        // } else {
        //     Container.get().intakeMotor.set(0);
        // }
    }
}