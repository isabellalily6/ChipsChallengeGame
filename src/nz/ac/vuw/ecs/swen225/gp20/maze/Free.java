package nz.ac.vuw.ecs.swen225.gp20.maze;

/**
 * From handout:
 * Actors can freely move onto those tiles.
 *
 * @author Benjamin Doornbos 300487256
 */
public class Free extends Tile {
    /**
     * Creates new free tile
     *
     * @param row row in the maze array
     * @param col col in the maze array
     */
    public Free(int row, int col) {
        super("data/free.png", row, col, true);
    }
}
