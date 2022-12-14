// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.StorageSubsystem;

public class IntakeCommand extends CommandBase {
  private IntakeSubsystem intake;
  private StorageSubsystem storage;
  private Timer timeoutTimer;
  private Double timeoutThreshold;
  /** Creates a new IntakeCommand. */
  public IntakeCommand(IntakeSubsystem intake, StorageSubsystem storage) {
    this.intake = intake;
    this.storage = storage;

    timeoutTimer = new Timer();
    timeoutThreshold = 3.5;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(intake);
    addRequirements(storage);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    timeoutTimer.reset();
    timeoutThreshold = 3.5;
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    // This is checking if the intake button is pressed and if the robot is not in test mode. If both
    // of these are true, then the intake will run.
    if (!RobotState.isTest() && Constants.driveController.getRawButton(Constants.intakeButton)){
      intake.runIntake();
      // This is checking if the top and bottom proximity sensors are not triggered. If they are not
      // triggered, then the intake will extend.
      if (!(storage.getTopProxSensor() && (storage.getBottomProxSensor() || intake.getBeamBreakSensor()))){
        //System.out.println("Intaking");
        if (!intake.isExtended()){
          intake.extend();
          
        }
      }else{
        //System.out.println("Not intaking");
        if (intake.isExtended()){
          intake.retract();
        }
      }
    }
    
    if (!storage.getTopProxSensor()){
      storage.wheelntake();
    }else{
      storage.wheelStop();
    }

    // This is checking if the bottom proximity sensor is triggered. If it is not triggered, then the
    // belt will run. If it is triggered, then it will check if the top proximity sensor is triggered.
    // If it is triggered, then the belt will stop. If it is not triggered, then the belt will run.
    if (!storage.getBottomProxSensor()){
      storage.beltIntake();
    }else{
      if (storage.getTopProxSensor()){
        storage.beltStop();
      }else{
        storage.beltIntake();
      }
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    if (!RobotState.isTest()){
      if (!Constants.driveController.getRawButton(Constants.readyToShootButton)){
        if (intake.isExtended()){
          intake.retract();
          intake.stopIntake();
        }
        storage.stop();
      }
    }

    if (interrupted){
      intake.retract();
    }

    intake.stop();

  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    // This is checking if the intake button is not pressed. If it is not pressed, then the timeout
    // timer will start. If the intake is extended, then the intake will retract and stop. If the
    // intake button is pressed, then the timeout timer will reset.
    if (!Constants.driveController.getRawButton(Constants.intakeButton)){
      timeoutTimer.start();
      if (intake.isExtended()){
        intake.retract();
        intake.stopIntake();
      }      
    }else{
      timeoutTimer.reset();
    }

    if(((storage.getTopProxSensor() && (storage.getBottomProxSensor() || intake.getBeamBreakSensor()))) || RobotState.isTest()){
      timeoutThreshold = -0.1;
    }


    return timeoutTimer.get() >= timeoutThreshold;
  }
}
