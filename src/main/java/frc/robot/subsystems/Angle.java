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
import frc.robot.subsystems.Launcher.levelSpeed;

public class Angle extends SubsystemBase{
    private CANSparkMax AngleMotor = new CANSparkMax(14, MotorType.kBrushless);
    public double stickSpeed = 0.0;
    private static double highSpeed = 1.0;
    private static double halfSpeed = 0.5;
    private static double stopSPeed = 0;

    private double m_speed = 0;
    private double m_prev_speed = m_speed;
    private levelSpeed m_targetLevel = levelSpeed.STOP;

    public enum levelSpeed {
        STOP,
        MAX, 
        HALF
    }

    public Angle() {
        AngleMotor.restoreFactoryDefaults();

        AngleMotor.setInverted(false);

        AngleMotor.setIdleMode(IdleMode.kBrake);

        AngleMotor.setSmartCurrentLimit(80);

        AngleMotor.burnFlash();
    }

    @Override
    public void periodic() {
        
    }

    public void setAngleSpeed(levelSpeed intake) {
        switch(intake) {
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
            AngleMotor.set(m_speed);
        }
    }

    public Command setTargetLevelAngle(levelSpeed level) {
        return this.runOnce(() -> m_targetLevel = level);
    }

    public Command setSpeedWithTargetAngle() {
        return this.runOnce(() -> this.setAngleSpeed(m_targetLevel));
    }

    public Command stopAngle() {
        return this.runOnce(() -> this.setAngleSpeed(levelSpeed.STOP));
    }
}
