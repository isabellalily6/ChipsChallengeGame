package nz.ac.vuw.ecs.swen225.gp20.application;

import nz.ac.vuw.ecs.swen225.gp20.maze.Maze;
import nz.ac.vuw.ecs.swen225.gp20.persistence.Level;
import nz.ac.vuw.ecs.swen225.gp20.persistence.LevelLoader;
import nz.ac.vuw.ecs.swen225.gp20.recnplay.RecordAndPlay;
import nz.ac.vuw.ecs.swen225.gp20.render.Canvas;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GUI extends JFrame implements KeyListener {
  // initialize screen sizes
  private int screenWidth = 900;
  private int screenHeight = 650;

  // initialize GUI fields
  private JPanel mainPanel = new JPanel();
  private Dashboard dashboard;
  private Canvas canvas;
  private Dialogues pausedDialogue;
  private Dialogues gameWon;
  private Dialogues gameLost;

  // initialize application
  private Main main;
  private Maze maze;

  /**
   * Create the JFrame for the game and sets all the default values.
   *
   * @param main
   * @param maze
   **/
  public GUI(Main main, Maze maze){
    this.main = main;
    this.maze = maze;
    this.canvas = new Canvas(maze);
    this.dashboard = new Dashboard(maze);

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
    gameWon = new Dialogues(main, "You have won the level!!!", "RESTART");
    gameLost = new Dialogues(main, "You have lost the game", "RETRY");
    setButtonActionListeners();
  }

  public void setButtonActionListeners(){
    pausedDialogue.getButton().addActionListener(method -> main.playGame());
    gameWon.setActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        main.startGame(main.getLevel());
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
   * @param timeLeft
   **/
  public void setTimer(int timeLeft){
    String time = Integer.toString(timeLeft);
    dashboard.setTimer(time);
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

  public Dialogues getGameWon() {
    return gameWon;
  }

  public Dialogues getGameLost() {
    return gameLost;
  }

  public void exitGame() {
    System.exit(0);
  }

  @Override
  public void keyTyped(KeyEvent keyEvent) {
  }

  @Override
  public void keyPressed(KeyEvent keyEvent) {
    // System.out.println("pressed");
    int keyCode = keyEvent.getKeyCode();
    if (keyEvent.getKeyCode() == KeyEvent.VK_UP) {
      // Move the chap up
      maze.moveChap(Maze.Direction.UP);
      RecordAndPlay.addMove(maze.getChap(), Maze.Direction.UP);
    }else if (keyEvent.getKeyCode() == KeyEvent.VK_DOWN) {
      // Move Chap down
      maze.moveChap(Maze.Direction.DOWN);
      RecordAndPlay.addMove(maze.getChap(), Maze.Direction.DOWN);
    }else if (keyEvent.getKeyCode() == KeyEvent.VK_RIGHT) {
      // Move chap right
      maze.moveChap(Maze.Direction.RIGHT);
      RecordAndPlay.addMove(maze.getChap(), Maze.Direction.RIGHT);
    }else if (keyEvent.getKeyCode() == KeyEvent.VK_LEFT) {
      // Move chap left
      maze.moveChap(Maze.Direction.LEFT);
      RecordAndPlay.addMove(maze.getChap(), Maze.Direction.LEFT);
    }else if(keyEvent.isControlDown() && keyEvent.getKeyCode() == KeyEvent.VK_X){
      //CTRL-X  - exit the game, the current game state will be lost, the next time the game is started,
      // it will resume from the last unfinished level
      System.out.println("EXIT But save level");
      exitGame();
    }else if(keyEvent.isControlDown() && keyEvent.getKeyCode() == KeyEvent.VK_S){
      //CTRL-S  - exit the game, saves the game state, game will resume next time the application will be started
      System.out.println("EXIT, save game state");
      LevelLoader.saveGameState(LevelLoader.getGameState(main));
      exitGame();
    }else if(keyEvent.isControlDown() && keyEvent.getKeyCode() == KeyEvent.VK_R){
      //CTRL-R  - resume a saved game
      System.out.println("Resume a saved game");
    }else if(keyEvent.isControlDown() && keyEvent.getKeyCode() == KeyEvent.VK_P){
      //CTRL-P  - start a new game at the last unfinished level
      System.out.println("Start a new game as last unfinished level");
    }else if(keyEvent.isControlDown() && keyEvent.getKeyCode() == KeyEvent.VK_1){
      //CTRL-1 - start a new game at level 1
      main.startGame(1);
      System.out.println("Start new game a level 1");
    }else if(keyEvent.getKeyCode() == KeyEvent.VK_SPACE){
      //SPACE - pause the game and display a “game is paused” dialog
      System.out.println("Pause and display");
      main.pauseGame();
    }else if(keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE){
      //ESC - close the “game is paused” dialog and resume the game
      System.out.println("CLose dialogue and Resume");
      main.playGame();
    }
    canvas.repaint();
  }

  @Override
  public void keyReleased(KeyEvent keyEvent) {

  }
}
