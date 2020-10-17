package nz.ac.vuw.ecs.swen225.gp20.recnplay.replayConstants;

import nz.ac.vuw.ecs.swen225.gp20.application.Main;
import nz.ac.vuw.ecs.swen225.gp20.recnplay.ReplayOptionDialog;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Hashtable;

import static nz.ac.vuw.ecs.swen225.gp20.recnplay.replayConstants.ButtonMaker.styleButton;

/**
 * Button options for replaying a game, ie: auto play, speed options, and step by step
 */
public class ReplayOptions implements ReplayDialog {

    @Override
    public ReplayOptionDialog createDialog(Main m) {
        m.pauseGame();
        var buttons = new ArrayList<JButton>();
        var autoReplayButton = new JButton("Auto play");
        styleButton(autoReplayButton, ReplayModes.AUTO_PLAY);

        var stepByStep = new JButton("Step by Step");
        styleButton(stepByStep, ReplayModes.STEP_BY_STEP);

        buttons.add(autoReplayButton);
        buttons.add(stepByStep);

        var slider = new JSlider(25, 150, 100);
        slider.setPaintLabels(true);

        Hashtable<Integer, JLabel> table = new Hashtable<>();
        table.put(25, new JLabel("25%"));
        table.put(100, new JLabel("100%"));
        table.put(150, new JLabel("150%"));
        slider.setLabelTable(table);

        return new ReplayOptionDialog(m, "Playback speed", buttons, slider);
    }
}
