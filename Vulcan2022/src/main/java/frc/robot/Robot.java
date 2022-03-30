// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;

/**
 * This is a demo program showing the use of the DifferentialDrive class, specifically it contains
 * the code necessary to operate a robot with tank drive.
 */
public class Robot extends TimedRobot {

  protected DriveTrain driveTrain;
  protected Autonomous autonomous;
  protected Climber climber;
  protected Transfer transfer;
  protected Intake intake;
  protected VisionShooterTurret visionShooterTurret;
  
  @Override
  public void robotInit() {
    driveTrain = new DriveTrain();
    autonomous = new Autonomous();
    climber = new Climber();
    transfer = new Transfer();
    intake = new Intake();
    visionShooterTurret = new VisionShooterTurret();

    driveTrain.teleopInit();
    climber.teleopInit();
    transfer.teleopInit();
    intake.teleopInit();
    visionShooterTurret.teleopInit();

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
    climber.teleopControl();
    transfer.teleopControl();
    intake.teleopControl();
    visionShooterTurret.teleopControl();
    
    //subscribe to meldrop
    //drop at tilted towers
    //oh no dr disrespect is right there
    //die
  }
} //nice