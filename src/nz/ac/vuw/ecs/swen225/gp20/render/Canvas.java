package nz.ac.vuw.ecs.swen225.gp20.render;

import javax.swing.*;
import java.awt.*;

public class Canvas extends JPanel {

    private static final int VIEW_SIZE = 9;
    private static final int CELL_SIZE = 10;
    private JLabel[][] components;

    public Canvas() {
        setPreferredSize(new Dimension(VIEW_SIZE * CELL_SIZE, VIEW_SIZE * CELL_SIZE));
    }

}
