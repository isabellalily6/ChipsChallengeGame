package nz.ac.vuw.ecs.swen225.gp20.application;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class GUI extends JFrame {
  // initialize screen sizes
  private int screenWidth = 900;
  private int screenHeight = 500;

  // initialize GUI fields
  JPanel mainPanel = new JPanel();
  GridBagConstraints gbc = new GridBagConstraints();

  // initialize application
  Main main;

  public GUI(Main main){
    this.main = main;

    setSize(screenWidth, screenHeight);
    setResizable(true);
    setVisible(true);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(EXIT_ON_CLOSE);

  }

  /**
   * Create the components for the gui and add them to the screen in the correct locations.
   **/
  public void setUpGui(){
    // Create the Maze
    JPanel maze = new JPanel();
    maze.setBackground(Color.blue);
    // Create the Dashboard
    JPanel dashboard = new JPanel();
    dashboard.setLayout(new GridBagLayout());
    dashboard.setBackground(Color.decode("#0C9036"));

    // Add components to the dashboard
    Border border = BorderFactory.createLineBorder(Color.BLUE, 5);

    JLabel level = new JLabel("LEVEL");
    level.setFont(new Font("Verdana", Font.PLAIN, 18));
    JLabel levelNum = new JLabel("0");
    levelNum.setFont(new Font("Verdana", Font.PLAIN, 18));
    levelNum.setBorder(border);
    JLabel time = new JLabel("TIME");
    time.setFont(new Font("Verdana", Font.PLAIN, 18));
    JLabel timeNum = new JLabel("");
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
    mainPanel.add(new Canvas(), gbc);

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

}
