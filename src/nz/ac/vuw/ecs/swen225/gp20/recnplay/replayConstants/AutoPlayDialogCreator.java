package nz.ac.vuw.ecs.swen225.gp20.recnplay.replayConstants;

import nz.ac.vuw.ecs.swen225.gp20.application.Main;

import java.util.ArrayList;

/**
 * Options for auto play, this version only has an exit button
 *
 * @author Callum McKay 300496765
 */
public class AutoPlayDialogCreator implements ReplayDialogCreator {

    @Override
    public ReplayOptionDialog createDialog(Main m) {
        return new ReplayOptionDialog(m, "", new ArrayList<>(), null);
    }
}
