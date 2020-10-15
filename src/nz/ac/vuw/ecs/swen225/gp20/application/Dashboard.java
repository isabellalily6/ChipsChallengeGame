package nz.ac.vuw.ecs.swen225.gp20.application;

import nz.ac.vuw.ecs.swen225.gp20.maze.Key;
import nz.ac.vuw.ecs.swen225.gp20.maze.Maze;
import nz.ac.vuw.ecs.swen225.gp20.render.Canvas;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;
import java.util.Set;

/**
 * Creates a dashboard on the GUI which includes:
 * Level number.
 * Time Left.
 * Number of treasure collected.
 * Images of the treasures collected.
 **/
public class Dashboard extends JPanel {
  // initialise the size of the dashboard
  private int WIDTH = 200;
  private int HEIGHT = 400;

  // initialise the components to be put on the Dashboard
  private JLabel level;
  private JLabel levelNum;
  private JLabel time;
  private JLabel timeNum;
  private JLabel chips;
  private JLabel chipsNum;
  private JLabel chipsCollected;
  private ArrayList<JLabel> treasuresCollected = new ArrayList<>();

  // Two panels which the dashboard consists of
  private JPanel topPanel = new JPanel(new GridLayout(7, 1));
  private JPanel bottomPanel = new JPanel(new GridLayout(2, 4));

  Maze maze;
  Canvas canvas;

  /**
   * Create a new instance of dashboard and set the default values
   **/
  public Dashboard(Maze maze, Canvas canvas){
    this.maze = maze;
    // Set up the components of the dashboard
    setLayout(new BorderLayout());
    setBackground(Color.lightGray);
    setFocusable(false);
    createComponents();
    addComponents();
    setPreferredSize(new Dimension(WIDTH, HEIGHT));
    Border border = BorderFactory.createLineBorder(Color.DARK_GRAY, 3);
    setBorder(border);
    // set the colour of the two panels
    topPanel.setBackground(Color.lightGray);
    bottomPanel.setBackground(Color.lightGray);
  }

  /**
   * Create the dashboard components
   **/
  public void createComponents(){
    // Set the boarder of some of the JLabels
    Border border = BorderFactory.createLineBorder(Color.DARK_GRAY, 5);

    // Create the top panel which contains the level, time and chips left

    // create the level label
    level = new JLabel("LEVEL", SwingConstants.CENTER);
    level.setFont(new Font("Verdana", Font.PLAIN, 18));

    // create the level number label
    levelNum = new JLabel("1", SwingConstants.CENTER);
    levelNum.setFont(new Font("Verdana", Font.PLAIN, 18));
    levelNum.setBorder(border);
    levelNum.setBackground(Color.lightGray);

    // create the time label
    time = new JLabel("TIME", SwingConstants.CENTER);
    time.setFont(new Font("Verdana", Font.PLAIN, 18));

    // create the time number label
    timeNum = new JLabel("", SwingConstants.CENTER);
    timeNum.setFont(new Font("Verdana", Font.PLAIN, 18));
    timeNum.setBorder(border);

    // create the chips label
    chips = new JLabel("TREASURES LEFT", SwingConstants.CENTER);
    chips.setFont(new Font("Verdana", Font.PLAIN, 18));

    // create the chips number label
    chipsNum = new JLabel(maze.getTreasuresLeft() + "", SwingConstants.CENTER);
    chipsNum.setFont(new Font("Verdana", Font.PLAIN, 18));
    chipsNum.setBorder(border);

    // create the chips collected label
    chipsCollected = new JLabel("KEYS COLLECTED", SwingConstants.CENTER);
    chipsCollected.setFont(new Font("Verdana", Font.PLAIN, 18));

    // Create the bottom panel which contains the Chaps Items
    for(int i = 0; i < 8; i++){
      JLabel label = new JLabel();
      treasuresCollected.add(label);
      // Set the boarder of the items
      border = BorderFactory.createLineBorder(Color.DARK_GRAY, 2);
      label.setBorder(border);
      label.setPreferredSize(new Dimension(WIDTH/4, WIDTH/4));
      bottomPanel.add(label);
    }
  }

  /**
   * Add the components to the dashboard
   **/
  public void addComponents(){
    topPanel.add(level);
    topPanel.add(levelNum);
    topPanel.add(time);
    topPanel.add(timeNum);
    topPanel.add(chips);
    topPanel.add(chipsNum);
    topPanel.add(chipsCollected);

    add(topPanel, BorderLayout.CENTER);
    add(bottomPanel, BorderLayout.SOUTH);
  }

  /**
   * Update the dashboard
   **/
  public void updateDashboard(){
    Set<Key.Colour> playersBackback = maze.getChap().getBackpack();
    int i = 0;
    for(Key.Colour colour: playersBackback){
      JLabel label = treasuresCollected.get(i);
      label.setIcon(canvas.makeImageIcon(getFile(colour)));
      i++;
    }
    chipsNum.setText(maze.getTreasuresLeft() + "");
  }

  /**
   * Reset the dashboard
   **/
  public void resetDashboard(){
    for(JLabel label: treasuresCollected){
      label.setIcon(null);
    }
    chipsNum.setText(maze.getTreasuresLeft() + "");
  }

  public String getFile(Key.Colour colour){
    String file = "data/";
    if(colour == Key.Colour.RED){
      file += "redKey.png";
    }
    else if(colour == Key.Colour.BLUE){
      file += "blueKey.png";
    }
    else if(colour == Key.Colour.GREEN){
      file += "greenKey.png";
    }
    return file;
  }

  /**
   * Update the maze in the dashboard to the new maze
   *
   * @param maze
   **/
  public void setMaze(Maze maze){
    this.maze = maze;
    this.resetDashboard();
  }

  /**
   * Set the time left text in the label to the new time left
   *
   * @param time
   **/
  public void setTimer(String time){
    timeNum.setText(time);
  }
}
