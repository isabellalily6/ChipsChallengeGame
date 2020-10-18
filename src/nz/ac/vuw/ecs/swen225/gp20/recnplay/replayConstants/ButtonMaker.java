package nz.ac.vuw.ecs.swen225.gp20.recnplay.replayConstants;

import nz.ac.vuw.ecs.swen225.gp20.application.Main;
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
        styleButton(button, null, null, false);
    }

    /**
     * gives the button the correct styling
     *
     * @param button button to style
     * @param m      main class to make the dialog with
     * @param mode   replay mode for the button
     * @param speed  determines wether to set replay speed
     */
    public static void styleButton(JButton button, Main m, ReplayModes mode, boolean speed) {
        button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 3));
        button.setBackground(Color.lightGray);
        button.setFont(new Font("Verdana", Font.PLAIN, 20));
        if (mode != null) {
            button.addActionListener(e -> {
                if (speed) {
                    RecordAndPlay.setRecordingMode(mode, ReplayOptionsCreator.speed);
                } else {
                    RecordAndPlay.setRecordingMode(mode, 0);
                }
                RecordAndPlay.playRecording(m);
            });
        }
    }
}
