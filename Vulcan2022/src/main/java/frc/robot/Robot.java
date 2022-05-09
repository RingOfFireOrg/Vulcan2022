// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.can.TalonFX;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj.motorcontrol.VictorSP;
import edu.wpi.first.wpilibj.Compressor;



/**
 * This is a demo program showing the use of the DifferentialDrive class, specifically it contains
 * the code necessary to operate a robot with tank drive.
 */
public class Robot extends TimedRobot {

  protected DriveTrain driveTrain;
  protected Autonomous autonomous;
  protected Pneumatics pneumatics;

  int counter = 0;
  
  @Override
  public void robotInit() {
    driveTrain = new DriveTrain();
    //autonomous = new Autonomous();
 
    //driveTrain.teleopInit();
    pneumatics = new Pneumatics();
    
    
  }

  @Override
  public void robotPeriodic() {

  }

  @Override
  public void autonomousInit() {
    //autonomous.autonomousInit();
  }

  @Override
  public void teleopInit() {
    pneumatics.teleopInit();
  }

  @Override
  public void teleopPeriodic() {
    //driveTrain.teleopControl();
    
    /*double speedRight= ControlSystems.getInstance().rightstick.getY();
    if (speedRight < 0.1 && speedRight > 0.1) {
      speedRight = 0;
    }
    double speedLeft = ControlSystems.getInstance().leftstick.getY();
    if (speedLeft < 0.1 && speedLeft > 0.1) {
      speedLeft = 0;
    }
    Container.getInstance().climberLeft.set(speedLeft);
    Container.getInstance().climberRight.set(speedRight);

    if (ControlSystems.getInstance().mGamepadA()) {
      Container.getInstance().winchMotor.set(1);
    } else if (ControlSystems.getInstance().mGamepadB()){
      Container.getInstance().winchMotor.set(-1);
    } else {
      Container.getInstance().winchMotor.set(0);
    }

    Container.getInstance().winchMotorTwo.set(ControlSystems.getInstance().mGamepadLeftY());*/
    pneumatics.teleopControl();
    
    //Container.getInstance().frontLeftMotor.set(speed);
  }
}