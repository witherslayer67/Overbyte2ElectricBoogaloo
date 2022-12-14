// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.TalonFXFeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonFX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.DataLogManager;
import frc.omegabytes.ShooterConfiguration;
import frc.robot.Constants;

public class ShooterSubsystem extends SubsystemBase {
  private DoubleSolenoid shooterSolenoids;

  private TalonFX topShooterMotor;
  private TalonFX bottomShooterMotor;


  private double topValue = 0.0;
  private double bottomValue = 0.0;
  
  private double topTunerRange = 0.0;
  private double bottomTunerRange = 0.0;

  private double topTunerValue = 0.0;
  private double bottomTunerValue = 0.0;

  
  /** Creates a new ShooterSubsystem. */
  public ShooterSubsystem(DoubleSolenoid shooterSolenoids) {
    this.shooterSolenoids = shooterSolenoids;
    topShooterMotor = new TalonFX(Constants.topShooterMotorPort);
    bottomShooterMotor = new TalonFX(Constants.bottomShooterMotorPort);

    //#region Set shooter talon PID
    /* Factory Default all hardware to prevent unexpected behaviour */
		topShooterMotor.configFactoryDefault();
    bottomShooterMotor.configFactoryDefault();
		
		/* Config neutral deadband to be the smallest possible */
		topShooterMotor.configNeutralDeadband(0.001);
    bottomShooterMotor.configNeutralDeadband(0.001);

		/* Config sensor used for Primary PID [Velocity] */
    topShooterMotor.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor, 0, 0);
    bottomShooterMotor.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor, 0, 0);
											

		/* Config the peak and nominal outputs */
		topShooterMotor.configNominalOutputForward(0);
		topShooterMotor.configNominalOutputReverse(0);
		topShooterMotor.configPeakOutputForward(1);
		topShooterMotor.configPeakOutputReverse(-1);

    // Setting the nominal and peak output of the motor.
    bottomShooterMotor.configNominalOutputForward(0);
		bottomShooterMotor.configNominalOutputReverse(0);
		bottomShooterMotor.configPeakOutputForward(1);
		bottomShooterMotor.configPeakOutputReverse(-1);

		/* Config the Velocity closed loop gains in slot0 */
		topShooterMotor.config_kF(0, Constants.shooterkF);
		topShooterMotor.config_kP(0, Constants.shooterkP);
		topShooterMotor.config_kI(0, Constants.shooterkI);
		topShooterMotor.config_kD(0, Constants.shooterkD);

    bottomShooterMotor.config_kF(0, Constants.shooterkF);
		bottomShooterMotor.config_kP(0, Constants.shooterkP);
		bottomShooterMotor.config_kI(0, Constants.shooterkI);
		bottomShooterMotor.config_kD(0, Constants.shooterkD);
    //#endregion
  }
 

  public boolean shoot(ShooterConfiguration shooterConfig){

    if (shooterConfig.getDistance() != 0.0){
      /*if (!Constants.useCalibrateController) {
        topValue = shooterConfig.getTopMotorSpeed();
        bottomValue = shooterConfig.getBottomMotorSpeed();
      }
      double topMotorSpeed = topValue;// * Constants.falconRPMToUPS;
      double bottomMotorSpeed = bottomValue;// * Constants.falconRPMToUPS;*/

      topShooterMotor.set(TalonFXControlMode.PercentOutput, shooterConfig.getTopMotorSpeed());
      bottomShooterMotor.set(TalonFXControlMode.PercentOutput, shooterConfig.getBottomMotorSpeed());

      shooterSolenoids.set(shooterConfig.isHoodUp() ? Value.kForward : Value.kReverse); //
      //System.out.printf("DEBUG: Shooter spinning at %.1f and %.1f (%s)%n", topMotorSpeed, bottomMotorSpeed, (shooterConfig.isHoodUp() ? "up" : "down"));
    }else{
      //DataLogManager.log("SHOOTER: Nothing to shoot at!");
      // TODO Should we really stop, or run the motors at some idle speed, so that they will be ready more quickly when a target is found?
      stop(); //og: stop();
    }

    return shooterConfig.getDistance() != 0.0;
  }

  /**
   * Set the top and bottom shooter motors to the idle speed.
   */
  public void idle(){
    double topMotorSpeed = 9001 * Constants.falconRPMToUPS; //2731.74
    double bottomMotorSpeed = -9001 * Constants.falconRPMToUPS; //-3715.11

    topShooterMotor.set(TalonFXControlMode.Velocity, topMotorSpeed);
    bottomShooterMotor.set(TalonFXControlMode.Velocity, bottomMotorSpeed);
  }

  /**
   * > Returns true if the shooter is within the error threshold of the target speed
   * 
   * @param shooterConfig The shooter configuration that we want to reach
   * @return A boolean value.
   */
  public boolean upToSpeed(ShooterConfiguration shooterConfig){
    double topError = shooterConfig.getTopMotorSpeed() - (topShooterMotor.getSelectedSensorVelocity() / (Constants.falconRPMToUPS));
    double bottomError = shooterConfig.getBottomMotorSpeed() - (bottomShooterMotor.getSelectedSensorVelocity() / (Constants.falconRPMToUPS));
  
    return Math.abs(topError) <= Constants.shooterErrorMax && Math.abs(bottomError) <= Constants.shooterErrorMax;
  }


  public void stop(){
    //DataLogManager.log("SHOOTER: Stopping shooting");
    topShooterMotor.set(TalonFXControlMode.PercentOutput, 0.0);
    bottomShooterMotor.set(TalonFXControlMode.PercentOutput, 0.0); 
  }

  /**
   * If the shooter solenoids are in the forward position, return true, otherwise return false
   * 
   * @return The state of the hood.
   */
  public boolean isHoodUp(){
    return (shooterSolenoids.get() == Value.kForward) ? true : false;
  }

  //#region Shooter calibration
  private void getShooterValuesFromSticks(){
    topValue = -Constants.calibrateController.getRawAxis(Constants.topRoughAxis) * 6380;
    bottomValue = Constants.calibrateController.getRawAxis(Constants.bottomRoughAxis) * 6380;

    topValue += topTunerValue + (Constants.calibrateController.getRawAxis(Constants.topFineAxis) * topTunerRange);
    bottomValue += bottomTunerValue + (Constants.calibrateController.getRawAxis(Constants.bottomFineAxis) * bottomTunerRange);


  }

  /**
   * This function sets the topTunerRange variable to the value passed in
   * 
   * @param value The value to set the top tuner range to.
   */
  public void setTopTunerRange(double value){
    topTunerRange = value;
  }

  /**
   * This function sets the bottom tuner range
   * 
   * @param value The value to set the parameter to.
   */
  public void setBottomTunerRange(double value){
    bottomTunerRange = value;
  }

  /**
   * > The topTunerValue is set to the current topTunerValue plus the value of the left joystick's
   * y-axis multiplied by the topTunerRange
   */
  public void setTopTunerValue(){
    topTunerValue += (Constants.calibrateController.getRawAxis(1) * topTunerRange);
  }

  /**
   * The bottom tuner value is increased by the value of the right joystick's y-axis multiplied by the
   * bottom tuner range
   */
  public void setBottomTunerValue(){
    bottomTunerValue += (Constants.calibrateController.getRawAxis(3) * bottomTunerRange);
  }

  /**
   * This function resets the value of the top tuner to 0.0
   */
  public void resetTopTunerValue(){
    topTunerValue = 0.0;
  }

  /**
   * This function resets the bottom tuner value to 0.0
   */
  public void resetBottomTunerValue(){
    bottomTunerValue = 0.0;
  }

  /**
   * This function returns the value of the top card in the deck
   * 
   * @return The topValue variable is being returned.
   */
  public double getTopValue(){
    return topValue;
  }

  /**
   * This function returns the bottom value of the range
   * 
   * @return The bottomValue variable is being returned.
   */
  public double getBottomValue(){
    return bottomValue;
  }

  //#endregion

  @Override
  public void periodic() {
    //idle();
    if (Constants.useCalibrateController) {
      if (Constants.calibrateController.getRawButton(Constants.runShooterButton)){
        getShooterValuesFromSticks();
      }
    }  
    
    SmartDashboard.putNumber("Top Shooter", (topShooterMotor.getSelectedSensorVelocity() / (Constants.falconRPMToUPS)));
    SmartDashboard.putNumber("Bottom Shooter", (bottomShooterMotor.getSelectedSensorVelocity() / (Constants.falconRPMToUPS)));

  }
  
}
