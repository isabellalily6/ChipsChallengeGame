package nz.ac.vuw.ecs.swen225.gp20.application;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class Dashboard extends JPanel {

  // initialise the components tobe put on the Dashboard
  private JLabel level;
  private JLabel levelNum;
  private JLabel time;
  private JLabel timeNum;
  private JLabel chips;
  private JLabel chipsNum;
  private JLabel chipsCollected;

  /**
   * Create a new instance of dashboard and set the default values
   **/
  public Dashboard(){
    // Set up the components of the dashboard
    setLayout(new GridLayout(7, 1));
    //setBackground(Color.decode("#0C9036"));
    setBackground(Color.lightGray);
    setFocusable(false);
    addComponents();
    Border border = BorderFactory.createLineBorder(Color.DARK_GRAY, 3);
    setBorder(border);
  }

  /**
   * Create the dashboard components
   **/
  public void createComponents(){
    // Set the boarder of some of the JLabels
    Border border = BorderFactory.createLineBorder(Color.DARK_GRAY, 5);

    // create the level label
    level = new JLabel("LEVEL", SwingConstants.CENTER);
    level.setFont(new Font("Verdana", Font.PLAIN, 18));

    // create the level number label
    levelNum = new JLabel("0", SwingConstants.CENTER);
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
    chips = new JLabel("CHIPS LEFT", SwingConstants.CENTER);
    chips.setFont(new Font("Verdana", Font.PLAIN, 18));

    // create the chips number label
    chipsNum = new JLabel("", SwingConstants.CENTER);
    chipsNum.setFont(new Font("Verdana", Font.PLAIN, 18));
    chipsNum.setBorder(border);

    // create the chips collected label
    chipsCollected = new JLabel("", SwingConstants.CENTER);
    chipsCollected.setFont(new Font("Verdana", Font.PLAIN, 18));
  }

  /**
   * Add the components to the dashboard
   *
   **/
  public void addComponents(){
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
