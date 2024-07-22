package frc.robot.subsystems;

import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.CANSparkLowLevel.MotorType;
import com.revrobotics.CANSparkMax;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;
import frc.robot.Constants;

public class Launcher extends SubsystemBase{
    private CANSparkMax LauncherRight = new CANSparkMax(20, MotorType.kBrushed);
    private CANSparkMax LauncherLeft = new CANSparkMax(19, MotorType.kBrushed);
    private CANSparkMax IntakeMotor = new CANSparkMax(15, MotorType.kBrushless);
    private CANSparkMax AngleMotor = new CANSparkMax(14, MotorType.kBrushless);
    private final CANSparkMax ClimberLeft = new CANSparkMax(21, MotorType.kBrushless);
  private final CANSparkMax ClimberRight = new CANSparkMax(22, MotorType.kBrushless);

    
    

    public double triggerSpeed = 0.0;
    private static double highSpeed = 1.0;
    private static double halfSpeed = 0.5;
    private static double stopSPeed = 0;

    private double m_speed = 0;
    private double m_prev_speed = m_speed;
    private levelSpeed m_targetLevel = levelSpeed.STOP;
    
  NetworkTableInstance mNetworkTable = NetworkTableInstance.getDefault();

  private NetworkTableEntry tv, tx, ty, ta;

    public enum levelSpeed {
        STOP,
        MAX, 
        HALF
    }

    public Launcher() {
        LauncherLeft.restoreFactoryDefaults();
        LauncherRight.restoreFactoryDefaults();

        LauncherLeft.setInverted(false);
        LauncherRight.setInverted(true);

        LauncherLeft.setIdleMode(IdleMode.kCoast);
        LauncherRight.setIdleMode(IdleMode.kCoast);

        LauncherLeft.setSmartCurrentLimit(120);
        LauncherRight.setSmartCurrentLimit(120);

        LauncherLeft.burnFlash();
        LauncherRight.burnFlash();
    }

    @Override
    public void periodic() {
        
    tv = mNetworkTable.getTable("limelight").getEntry("tv");

        double targetVisible = tv.getDouble(0.00);

        if (targetVisible == 1) {
            LauncherLeft.set(1);
            LauncherRight.set(1);
          } else if (targetVisible == 0) {}

          if (new XboxController(1).getRightTriggerAxis() > 0.3) {
            ClimberLeft.setInverted(false);
            ClimberRight.setInverted(true);
            ClimberLeft.set(new XboxController(1).getRightTriggerAxis() * 2);
            ClimberRight.set(new XboxController(1).getRightTriggerAxis() * 2);
          } else if (new XboxController(1).getLeftTriggerAxis() > 0.3){
            ClimberLeft.set(new XboxController(1).getLeftTriggerAxis() * 2);
            ClimberRight.set(new XboxController(1).getLeftTriggerAxis() * 2);
            ClimberLeft.setInverted(true);
            ClimberRight.setInverted(false);
          } else {
            ClimberLeft.set(0);
            ClimberRight.set(0);
          }

          if (new XboxController(1).getLeftY() > 0.15 || new XboxController(1).getLeftY() < 0.15) {
            IntakeMotor.set(new XboxController(1).getLeftY());
        } else {
            IntakeMotor.set(0);
        }

        AngleMotor.set(new XboxController(1).getRightY() / 4);

        if (new XboxController(0).getLeftTriggerAxis() > 0.3)
    {
      LauncherLeft.setInverted(true);
      LauncherRight.setInverted(false);
      LauncherRight.set(new XboxController(0).getLeftTriggerAxis());
      LauncherLeft.set(new XboxController(0).getLeftTriggerAxis());
    }
    else if (new XboxController(0).getRightTriggerAxis() > 0.3)
    {
      LauncherLeft.setInverted(false);
      LauncherRight.setInverted(true);
      LauncherRight.set(new XboxController(0).getRightTriggerAxis());
      LauncherLeft.set(new XboxController(0).getRightTriggerAxis());
    }
    else 
    {
      LauncherRight.set(0);
      LauncherLeft.set(0);
    }
    }

    public void setLauncherSpeed(levelSpeed Launch) {
        switch(Launch) {
            case MAX:
            m_speed = highSpeed;
            break;

            case HALF:
            m_speed = halfSpeed;
            break;

            case STOP:
            m_speed = stopSPeed;
            break;
        }

        if (m_speed != m_prev_speed) {
            m_prev_speed = m_speed;
            LauncherLeft.set(m_speed);
            LauncherRight.set(m_speed);
        }
    }

    public Command setTargetLevel(levelSpeed level) {
        return this.runOnce(() -> m_targetLevel = level);
    }

    public Command setSpeedWithTarget() {
        return this.runOnce(() -> this.setLauncherSpeed(m_targetLevel));
    }

    public Command stop() {
        return this.runOnce(() -> this.setLauncherSpeed(levelSpeed.STOP));
    }

    

    /*public Command setOutSpeed() {
        return this.run(() -> triggerSpeed = (new XboxController(1).getRightTriggerAxis() * 10));
    }

    public Command stopSpeed() {
        return this.run(() -> triggerSpeed = 0.0);
    }*/
}
