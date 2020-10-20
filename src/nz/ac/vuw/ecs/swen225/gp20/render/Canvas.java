package nz.ac.vuw.ecs.swen225.gp20.render;

import nz.ac.vuw.ecs.swen225.gp20.maze.Block;
import nz.ac.vuw.ecs.swen225.gp20.maze.Cobra;
import nz.ac.vuw.ecs.swen225.gp20.maze.Maze;
import nz.ac.vuw.ecs.swen225.gp20.maze.Tile;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Objects of this class provide a simple 2D view of the maze
 * to be embedded in the application. It is updated when any
 * actor moves. It only uses a certain focus region of the maze
 * which is determined based on the position of the player.
 *
 * @author Seth Patel
 **/
public class Canvas extends JPanel {

    private static final int VIEW_SIZE = 9;
    private static final int VIEW_SIDE = (VIEW_SIZE - 1) / 2;
    private static final int TILE_SIZE = 50;
    private Maze maze;
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
        setMinimumSize(new Dimension(VIEW_SIZE * TILE_SIZE, VIEW_SIZE * TILE_SIZE));
        setMaximumSize(new Dimension(VIEW_SIZE * TILE_SIZE, VIEW_SIZE * TILE_SIZE));
        setLayout(new GridLayout(VIEW_SIZE, VIEW_SIZE, 0, 0));
        createComponents();
    }

    /**
     * Create the components for the canvas.
     **/
    private void createComponents() {
        removeAll();
        Tile centre = maze.getChap().getLocation();
        for (int row = centre.getRow() - VIEW_SIDE, y = 0; row <= centre.getRow() + VIEW_SIDE; row++, y++) {
            for (int col = centre.getCol() - VIEW_SIDE, x = 0; col <= centre.getCol() + VIEW_SIDE; col++, x++) {
                components[x][y] = new JLabel(makeImageIcon(maze.getTiles()[col][row].getImageURl()));
                add(components[x][y]);
            }
        }
        components[VIEW_SIDE][VIEW_SIDE].setIcon(makeImageIcon(maze.getChap().getImageURl()));
    }

    /**
     * Refresh the components for the canvas.
     **/
    public void refreshComponents() {
        if (maze == null) return;
        Tile centre = maze.getChap().getLocation();
        for (int row = centre.getRow() - VIEW_SIDE, y = 0; row <= centre.getRow() + VIEW_SIDE; row++, y++) {
            for (int col = centre.getCol() - VIEW_SIDE, x = 0; col <= centre.getCol() + VIEW_SIDE; col++, x++) {
                if (row < 0 || row > maze.getTiles()[0].length - 1 || col < 0 || col > maze.getTiles().length - 1) {
                    components[x][y].setIcon(makeImageIcon("data/free.png"));
                } else {
                    int finalCol = col;
                    int finalRow = row;
                    List<Cobra> cobras = maze.getCobras().stream().filter(c -> c.getLocation().equals(maze.getTiles()[finalCol][finalRow])).collect(Collectors.toList());
                    if (maze != null && maze.getBlocks() != null && !maze.getBlocks().isEmpty()) {
                        if(maze.getBlocks().stream().anyMatch(b -> b.getLocation().equals(maze.getTiles()[finalCol][finalRow]))) {
                            components[x][y].setIcon(makeImageIcon("data/block.png"));
                        } else if(!cobras.isEmpty()) {
                            components[x][y].setIcon(makeImageIcon(cobras.get(0).getImageURl()));
                        } else {
                            ImageIcon icon = makeImageIcon(maze.getTiles()[col][row].getImageURl());
                            components[x][y].setIcon(icon);
                        }
                    } else if(maze != null) {
                        ImageIcon icon = makeImageIcon(maze.getTiles()[col][row].getImageURl());
                        components[x][y].setIcon(icon);
                    }
                }
            }
        }
        components[VIEW_SIDE][VIEW_SIDE].setIcon(makeImageIcon(maze.getChap().getImageURl()));
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
    public static ImageIcon makeImageIcon(String filename) {
        return scaleImage(new ImageIcon(filename));
    }

    /**
     * Set the value of the maze.
     *
     * @param maze the value to set
     **/
    public void setMaze(Maze maze) {
        this.maze = maze;
    }
}
