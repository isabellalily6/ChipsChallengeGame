package nz.ac.vuw.ecs.swen225.gp20.commons;

/**
 * Enum that determines the direction of one of chap's moves
 *
 * @author Benjamin Doornbos
 */
public enum Direction {
    /**
     * Moving up one row
     */
    UP("Up"),
    /**
     * Moving down one row
     */
    DOWN("Down"),
    /**
     * Moving left one column
     */
    LEFT("Left"),
    /**
     * Moving right one column
     */
    RIGHT("Right");

    private final String name;

    Direction(String name) {
        this.name = name;
    }

    /**
     * Gets the properly formatted name of this direction
     *
     * @return Properly formatted name of this direction
     */
    public String getName() {
        return name;
    }
}
