// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
//import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.PneumaticHub;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class PneumaticsSubsystem extends SubsystemBase {
  private PneumaticHub pneumaticHub;
  private Compressor compressor;
  private DoubleSolenoid intakeSolenoids;
  private DoubleSolenoid shooterSolenoids;
 

 
  /** Creates a new PneumaticsSubsystem. */
  public PneumaticsSubsystem() {
    pneumaticHub = new PneumaticHub();
    compressor = new Compressor(PneumaticsModuleType.REVPH);
    intakeSolenoids = new DoubleSolenoid(PneumaticsModuleType.REVPH, Constants.intakeExtendPort, Constants.intakeRetractPort);
    shooterSolenoids = new DoubleSolenoid(PneumaticsModuleType.REVPH, Constants.shooterExtendPort, Constants.shooterRetractPort);
  }

  /**
   * This function enables the digital output of the compressor.
   */
  public void start(){
    compressor.enableDigital();
  }

  /**
   * This function disables the compressor.
   */
  public void stop(){
    compressor.disable();
  }

  /**
   * This function returns the state of the compressor
   * 
   * @return The state of the compressor.
   */
  public boolean get(){
    return pneumaticHub.getCompressor();
  }

  /**
   * This function returns the intakeSolenoids object
   * 
   * @return The intakeSolenoids variable is being returned.
   */
  public DoubleSolenoid getIntakeSolenoids(){
    return intakeSolenoids;
  }

  /**
   * This function returns the shooterSolenoids variable
   * 
   * @return The shooterSolenoids
   */
  public DoubleSolenoid getShooterSolenoids(){
    return shooterSolenoids;
  }
  
  @Override
  public void periodic() {
    
  }
}
