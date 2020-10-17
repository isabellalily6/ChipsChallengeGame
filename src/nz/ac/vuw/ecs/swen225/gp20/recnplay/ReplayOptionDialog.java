package nz.ac.vuw.ecs.swen225.gp20.recnplay;

import nz.ac.vuw.ecs.swen225.gp20.application.Main;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

/**
 * Create a Dialogue which displays text and then two buttons
 **/
public class ReplayOptionDialog extends JDialog implements KeyListener {
  // Components of the JDialogue
  private final JLabel label;
  private final ArrayList<JButton> buttons;
  private final JButton closeButton;

  /**
   * Create a new instance of Dialogue
   *
   * @param main
   * @param labelText
   * @param buttonsText text to display on the buttons
   **/
  public ReplayOptionDialog(Main main, String labelText, ArrayList<String> buttonsText) {
    // initialize the settings for the dialogue
    this.setModal(true);
    this.addKeyListener(this);
    this.setFocusable(true);
    this.setLocationRelativeTo(this);
    this.setSize(500, 300);
    this.setLayout(new GridLayout(3, 1));
    this.setBackground(Color.lightGray);

    // create the components for the dialogue
    label = new JLabel(labelText, SwingConstants.CENTER);
    label.setFont(new Font("Verdana", Font.PLAIN, 25));
    buttons = new ArrayList<JButton>();
    for (var message : buttonsText) {

    }
    button = new JButton(buttonText);
    button.setBackground(Color.lightGray);
    button.setFont(new Font("Verdana", Font.PLAIN, 20));
    Border border = BorderFactory.createLineBorder(Color.DARK_GRAY, 3);
    button.setBorder(border);
    closeButton = new JButton("Exit Recording");
    closeButton.setBorder(border);
    closeButton.setBackground(Color.lightGray);
    closeButton.setFont(new Font("Verdana", Font.PLAIN, 20));
    closeButton.addActionListener(method -> {
      dispose();
      main.exitGame();
    });

    // add the components to the dialogue
    this.add(label);
    this.add(button);
    this.add(closeButton);
    this.toFront();
    this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
  }

  /**
   * Get the first button on the JDialogue
   *
   * @return the first button
   **/
  public JButton getButton() {
    return button;
  }

  /**
   * Set the action listener for the first button in the dialogue
   *
   * @param actionListener
   **/
  public void setActionListener(ActionListener actionListener) {
    button.addActionListener(actionListener);
  }

  @Override
  public void keyTyped(KeyEvent keyEvent) {

  }

  @Override
  public void keyPressed(KeyEvent keyEvent) {

  }

  @Override
  public void keyReleased(KeyEvent keyEvent) {
    if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
      //ESC - close the “game is paused” dialog and resume the game
      System.out.println("CLose dialogue and Resume");
      main.playGame();
    }
  }
}
