package nz.ac.vuw.ecs.swen225.gp20.application;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Dialogues extends JDialog implements KeyListener {

  private JLabel label;
  private JButton button;
  private JButton closeButton;
  private Main main;

  public Dialogues(Main main, String labelText, String buttonText){
    this.main = main;
    this.setModal(true);
    this.addKeyListener(this);
    this.setFocusable(true);
    this.setLocationRelativeTo(this);
    this.setSize(500, 300);
    this.setLayout(new GridLayout(3, 1));
    this.setBackground(Color.lightGray);
    label = new JLabel(labelText, SwingConstants.CENTER);
    label.setFont(new Font("Verdana", Font.PLAIN, 25));
    button = new JButton(buttonText);
    button.setBackground(Color.lightGray);
    button.setFont(new Font("Verdana", Font.PLAIN, 20));
    Border border = BorderFactory.createLineBorder(Color.DARK_GRAY, 3);
    button.setBorder(border);
    closeButton = new JButton("Exit Game");
    closeButton.setBorder(border);
    closeButton.setBackground(Color.lightGray);
    closeButton.setFont(new Font("Verdana", Font.PLAIN, 20));
    closeButton.addActionListener(method -> {dispose(); main.exitGame();});
    this.add(label);
    this.add(button);
    this.add(closeButton);
    this.toFront();
    this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
  }

  public JLabel getLabel() {
    return label;
  }

  public JButton getButton() {
    return button;
  }

  public void setActionListener(ActionListener actionListener){
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
   if(keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE){
      //ESC - close the “game is paused” dialog and resume the game
      System.out.println("CLose dialogue and Resume");
      main.playGame();
    }
  }
}
