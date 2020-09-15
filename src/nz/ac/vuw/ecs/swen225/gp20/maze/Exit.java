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
     * @param row row in the maze array
     * @param col col in the maze array
     */
    public Exit(int row, int col) {
        super("data/exit.png", row, col, true);
    }
}
