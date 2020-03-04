package frc.robot.subsystems;

import io.github.pseudoresonance.pixy2api.*;
import io.github.pseudoresonance.pixy2api.links.*;
import java.util.ArrayList;

/**
 * A Pixy2 camera connected to the roborio via an I2C port, set up to track color blocks.
 */
public class Pixy {

    private Pixy2 pixy;
    private Pixy2CCC pixyCCC;
    protected int frameWidth = -1;
    protected int frameHeight = -1;

    public Pixy() {
        pixy = Pixy2.createInstance(new I2CLink()); // Creates a new Pixy2 camera using I2C
        System.out.println(pixy.init()); // Initializes the camera and prepares to send/receive data
        pixy.setLamp((byte) 1, (byte) 1); // Turns the LEDs on
        pixy.setLED(0, 255, 0); // Sets the RGB LED to full white
        pixyCCC = pixy.getCCC(); // Sets up for Color Connected
        System.out.println(pixy);
    }

    /**
     * Sets the RGB LED color.
     * @param r Amount of red (0-255)
     * @param g Amount of green (0-255)
     * @param b Amount of blue (0-255)
     * @return Pixy2 error code
     */
    public byte setLED(int r, int g, int b) {
        return pixy.setLED(r, g, b);
    }

    /**
     * @return Width of the Pixy's field of view.
     */
    public int getFrameWidth() {
        return pixy.getFrameWidth();
    }

    /**
     * Finds the biggest block available that matches the color in the 1st color slot of the pixy.
     * @return The biggest block found, or null if 0 or less found.
     */
    public Pixy2CCC.Block getBiggestBlock() {
        int blockCount = 0;
        // Attempt to find blocks
        try {
            blockCount = pixyCCC.getBlocks(false, Pixy2CCC.CCC_SIG_ALL, 25);
        } catch (java.lang.NullPointerException e) {
            return null;
        }
        if (blockCount <= 0) {
            return null; //if no blocks or error, escape
        }
        // Get largest of the blocks found
        ArrayList<Pixy2CCC.Block> blocks = pixy.getCCC().getBlocks();
        Pixy2CCC.Block largestBlock = null;
        for (Pixy2CCC.Block block : blocks) {
            if (largestBlock == null) {
                largestBlock = block;
            } else if (block.getWidth() > largestBlock.getWidth()) {
                largestBlock = block;
            }
        }
        return largestBlock;
    }
}