package nz.ac.vuw.ecs.swen225.gp20.render;

import nz.ac.vuw.ecs.swen225.gp20.maze.Tile;

import javax.swing.*;
import java.awt.*;

public class Canvas extends JPanel {

    private static final int VIEW_SIZE = 9;
    private static final int CELL_SIZE = 10;
    private final Tile[][] tiles;
    private final JLabel[][] components;

    public Canvas(Tile[][] tiles) {
        this.tiles = tiles;
        components = new JLabel[VIEW_SIZE][VIEW_SIZE];
        setPreferredSize(new Dimension(VIEW_SIZE * CELL_SIZE, VIEW_SIZE * CELL_SIZE));
        setLayout(new GridBagLayout());
    }

    private void createComponents() {
        for (int y = 0; y < VIEW_SIZE; y++) {
            for (int x = 0; x < VIEW_SIZE; x++) {
                components[x][y] = new JLabel(tiles[x][y].getImageURL());
            }
        }
    }

    private static ImageIcon makeImageIcon(String filename) {
        java.net.URL imageURL = Canvas.class.getResource(filename);
        return imageURL != null ? new ImageIcon(imageURL) : null;
    }

}
