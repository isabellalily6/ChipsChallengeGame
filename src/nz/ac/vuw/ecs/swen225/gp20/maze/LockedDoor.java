package nz.ac.vuw.ecs.swen225.gp20.maze;

/**
 * From handout:
 * Chap can only move onto those tiles if they have the key with the matching colour -- this unlocks the door.
 * After unlocking the door, the locked door turns into a free tile, and Chap keeps the key.
 *
 * @author Benjamin Doornbos 300487256
 */
public class LockedDoor extends Tile {
    private final Key.Colour lockColour;

    /**
     * Creates new locked door
     *
     * @param col        col in the maze array
     * @param row        row in the maze array
     * @param lockColour the colour of key that unlocks this door
     */
    public LockedDoor(int col, int row, Key.Colour lockColour) {
        super("data/lockedDoor.png", col, row, false, true);
        this.lockColour = lockColour;
    }

    /**
     * @return the colour of key that unlocks this door
     */
    public Key.Colour getLockColour() {
        return lockColour;
    }

    @Override
    public String toString() {
        return "{\"type\": \""+this.getClass().getSimpleName()+"\", \"color\": \""+lockColour.getName().toLowerCase()+"\"}";
    }
}
