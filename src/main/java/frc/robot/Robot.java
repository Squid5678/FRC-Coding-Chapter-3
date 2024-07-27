// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
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

  /* Instantiate the 4 Drivetrain Motors */
  private TalonFX leftSidePrimary = new TalonFX(0);
  private TalonFX leftSideFollower = new TalonFX(1);
  private TalonFX rightSidePrimary = new TalonFX(2);
  private TalonFX rightSideFollower = new TalonFX(3);

  /* Instantiate the Shooter Motor */
  private TalonFX shooterMotor = new TalonFX(4);

  /* Instantiate the Elevator Motor */
  private TalonFX elevatorMotor = new TalonFX(5);

  /* Instantiate the WPILib Timer */
  private Timer timer = new Timer();

  /* Instantiate our XBOX Controller */
  private final Joystick xboxController = new Joystick(0);
  

  /*For toggle */
  private boolean buttonIsPressed;

  /*For elevator */
  private double elevatorTime;
  private int direction = -1; //-1 by default so that it flips to +1 on first toggle

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    // Instantiate our RobotContainer.  This will perform all our button bindings, and put our
    // autonomous chooser on the dashboard.
    m_robotContainer = new RobotContainer(); 

    /* Begin by setting all of the motors to their factory defaults. */
    leftSidePrimary.getConfigurator().apply(new TalonFXConfiguration());
    leftSideFollower.getConfigurator().apply(new TalonFXConfiguration());
    rightSidePrimary.getConfigurator().apply(new TalonFXConfiguration());
    rightSideFollower.getConfigurator().apply(new TalonFXConfiguration());
    shooterMotor.getConfigurator().apply(new TalonFXConfiguration());
    elevatorMotor.getConfigurator().apply(new TalonFXConfiguration());

    /* Next, set the necessary inverts and tell the follower motors to follow. */
    rightSidePrimary.setInverted(true);
    rightSideFollower.setInverted(true);
    leftSideFollower.setControl(new Follower(0, false));
    rightSideFollower.setControl(new Follower(2, false));

    /* Finally, call the custom method that sets the current limits. */
    configureMotorCurrentLimits();
  }

  /**
   * Method used to configure the motor current limits.
   */
  public void configureMotorCurrentLimits() {
    /* Declare the current configuration. */
    var currentConfiguration = new CurrentLimitsConfigs();

    /* Refresh all the motor's existing current configuration with our new motor configuration.
     * This essentially tells the motor to expect a new current configuration so we don't
     * end up in a fight with other timing loops when initializing the motor's other configs.
    */
    leftSidePrimary.getConfigurator().refresh(currentConfiguration);
    leftSideFollower.getConfigurator().refresh(currentConfiguration);
    rightSidePrimary.getConfigurator().refresh(currentConfiguration);
    rightSideFollower.getConfigurator().refresh(currentConfiguration);
    shooterMotor.getConfigurator().refresh(currentConfiguration);
    elevatorMotor.getConfigurator().refresh(currentConfiguration);

    /* Setup the Current Configuration */
    currentConfiguration.StatorCurrentLimit = 80;
    currentConfiguration.StatorCurrentLimitEnable = true;

    /* Finally, apply the current configuration. */
    leftSidePrimary.getConfigurator().apply(currentConfiguration);
    leftSideFollower.getConfigurator().apply(currentConfiguration);
    rightSidePrimary.getConfigurator().apply(currentConfiguration);
    rightSideFollower.getConfigurator().apply(currentConfiguration);
    shooterMotor.getConfigurator().apply(currentConfiguration);
    elevatorMotor.getConfigurator().apply(currentConfiguration);
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

    /* Reset the timer before teleop starts. */
    timer.stop();
    timer.reset();
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    /* TANK DRIVE CONTROL */
    leftSidePrimary.set(xboxController.getRawAxis(1)); //should be the y-axis of the left joystick
    rightSidePrimary.set(xboxController.getRawAxis(2)); //should be the y-axis of the right joystick

    /* SHOOTER MOTOR CONTROL */
    /* NOTE: Change button ID to whatever you want (my controller's A button = 1) */
    if (xboxController.getRawButton(1)) {
      shooterMotor.set(0.5);
    } else {
      shooterMotor.set(0);
    }

    


    /* TOGGLE HANDLING */

    /* NOTE: Change button ID to whatever you want (my controller's B button = 2) */

    /*CASE 1: Pressing button */
    if (xboxController.getRawButton(2) && buttonIsPressed == false) {
      buttonIsPressed = true;
      
      direction = -1*direction;
      timer.reset();
      timer.start();
    }
    /* CASE 2: Releasing button */
    else if (!xboxController.getRawButton(2) && buttonIsPressed == true){
      buttonIsPressed = false;
    }

    /* ELEVATOR MOTOR CONTROL */    
    if (timer.get() > 5){
      elevatorMotor.set(0);
      timer.stop();
    }
    else if (timer.get() >0.01) {
      elevatorMotor.set(0.5 * direction);
    }

   
    
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