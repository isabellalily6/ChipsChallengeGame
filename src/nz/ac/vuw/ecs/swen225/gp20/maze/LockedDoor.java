package nz.ac.vuw.ecs.swen225.gp20.maze;

/**
 * From handout:
 * Chap can only move onto those tiles if they have the key with the matching colour -- this unlocks the door.
 * After unlocking the door, the locked door turns into a free tile, and Chap keeps the key.
 *
 * @author Benjamin Doornbos 300487256
 */
public class LockedDoor extends Tile {

    /**
     * Creates new locked door
     *
     * @param row row in the maze array
     * @param col col in the maze array
     */
    public LockedDoor(int row, int col) {
        super("data/lockedDoor.png", row, col, false);
    }
}
