package nz.ac.vuw.ecs.swen225.gp20.render;

import nz.ac.vuw.ecs.swen225.gp20.maze.Maze;
import nz.ac.vuw.ecs.swen225.gp20.maze.Tile;

import javax.swing.*;
import java.awt.*;

/**
 * Objects of this class provide a simple 2D view of the maze,
 * to be embedded in the application. It is updated when any
 * actor moves. It only uses a certain focus region of the maze,
 * which is determined based on the position of the player.
 *
 * @author Seth Patel
 **/
public class Canvas extends JPanel {
    private static final int VIEW_SIZE = 9;
    private static final int TILE_SIZE = 10;
    private final Maze maze;
    private final JLabel[][] components;

    /**
     * New canvas to render the game.
     *
     * @param maze the maze to be rendered
     **/
    public Canvas(Maze maze) {
        this.maze = maze;
        components = new JLabel[VIEW_SIZE][VIEW_SIZE];
        setPreferredSize(new Dimension(VIEW_SIZE * TILE_SIZE, VIEW_SIZE * TILE_SIZE));
        setLayout(new GridLayout());
        createComponents();
    }

    /**
     * Create the components for the canvas.
     **/
    private void createComponents() {
        removeAll();
        Tile centre = maze.getChap().getLocation();
        for (int row = centre.getRow() - 4, y = 0; row <= centre.getRow() + 4; row++, y++) {
            for (int col = centre.getCol() - 4, x = 0; col <= centre.getCol() + 4; col++, x++) {
                components[x][y] = new JLabel(makeImageIcon(maze.getTiles()[col][row].getImageURl()));
                add(components[x][y]);
            }
        }
    }

    /**
     * Refresh the components for the canvas.
     **/
    public void refreshComponents() {
        Tile centre = maze.getChap().getLocation();
        for (int row = centre.getRow() - 4, y = 0; row <= centre.getRow() + 4; row++, y++) {
            for (int col = centre.getCol() - 4, x = 0; col <= centre.getCol() + 4; col++, x++) {
                ImageIcon icon = makeImageIcon(maze.getTiles()[col][row].getImageURl());
                components[x][y].setIcon(icon);
            }
        }
    }

    /**
     * Scale an image to the required size.
     *
     * @param baseImage the image to scale
     * @return the scaled image
     **/
    private static ImageIcon scaleImage(ImageIcon baseImage) {
        return new ImageIcon(baseImage.getImage().getScaledInstance(TILE_SIZE, TILE_SIZE, Image.SCALE_SMOOTH));
    }

    /**
     * Convert a given image to an ImageIcon.
     *
     * @param filename the image file e.g. "data/image.png"
     * @return the converted image
     **/
    private static ImageIcon makeImageIcon(String filename) {
        java.net.URL imageURL = Canvas.class.getResource(filename);
        ImageIcon icon = null;
        if (imageURL != null) {
            icon = scaleImage(new ImageIcon(imageURL));
        }
        return icon;
    }

}
