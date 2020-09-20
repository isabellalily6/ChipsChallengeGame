package nz.ac.vuw.ecs.swen225.gp20.application;

import javafx.scene.input.KeyCode;
import nz.ac.vuw.ecs.swen225.gp20.maze.Maze;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GUI extends JFrame implements KeyListener {
  // initialize screen sizes
  private int screenWidth = 900;
  private int screenHeight = 500;

  // initialize GUI fields
  JPanel mainPanel = new JPanel();
  JPanel dashboard = new JPanel();
  GridBagConstraints gbc = new GridBagConstraints();
  JLabel timeNum = new JLabel("");

  // initialize application
  Main main;
  Maze maze;


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
    // Create the Dashboard
    //dashboard.addKeyListener(this);
    dashboard.setLayout(new GridLayout(7, 1));
    dashboard.setBackground(Color.decode("#0C9036"));

    dashboard.setFocusable(false);
    // Add components to the dashboard
    Border border = BorderFactory.createLineBorder(Color.BLUE, 5);

    JLabel level = new JLabel("LEVEL");
    level.setFont(new Font("Verdana", Font.PLAIN, 18));
    JLabel levelNum = new JLabel("0");
    levelNum.setFont(new Font("Verdana", Font.PLAIN, 18));
    levelNum.setBorder(border);
    levelNum.setBackground(Color.lightGray);
    JLabel time = new JLabel("TIME");
    time.setFont(new Font("Verdana", Font.PLAIN, 18));
    timeNum.setFont(new Font("Verdana", Font.PLAIN, 18));
    timeNum.setBorder(border);
    JLabel chips = new JLabel("CHIPS LEFT");
    chips.setFont(new Font("Verdana", Font.PLAIN, 18));
    JLabel chipsNum = new JLabel("");
    chipsNum.setFont(new Font("Verdana", Font.PLAIN, 18));
    chipsNum.setBorder(border);
    JLabel chipsCollected = new JLabel("");
    chipsCollected.setFont(new Font("Verdana", Font.PLAIN, 18));

    // Add the components to the dashboard
    gbc.fill = GridBagConstraints.CENTER;
    gbc.gridheight = 1;
    gbc.gridwidth = 1;
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weighty = 1;
    gbc.weightx = 1;
    dashboard.add(level, gbc);
    gbc.gridy = 1;
    dashboard.add(levelNum, gbc);
    gbc.gridy = 2;
    dashboard.add(time, gbc);
    gbc.gridy = 3;
    dashboard.add(timeNum, gbc);
    gbc.gridy = 4;
    dashboard.add(chips, gbc);
    gbc.gridy = 5;
    dashboard.add(chipsNum, gbc);
    gbc.gridy = 6;
    dashboard.add(chipsCollected, gbc);

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

  public void setTimer(int timeLeft){
    String time = Integer.toString(timeLeft);
    timeNum.setText(time);
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
    }else if (keyEvent.getKeyCode() == KeyEvent.VK_DOWN) {
      // Move Chap down
    }else if (keyEvent.getKeyCode() == KeyEvent.VK_RIGHT) {
      // Move chap right
    }else if (keyEvent.getKeyCode() == KeyEvent.VK_LEFT) {
      // Move chap left
    }else if(keyEvent.isControlDown() && keyEvent.getKeyCode() == KeyEvent.VK_X){
      //CTRL-X  - exit the game, the current game state will be lost, the next time the game is started,
      // it will resume from the last unfinished level
      System.out.println("EXIT But save level");
      System.exit(0);
      //eyEvent.VK_CONTROL) && activeKeys.contains(KeyEvent.VK_X
    }else if(keyEvent.isControlDown() && keyEvent.getKeyCode() == KeyEvent.VK_S){
      //CTRL-S  - exit the game, saves the game state, game will resume next time the application will be started
      System.out.println("EXIT, save game state");
      System.exit(0);
      //eyEvent.VK_CONTROL) && activeKeys.contains(KeyEvent.VK_X
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
