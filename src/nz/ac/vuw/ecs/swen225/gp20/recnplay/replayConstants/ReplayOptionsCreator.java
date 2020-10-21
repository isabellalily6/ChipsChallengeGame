package nz.ac.vuw.ecs.swen225.gp20.recnplay.replayConstants;

import nz.ac.vuw.ecs.swen225.gp20.application.Main;
import nz.ac.vuw.ecs.swen225.gp20.recnplay.ReplayOptionDialog;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Hashtable;

import static nz.ac.vuw.ecs.swen225.gp20.recnplay.replayConstants.ButtonMaker.styleButton;

/**
 * Button options for replaying a game, ie: auto play, speed options, and step by step
 *
 * @author Callum McKay 300496765
 */
public class ReplayOptionsCreator implements ReplayDialogCreator {

    static int speed = 0;

    @Override
    public ReplayOptionDialog createDialog(Main m) {
        m.pauseGame(false);
        var buttons = new ArrayList<JButton>();
        var autoReplayButton = new JButton("Auto play");


        var stepByStep = new JButton("Step by Step");
        styleButton(stepByStep, m, ReplayModes.STEP_BY_STEP, false);

        buttons.add(autoReplayButton);
        buttons.add(stepByStep);

        var slider = new JSlider(25, 200, 100);
        slider.setPaintLabels(true);

        Hashtable<Integer, JLabel> table = new Hashtable<>();
        table.put(25, new JLabel("25%"));
        table.put(100, new JLabel("100%"));
        table.put(200, new JLabel("200%"));
        slider.setLabelTable(table);
        slider.addChangeListener(c -> speed = slider.getValue());
        styleButton(autoReplayButton, m, ReplayModes.AUTO_PLAY, true);

        return new ReplayOptionDialog(m, "Playback speed", buttons, slider);
    }
}
