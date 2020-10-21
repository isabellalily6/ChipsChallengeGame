package nz.ac.vuw.ecs.swen225.gp20.application;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Create a Dialogue which displays text and then two buttons
 **/
public class Dialogues extends JDialog implements KeyListener {
  // Components of the JDialogue
  private JTextPane text = new JTextPane();
  private JButton button;
  private JButton closeButton;
  private Main main;

  /**
   * Create a new instance of Dialogue
   *
   * @param main
   * @param labelText
   * @param buttonText
   **/
  public Dialogues(Main main, String labelText, String buttonText){
    this.main = main;
    // initialize the settings for the dialogue
    this.setModal(true);
    this.addKeyListener(this);
    this.setFocusable(true);
    this.setLocationRelativeTo(main.getGui());
    this.setSize(500, 300);
    this.setLayout(new GridLayout(3, 1));
    this.setBackground(Color.lightGray);

    // create the components for the dialogue

    // create the field for the text label
    text.setText(labelText);
    text.setEditable(false);
    JScrollPane scrollText = new JScrollPane(text);
    // center the text on the JTextPane
    StyledDocument doc = text.getStyledDocument();
    SimpleAttributeSet centerAlign = new SimpleAttributeSet();
    StyleConstants.setAlignment(centerAlign, StyleConstants.ALIGN_CENTER);
    doc.setParagraphAttributes(0, doc.getLength(), centerAlign, false);

    text.setFont(new Font("Verdana", Font.PLAIN, 15));

    // create the button based on what the user passes in as a parameter
    button = new JButton(buttonText);
    button.setBackground(Color.lightGray);
    button.setFont(new Font("Verdana", Font.PLAIN, 20));
    Border border = BorderFactory.createLineBorder(Color.DARK_GRAY, 3);
    button.setBorder(border);

    // create the button to close the game
    closeButton = new JButton("Exit Game");
    closeButton.setBorder(border);
    closeButton.setBackground(Color.lightGray);
    closeButton.setFont(new Font("Verdana", Font.PLAIN, 20));
    closeButton.addActionListener(method -> {dispose(); main.exitGame();});

    // add the components to the dialogue
    this.add(scrollText);
    this.add(button);
    this.add(closeButton);
    this.toFront();
    this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
  }

  /**
   * Set the action listener for the first button in the dialogue
   *
   * @param actionListener
   **/
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
