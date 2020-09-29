package nz.ac.vuw.ecs.swen225.gp20.application;

import nz.ac.vuw.ecs.swen225.gp20.maze.Maze;
import nz.ac.vuw.ecs.swen225.gp20.persistence.LevelLoader;
import nz.ac.vuw.ecs.swen225.gp20.recnplay.RecordAndPlay;

import java.util.Timer;
import java.util.TimerTask;

public class Main {
  // initialize the game variables
  private final GUI gui;
  private Maze maze;

  // game information
  private int level = 1;
  private final int maxTime = 100;
  private int timeLeft = maxTime;

  private Timer timer = new Timer();
  private TimerTask timerTask;

  private boolean gamePaused = false;


  /**
   * Create a new instance of the game application
   **/
  public Main() {
    // create the maze and the gui for the game
    maze = new Maze(level);
      gui = new GUI(this, maze);
      gui.setUpGui();
      startTimer();
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
          // if the timer has reached 0, cancel the timer
          if(timeLeft==0){
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
      RecordAndPlay.startRecording();
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
    RecordAndPlay.loadRecording();
  }

  /**
   * Replay a recording of a game
   **/
  public void replayRecording(){

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

  }

  /**
   * Start a game from the level passed in as a parameter
   **/
  public void startGame(int level){
    if(timeLeft != 0 && timeLeft != maxTime){
      timer.cancel();
    }
    this.maze = new Maze(1);
    gui.getCanvas().setMaze(maze);
    gui.setMaze(maze);
    startTimer();
  }

  /**
   * Pauses the game
   **/
    public void pauseGame(){
      gui.displayPausedDialogue();
      gamePaused = true;
    }

  /**
   * Resumes the game
   **/
    public void playGame(){
      gui.hidePausedDialogue();
      gamePaused = false;
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
   * Get the time left
   *
   * @return the time left in the timer
   **/
  public int getTimeLeft() {
    return timeLeft;
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
