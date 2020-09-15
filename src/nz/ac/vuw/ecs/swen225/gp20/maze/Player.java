package nz.ac.vuw.ecs.swen225.gp20.maze;

/**
 * From Handout:
 * The hero of the game. Chap can be moved by key strokes (up-right-down-left), his movement is restricted by the
 * nature of the tiles (for instance, he cannot move into walls). Note that the icon may depend on the current
 * direction of movement.
 *
 * @author Benjamin Doornbos
 */
public class Player extends Actor {
    /**
     * Creates a new Actor
     *
     * @param location the tile this actor is standing on
     */
    public Player(Tile location) {
        super(location);
    }


}
