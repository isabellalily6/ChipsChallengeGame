package nz.ac.vuw.ecs.swen225.gp20.recnplay.replayConstants;

import nz.ac.vuw.ecs.swen225.gp20.application.Main;
import nz.ac.vuw.ecs.swen225.gp20.recnplay.RecordAndPlay;
import nz.ac.vuw.ecs.swen225.gp20.recnplay.ReplayOptionDialog;

import javax.swing.*;
import java.util.ArrayList;

import static nz.ac.vuw.ecs.swen225.gp20.recnplay.replayConstants.ButtonMaker.styleButton;

/**
 * Options for auto play
 */
public class AutoPlayDialogCreator implements ReplayDialogCreator {

    @Override
    public ReplayOptionDialog createDialog(Main m) {
        var buttons = new ArrayList<JButton>();
        var resumeButton = new JButton("Resume");
        styleButton(resumeButton);
        resumeButton.addActionListener(e -> RecordAndPlay.resumeRecording());

        var pauseButton = new JButton("Pause");
        styleButton(pauseButton);
        pauseButton.addActionListener(e -> RecordAndPlay.pauseRecording());

        buttons.add(resumeButton);
        buttons.add(pauseButton);

        return new ReplayOptionDialog(m, "", buttons, null);
    }
}
