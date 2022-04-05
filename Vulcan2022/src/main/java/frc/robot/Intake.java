package frc.robot;

import edu.wpi.first.wpilibj.motorcontrol.VictorSP;

public class Intake {
      
    public VictorSP intakeMotor;
    private double intakeSpeed = 1;
    private VictorSP intakeExtendingMotor; 
    private double intakeExtendingSpeed = 1; 

    public void teleopInit() {
        intakeMotor = Container.get().intakeMotor;
        intakeExtendingMotor = Container.get().intakeExtendingMotor;
    }

    public void teleopControl() {
        if (Controllers.get().mGamepadA()) { //In
            intakeMotor.set(intakeSpeed);
        } else if (Controllers.get().mGamepadB()) { //Out
            intakeMotor.set(-intakeSpeed);
        } else {
            intakeMotor.set(0);
        }

        if (Controllers.get().mGamepadPov() == "up") { //In
            intakeExtendingMotor.set(intakeExtendingSpeed);
        } else if (Controllers.get().mGamepadPov() == "down") { //Out
            intakeExtendingMotor.set(-intakeExtendingSpeed);
        } else {
            intakeExtendingMotor.set(0);
        }
    }
}