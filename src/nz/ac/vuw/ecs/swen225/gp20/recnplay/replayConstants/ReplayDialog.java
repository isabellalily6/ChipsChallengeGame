package nz.ac.vuw.ecs.swen225.gp20.recnplay.replayConstants;

import nz.ac.vuw.ecs.swen225.gp20.application.Main;
import nz.ac.vuw.ecs.swen225.gp20.recnplay.ReplayOptionDialog;

/**
 * Interface for replay dialogs
 */
public interface ReplayDialog {
    /**
     * @param m main class to create the dialog with
     * @return a ReplayOptionDialog
     */
    ReplayOptionDialog createDialog(Main m);
}
