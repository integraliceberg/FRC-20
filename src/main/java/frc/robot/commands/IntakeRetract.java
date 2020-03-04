package frc.robot.commands;

import frc.robot.subsystems.Subsystems;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.RobotMap;

/**
 * Retracts the intake.
 */
public class IntakeRetract extends Command {

    public IntakeRetract() {
        super("IntakeRetract");
        requires(Subsystems.intake);
    }

    @Override
    public void initialize() {}

    @Override
    public void execute() {
        Subsystems.intake.intakeRetract();
        RobotMap.isIntakeDown = false;
    }

    @Override
    public boolean isFinished() {
        return true;
    }

    @Override
    public void interrupted() {
    }

    @Override
    public void end() {
    }
}