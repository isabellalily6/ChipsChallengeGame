package nz.ac.vuw.ecs.swen225.gp20.recnplay.replayConstants;

import nz.ac.vuw.ecs.swen225.gp20.application.Main;
import nz.ac.vuw.ecs.swen225.gp20.recnplay.ReplayOptionDialog;

import java.util.ArrayList;

/**
 * Options for auto play, this version only has an exit button
 */
public class AutoPlayDialogCreator implements ReplayDialogCreator {

    @Override
    public ReplayOptionDialog createDialog(Main m) {
        return new ReplayOptionDialog(m, "", new ArrayList<>(), null);
    }
}
