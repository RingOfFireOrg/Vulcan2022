package frc.robot;

import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.revrobotics.CANSparkMax;

public class Climber {
      
    CANSparkMax climberLeft;
    CANSparkMax climberRight;

    public void teleopInit() {
        climberLeft = Container.get().climberLeft;
        climberRight = Container.get().climberRight;
    }

    public void teleopControl() {
        double speedRight = ControlSystems.get().cGamepadLeftY();
        if (speedRight > -0.1 && speedRight < 0.1) {
            speedRight = 0;
        }

        double speedLeft = ControlSystems.get().cGamepadRightY();
        if (speedLeft > -0.1 && speedLeft < 0.1) {
            speedLeft = 0;
        }

        if (ControlSystems.get().cGamepadX()) {
            speedLeft = -1;
            speedRight = -1;
        }
        if (ControlSystems.get().cGamepadY()) {
            speedLeft = 1;
            speedRight = 1;
        }

        Container.get().climberLeft.set(speedLeft);
        Container.get().climberRight.set(speedRight);

        if (ControlSystems.get().cGamepadA()) {
            Container.get().winchMotor.set(1);
        } else if (ControlSystems.get().cGamepadB()){
            Container.get().winchMotor.set(-1);
        } else {
            Container.get().winchMotor.set(0);
        }

        double winchSpeed = 0;
        if (ControlSystems.get().cGamepadPov() == "up") {
            winchSpeed = 0.8;
        } else if (ControlSystems.get().cGamepadPov() == "down") {
            winchSpeed = -0.8;
        }
        Container.get().winchMotorTwo.set(winchSpeed);
    }
}
