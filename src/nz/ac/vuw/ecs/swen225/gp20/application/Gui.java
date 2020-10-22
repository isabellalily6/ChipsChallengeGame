package nz.ac.vuw.ecs.swen225.gp20.application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.InvocationTargetException;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import nz.ac.vuw.ecs.swen225.gp20.commons.Direction;
import nz.ac.vuw.ecs.swen225.gp20.commons.Sound;
import nz.ac.vuw.ecs.swen225.gp20.maze.InfoField;
import nz.ac.vuw.ecs.swen225.gp20.maze.Maze;
import nz.ac.vuw.ecs.swen225.gp20.recnplay.RecordAndPlay;
import nz.ac.vuw.ecs.swen225.gp20.recnplay.RecordedMove;
import nz.ac.vuw.ecs.swen225.gp20.render.Canvas;
import nz.ac.vuw.ecs.swen225.gp20.render.SoundEffect;

/**
 * Create a Gui which displays the game on the screen.
 **/
public class Gui extends JFrame implements KeyListener {
  // initialize the screen sizes
  private int screenwidth = 900;
  private int screenHeight = 650;

  // initialize GUI fields
  private final JPanel mainPanel = new JPanel();
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
   * @param main main
   * @param maze maze
   **/
  public Gui(Main main, Maze maze) {
    // initialise variables
    this.main = main;
    this.maze = maze;
    this.canvas = new Canvas(maze);
    this.dashboard = new Dashboard(maze);

    // set the frame requirements
    addKeyListener(this);
    setSize(screenwidth, screenHeight);
    setResizable(true);
    setVisible(true);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setFocusable(true);
    setFocusTraversalKeysEnabled(false);
    setResizable(false);

    // create the dialogues
    pausedDialogue = new Dialogues(main, "GAME IS PAUSED", "RESUME");
    gameWon = new Dialogues(main, "You have won the level!!!", "NEXT GAME");
    gameLost = new Dialogues(main, "You have lost the game", "RETRY");
    // set the actions for the dialogues
    setButtonActionListeners();
  }

  /**
   * Set the action listeners for the buttons on the default JDialogues.
   **/
  public void setButtonActionListeners() {
    // add action listeners to the button in the paused dialogue
    pausedDialogue.setActionListener(method -> main.playGame());
    // add action listeners to the button in the game won dialogue
    gameWon.setActionListener(method -> {
      // if the player has won the game and they aren't on the last level of the game
      if (maze.getState().equals(Maze.LevelState.WON) && main.getLevel() != main.getLastLevel()) {
        // increase the level by one and start a new game at the new level
        main.setLevel(main.getLevel() + 1);
        main.startGame(main.getLevel());
        RecordAndPlay.recordLevelChange();
      } else {
        // otherwise start a new game at level 1
        main.startGame(1);
      }
      gameWon.dispose();
    });
    // add action listeners to the button in the game lost dialogue
    gameLost.setActionListener(method -> {
      // start a new game at the current level
      main.startGame(main.getLevel());
      gameLost.dispose();
    });
  }

  /**
   * Get the Canvas.
   *
   * @return the canvas
   **/
  public Canvas getCanvas() {
    return canvas;
  }

  /**
   * Create the components for the gui and adds them to the screen in the correct locations.
   **/
  public void setUpGui() {
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

    // Create a panel to place the canvas on to keep the correct size of the canvas
    JPanel canvasPanel = new JPanel();
    Border borderGray = BorderFactory.createLineBorder(Color.DARK_GRAY, 3);
    canvas.setBorder(borderGray);
    canvasPanel.add(canvas);

    // add the components to the main panel in the correct locations
    mainPanel.add(canvasPanel, BorderLayout.CENTER);
    mainPanel.add(dashboard, BorderLayout.EAST);

    // add the components to the screen and redraw the screen
    add(mainPanel);
    validate();
    repaint();
  }

  /**
   * Call the method in the dashboard, to change the time displayed to the new time left..
   *
   * @param timeLeft time left
   **/
  public void setTimer(int timeLeft) {
    String time = Integer.toString(timeLeft);
    dashboard.setTimer(time);
  }

  /**
   * Set the maze for each component in the gui.
   *
   * @param maze - the maze to update the current maze to
   **/
  public void setMaze(Maze maze) {
    this.maze = maze;
    dashboard.setMaze(maze);
    canvas.setMaze(maze);
  }

  /**
   * Displays the paused dialogue on the screen.
   **/
  public void displayPausedDialogue() {
    pausedDialogue.setVisible(true);
  }

  /**
   * Hides the paused dialogue.
   **/
  public void hidePausedDialogue() {
    pausedDialogue.setVisible(false);
  }

  /**
   * Get the game won dialogue.
   *
   * @return the game won dialogue
   **/
  public Dialogues getGameWon() {
    return gameWon;
  }

  /**
   * Get the game lost dialogue.
   *
   * @return the game lost dialogue
   **/
  public Dialogues getGameLost() {
    return gameLost;
  }

  /**
   * Get the Maze.
   *
   * @return the maze
   **/
  public Maze getMaze() {
    return maze;
  }

  /**
   * Get the dashboard.
   *
   * @return the dashboard
   **/
  public Dashboard getDashboard() {
    return dashboard;
  }

  /**
   * Set the direction of the chap.
   *
   * @param direction - the direction to set the chap to
   **/
  public void setChapDirection(Direction direction) {
    maze.getChap().setDir(direction);
  }

  /**
   * Move the chap.
   *
   * @param direction - the direction to move the chap in
   **/
  public void moveChap(Direction direction) {
    maze.moveChap(direction);
  }

  /**
   * Play the sound passed in as a parameter.
   *
   * @param sound - the sound to play
   **/
  public void playSound(Sound sound) {
    SoundEffect.play(sound);
  }

  /**
   * Update the GUI.
   *
   * @param useThread - true if a thread is being used otherwise false
   **/
  public void updateGui(Boolean useThread) {
    if (useThread) {
      try {
        // thread safe repainting
        SwingUtilities.invokeAndWait((canvas::refreshComponents));
        SwingUtilities.invokeAndWait((canvas::repaint));
        SwingUtilities.invokeAndWait((this::repaint));
      } catch (InterruptedException | InvocationTargetException e) {
        // redo them without thread safety
        canvas.refreshComponents();
        canvas.repaint();
        repaint();
      }
    } else {
      canvas.refreshComponents();
      canvas.repaint();
      repaint();
    }
    dashboard.updateDashboard();
  }

  @Override
  public void keyTyped(KeyEvent keyEvent) {
  }

  @Override
  public void keyPressed(KeyEvent keyEvent) {
    // if the record and play isn't currently player
    if (!RecordAndPlay.getPlayingRecording()) {
      if (keyEvent.isControlDown() && keyEvent.getKeyCode() == KeyEvent.VK_X) {
        //CTRL-X  - exit the game, the current game state will be lost, the next time the
        // game is started, it will resume from the last unfinished level
        main.saveFile(true, true);
        main.exitGame();
      } else if (keyEvent.isControlDown() && keyEvent.getKeyCode() == KeyEvent.VK_S) {
        //CTRL-S  - exit the game, saves the game state, game will resume next time the
        // application will be started
        main.saveFile(false, true);
        main.exitGame();
      } else if (keyEvent.isControlDown() && keyEvent.getKeyCode() == KeyEvent.VK_R) {
        //CTRL-R  - resume a saved game
        main.loadFile(false);
      } else if (keyEvent.isControlDown() && keyEvent.getKeyCode() == KeyEvent.VK_P) {
        //CTRL-P  - start a new game at the last unfinished level
        main.startGame(main.getLevel());
      } else if (keyEvent.isControlDown() && keyEvent.getKeyCode() == KeyEvent.VK_1) {
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
    }
    updateGui(false);
  }

  @Override
  public void keyReleased(KeyEvent keyEvent) {
    Direction direction = null;
    Sound sound = null;
    if (!RecordAndPlay.getPlayingRecording()) {
      if (keyEvent.getKeyCode() == KeyEvent.VK_UP) {
        // Set the direction to up
        direction = Direction.UP;
      } else if (keyEvent.getKeyCode() == KeyEvent.VK_DOWN) {
        // Set the direction to down
        direction = Direction.DOWN;
      } else if (keyEvent.getKeyCode() == KeyEvent.VK_RIGHT) {
        // Set the direction to right
        direction = Direction.RIGHT;
      } else if (keyEvent.getKeyCode() == KeyEvent.VK_LEFT) {
        // Set the direction to left
        direction = Direction.LEFT;
      }
    }
    if (direction != null) {
      // move the chap in the correct direction and get the sound to play
      sound = maze.moveChap(direction);
      RecordAndPlay.addMove(new RecordedMove(direction, main.getTimeLeft(),
              RecordAndPlay.recordedMovesSize(), maze.getLevel()));
    }
    if (sound != null) {
      // animate the movement of the player
      canvas.movePlayer(direction);
      // play the sound
      SoundEffect.play(sound);
    }

    updateGui(false);

    // if the chap is on an info field, create a dialogue to display the info field information.
    if (maze.getChap().getLocation() instanceof InfoField) {
      InfoField location = (InfoField) maze.getChap().getLocation();
      String info = location.getInfo();
      Dialogues infoPanel = new Dialogues(main, info, "Close");
      // Close the dialogue once the close button is pressed
      infoPanel.setActionListener(method -> infoPanel.dispose());
      infoPanel.setVisible(true);
    }
  }
}