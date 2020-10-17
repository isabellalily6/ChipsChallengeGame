package nz.ac.vuw.ecs.swen225.gp20.recnplay;

import nz.ac.vuw.ecs.swen225.gp20.application.Main;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;

/**
 * Create a Dialogue which displays text and then two buttons
 **/
public class ReplayOptionDialog extends JDialog {
  // Components of the JDialogue
  private final JLabel label;
  private final ArrayList<JButton> buttons;
  private final JButton closeButton;

  /**
   * Create a new instance of Dialogue
   *
   * @param main      the Main class for this dialog to refer too
   * @param labelText label for this dialog
   * @param buttons   buttons to display in this dialog
   **/
  public ReplayOptionDialog(Main main, String labelText, ArrayList<JButton> buttons) {
    // initialize the settings for the dialogue
    this.setModal(true);
    this.setFocusable(true);
    this.setLocationRelativeTo(this);
    this.setSize(500, 300);
    this.setLayout(new GridLayout(3, 1));
    this.setBackground(Color.lightGray);
    this.buttons = buttons;

    // create the components for the dialogue
    label = new JLabel(labelText, SwingConstants.CENTER);
    label.setFont(new Font("Verdana", Font.PLAIN, 25));
    buttons = new ArrayList<>();
    Border border = BorderFactory.createLineBorder(Color.DARK_GRAY, 3);
    for (var button : buttons) {
//      var button = new JButton(message);
//      button.setBackground(Color.lightGray);
//      button.setFont(new Font("Verdana", Font.PLAIN, 20));
//      button.setBorder(border);
      this.add(button);
    }

    closeButton = new JButton("Exit Recording");
    closeButton.setBorder(border);
    closeButton.setBackground(Color.lightGray);
    closeButton.setFont(new Font("Verdana", Font.PLAIN, 20));
    closeButton.addActionListener(method -> {
      dispose();
      main.startGame(1);
    });

    // add the components to the dialogue
    this.add(label);
    this.add(closeButton);
    this.toFront();
    this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
  }
}
