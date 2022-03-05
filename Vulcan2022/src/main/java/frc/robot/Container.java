package frc.robot; 

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
 
import javax.swing.TransferHandler.TransferSupport;

import com.revrobotics.RelativeEncoder;
import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.ctre.phoenix.motorcontrol.can.TalonFX;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.motorcontrol.VictorSP;


public class Container {
    public RelativeEncoder leftEncoder, rightEncoder;
    public CANSparkMax turret;
    public TalonFX shooterFalcon;
    
    // public CANSparkMax backRightMotor;
    // public CANSparkMax backLeftMotor;
    public VictorSP transfer2;

    public VictorSP frontLeftMotor;
    public VictorSP frontRightMotor;
    public TalonFX jeremyMotor;
    // public AHRS ahrs;

    private static Container theTrueContainer;

    private Container() {
        turret = new CANSparkMax(1, MotorType.kBrushless);
        turret.setInverted(true);
        shooterFalcon = new TalonFX(1);
        jeremyMotor = new TalonFX(5);
        transfer2 = new VictorSP(2);

        frontLeftMotor = new VictorSP(3);
        frontRightMotor = new VictorSP(4);
        // frontRightMotor = new CANSparkMax(RobotMap.DT_RIGHT_FORWARD, MotorType.kBrushless);
        // frontRightMotor.setInverted(false);
        
        // backRightMotor = new CANSparkMax(RobotMap.DT_RIGHT_BACK, MotorType.kBrushless);
        // backRightMotor.setInverted(false);
        
        /*backLeftMotor = new CANSparkMax(RobotMap.DT_LEFT_BACK, MotorType.kBrushless);
        backLeftMotor.setInverted(true);*/

        //ahrs = new AHRS(SerialPort.Port.kUSB);
        // ahrs.reset();
    }

    public double getLeftInches() {
        return leftEncoder.getPosition() / RobotMap.DRIVEBASE_GEAR_RATIO * Math.PI * RobotMap.DRIVE_WHEEL_DIAMETER_IN;
    }

    public double getRightInches() {
        return rightEncoder.getPosition() / RobotMap.DRIVEBASE_GEAR_RATIO * Math.PI * RobotMap.DRIVE_WHEEL_DIAMETER_IN;
    }

    public static Container getInstance() { //nice
        if (theTrueContainer != null) {
            return theTrueContainer;
        }                   
        else {
            theTrueContainer = new Container();
            return theTrueContainer;
        }
    }
}
