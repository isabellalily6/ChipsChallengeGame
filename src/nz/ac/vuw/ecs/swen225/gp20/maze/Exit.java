package nz.ac.vuw.ecs.swen225.gp20.maze;

/**
 * From handout:
 * Once Chap reaches this tile, the game level is finished.
 *
 * @author Benjamin Doornbos 300487256
 */
public class Exit extends Tile {
    /**
     * Creates new Exit tile
     *
     * @param col col in the maze array
     * @param row row in the maze array
     */
    public Exit(int col, int row) {
        super("data/exit.png", col, row, true, false);
    }
}
