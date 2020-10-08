package nz.ac.vuw.ecs.swen225.gp20.maze;

/**
 * From handout:
 * Behaves like a wall time for Chap as long as there are still uncollected treasures. Once the treasure chest is full
 * (all treasures have been collected), Chap can pass through the lock.
 *
 * @author Benjamin Doornbos 300487256
 */
public class ExitLock extends Tile {
    /**
     * Creates new ExitLock tile
     *
     * @param col col in the maze array
     * @param row row in the maze array
     */
    public ExitLock(int col, int row) {
        super("data/exitLock.png", col, row, false, false);
    }
}
