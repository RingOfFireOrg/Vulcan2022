package frc.robot;

import com.revrobotics.CANSparkMax;

public class Climber {
      
    CANSparkMax climberLeft;
    CANSparkMax climberRight;

    public void teleopInit() {
        climberLeft = Container.get().climberLeft;
        climberRight = Container.get().climberRight;
    }

    public void teleopControl() {
        double speedRight= ControlSystems.get().rightstick.getY();
        if (speedRight < 0.1 && speedRight > 0.1) {
            speedRight = 0;
        }
        double speedLeft = ControlSystems.get().leftstick.getY();
        if (speedLeft < 0.1 && speedLeft > 0.1) {
            speedLeft = 0;
        }
        Container.get().climberLeft.set(speedLeft);
        Container.get().climberRight.set(speedRight);

        if (ControlSystems.get().mGamepadA()) {
            Container.get().winchMotor.set(1);
        } else if (ControlSystems.get().mGamepadB()){
            Container.get().winchMotor.set(-1);
        } else {
            Container.get().winchMotor.set(0);
        }

        Container.get().winchMotorTwo.set(ControlSystems.get().mGamepadLeftY());
    }
}
