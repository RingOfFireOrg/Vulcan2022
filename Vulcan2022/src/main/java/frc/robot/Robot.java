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


/**
 * This is a demo program showing the use of the DifferentialDrive class, specifically it contains
 * the code necessary to operate a robot with tank drive.
 */
public class Robot extends TimedRobot {

  protected DriveTrain driveTrain;
  protected Autonomous autonomous;
  int counter = 0;
  
  @Override
  public void robotInit() {
    driveTrain = new DriveTrain();
    autonomous = new Autonomous();

    driveTrain.teleopInit();
  }

  @Override
  public void robotPeriodic() {

  }

  @Override
  public void autonomousInit() {
    autonomous.autonomousInit();
  }

  
  @Override
  public void teleopPeriodic() {
    driveTrain.teleopControl();
    counter++;
    /*double speed = ControlSystems.getInstance().rightstick.getY();
    if (speed < 0.05 && speed > 0.e05) {
      speed = 0;
    }*/
   // Container.getInstance().frontLeftMotor.set(speed);
    Container.getInstance().shooterFalcon.set(ControlMode.PercentOutput, ControlSystems.getInstance().rightstick.getY()*1);
    Container.getInstance().frontLeftMotor.set(ControlSystems.getInstance().leftstick.getY()*.2);
  Container.getInstance().transfer2.set(ControlSystems.getInstance().leftstick.getX());
  }
}