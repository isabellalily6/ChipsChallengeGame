package nz.ac.vuw.ecs.swen225.gp20.maze;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * From Handout:
 * The hero of the game. Chap can be moved by key strokes (up-right-down-left), his movement is restricted by the
 * nature of the tiles (for instance, he cannot move into walls). Note that the icon may depend on the current
 * direction of movement.
 *
 * @author Benjamin Doornbos
 */
public class Player extends Actor {
    //colours represent the actual keys not the key tiles
    private final Set<Key.Colour> backpack;
    private int treasuresCollected;

    /**
     * Creates a new Actor
     *
     * @param location the tile this actor is standing on
     */
    public Player(Tile location) {
        super(location, "player");
        backpack = new HashSet<>();
        treasuresCollected = 0;
    }

    /**
     * Increments the amount of treasures the player has collected
     */
    public void incrementTreasures() {
        treasuresCollected++;
    }

    /**
     * Gets how many treasures chap has collected
     *
     * @return how many treasures chap has collected
     */
    public int getTreasuresCollected() {
        return treasuresCollected;
    }

    /**
     * Adds the colour corresponding to a key just picked up to Chap's backpack
     *
     * @param col colour of the key just picked up
     */
    public void addToBackPack(Key.Colour col) {
        backpack.add(col);
    }

    /**
     * Returns an unmodifiable version of the backpack set (all of the keys chap has)
     *
     * @return an unmodifiable version of the backpack set (all of the keys chap has)
     */
    public Set<Key.Colour> getBackpack() {
        return Collections.unmodifiableSet(backpack);
    }

    /**
     * Check if the backpack contains the given col
     *
     * @param col col to check
     * @return whether the backpack contains this colour
     */
    public boolean backpackContains(Key.Colour col) {
        return backpack.contains(col);
    }
}
