package frc.robot; 

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
 
import javax.swing.TransferHandler.TransferSupport;
  
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.SerialPort;

//NEED IMPORTS

public class Autonomous {
    //private SpeedControllerGroup leftMotors, rightMotors; 
    //private CANEncoder leftEncoder, rightEncoder;
    private int autonomousStep = -1;
    private double FEET = 8.50; // To go one FEET, the robot encoder has to read ~8.50 inches of the wheel
    
    /*
    switch (autonomousStep) {
        case 0: {
            //Setup
            
            autonomousStep++;
            break;
        }
    }*/
}
