package frc.robot;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.revrobotics.CANSparkMax;

public class Shooter {
      
    public TalonFX shooter;
    public CANSparkMax transferMotor1;
    public CANSparkMax transferMotor2;
    
    private double lowShooterSpeed = 0.3;
    private double highShooterSpeed = 0.6;
    private double second = 20;
    private double startTransferDelay = second * 3;
    private double startTransferTimer = 0;
    private double transferSpeed = 0.5;

    public void teleopInit() {
        shooter = Container.get().shooter;
        transferMotor1 = Container.get().transferMotor1;
        transferMotor2 = Container.get().transferMotor2;
    }

    public void teleopControl() {
        double shooterSpeed = 0;
        if (ControlSystems.get().mGamepadRightTrigger() > 0.1) {
            shooterSpeed = highShooterSpeed;
        } else if (ControlSystems.get().mGamepadLeftTrigger() > 0.1) {
            shooterSpeed = lowShooterSpeed;
        }

        if (ControlSystems.get().mGamepadLeftBumper() == true) {
            startTransferTimer++;
            if (startTransferTimer > startTransferDelay) {
                transferIn();
            } else {
                transferStop();
            }
            shooterSpeed = lowShooterSpeed;
        } else if (ControlSystems.get().mGamepadRightBumper() == true) {
            startTransferTimer++;
            if (startTransferTimer > startTransferDelay) {
                transferIn();
            } else {
                transferStop();
            }
        } else {
            startTransferTimer = 0;
        }

        Container.get().shooter.set(ControlMode.PercentOutput, shooterSpeed);
    }

    public void transferIn() {
        transferMotor1.set(transferSpeed);
        transferMotor2.set(-transferSpeed);
    }

    public void transferStop() {
        transferMotor1.set(0);
        transferMotor2.set(0);
    }
}