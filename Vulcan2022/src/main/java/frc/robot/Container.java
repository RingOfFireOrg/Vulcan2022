package frc.robot; 

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
 
import javax.swing.TransferHandler.TransferSupport;

import com.revrobotics.RelativeEncoder;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.motorcontrol.VictorSP;


public class Container {
    public RelativeEncoder leftEncoder, rightEncoder;

    //public VictorSP climberRight;
    //public VictorSP climberLeft;
    public CANSparkMax frontRightMotor;
    public CANSparkMax backRightMotor;
    public CANSparkMax frontLeftMotor;
    public CANSparkMax backLeftMotor;
    public VictorSP intakeMotor;
    public VictorSP transferMotor1;
    public VictorSP transferMotor2;
    public TalonFX shooter;
    public CANSparkMax turretMotor;

    public AHRS ahrs;

    private static Container theTrueContainer;

    private Container() {
        // climberRight = new VictorSP(1);
        // climberLeft = new VictorSP(3);

        frontLeftMotor = new CANSparkMax(RobotMap.DT_LEFT_FORWARD, MotorType.kBrushless);
        frontLeftMotor.setInverted(true);

        frontRightMotor = new CANSparkMax(RobotMap.DT_RIGHT_FORWARD, MotorType.kBrushless);
        frontRightMotor.setInverted(false);
        
        backRightMotor = new CANSparkMax(RobotMap.DT_RIGHT_BACK, MotorType.kBrushless);
        backRightMotor.setInverted(false);
        
        backLeftMotor = new CANSparkMax(RobotMap.DT_LEFT_BACK, MotorType.kBrushless);
        backLeftMotor.setInverted(true);
        
        intakeMotor = new VictorSP(9);

        leftEncoder = frontLeftMotor.getEncoder();
        rightEncoder = frontRightMotor.getEncoder();

        transferMotor1 = new VictorSP(8);
        transferMotor2 = new VictorSP(7);

        shooter = new TalonFX(6);

        turretMotor = new CANSparkMax(RobotMap.TURRET_SPINNER, MotorType.kBrushless);

        ahrs = new AHRS(SerialPort.Port.kUSB);
        ahrs.reset();
    } //nice

    public double getLeftInches() {
        return leftEncoder.getPosition() / RobotMap.DRIVEBASE_GEAR_RATIO * Math.PI * RobotMap.DRIVE_WHEEL_DIAMETER_IN;
    }

    public double getRightInches() {
        return rightEncoder.getPosition() / RobotMap.DRIVEBASE_GEAR_RATIO * Math.PI * RobotMap.DRIVE_WHEEL_DIAMETER_IN;
    }

    public static Container get() {
        if (theTrueContainer != null) {
            return theTrueContainer;
        }                   
        else {
            theTrueContainer = new Container();
            return theTrueContainer;
        }
    }
}
