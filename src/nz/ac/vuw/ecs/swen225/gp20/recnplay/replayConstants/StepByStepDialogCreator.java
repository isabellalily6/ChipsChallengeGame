package nz.ac.vuw.ecs.swen225.gp20.recnplay.replayConstants;

import nz.ac.vuw.ecs.swen225.gp20.application.Main;
import nz.ac.vuw.ecs.swen225.gp20.recnplay.RecordAndPlay;
import nz.ac.vuw.ecs.swen225.gp20.recnplay.ReplayOptionDialog;

import javax.swing.*;
import java.util.ArrayList;

import static nz.ac.vuw.ecs.swen225.gp20.recnplay.replayConstants.ButtonMaker.styleButton;

/**
 * Creates the step by step dialog for step by step playback
 *
 * @author Callum McKay 300496765
 */
public class StepByStepDialogCreator implements ReplayDialogCreator {

    @Override
    public ReplayOptionDialog createDialog(Main m) {
        var buttons = new ArrayList<JButton>();
        var stepForward = new JButton("Step forward");
        styleButton(stepForward);
        stepForward.addActionListener(e -> RecordAndPlay.stepForward());

        var stepBackwards = new JButton("Step backwards (EXPERIMENTAL)");
        styleButton(stepBackwards);
        stepBackwards.addActionListener(e -> RecordAndPlay.stepBackward());

        buttons.add(stepForward);
        buttons.add(stepBackwards);

        return new ReplayOptionDialog(m, "", buttons, null);
    }
}
