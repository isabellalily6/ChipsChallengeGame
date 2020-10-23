package nz.ac.vuw.ecs.swen225.gp20.maze;

import nz.ac.vuw.ecs.swen225.gp20.commons.Direction;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * This class will provide the underlying functionality for all actors (chap and his enemies)
 *
 * @author Benjamin Doornbos
 */
public abstract class Actor {
    private final String name;
    private Tile location;
    private Direction dir;

    /**
     * Creates a new Actor
     *
     * @param location the tile this actor is standing on
     * @param name     the name of this type of actor (player, cobra, etc.)
     */
    public Actor(Tile location, String name) {
        //precondition - actor is not standing somewhere they're not supposed to be
        checkArgument(location.isAccessible());
        //precondition- actor has a name
        checkArgument(name.length() > 0);
        this.location = location;
        location.onEntry();
        this.dir = Direction.UP;
        this.name = name;
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
        location.onEntry();
    }

    /**
     * @return the direction this actor is facing
     */
    public Direction getDir() {
        return dir;
    }

    /**
     * @param dir direction this actor is now facing
     */
    public void setDir(Direction dir) {
        this.dir = dir;
    }

    /**
     * @return the URL of the image corresponding to this actor
     */
    public String getImageURl() {
        return "data/" + name + dir.getName() + ".png";
    }
}
