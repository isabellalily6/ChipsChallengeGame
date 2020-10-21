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

  /**
   * Create a new instance of Dialogue
   *
   * @param main       the Main class for this dialog to refer too
   * @param sliderText label for this dialog
   * @param buttons    buttons to display in this dialog
   * @param slider     slider for choosing speed, null if you dont want this option
   */
  public ReplayOptionDialog(Main main, String sliderText, ArrayList<JButton> buttons, JSlider slider) {
    // initialize the settings for the dialogue
    this.setModal(true);
    this.setFocusable(true);
    this.setLocationRelativeTo(this);
    this.setSize(500, 300);
    this.setLayout(new GridLayout(5, 1));
    this.setBackground(Color.lightGray);

    Border border = BorderFactory.createLineBorder(Color.DARK_GRAY, 3);

    if (slider != null) {
      this.setLayout(new GridLayout(buttons.size() + 3, 1));
      if (sliderText != null && !sliderText.isBlank()) {
        // Components of the JDialogue
        JLabel label = new JLabel(sliderText, SwingConstants.CENTER);
        label.setFont(new Font("Verdana", Font.PLAIN, 25));
        label.setBorder(border);
        this.add(label);
      }
      this.add(slider, BorderLayout.NORTH);
    } else {
      this.setLayout(new GridLayout(buttons.size() + 1, 1));
    }

    for (var button : buttons) {
      this.add(button);
    }

    JButton closeButton = new JButton("Exit Recording");
    closeButton.setBorder(border);
    closeButton.setBackground(Color.lightGray);
    closeButton.setFont(new Font("Verdana", Font.PLAIN, 20));
    closeButton.addActionListener(method -> {
      RecordAndPlay.endPlayingRecording();
      main.getTimer().cancel();
      main.getTimer().purge();
      main.startGame(1);
      main.setLevel(1);
      main.playGame();
      dispose();
    });

    // add the components to the dialogue
    this.add(closeButton);
    this.toFront();
    this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
  }
}
