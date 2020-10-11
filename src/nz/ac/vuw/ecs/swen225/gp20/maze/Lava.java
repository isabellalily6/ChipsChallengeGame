package nz.ac.vuw.ecs.swen225.gp20.maze;

/**
 * This tile will kill Chap if he walks on it. Blocks can also be pushed into the lava
 *
 * @author Benjamin Doornbos
 */
public class Lava extends Tile {
    /**
     * Creates a new Lava tile.
     *
     * @param col the column to put this tile in
     * @param row the row to put this tile in
     */
    public Lava(int col, int row) {
        super("data/lava.png", col, row, true, false);
    }


}
