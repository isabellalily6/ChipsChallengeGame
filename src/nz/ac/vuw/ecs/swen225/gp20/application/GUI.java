package nz.ac.vuw.ecs.swen225.gp20.application;

import nz.ac.vuw.ecs.swen225.gp20.commons.Sound;
import nz.ac.vuw.ecs.swen225.gp20.maze.Direction;
import nz.ac.vuw.ecs.swen225.gp20.maze.InfoField;
import nz.ac.vuw.ecs.swen225.gp20.maze.Maze;
import nz.ac.vuw.ecs.swen225.gp20.recnplay.RecordAndPlay;
import nz.ac.vuw.ecs.swen225.gp20.recnplay.RecordedMove;
import nz.ac.vuw.ecs.swen225.gp20.render.Canvas;
import nz.ac.vuw.ecs.swen225.gp20.render.SoundEffect;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Create a GUI which displays the game on the screen
 **/
public class GUI extends JFrame implements KeyListener {
  // initialize screen sizes
  private final int screenWidth = 900;
  private final int screenHeight = 650;

  // initialize GUI fields
  private final JPanel mainPanel = new JPanel();
  private final Dashboard dashboard;
  private final Canvas canvas;
  private final Dialogues pausedDialogue;
  private final Dialogues gameWon;
  private final Dialogues gameLost;

  // initialize application
  private final Main main;
  private Maze maze;

  /**
   * Create the JFrame for the game and sets all the default values.
   *
   * @param main main
   * @param maze maze
   **/
  public GUI(Main main, Maze maze){
    this.main = main;
    this.maze = maze;
    this.canvas = new Canvas(maze);
    this.dashboard = new Dashboard(maze, canvas);

    // set the frame requirements
    addKeyListener(this);
    setSize(screenWidth, screenHeight);
    setResizable(true);
    setVisible(true);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setFocusable(true);
    setFocusTraversalKeysEnabled(false);
    setResizable(false);

    // create dialogues
    pausedDialogue = new Dialogues(main, "GAME IS PAUSED", "RESUME");
    gameWon = new Dialogues(main, "You have won the level!!!", "NEXT GAME");
    gameLost = new Dialogues(main, "You have lost the game", "RETRY");
    setButtonActionListeners();
  }

  /**
   * Set the action listeners for the buttons on the JDialogues
   **/
  public void setButtonActionListeners(){
    // add action listeners to the buttons in the possible dialogues
    pausedDialogue.setActionListener(method -> main.playGame());
    gameWon.setActionListener(

            new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        if(maze.getState().equals(Maze.LevelState.WON) && main.getLevel()!=2){
          main.setLevel(main.getLevel()+1);
          main.startGame(main.getLevel());
        }else {
          main.startGame(1);
        }
        gameWon.dispose();
      }
    });
    gameLost.setActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        main.startGame(main.getLevel());
        gameLost.dispose();
      }
    });
  }

  /**
   * Get Canvas
   *
   * @return the canvas
   **/
  public Canvas getCanvas() {
    return canvas;
  }

  /**
   * Create the components for the gui and adds them to the screen in the correct locations.
   **/
  public void setUpGui(){
    // set the menu bar
    setJMenuBar(new MenuBar(main));

    // set the layout for the main panel
    mainPanel.setLayout(new BorderLayout(50, 50));

    // Create and set the boarder for the main panel
    EmptyBorder border = new EmptyBorder(50, 50, 50, 50);
    mainPanel.setBorder(border);

    // create a new canvas to add to the frame
    canvas.setFocusable(false);
    canvas.setBackground(Color.LIGHT_GRAY);

    // Create a panel to place the canvas on to keep the correct size of the canva
    JPanel canvasPanel = new JPanel();
    Border borderGray = BorderFactory.createLineBorder(Color.DARK_GRAY, 3);
    canvas.setBorder(borderGray);
    canvasPanel.add(canvas);

    // add the components to the main panel in the correct locations
    mainPanel.add(canvasPanel, BorderLayout.CENTER);
    mainPanel.add(dashboard, BorderLayout.EAST);

    add(mainPanel);
    validate();
    repaint();
  }

  /**
   * Call the method in the dashboard, to change the time displayed to the new time left
   *
   * @param timeLeft time left
   **/
  public void setTimer(int timeLeft){
    String time = Integer.toString(timeLeft);
    dashboard.setTimer(time);
  }

  /**
   * Set the maze
   *
   * @param maze maze
   **/
  public void setMaze(Maze maze) {
    this.maze = maze;
    dashboard.setMaze(maze);
  }

  /**
   * Displays the paused dialogue on the screen.
   **/
  public void displayPausedDialogue(){
    pausedDialogue.setVisible(true);
  }

  /**
   * Hides the paused dialogue.
   **/
  public void hidePausedDialogue(){
    pausedDialogue.setVisible(false);
  }

  /**
   * Get the game won dialogues
   *
   * @return the game won dialogue
   **/
  public Dialogues getGameWon() {
    return gameWon;
  }

  /**
   * Get the game lost dialogues
   *
   * @return the game lost dialogue
   **/
  public Dialogues getGameLost() {
    return gameLost;
  }

  /**
   * Get the dashboard
   *
   * @return the dashboard
   **/
  public Dashboard getDashboard() {
    return dashboard;
  }

  @Override
  public void keyTyped(KeyEvent keyEvent) {
  }

  @Override
  public void keyPressed(KeyEvent keyEvent) {
  }

  public Maze getMaze() {
    return maze;
  }

  @Override
  public void keyReleased(KeyEvent keyEvent) {
    Sound sound = null;
    if(RecordAndPlay.getPlayingRecording()){

    }
    else if (keyEvent.getKeyCode() == KeyEvent.VK_UP) {
      // Move the chap up
      sound = maze.moveChap(Direction.UP);
      RecordAndPlay.addMove(new RecordedMove(maze.getChap(), Direction.UP, main.getTimeLeft(), RecordAndPlay.recordedMovesSize()));
    } else if (keyEvent.getKeyCode() == KeyEvent.VK_DOWN) {
      // Move Chap down
      sound = maze.moveChap(Direction.DOWN);
      RecordAndPlay.addMove(new RecordedMove(maze.getChap(), Direction.DOWN, main.getTimeLeft(), RecordAndPlay.recordedMovesSize()));
    }else if (keyEvent.getKeyCode() == KeyEvent.VK_RIGHT) {
      // Move chap right
      sound = maze.moveChap(Direction.RIGHT);
      RecordAndPlay.addMove(new RecordedMove(maze.getChap(), Direction.RIGHT, main.getTimeLeft(), RecordAndPlay.recordedMovesSize()));
    }else if (keyEvent.getKeyCode() == KeyEvent.VK_LEFT) {
      // Move chap left
      sound = maze.moveChap(Direction.LEFT);
      RecordAndPlay.addMove(new RecordedMove(maze.getChap(), Direction.LEFT, main.getTimeLeft(), RecordAndPlay.recordedMovesSize()));
    }else if(keyEvent.isControlDown() && keyEvent.getKeyCode() == KeyEvent.VK_X){
      //CTRL-X  - exit the game, the current game state will be lost, the next time the game is started,
      // it will resume from the last unfinished level
      System.out.println("EXIT But save level");
      main.saveFile(true);
      main.exitGame();
    }else if(keyEvent.isControlDown() && keyEvent.getKeyCode() == KeyEvent.VK_S){
      //CTRL-S  - exit the game, saves the game state, game will resume next time the application will be started
      System.out.println("EXIT, save game state");
      main.saveFile(false);
      main.exitGame();
    }else if(keyEvent.isControlDown() && keyEvent.getKeyCode() == KeyEvent.VK_R){
      //CTRL-R  - resume a saved game

      System.out.println("Resume a saved game");
    }else if(keyEvent.isControlDown() && keyEvent.getKeyCode() == KeyEvent.VK_P){
      //CTRL-P  - start a new game at the last unfinished level
      main.startGame(main.getLevel());
    }else if (keyEvent.isControlDown() && keyEvent.getKeyCode() == KeyEvent.VK_1) {
      //CTRL-1 - start a new game at level 1
      main.startGame(1);
      System.out.println("Start new game a level 1");
    } else if (keyEvent.getKeyCode() == KeyEvent.VK_SPACE) {
      //SPACE - pause the game and display a “game is paused” dialog
      System.out.println("Pause and display");
      main.pauseGame(true);
    } else if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
      //ESC - close the “game is paused” dialog and resume the game
      System.out.println("Close dialogue and Resume");
      main.playGame();
    }
    if (sound != null) SoundEffect.play(sound);
    dashboard.updateDashboard();
    canvas.refreshComponents();
    canvas.repaint();
    if(maze.getChap().getLocation() instanceof InfoField){
      InfoField location = (InfoField) maze.getChap().getLocation();
      String info = location.getInfo();
        Dialogues infoPanel = new Dialogues(main, info, "Close");
        infoPanel.setActionListener(method -> infoPanel.dispose());
        infoPanel.setVisible(true);
    }

  }
}