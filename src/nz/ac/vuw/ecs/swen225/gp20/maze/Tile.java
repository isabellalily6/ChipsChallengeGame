package nz.ac.vuw.ecs.swen225.gp20.maze;

/**
 * Tile class declares all of the required fields and methods for any tile on the maze.
 *
 * @author Benjamin Doornbos 300487256
 */
public abstract class Tile {
    private final String imageURl;
    private final int row;
    private final int col;
    private final boolean accessible;

    /**
     * Initializes all required fields
     *
     * @param imageURl   the URL for the image corresponding to a tile
     * @param row        row in the maze array
     * @param col        col in the maze array
     * @param accessible whether an actor can walk onto this tile
     */
    public Tile(String imageURl, int row, int col, boolean accessible) {
        this.imageURl = imageURl;
        this.row = row;
        this.col = col;
        this.accessible = accessible;
    }

    /**
     * @return whether an actor can step on this tile
     */
    public boolean isAccessible() {
        return accessible;
    }

    /**
     * @return the string that is the URL of the image representation for this object
     */
    public String getImageURl() {
        return imageURl;
    }

    /**
     * @return row in the maze this tile is in
     */
    public int getRow() {
        return row;
    }

    /**
     * @return col in the maze this tile is in
     */
    public int getCol() {
        return col;
    }
}
