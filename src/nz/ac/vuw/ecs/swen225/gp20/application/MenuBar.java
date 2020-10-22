package nz.ac.vuw.ecs.swen225.gp20.application;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import nz.ac.vuw.ecs.swen225.gp20.recnplay.RecordAndPlay;

/**
 * Create a MenuBar to be added to the main GUI.
 **/
public class MenuBar extends JMenuBar {

  private String instructions = "Use the arrow keys to move Chap around to collect all "
          + "the keys and treasures before the time runs out!"
          + "Useful items such as keys and shields must be picked up and added to "
          + "Chip's inventory. To pick up items, move Chip over the item.";

  /**
   * Create a new menu bar and set the default values for the menu bar.
   *
   * @param main - the main game
   **/
  public MenuBar(Main main) {
    // Create the file menu
    JMenu file = new JMenu("File");

    // create and add the load button
    JMenuItem load = new JMenuItem("Load");
    load.addActionListener(method -> {
      main.loadFile(false);
      main.getGui().updateGui(false);
    });
    file.add(load);

    // create and add the save button
    JMenuItem save = new JMenuItem("Save");
    save.addActionListener(method -> main.saveFile(false, false));
    file.add(save);

    // create and add the quit button
    JMenuItem quit = new JMenuItem("Quit");
    quit.addActionListener(method -> {
      RecordAndPlay.saveRecording();
      System.exit(0);
    });
    file.add(quit);

    // Create the game menu
    JMenu game = new JMenu("Game");

    // create and add the pause button
    JMenuItem pause = new JMenuItem("Pause");
    pause.addActionListener(method -> main.pauseGame(true));
    game.add(pause);

    // create and add a help button
    JMenuItem help = new JMenuItem("Help");
    help.addActionListener(method -> {
      Dialogues helpDialogue = new Dialogues(main, instructions, "Close");
      helpDialogue.setActionListener(close -> helpDialogue.dispose());
      helpDialogue.setVisible(true);
    });
    game.add(help);

    // Create the recording menu
    JMenu recording = new JMenu("Recording");

    // create and add the start recording button
    JMenuItem startRecording = new JMenuItem("Start Recording");
    startRecording.addActionListener(method -> main.startRecording());
    recording.add(startRecording);

    // create and add the save recording button
    JMenuItem saveRecording = new JMenuItem("Save Recording");
    saveRecording.addActionListener(method -> main.saveRecording());
    recording.add(saveRecording);

    // create and add the load recording button
    JMenuItem loadRecording = new JMenuItem("Play Recording");
    loadRecording.addActionListener(method -> main.loadRecording());
    recording.add(loadRecording);

    // add each section to the menu bar
    add(file);
    add(game);
    add(recording);
  }
}
