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
  protected Turret turret;
  
  @Override
  public void robotInit() {
    driveTrain = new DriveTrain();
    autonomous = new Autonomous();
    turret = new Turret();

    driveTrain.teleopInit();
    turret.teleopInit();
  }

  @Override
  public void robotPeriodic() {

  }
  

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
    
    if (ControlSystems.get().mGamepadA()) {
      Container.get().intakeMotor.set(.8);
    } else if (ControlSystems.get().mGamepadB()) {
      Container.get().intakeMotor.set(-.8);
    } else {
      Container.get().intakeMotor.set(0);
    }

    if (ControlSystems.get().mGamepadX()) {
      Container.get().transferMotor1.set(0.5);
      Container.get().transferMotor2.set(-0.5);
    } else if (ControlSystems.get().mGamepadY()) {
      Container.get().transferMotor1.set(-0.5);
      Container.get().transferMotor2.set(0.5);
    } else {
      Container.get().transferMotor1.set(0);
      Container.get().transferMotor2.set(0);
    }

   // double shooterSpeed = 0;
    // if (ControlSystems.get().mGamepadRightBumper() == true) {
    //   shooterSpeed = 0.8;
    // } else if (ControlSystems.get().mGamepadLeftBumper() == true) {
    //   shooterSpeed = 0.3;
    // }

    double shooterSpeed = ControlSystems.get().mGamepadRightBumper() ? 0.5 : ControlSystems.get().mGamepadLeftBumper() ? 0.35 : 0;

    Container.get().shooter.set(ControlMode.PercentOutput, shooterSpeed);

    //subscribe to meldrop
    //drop at tilted towers
    //oh no dr disrespect is right there
    //die
  }
}