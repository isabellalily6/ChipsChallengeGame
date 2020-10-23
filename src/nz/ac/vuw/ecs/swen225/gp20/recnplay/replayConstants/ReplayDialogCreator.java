package nz.ac.vuw.ecs.swen225.gp20.recnplay.replayConstants;

import nz.ac.vuw.ecs.swen225.gp20.application.Main;

/**
 * Interface for replay dialogs
 *
 * @author Callum McKay 300496765
 */
public interface ReplayDialogCreator {
    /**
     * Creates a dialog which contains options for replaying a game
     *
     * @param m main class to create the dialog with options for the current playback of a recording
     * @return a ReplayOptionDialog
     */
    ReplayOptionDialog createDialog(Main m);
}
