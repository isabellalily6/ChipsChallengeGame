package nz.ac.vuw.ecs.swen225.gp20.application;

import nz.ac.vuw.ecs.swen225.gp20.maze.Maze;

import java.util.Timer;
import java.util.TimerTask;

public class Main {
    // initialize the game variables
    private GUI gui;
    private Maze maze;

    // game information
    private int level = 1;
    private int maxTime = 10;
    private int timeLeft = maxTime;

    private Timer timer = new Timer();
    private TimerTask timerTask;

    private boolean gamePaused = false;


  /**
   *  Create a new instance of the game application
   **/
    public Main(){
      // create the maze and the gui for the game
      maze = new Maze(level);
      gui = new GUI(this, maze);
      gui.setUpGui();
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
          else {
            timeLeft -= 1;
          }
        }
      };
    }

    public void pauseGame(){
      gui.displayPausedDialogue();
      gamePaused = true;
    }

    public void playGame(){
      gui.hidePausedDialogue();
      gamePaused = false;
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
