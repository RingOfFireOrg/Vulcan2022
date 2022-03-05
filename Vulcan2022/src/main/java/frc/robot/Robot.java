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
  public void autonomousPeriodic() {
    autonomous.autonomousPeriodic();
  }
  
  @Override
  public void teleopPeriodic() {
    
    driveTrain.teleopControl();
    
    if (ControlSystems.getInstance().mGamepadA()) {
      Container.getInstance().intakeMotor.set(.8);
    } else if (ControlSystems.getInstance().mGamepadB()) {
      Container.getInstance().intakeMotor.set(-.8);
    } else {
      Container.getInstance().intakeMotor.set(0);
    }

    if (ControlSystems.getInstance().mGamepadX()) {
      Container.getInstance().transferMotor1.set(0.5);
      Container.getInstance().transferMotor2.set(-0.5);
    } else if (ControlSystems.getInstance().mGamepadY()) {
      Container.getInstance().transferMotor1.set(-0.5);
      Container.getInstance().transferMotor2.set(0.5);
    } else {
      Container.getInstance().transferMotor1.set(0);
      Container.getInstance().transferMotor2.set(0);
    }

    Container.getInstance().shooter.set(ControlMode.PercentOutput, ControlSystems.getInstance().mGamepadRightY() * .8);

    //subscribe to meldrop
    //drop at tilted towers
    //oh no dr disrespect is right there
    //die
    
    /*double speed = ControlSystems.getInstance().rightstick.getY();
    if (speed < 0.05 && speed > 0.05) {
      speed = 0;
    }*/
    //Container.getInstance().climberLeft.set(ControlSystems.getInstance().rightstick.getY());
    //Container.getInstance().climberRight.set(ControlSystems.getInstance().leftstick.getY());
    //Container.getInstance().frontLeftMotor.set(speed);
  }
}