package frc.robot; 

import com.revrobotics.RelativeEncoder;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.motorcontrol.VictorSP;


public class Container {
    public RelativeEncoder leftEncoder, rightEncoder;

    public CANSparkMax climberRight;
    public CANSparkMax climberLeft;
    public VictorSP winchMotor;
    public VictorSP winchMotorTwo;
    public CANSparkMax frontRightMotor;
    public CANSparkMax backRightMotor;
    public CANSparkMax frontLeftMotor;
    public CANSparkMax backLeftMotor;
    public VictorSP intakeMotor;
    public CANSparkMax transferMotor1;
    public CANSparkMax transferMotor2;
    public TalonFX shooter;
    public CANSparkMax turretMotor;
    public DigitalInput rightLimitSwitch;
    public DigitalInput leftLimitSwitch;
    public RelativeEncoder turretEncoder;

    public AHRS ahrs;

    private static Container theTrueContainer;

    private Container() {
        climberRight = new CANSparkMax(7, MotorType.kBrushless);
        climberLeft = new CANSparkMax(8, MotorType.kBrushless);
        
        winchMotor = new VictorSP(7);
        winchMotorTwo = new VictorSP(8);
        
        frontLeftMotor = new CANSparkMax(RobotMap.DT_LEFT_FORWARD, MotorType.kBrushless);
        frontLeftMotor.setInverted(true);

        frontRightMotor = new CANSparkMax(RobotMap.DT_RIGHT_FORWARD, MotorType.kBrushless);
        frontRightMotor.setInverted(false);
        
        backRightMotor = new CANSparkMax(RobotMap.DT_RIGHT_BACK, MotorType.kBrushless);
        backRightMotor.setInverted(false);
        
        backLeftMotor = new CANSparkMax(RobotMap.DT_LEFT_BACK, MotorType.kBrushless);
        backLeftMotor.setInverted(true);
        
        intakeMotor = new VictorSP(6);

        leftEncoder = frontLeftMotor.getEncoder();
        rightEncoder = frontRightMotor.getEncoder();

        transferMotor1 = new CANSparkMax(11, MotorType.kBrushless);
        transferMotor2 = new CANSparkMax(12, MotorType.kBrushless);

        shooter = new TalonFX(6);

        turretMotor = new CANSparkMax(RobotMap.TURRET_SPINNER, MotorType.kBrushless);
        turretEncoder = turretMotor.getEncoder();

        rightLimitSwitch = new DigitalInput(1);
        leftLimitSwitch = new DigitalInput(2);

        ahrs = new AHRS(SerialPort.Port.kUSB);
        ahrs.reset();
    }

    public double getLeftInches() {
        return leftEncoder.getPosition() / RobotMap.DRIVEBASE_GEAR_RATIO * Math.PI * RobotMap.DRIVE_WHEEL_DIAMETER_IN;
    }

    public double getRightInches() {
        return rightEncoder.getPosition() / RobotMap.DRIVEBASE_GEAR_RATIO * Math.PI * RobotMap.DRIVE_WHEEL_DIAMETER_IN;
    }

    public static Container get() {
        if (theTrueContainer != null) return theTrueContainer;

        theTrueContainer = new Container();
        return theTrueContainer;
    }
}
