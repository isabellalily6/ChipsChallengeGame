package nz.ac.vuw.ecs.swen225.gp20.render;

import nz.ac.vuw.ecs.swen225.gp20.maze.Player;
import nz.ac.vuw.ecs.swen225.gp20.maze.Tile;

import javax.swing.*;
import java.awt.*;

public class Canvas extends JPanel {

    private static final int VIEW_SIZE = 9;
    private static final int TILE_SIZE = 10;
    private final Tile[][] tiles;
    private final JLabel[][] components; // the 9x9 focus area
    private final Player player;

    public Canvas(Tile[][] tiles, Player player) {
        this.tiles = tiles;
        this.player = player;
        components = new JLabel[VIEW_SIZE][VIEW_SIZE];
        setPreferredSize(new Dimension(VIEW_SIZE * TILE_SIZE, VIEW_SIZE * TILE_SIZE));
        setLayout(new GridBagLayout());
    }

    private void createComponents() {
        // Tile containing the player should be in row 5, column 5, the centre of the 9x9 grid


    }

    private static ImageIcon makeImageIcon(String filename) {
        java.net.URL imageURL = Canvas.class.getResource(filename);
        return imageURL != null ? new ImageIcon(imageURL) : null;
    }

}
