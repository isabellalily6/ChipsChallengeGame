package nz.ac.vuw.ecs.swen225.gp20.maze;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * This class will provide the underlying functionality for all actors (chap and his enemies)
 *
 * @author Benjamin Doornbos
 */
public abstract class Actor {
    private Tile location;

    /**
     * Creates a new Actor
     *
     * @param location the tile this actor is standing on
     */
    public Actor(Tile location) {
        //precondition - actor is not standing somewhere they're not supposed to be
        checkArgument(location.isAccessible());
        this.location = location;
    }


    /**
     * @return the tile this actor is standing on
     */
    public Tile getLocation() {
        return location;
    }

    /**
     * @param location new location for the actor to occupy
     */
    public void setLocation(Tile location) {
        this.location = location;
    }


}
