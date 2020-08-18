package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.Subsystems;

/**
 * Spins the flywheel, helix, and intake motors all backwards in order to expel power cells.
 */
public class Vomit extends Command {

    public Vomit() {
        super("Vomit");
        requires(Subsystems.flyboi);
        requires(Subsystems.helix);
        //still has control over intake up/down
    }

    @Override
    protected void initialize() {
        Subsystems.flyboi.spinWheel(-0.6);
        Subsystems.helix.setHelixMotors(-0.9);
        Subsystems.intake.setIntakeMotors(-0.8);
    }

    @Override
    protected void execute() {}

    @Override
    protected boolean isFinished() {
        return false;
    }

    @Override
    protected void interrupted() {
        Subsystems.flyboi.stopWheel();
        Subsystems.helix.stopHelixMotors();
        Subsystems.intake.stopIntakeMotors();
    }

    @Override
    protected void end() {
        Subsystems.flyboi.stopWheel();
        Subsystems.helix.stopHelixMotors();
        Subsystems.intake.stopIntakeMotors();
    }

}