package frc.robot;

import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.motorcontrol.VictorSP;

public class Climber {
      
    CANSparkMax climberLeft;
    CANSparkMax climberRight;
    VictorSP winchMotor;
    VictorSP winchMotorTwo;

    public void teleopInit() {
        climberLeft = Container.get().climberLeft;
        climberRight = Container.get().climberRight;
        winchMotor = Container.get().winchMotor;
        winchMotorTwo = Container.get().winchMotorTwo;
    }

    public void teleopControl() {
        double speedRight = Controllers.get().cGamepadLeftY();
        if (speedRight > -0.1 && speedRight < 0.1) {
            speedRight = 0;
        }

        double speedLeft = Controllers.get().cGamepadRightY();
        if (speedLeft > -0.1 && speedLeft < 0.1) {
            speedLeft = 0;
        }

        if (Controllers.get().cGamepadX()) {
            speedLeft = -1;
            speedRight = -1;
        }
        if (Controllers.get().cGamepadY()) {
            speedLeft = 1;
            speedRight = 1;
        }

        climberLeft.set(speedLeft);
        climberRight.set(speedRight);

        double winchSpeed = 0;
        if (Controllers.get().cGamepadA()) {
            winchSpeed = 1;
        } else if (Controllers.get().cGamepadB()){
            winchSpeed = -1;
        }
        
        winchMotor.set(winchSpeed);

        double winch2Speed = 0;
        if (Controllers.get().cGamepadPov() == "up") {
            winch2Speed = 0.8;
        } else if (Controllers.get().cGamepadPov() == "down") {
            winch2Speed = -0.8;
        }
        winchMotorTwo.set(winch2Speed);
    }
}
