package nz.ac.vuw.ecs.swen225.gp20.application;

import nz.ac.vuw.ecs.swen225.gp20.maze.Maze;
import nz.ac.vuw.ecs.swen225.gp20.persistence.LevelLoader;
import nz.ac.vuw.ecs.swen225.gp20.recnplay.RecordAndPlay;
import nz.ac.vuw.ecs.swen225.gp20.render.Music;

import java.net.SecureCacheResponse;
import java.util.Timer;
import java.util.TimerTask;

/**
 * From Handout:
 * The application window should display the time left to play, the current level, keys collected,
 * and the number of treasures that still need to be collected.  It should also offer buttons
 * and menu items to pause and exit the game, to save the game state and to resume a saved game,
 * and to display a help page with game rules.
 *
 * This module also manages a countdown -- each level has a maximum time associated with
 * it (such as 1 min), and the once the countdown reaches zero, the game terminates
 * with a group dialog informing the user, and resetting the game to replay the level.
 *
 * @author Isabella Tomaz-Ketley
 */
public class Main {
  // initialize the game variables
  private GUI gui;
  private Maze maze;

  // initialise the game information
  private int level = 1;
  private int maxTime = 100;
  private int timeLeft = maxTime;

  // initialize the timer variables
  private Timer timer = new Timer();
  private TimerTask timerTask;

  // variable to check whether the game is paused or not
  private boolean gamePaused = false;


  /**
   * Create a new instance of the game application
   **/
  public Main() {
    // create the maze and the gui for the game
    maze = new Maze(level);
    gui = new GUI(this, maze);
    gui.setUpGui();
    new Music();
    startTimer();
  }


  /**
   * Create a new instance of the game which doesnt start a timer
   * or create the GUI for testing purposes
   **/
  public Main(Boolean monkeyTest) {
    // create the maze and the gui for the game
    maze = new Maze(level);
    if(monkeyTest){
      gui = new GUI(this, maze);
      gui.setUpGui();
    }

  }

  /**
   * Start a timer to do the timer task every second
   **/
  public void startTimer(){
    // set the start time of the timer
    timeLeft = maxTime;
    // create the time and the timer task
    timer = new Timer();
    createTimerTask();
    // schedule the timer to do the task every second
    timer.schedule(timerTask, 0,1000);
  }

  /**
   * Create a new timer task for the timer which will count down every second
   **/
  public void createTimerTask(){
    // create a new timer task
    timerTask = new TimerTask() {
      @Override
      public void run() {
        // show the time on the gui label
        gui.setTimer(timeLeft);
        if(maze.isLevelOver()){
          gui.getGameWon().setVisible(true);
          timeLeft = maxTime;
          timer.cancel();
        }
        // if the timer has reached 0, cancel the timer
        if(timeLeft==0){
          gui.getGameLost().setVisible(true);
          timer.cancel();
        }
        // otherwise decrease the timer by 1.
        else if(!gamePaused){
          timeLeft -= 1;
        }
      }
    };
  }

  /**
   * Start the recording of a game
   **/
  public void startRecording(){
    RecordAndPlay.startRecording(this);
  }

  /**
   * Save the recording of a game
   **/
  public void saveRecording(){
    RecordAndPlay.saveRecording();
  }

  /**
   * Load a recording of a game
   **/
  public void loadRecording(){
    RecordAndPlay.loadRecording(this);
  }

  /**
   * Replay a recording of a game
   **/
  public void replayRecording(){
    RecordAndPlay.playRecording(this);
  }

  /**
   * Load a file for the game
   **/
  public void loadFile(){

  }

  /**
   * Save a game to a file
   **/
  public void saveFile(){
    LevelLoader.saveGameState(LevelLoader.getGameState(this));
  }

  /**
   * Exit the game
   **/
  public void exitGame(){
    System.exit(0);
  }

  /**
   * Start a game from the level passed in as a parameter
   **/
  public void startGame(int level){
    // if the timer is still going, cancel it
    if(timeLeft != 0 && timeLeft != maxTime){
      timer.cancel();
    }
    // create a new maze and set this in canvas and the gui
    this.maze = new Maze(level);
    gui.getCanvas().setMaze(maze);
    gui.setMaze(maze);
    gui.getCanvas().repaint();
    startTimer();
  }

  /**
   * Pauses the game
   *
   * @param showDialogue
   **/
  public void pauseGame(boolean showDialogue){
    gamePaused = true;
    if(showDialogue) {
      gui.displayPausedDialogue();
    }
  }

  /**
   * Resumes the game
   **/
  public void playGame(){
    gamePaused = false;
    gui.hidePausedDialogue();
  }

  /**
   * Get the maze
   *
   * @return the current maze
   **/
  public Maze getMaze() {
    return maze;
  }

  /**
   * Get the gui
   *
   * @return the gui
   **/
  public GUI getGui() {
    return gui;
  }

  /**
   * Get the level
   *
   * @return the current maze
   **/
  public int getLevel() {
    return level;
  }

  /**
   * Get the time left
   *
   * @return the time left in the timer
   **/
  public int getTimeLeft() {
    return timeLeft;
  }

  /**
   * Set the maze
   *
   * @param maze
   **/
  public void setMaze(Maze maze) {
    this.maze = maze;
  }

  /**
   * Returns the timer
   *
   * @return the timer
   **/
  public Timer getTimer() {
    return timer;
  }


  /**
   * Set the time left
   *
   * @param time
   **/
  public void setTimeLeft(int time) {
    timeLeft = time;
  }

  /**
   * Creates a new instance of main to run the ChapsChallenge game
   *
   * @param args
   **/
  public static void main(String[] args) {
    System.out.println("Welcome to Chaps Challenge!");
    new Main();
  }
}
