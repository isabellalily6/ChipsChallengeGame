package nz.ac.vuw.ecs.swen225.gp20.maze;

/**
 * Tile class declares all of the required fields and methods for any tile on the maze.
 * @author Benjamin Doornbos 300487256
 */
public abstract class Tile {
    private String imageURl;

    private int row;
    private int col;


    public String getImageURl() {
        return imageURl;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}
