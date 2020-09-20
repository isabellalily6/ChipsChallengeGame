package nz.ac.vuw.ecs.swen225.gp20.application;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class Dashboard extends JPanel {

  private JLabel timeNum = new JLabel("");

  public Dashboard(){
    // Set up the components of the dashboard
    setLayout(new GridLayout(7, 1));
    setBackground(Color.decode("#0C9036"));
    setFocusable(false);
    addComponents();
  }

  /**
   * Create and add the components to the dashboard
   *
   **/
  public void addComponents(){
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


    add(level);
    add(levelNum);
    add(time);
    add(timeNum);
    add(chips);
    add(chipsNum);
    add(chipsCollected);
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
