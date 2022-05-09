package frc.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class Pneumatics extends TeleopModule{
    
    DoubleSolenoid piston1;
    DoubleSolenoid piston2;

    public void Pneumatics() {
        piston1 = new DoubleSolenoid (3, null, 2, 4);
      //  piston1 = new DoubleSolenoid (3, null, 2, 4);
    }

    @Override
    public void teleopControl() {
        if(ControlSystems.getInstance().mGamepadA()) {
            piston1.set(Value.kForward);
        } else if (ControlSystems.getInstance().mGamepadB()) {
            piston1.set(Value.kReverse);
        } //else {
            //piston1.set(Value.kOff);
        //}
        
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
