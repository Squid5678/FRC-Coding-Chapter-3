// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private Command m_autonomousCommand;

  private RobotContainer m_robotContainer;
  
  private TalonFX primaryMotor = new TalonFX(1);
  private TalonFX followerMotor1 = new TalonFX(2);
  private TalonFX followerMotor2 = new TalonFX(3);

  private TalonFX leftMotor = new TalonFX(4);
  private TalonFX rightMotor = new TalonFX(5);


  /* For testing SmartDashboard printouts */
  private int incrementorRobot = 0;
  private int incrementorTeleop = 0;

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    // Instantiate our RobotContainer.  This will perform all our button bindings, and put our
    // autonomous chooser on the dashboard.
    m_robotContainer = new RobotContainer(); 

    /* Reset the motor to its factory default. */
    primaryMotor.getConfigurator().apply(new TalonFXConfiguration());
    followerMotor1.getConfigurator().apply(new TalonFXConfiguration());
    followerMotor2.getConfigurator().apply(new TalonFXConfiguration());
    leftMotor.getConfigurator().apply(new TalonFXConfiguration());
    rightMotor.getConfigurator().apply(new TalonFXConfiguration());

    /* Configure the current of the motor */
    var currentConfiguration = new CurrentLimitsConfigs();
    currentConfiguration.StatorCurrentLimit = 80;
    currentConfiguration.StatorCurrentLimitEnable = true;

    primaryMotor.getConfigurator().refresh(currentConfiguration);
    followerMotor1.getConfigurator().refresh(currentConfiguration);
    followerMotor2.getConfigurator().refresh(currentConfiguration);
    leftMotor.getConfigurator().refresh(currentConfiguration);
    rightMotor.getConfigurator().refresh(currentConfiguration);

    primaryMotor.getConfigurator().apply(currentConfiguration);
    followerMotor1.getConfigurator().apply(currentConfiguration);
    followerMotor2.getConfigurator().apply(currentConfiguration);
    leftMotor.getConfigurator().apply(currentConfiguration);
    rightMotor.getConfigurator().apply(currentConfiguration);

    followerMotor1.setControl(new Follower(1, false));
    followerMotor2.setControl(new Follower(1, false));

    rightMotor.setInverted(true);
    
  }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    // Runs the Scheduler.  This is responsible for polling buttons, adding newly-scheduled
    // commands, running already-scheduled commands, removing finished or interrupted commands,
    // and running subsystem periodic() methods.  This must be called from the robot's periodic
    // block in order for anything in the Command-based framework to work.
    CommandScheduler.getInstance().run();

    /* Simple printout of a string */
    SmartDashboard.putString("FIRST PRINT", "Hello World!");

    /* Incrementor that always runs while the robot is turned on and code is loaded */
    SmartDashboard.putNumber("INCREMENTOR ROBOT", incrementorRobot++);

    /* Live readout of the primary motor's percent out */
    SmartDashboard.putNumber("PRIMARY MOTOR % OUT", primaryMotor.get());

    /* Live readout of the primary motor's voltage */
    SmartDashboard.putNumber("PRIMARY MOTOR VOLTAGE", primaryMotor.getMotorVoltage().getValueAsDouble());

  }

  /** This function is called once each time the robot enters Disabled mode. */
  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  /** This autonomous runs the autonomous command selected by your {@link RobotContainer} class. */
  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    // schedule the autonomous command (example)
    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();
    }
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {}

  @Override
  public void teleopInit() {
    // This makes sure that the autonomous stops running when
    // teleop starts running. If you want the autonomous to
    // continue until interrupted by another command, remove
    // this line or comment it out.
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    /* Set the motor's percent output. */
    primaryMotor.set(0.5);
    leftMotor.set(0.5);
    rightMotor.set(0.5);


    /* Incrementor that only runs when the robot is enabled in Teleop mode */
    SmartDashboard.putNumber("INCREMENTOR TELEOP", incrementorTeleop++);

    

  }

  @Override
  public void testInit() {
    // Cancels all running commands at the start of test mode.
    CommandScheduler.getInstance().cancelAll();
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {}

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {}
}