package nz.ac.vuw.ecs.swen225.gp20.application;

import javafx.scene.input.KeyCode;
import nz.ac.vuw.ecs.swen225.gp20.maze.Maze;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GUI extends JFrame implements KeyListener {
  // initialize screen sizes
  private int screenWidth = 900;
  private int screenHeight = 500;

  // initialize GUI fields
  private JPanel mainPanel = new JPanel();
  private Dashboard dashboard = new Dashboard();
  private GridBagConstraints gbc = new GridBagConstraints();

  // initialize application
  private Main main;
  private Maze maze;

  /**
   * Create the JFrame for the game and sets all the default values.
   **/
  public GUI(Main main, Maze maze){
    this.main = main;
    this.maze = maze;

    // set the frame requirements
    addKeyListener(this);
    setSize(screenWidth, screenHeight);
    setResizable(true);
    setVisible(true);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setFocusable(true);
    setFocusTraversalKeysEnabled(false);
    setAlwaysOnTop(true);
  }

  /**
   * Create the components for the gui and adds them to the screen in the correct locations.
   **/
  public void setUpGui(){
    // Create the Maze
    JPanel maze = new JPanel();
    maze.setBackground(Color.blue);

    // Add the dashboard and maze to the gui
    mainPanel.setLayout(new GridBagLayout());
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridheight = 1;
    gbc.gridwidth = 1;
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weighty = 1;
    gbc.weightx = 6;
    gbc.insets = new Insets(50, 50, 50, 50);
    Canvas canvas = new Canvas();
    canvas.setFocusable(false);
    mainPanel.add(canvas, gbc);

    gbc.gridx = 1;
    gbc.weightx = 2;
    gbc.insets = new Insets(50, 50, 50, 50);
    mainPanel.add(dashboard, gbc);

    dashboard.setVisible(true);
    mainPanel.setVisible(true);

    this.add(mainPanel);
    this.validate();
    this.repaint();
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
    }else if (keyEvent.getKeyCode() == KeyEvent.VK_DOWN) {
      // Move Chap down
      maze.moveChap(Maze.Direction.DOWN);
    }else if (keyEvent.getKeyCode() == KeyEvent.VK_RIGHT) {
      // Move chap right
      maze.moveChap(Maze.Direction.RIGHT);
    }else if (keyEvent.getKeyCode() == KeyEvent.VK_LEFT) {
      // Move chap left
      maze.moveChap(Maze.Direction.LEFT);
    }else if(keyEvent.isControlDown() && keyEvent.getKeyCode() == KeyEvent.VK_X){
      //CTRL-X  - exit the game, the current game state will be lost, the next time the game is started,
      // it will resume from the last unfinished level
      System.out.println("EXIT But save level");
      System.exit(0);
    }else if(keyEvent.isControlDown() && keyEvent.getKeyCode() == KeyEvent.VK_S){
      //CTRL-S  - exit the game, saves the game state, game will resume next time the application will be started
      System.out.println("EXIT, save game state");
      System.exit(0);
    }else if(keyEvent.isControlDown() && keyEvent.getKeyCode() == KeyEvent.VK_R){
      //CTRL-R  - resume a saved game
      System.out.println("Resume a saved game");
    }else if(keyEvent.isControlDown() && keyEvent.getKeyCode() == KeyEvent.VK_P){
      //CTRL-P  - start a new game at the last unfinished level
      System.out.println("Start a new game as last unfinished level");
    }else if(keyEvent.isControlDown() && keyEvent.getKeyCode() == KeyEvent.VK_1){
      //CTRL-1 - start a new game at level 1
      System.out.println("Start new game a level 1");
    }else if(keyEvent.getKeyCode() == KeyEvent.VK_SPACE){
      //SPACE - pause the game and display a “game is paused” dialog
      System.out.println("Pause and display");
    }else if(keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE){
      //ESC - close the “game is paused” dialog and resume the game
      System.out.println("CLose dialogue and Resume");
    }
  }

  @Override
  public void keyReleased(KeyEvent keyEvent) {

  }
}
