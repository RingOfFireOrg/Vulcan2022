package frc.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class Pneumatics extends TeleopModule{
    
    DoubleSolenoid piston1;
    DoubleSolenoid piston2;

    public Pneumatics() {
        piston1 = new DoubleSolenoid (PneumaticsModuleType.CTREPCM, 1, 0);
      //  piston1 = new DoubleSolenoid (3, null, 2, 4);
    }

    @Override
    public void teleopControl() {
        if(ControlSystems.getInstance().mGameCubeA()) {
            piston1.set(Value.kForward);
        } else if (ControlSystems.getInstance().mGameCubeB()) {
            piston1.set(Value.kReverse);
        } else {
            piston1.set(Value.kOff);
        }
        
    }

    @Override
    public void teleopInit() {
        // TODO Auto-generated method stub
        
    }
    @Override
    public void periodic() {
        // TODO Auto-generated method stub
        
    }

}
