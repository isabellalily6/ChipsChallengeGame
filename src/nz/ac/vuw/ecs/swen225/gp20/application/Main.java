package nz.ac.vuw.ecs.swen225.gp20.application;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import nz.ac.vuw.ecs.swen225.gp20.commons.FileChooser;
import nz.ac.vuw.ecs.swen225.gp20.maze.Maze;
import nz.ac.vuw.ecs.swen225.gp20.persistence.LevelLoader;
import nz.ac.vuw.ecs.swen225.gp20.recnplay.RecordAndPlay;
import nz.ac.vuw.ecs.swen225.gp20.render.Music;

/**
 * From Handout:
 * The application window should display the time left to play, the current level, keys collected,
 * and the number of treasures that still need to be collected.  It should also offer buttons
 * and menu items to pause and exit the game, to save the game state and to resume a saved game,
 * and to display a help page with game rules.
 * This module also manages a countdown -- each level has a maximum time associated with
 * it (such as 1 min), and the once the countdown reaches zero, the game terminates
 * with a group dialog informing the user, and resetting the game to replay the level.
 *
 * @author Isabella Tomaz-Ketley 300494939
 */
public class Main {
  // initialize the game variables
  private Gui gui = null;
  private Maze maze;

  // initialise the game information
  private int lastLevel = 2;
  private int level = 1;
  private int maxTime = 100;
  private int timeLeft = maxTime;

  // initialize the timer variables
  private Timer timer = new Timer();
  private TimerTask timerTask;

  // variable to check whether the game is paused or not
  private boolean gamePaused = false;

  // name of the file to save current game state and load game state from
  private final File file = new File("levels/GameState.json");

  /**
   * Create a new instance of the game application.
   **/
  public Main() {
    // check if there is a default file to load from
    if (file.exists()) {
      // if the file exists load from this file and then delete it
      loadFile(true);
      if (!file.delete()) {
        if (file.renameTo(new File("levels/oldGameState.json"))) {
          file.deleteOnExit();
        }
      }
    } else {
      // otherwise create the maze for the game
      maze = new Maze(level);
    }
    // create the gui for the game
    gui = new Gui(this, maze);
    gui.setUpGui();
    new Music();
    // start the timer
    startTimer(maxTime != timeLeft);
    gui.updateGui(false);
  }

  /**
   * Create a new instance of the game which doesnt start a timer
   * or create the GUI for testing purposes.
   *
   * @param monkeyTest - true if monkeytests want the gui to be set up
   * @param level - the level for the game to start at
   **/
  public Main(Boolean monkeyTest, int level) {
    // create the maze and the gui for the game
    this.level = level;
    maze = new Maze(level);
    if (monkeyTest) {
      gui = new Gui(this, maze);
      gui.setUpGui();
    }
  }

  /**
   * Start a timer to do the timer task every second.
   *
   * @param loadedGame - true if the game has been loaded from a file, otherwise false.
   **/
  public void startTimer(Boolean loadedGame) {
    // if we are not playing a game which has been loaded, reset the max time on the timer
    if (!loadedGame) {
      // set the start time of the timer
      timeLeft = maxTime;
    }
    // create the time and the timer task
    timer = new Timer();
    createTimerTask();
    // schedule the timer to do the task every second
    timer.schedule(timerTask, 0, 1000);
  }

  /**
   * Create a new timer task for the timer which will count down every second.
   **/
  public void createTimerTask() {
    // create a new timer task
    timerTask = new TimerTask() {
      @Override
      public void run() {
        // show the time on the gui label
        gui.setTimer(timeLeft);
        // check if the level is over
        if (maze.isLevelOver()) {
          // if the player has won then show the won dialogue
          if (maze.getState().equals(Maze.LevelState.WON)) {
            if (level == lastLevel) {
              // save the recording if the player has won the last level
              RecordAndPlay.saveRecording();
            }
            gui.getGameWon().setVisible(true);
          } else {
            // otherwise, save the recording and show the game lost dialogue
            RecordAndPlay.saveRecording();
            gui.getGameLost().setVisible(true);
          }
          // reset the time left and cancel the timer
          timeLeft = maxTime;
          timer.cancel();
        } else if (timeLeft == 0) {
          // if the timer has reached 0, cancel the timer and show the game lost dialogue
          RecordAndPlay.saveRecording();
          gui.getGameLost().setVisible(true);
          timer.cancel();
        } else if (!gamePaused) {
          // otherwise decrease the timer by 1 if the game isn't paused.
          timeLeft -= 1;
        }
      }
    };
  }

  /**
   * Start the recording of a game.
   **/
  public void startRecording() {
    RecordAndPlay.startRecording(this);
  }

  /**
   * Save the recording of a game.
   **/
  public void saveRecording() {
    RecordAndPlay.saveRecording();
  }

  /**
   * Load a recording of a game.
   **/
  public void loadRecording() {
    RecordAndPlay.loadRecording(this);
  }

  /**
   * Load a file for the game.
   *
   * @param defaultFile - true if the default file, gameState.json is to be used, otherwise false
   **/
  public void loadFile(Boolean defaultFile) {
    // if the game is loading the default gameState file, then load it
    if (defaultFile) {
      LevelLoader.loadOldGame(this, file);
    } else {
      // otherwise the user chooses a file to load and load from that file
      File chosenFile = FileChooser.getJsonFileToLoad(gui, "saves");
      if (chosenFile != null) {
        LevelLoader.loadOldGame(this, chosenFile);
      }
    }
  }

  /**
   * Save a game to a file.
   *
   * @param saveCurrentLevel - true if the current level is to be saved, otherwise false
   * @param defaultFile - true if the default file, gameState.json is to be used, otherwise false
   **/
  public void saveFile(Boolean saveCurrentLevel, Boolean defaultFile) {
    // if the current level wants to be saved
    if (saveCurrentLevel) {
      // create a new game at the current level and save it
      startGame(level);
      LevelLoader.saveGameState(LevelLoader.getGameState(this), file);
    } else if (defaultFile) {
      // otherwise if the current game state is to be saved to the default gameState file,
      // save the file
      LevelLoader.saveGameState(LevelLoader.getGameState(this), file);
    } else {
      // otherwise if the user wants to choose where to save the file, let the user chose a file
      // and save the game state to the chosen file
      LevelLoader.saveGameState(LevelLoader.getGameState(this),
              FileChooser.getFileToSave(gui, "saves"));
    }
  }

  /**
   * Exit the game.
   **/
  public void exitGame() {
    RecordAndPlay.saveRecording();
    System.exit(0);
  }

  /**
   * Start a game from the level passed in as a parameter.
   *
   * @param level - the level to start the game at
   **/
  public void startGame(int level) {
    // if the timer is still going, cancel it
    if (timeLeft != 0 && timeLeft != maxTime && !maze.isLevelOver()) {
      timer.cancel();
    }
    // create a new maze and set this in the gui
    setMaze(new Maze(level));
    gui.getDashboard().resetDashboard();
    gui.updateGui(false);
    startTimer(false);
  }

  /**
   * Pauses the game.
   *
   * @param showDialogue - true if the the pause dialogue is to be displayed, otherwise false
   **/
  public void pauseGame(boolean showDialogue) {
    gamePaused = true;
    if (showDialogue) {
      gui.displayPausedDialogue();
    }
  }

  /**
   * Resumes the game.
   **/
  public void playGame() {
    gamePaused = false;
    gui.hidePausedDialogue();
  }

  /**
   * Get the maze.
   *
   * @return the current maze
   **/
  public Maze getMaze() {
    return maze;
  }

  /**
   * Get the gui.
   *
   * @return the gui
   **/
  public Gui getGui() {
    return gui;
  }

  /**
   * Get the level.
   *
   * @return the current maze
   **/
  public int getLevel() {
    return level;
  }

  /**
   * Get the last level of the game.
   *
   * @return the last level of the game
   **/
  public int getLastLevel() {
    return lastLevel;
  }

  /**
   * Set the level.
   *
   * @param level - level to set update the level to
   **/
  public void setLevel(int level) {
    this.level = level;
  }

  /**
   * Get the time left.
   *
   * @return the time left in the timer
   **/
  public int getTimeLeft() {
    return timeLeft;
  }

  /**
   * Set the maze.
   *
   * @param maze - the new maze to update the maze to
   **/
  public void setMaze(Maze maze) {
    this.maze = maze;
    if (gui != null) {
      gui.setMaze(maze);
    }
  }

  /**
   * Set the maze.
   *
   * @param maze - the current maze
   **/
  public void setGui(Maze maze) {
    gui = new Gui(this, maze);
  }

  /**
   * Returns the timer.
   *
   * @return the timer
   **/
  public Timer getTimer() {
    return timer;
  }

  /**
   * Set the time left.
   *
   * @param time - the time to set the time left to
   **/
  public void setTimeLeft(int time) {
    timeLeft = time;
  }

  /**
   * Return whether the player has one the game or not.
   *
   * @return true if the player has won, otherwise false
   **/
  public boolean isLevelWon() {
    return maze.getState().equals(Maze.LevelState.WON);
  }

  /**
   * Creates a new instance of main to run the ChapsChallenge game.
   *
   * @param args - the arguments
   **/
  public static void main(String[] args) {
    new Main();
  }
}
