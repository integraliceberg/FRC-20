package frc.robot.commands.autonomous;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import frc.robot.commands.*;

/**
 * Creates an autonomous according to the choices chosen in Shuffleboard.
 */
public class AutonomousSwitch extends CommandGroup {

    public final StartingPosition startingPosition;
    public final double delay;
    public final boolean pushRobot;
    public final IntakeSource intakeSource;
    public String description = "";

    public final double robotLength = 38.75; //in inches

    public enum StartingPosition {
        LEFT, CENTER, RIGHT
    }

    public enum IntakeSource {
        TRENCH, RENDEZVOUS, MIXED
    }

    /**
     * Creates an autonomous according to the choices chosen in Shuffleboard.
     * @param StartingPosition Where the robot starts on the field (right of power port, left of it, or directly in front of it).
     * @param Delay How long the robot should delay starting its automous, in seconds.
     * @param PushRobot Whether our robot will push another robot across the initiation line.
     * @param IntakeSource Where we intake power cells from after shooting (trench, rendezvous, or a mix of the two).
     * @param VisionEnabled Whether or not our robot will use vision/sensing in its autonomous routine. If false, this will use dead reckoning.
     */
    public AutonomousSwitch(StartingPosition StartingPosition, double Delay, boolean PushRobot, IntakeSource IntakeSource) {
        this.startingPosition = StartingPosition;
        this.delay = Delay;
        this.pushRobot = PushRobot;
        this.intakeSource = IntakeSource;

        addSequential(new WaitCommand(delay)); //wait for specified delay
        description = "shoots after a delay of " + Math.round(delay*100.0)/100.0 + ", "; //rounding!!!!!!!

        if (pushRobot) {
            description += "pushes robot, ";
            addSequential(new DriveStraight(-8, 2, 0.5)); //push robot off line
            addSequential(new DriveStraight(8, 2, 0.5)); //go back to original position
        } else {
            description += "doesn't push robot, ";
        }

        // MAIN AUTO CODE

        if (startingPosition == AutonomousSwitch.StartingPosition.CENTER) {
            description = "Starts at center, " + description;

            addSequential(new DriveStraight(120 - robotLength - 24, 0.5, 5)); //drive to goal
            addSequential(new DriveStraight(18, 0.1, 3));
            addSequential(new StartFlywheel()); //shoot 3 cells
            addSequential(new HelixShoot(), 4.5);
            addSequential(new ShootStop()); //stop shooting

            if (IntakeSource == AutonomousSwitch.IntakeSource.TRENCH) {
                description += "intakes from trench.";

                addSequential(new DriveStraight((pushRobot ? -110 + (1.5*robotLength): -110), 0.5, 8)); //back away from goal
                addSequential(new Turn(180 - Math.toDegrees(Math.atan(66.91/(86.63-(robotLength/2)))), 0.3, 8)); //turn right towards trench
                addSequential(new DriveStraight(Math.sqrt(Math.pow(66.91, 2) + Math.pow(86.63 - (robotLength/2), 2)), 0.5, 8)); //go towards trench until aligned
                addSequential(new Turn(Math.toDegrees(Math.atan(66.91/(86.63-(robotLength/2)))), 0.3, 8)); //turn rest of right towards trench
                addSequential(new IntakeExtend()); //turn intake down & on
                addSequential(new IntakeIn());
                addSequential(new DriveStraight((pushRobot ? 117 + (1.5*robotLength) : 117), 0.2, 8)); //go to intake 5 cells from whole trench run
                //ABOVE SHOULD BE 180 INSTEAD OF 117
                addSequential(new IntakeOff()); //turn intake off & up
                addSequential(new IntakeRetract());
                // addSequential(new DriveStraight(-180, 0.3, 8)); //end closer to power port

            } else if (IntakeSource == AutonomousSwitch.IntakeSource.RENDEZVOUS) {
                description += "intakes from rendezvous.";

                addSequential(new DriveStraight((pushRobot ? -120 + (1.5*robotLength): -120), 0.5, 8));
                addSequential(new Turn(-90 - Math.toDegrees(Math.atan((86.63-0.5*robotLength)/115)), 0.3, 8)); //turn left away from rendezvous
                addSequential(new DriveStraight(115*(1/Math.cos(1/Math.atan((86.63-0.5*robotLength)/115))), 0.5, 8)); //go straight
                addSequential(new Turn(-Math.toDegrees(Math.atan((86.63-0.5*robotLength)/115))-22.5, 0.3, 8)); //turn towards first 2 cells
                addSequential(new IntakeExtend()); //turn intake down & on
                addSequential(new IntakeIn());
                addSequential(new DriveStraight(115*Math.tan(Math.toDegrees(22.5))*Math.cos(Math.toDegrees(22.5)), 0.2, 8)); //go to intake those 2 cells & cross 2x4
                addSequential(new IntakeOff()); //turn intake off & up
                addSequential(new IntakeRetract());
                addSequential(new Turn(-90, 0.3, 8)); //turn towards 3 remaining cells
                addSequential(new DriveStraight((1/Math.sin(Math.toDegrees(22.5))*(115*Math.toDegrees(Math.tan(22.5)))), 0.5, 8)); //go to intake further cells
                addSequential(new Turn(90, 0.3, 8)); //turn towards closer cells
                addSequential(new IntakeExtend()); //turn intake down & on
                addSequential(new IntakeIn());
                addSequential(new DriveStraight(30, 0.2, 8)); //go to intake last of 3 cells
                addSequential(new IntakeOff()); //turn intake off & up
                addSequential(new IntakeRetract());
                addSequential(new Turn(-90, 0.3, 8)); //turn so shooter faces towards power port
                addSequential(new DriveStraight(30, 0.5, 8)); //end across 2x4, closer to power port

            } else if (IntakeSource == AutonomousSwitch.IntakeSource.MIXED) {
                description += "intakes 3 from trench + 2 from rendezvous.";

                addSequential(new DriveStraight((pushRobot ? -110 + (1.5*robotLength): -110), 0.5, 8)); //back away from goal
                // addSequential(new Turn(180 - Math.toDegrees(Math.atan(66.91/(86.63-(robotLength/2)))), 0.3, 8)); //turn right towards trench
                // addSequential(new DriveStraight(Math.sqrt(Math.pow(66.91, 2) + Math.pow(86.63 - (robotLength/2), 2)), 0.5, 8)); //go towards trench until aligned
                // addSequential(new Turn(Math.toDegrees(Math.atan(66.91/(86.63-(robotLength/2)))), 0.3, 8)); //turn rest of right towards trench
                // addSequential(new IntakeExtend()); //turn intake down & on
                // addSequential(new IntakeIn());
                // addSequential(new DriveStraight((pushRobot ? 117 + (1.5*robotLength) : 117), 0.4, 8)); //go to intake first 3 cells from trench
                // addSequential(new IntakeOff()); //turn intake off & up
                // addSequential(new IntakeRetract());
                // addSequential(new Turn(90, 0.3, 8)); //turn right towards remaining rendezvous cells
                // addSequential(new DriveStraight(44.3185, 0.4, 5)); //drive to rendezvous
                // addSequential(new Turn(67.5, 0.4, 5)); //turn towards power cells
                // addSequential(new IntakeExtend()); //turn intake down & on
                // addSequential(new IntakeIn());
                // addSequential(new DriveStraight(110.5575, 0.2, 10)); //go to intake remaining 2 power cells
                // addSequential(new IntakeOff()); //turn intake off & up
                // addSequential(new IntakeRetract());
                // addSequential(new DriveStraight(-47.77875, 0.4, 5)); //back up
                // addSequential(new Turn(22.5, 0.4, 3)); //go out of rendezvous
                // addSequential(new DriveStraight(30, 0.4, 15));

            }
        } else if (startingPosition == AutonomousSwitch.StartingPosition.RIGHT) {
            description = "Starts on right, " + description;

            addSequential(new DriveStraight(10, 0.4, 8)); //just cross line

            // addSequential(new DriveStraight(-12, 0.5, 8)); //drive to goal
            // addSequential(new WaitCommand(1)); //addSequential(new Shoot()) //shoot 3 cells
            // addSequential(new DriveStraight(12, 0.5, 8)); //back away from goal

            if (IntakeSource == AutonomousSwitch.IntakeSource.TRENCH) {
                description += "intakes from trench.";
            
                // addSequential(new Turn(45, 0.3, 8)); //turn right towards trench
                // addSequential(new WaitCommand(1)); //addSequential(new IntakeOn()); //turn intake down & on
                // addSequential(new DriveStraight(24, 0.5, 8)); //go to intake 5 cells from whole trench run
                // addSequential(new WaitCommand(1)); //addSequential(new IntakeOff());
                // addSequential(new DriveStraight(-24, 0.5, 8)); //end closer to power port

            } else { // MIXED
                description += "intakes 3 from trench + 2 from rendezvous.";

                // addSequential(new Turn(45, 0.3, 8)); //turn right towards trench
                // addSequential(new WaitCommand(1)); //addSequential(new IntakeOn()); //turn intake down & on
                // addSequential(new DriveStraight(12, 0.5, 8)); //go to intake first 3 cells from trench
                // addSequential(new Turn(90, 0.3, 8)); //turn right towards remaining rendezvous cells
                // addSequential(new DriveStraight(12, 0.5, 8)); //go to intake 2 final cells
                // addSequential(new WaitCommand(1)); //addSequential(new IntakeOff()); //turn intake off & up
                // addSequential(new DriveStraight(-6, 0.5, 8)); //back up a lil
                // addSequential(new Turn(90, 0.3, 8)); //end turning back towards power port

            }
        } else if (startingPosition == AutonomousSwitch.StartingPosition.LEFT) {
            description = "Starts on left, " + description;

            addSequential(new DriveStraight(10, 0.4, 8)); //just cross line

            // addSequential(new DriveStraight(-12, 0.5, 8)); //drive to goal
            // addSequential(new WaitCommand(1)); //addSequential(new Shoot()) //shoot 3 cells
            // addSequential(new DriveStraight(12, 0.5, 8)); //back away from goal

            //Must be intaking from rendezvous
            description += "intakes from rendezvous.";

            // addSequential(new Turn(-45, 0.3, 8)); //turn left towards rendezvous
            // addSequential(new WaitCommand(1)); //addSequential(new IntakeOn()); //turn intake down & on
            // addSequential(new DriveStraight(12, 0.5, 8)); //go to intake first 2 cells
            // addSequential(new DriveStraight(-6, 0.5, 8)); //back up
            // addSequential(new Turn(45, 0.3, 8)); //turn towards last cell
            // addSequential(new DriveStraight(12, 0.5, 8)); //go to intake last of 3 cells
            // addSequential(new Turn(-135, 0.3, 8)); //turn towards other 2
            // addSequential(new DriveStraight(24, 0.5, 8)); //go to intake last 2
            // addSequential(new WaitCommand(1)); //addSequential(new IntakeOff()); //turn intake off & up
            // addSequential(new Turn(90, 0.3, 8)); //turn so shooter faces towards power port

        }
    }


    /**
     * @param StartingPosition
     * @param Delay
     * @param PushRobot
     * @param IntakeSource
     * @return Whether these inputs would create the same autonomous as currently exists (i.e. all settings match exactly).
     */
    public boolean matchesSettings(StartingPosition StartingPosition, double Delay, boolean PushRobot, IntakeSource IntakeSource) {
        return (startingPosition == StartingPosition) && (delay == Delay) && (pushRobot == PushRobot) && (intakeSource == IntakeSource);
    }
    /**
     * Checks if this is a valid choice.
     * @param StartingPosition
     * @param IntakeSource
     * @return Whether this can create an autonomous.
     */
    public static boolean doChoicesWork(StartingPosition StartingPosition, IntakeSource IntakeSource) {
        if (StartingPosition == AutonomousSwitch.StartingPosition.LEFT && IntakeSource == AutonomousSwitch.IntakeSource.TRENCH) {
            return false;
        } else if (StartingPosition == AutonomousSwitch.StartingPosition.RIGHT && IntakeSource == AutonomousSwitch.IntakeSource.RENDEZVOUS) {
            return false;
        } else if (StartingPosition == AutonomousSwitch.StartingPosition.LEFT && IntakeSource == AutonomousSwitch.IntakeSource.MIXED) {
            return false;
        }

        return true;
    }
}