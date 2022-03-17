package frc.robot;
import edu.wpi.first.wpilibj.motorcontrol.VictorSP;

public class Intake {
      
    public VictorSP intakeMotor;
    private double intakeSpeed = 0.8;

    public void teleopInit() {
        intakeMotor = Container.get().intakeMotor;
    }

    public void teleopControl() {
        if (ControlSystems.get().mGamepadA()) { //In
            Container.get().intakeMotor.set(intakeSpeed);
        } else if (ControlSystems.get().mGamepadB()) { //Out
            Container.get().intakeMotor.set(intakeSpeed);
        } else {
            Container.get().intakeMotor.set(0);
        }
    }
}