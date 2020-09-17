package nz.ac.vuw.ecs.swen225.gp20.maze;

/**
 * Tile class declares all of the required fields and methods for any tile on the maze.
 *
 * @author Benjamin Doornbos 300487256
 */
public abstract class Tile {
    private final String imageURl;
    private final int col;
    private final int row;
    private final boolean accessible;
    private final boolean freeOnEntry;
    private boolean occupied;

    /**
     * Initializes all required fields
     *
     * @param imageURl    the URL for the image corresponding to a tile
     * @param col         col in the maze array
     * @param row         row in the maze array
     * @param accessible  whether an actor can walk onto this tile
     * @param freeOnEntry whether this tile needs to become free when a player enters it
     */
    public Tile(String imageURl, int col, int row, boolean accessible, boolean freeOnEntry) {
        this.imageURl = imageURl;
        this.col = col;
        this.row = row;
        this.accessible = accessible;
        this.freeOnEntry = freeOnEntry;
    }

    /**
     * Copy Constructor.
     *
     * @param toCopy Tile to make a copy of
     */
    public Tile(Tile toCopy) {
        this.imageURl = toCopy.imageURl;
        this.col = toCopy.col;
        this.row = toCopy.row;
        this.accessible = toCopy.accessible;
        this.freeOnEntry = toCopy.freeOnEntry;
        this.occupied = toCopy.occupied;
    }

    /**
     * This will perform actions required once an actor enters this tile (to be overridden by classes like Treasure)
     * TODO: this method may be redundant
     *
     * @param actor the actor entering this tile
     */
    public void onEntry(Actor actor) {
        setOccupied(true);
    }

    /**
     * This will perform actions required once an actor exits this tile
     */
    public void onExit() {
        setOccupied(false);
    }

    /**
     * @return Whether this tile is occupied
     */
    public boolean isOccupied() {
        return occupied;
    }

    /**
     * Change the value of the occupied boolean
     *
     * @param occupied whether this tile is occupied
     */
    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    /**
     * @return Whether this tile needs to be changed to a free tile when a player enters it
     */
    public boolean isFreeOnEntry() {
        return freeOnEntry;
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
     * @return col in the maze this tile is in
     */
    public int getCol() {
        return col;
    }

    /**
     * @return row in the maze this tile is in
     */
    public int getRow() {
        return row;
    }
}
