// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj.motorcontrol.VictorSP;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;

/**
 * This is a demo program showing the use of the DifferentialDrive class, specifically it contains
 * the code necessary to operate a robot with tank drive.
 */
public class Robot extends TimedRobot {

  protected DriveTrain driveTrain;
  protected Autonomous autonomous;
  protected Turret turret;
  protected Climber climber;
  protected Transfer transfer;
  protected Intake intake;
  protected Shooter shooter;
  
  @Override
  public void robotInit() {
    driveTrain = new DriveTrain();
    autonomous = new Autonomous();
    turret = new Turret();
    climber = new Climber();
    transfer = new Transfer();
    intake = new Intake();
    shooter = new Shooter();

    driveTrain.teleopInit();
    turret.teleopInit();
    climber.teleopInit();
    transfer.teleopInit();
    intake.teleopInit();
    shooter.teleopInit();

    UsbCamera camera = CameraServer.startAutomaticCapture();
    camera.setResolution(640, 480);
  }

  @Override
  public void robotPeriodic() {}

  @Override
  public void autonomousInit() {
    autonomous.autonomousInit();
  }

  @Override
  public void autonomousPeriodic() {
    autonomous.autonomousPeriodic();
  }
  
  @Override
  public void teleopPeriodic() {
    driveTrain.teleopControl();
    turret.teleopControl();
    climber.teleopControl();
    transfer.teleopControl();
    intake.teleopControl();
    shooter.teleopControl();

    //Drivetrain (2)
    //Container.get().transferMotor1.set(1);
    //Container.get().transferMotor2.set(1);
    
    
    //turret  
    //Container.get().turretMotor.set(0.2);

    //climber
    //Container.get().climberLeft.set(-0.6);
    //Container.get().climberRight.set(-0.6);
    //Container.get().winchMotorTwo.set(1);
    //Container.get().winchMotor.set(-1);
    //shooter
    //Container.get().shooter.set(ControlMode.PercentOutput, 0.25);

    //Intake 
    //Container.get().intakeMotor.set(1);

    //subscribe to meldrop
    //drop at tilted towers
    //oh no dr disrespect is right there
    //die
  }
}