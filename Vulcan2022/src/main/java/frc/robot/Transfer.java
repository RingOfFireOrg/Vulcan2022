package frc.robot;
import com.revrobotics.CANSparkMax;

public class Transfer {
      
    public CANSparkMax transferMotor1;
    public CANSparkMax transferMotor2;
    private double transferSpeed = 0.33;

    public void teleopInit() {
        transferMotor1 = Container.get().transferMotor1;
        transferMotor2 = Container.get().transferMotor2;
    }

    public void teleopControl() {           
        if (Controllers.get().mGamepadX()) { //In
            transferMotor1.set(transferSpeed);
            transferMotor2.set(-transferSpeed);
        } else if (Controllers.get().mGamepadY()) { //Out
            transferMotor1.set(-transferSpeed);
            transferMotor2.set(transferSpeed);
        } else { //Stop
            transferMotor1.set(0);
            transferMotor2.set(0);
        }
    }
}