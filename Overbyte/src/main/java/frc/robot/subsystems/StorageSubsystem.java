// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Timer;

import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class StorageSubsystem extends SubsystemBase {
  private TalonFX wheel;
  private TalonSRX belt;
  private DigitalInput topProxSensor;
  private DigitalInput bottomProxSensor;
  private Timer shootTimer;
  /** Creates a new StorageSubsystem. */
  public StorageSubsystem() {
    wheel = new TalonFX(Constants.storageWheelMotorPort);
    belt = new TalonSRX(Constants.storageBeltMotorPort);
    topProxSensor = new DigitalInput(Constants.storageTopProxSensorPort);
    bottomProxSensor = new DigitalInput(Constants.storageBottomProxSensorPort);
  }

  /**
   * This function returns the value of the top proximity sensor
   * 
   * @return The value of the topProxSensor.
   */
  public boolean getTopProxSensor(){
    return !topProxSensor.get();
  }

  /**
   * This function returns the value of the bottom proximity sensor
   * 
   * @return The value of the sensor.
   */
  public boolean getBottomProxSensor(){
    return !bottomProxSensor.get();
  }
/**
 * Sets the speed of the wheel to the speedPercent value.
 * 
 * @param speedPercent The speed of the wheel, from -1 to 1.
 */
  public void setWheelSpeed(double speedPercent){
    wheel.set(TalonFXControlMode.PercentOutput, speedPercent);
  }

  /**
   * Set the speed of the belt to the given speed percentage.
   * 
   * @param speedPercent The speed of the belt, from -1 to 1.
   */
  public void setBeltSpeed(double speedPercent){
    belt.set(TalonSRXControlMode.PercentOutput, speedPercent);
  }

  /**
   * If the top proximity sensor is triggered, run the intake wheel at 10% power, otherwise run the
   * intake wheel at 0% power
   */
  public void intake(){
    if (getTopProxSensor()){
      wheel.set(TalonFXControlMode.PercentOutput, -0.1);
    }else{
      wheel.set(TalonFXControlMode.PercentOutput, 0.0);
    }
    belt.set(TalonSRXControlMode.PercentOutput, -0.5);
  }

  public void feedShooter(){
    if (!getTopProxSensor()){
      shootTimer.start();
      if (shootTimer.hasElapsed(.25)){
        wheel.set(TalonFXControlMode.PercentOutput, -0.25);
      }else{
        wheel.set(TalonFXControlMode.PercentOutput, 0.0);
      }
    }else{
      wheel.set(TalonFXControlMode.PercentOutput, -0.25);
    }
    belt.set(TalonSRXControlMode.PercentOutput, -0.25);
  }

  public void wheelFeed(){
    setWheelSpeed(-0.7);
  }

  /**
   * This function sets the wheel speed to -0.25.
   */
  public void wheelntake(){
    setWheelSpeed(-0.25);
  }

  /**
   * It stops the wheel.
   */
  public void wheelStop(){
    setWheelSpeed(0.0);
  }

  /**
   * > This function sets the speed of the belt to -0.25
   */
  public void beltFeed(){
    setBeltSpeed(-0.25);
  }

  public void beltIntake(){
    setBeltSpeed(-0.5);
  }

  /**
   * This function sets the speed of the belt to 0.30
   */
  public void beltReverse(){
    setBeltSpeed(0.30);
  }

  /**
   * > This function sets the speed of the belt to 0.0
   */
  public void beltStop(){
    setBeltSpeed(0.0);
  }



  public void stop(){
    wheelStop();
    beltStop();
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    ////System.out.println("Top: " + (getTopProxSensor() ? "Has " : "No ") + "Ball");
    ////System.out.println("Bottom: " + (getBottomProxSensor() ? "Has " : "No ") + "Ball");
  }
}
