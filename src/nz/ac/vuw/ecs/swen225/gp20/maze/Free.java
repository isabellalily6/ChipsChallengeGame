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
     * @param col col in the maze array
     * @param row row in the maze array
     */
    public Free(int col, int row) {
        super("data/free.png", col, row, true, false);
    }
}
