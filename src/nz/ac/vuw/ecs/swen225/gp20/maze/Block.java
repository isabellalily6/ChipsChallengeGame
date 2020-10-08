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
     * @return column this block is in
     */
    public int getCol() {
        return col;
    }

    /**
     * @return row this block is in
     */
    public int getRow() {
        return row;
    }

    /**
     * @return location of this block
     */
    public Tile getLocation() {
        return location;
    }

    /**
     * @param loc new location for the block to reside
     */
    public void setLocation(Tile loc) {
        location = loc;
        col = loc.getCol();
        row = loc.getRow();
    }
}
