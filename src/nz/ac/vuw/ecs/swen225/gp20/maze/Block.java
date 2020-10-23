package nz.ac.vuw.ecs.swen225.gp20.maze;

/**
 * This is a block that can be pushed around by Chap. It can be pushed into lava
 *
 * @author Benjamin Doornbos
 */
public class Block {
    private int col;
    private int row;
    private Tile location;

    /**
     * Creates a new Block.
     *
     * @param col the column to put this block in
     * @param row the row to put this block in
     */
    public Block(int col, int row) {
        this.col = col;
        this.row = row;
    }

    /**
     * Gets the column this block is in
     *
     * @return column this block is in
     */
    public int getCol() {
        return col;
    }

    /**
     * Gets row this block is in
     *
     * @return row this block is in
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets the location of this block
     *
     * @return location of this block
     */
    public Tile getLocation() {
        return location;
    }

    /**
     * Sets the location, row and col fields
     *
     * @param loc new location for the block to reside
     */
    public void setLocation(Tile loc) {
        location = loc;
        col = loc.getCol();
        row = loc.getRow();
    }

    /**
     * Gets the string name of the image file for this class
     *
     * @return the string name of the image file for this class
     */
    public String getImageURL() {
        return "data/block.png";
    }
}
