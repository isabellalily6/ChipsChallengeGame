package nz.ac.vuw.ecs.swen225.gp20.recnplay.replayConstants;

import nz.ac.vuw.ecs.swen225.gp20.recnplay.RecordAndPlay;

import javax.swing.*;
import java.awt.*;

/**
 * Gives a button proper formatting
 */
public class ButtonMaker {

    /**
     * gives the button the correct styling
     *
     * @param button button to style
     */
    public static void styleButton(JButton button) {
        styleButton(button, null);
    }

    /**
     * gives the button the correct styling
     *
     * @param button button to style
     * @param mode   replay mode for the button
     */
    public static void styleButton(JButton button, ReplayModes mode) {
        button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 3));
        button.setBackground(Color.lightGray);
        button.setFont(new Font("Verdana", Font.PLAIN, 20));
        if (mode != null) {
            button.addActionListener(e -> RecordAndPlay.setRecordingMode(mode));
        }
    }
}
