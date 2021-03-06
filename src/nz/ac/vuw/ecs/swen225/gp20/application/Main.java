package nz.ac.vuw.ecs.swen225.gp20.application;

import nz.ac.vuw.ecs.swen225.gp20.maze.Maze;

import java.util.Timer;
import java.util.TimerTask;

public class Main {
    // initialize the game variables
    GUI gui;
    Maze maze;

    // game information
    int level = 1;
    int maxTime = 10;
    int timeLeft = maxTime;

    Timer timer = new Timer();
    TimerTask timerTask;

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
