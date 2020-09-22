package nz.ac.vuw.ecs.swen225.gp20.application;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuBar extends JMenuBar {

  /**
   * Create a new menu bar and set the default values for the menu bar
   **/
  public MenuBar(Main main){
    // Create the file menu
    JMenu file = new JMenu("File");

    // create and add the load button
    JMenuItem load = new JMenuItem("Load");
    load.addActionListener(method -> main.loadFile());
    file.add(load);

    // create and add the save button
    JMenuItem save = new JMenuItem("Save");
    save.addActionListener(method -> main.saveFile());
    file.add(save);

    // create and add the quit button
    JMenuItem quit = new JMenuItem("Quit");
    quit.addActionListener(method -> System.exit(0));
    file.add(quit);

    // Create the game menu
    JMenu game = new JMenu("Game");

    // create and add the pause button
    JMenuItem pause = new JMenuItem("Pause");
    pause.addActionListener(method -> main.pauseGame());
    game.add(pause);

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
    JMenuItem loadRecording = new JMenuItem("Load Recording");
    loadRecording.addActionListener(method -> main.loadRecording());
    recording.add(loadRecording);

    // create and add the replay recording button
    JMenuItem replayRecording = new JMenuItem("Replay Recording");
    replayRecording.addActionListener(method -> main.replayRecording());
    recording.add(replayRecording);

    // add each section to the menu bar
    add(file);
    add(game);
    add(recording);
  }
}
