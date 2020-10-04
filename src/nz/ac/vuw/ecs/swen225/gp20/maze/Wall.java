package nz.ac.vuw.ecs.swen225.gp20.maze;

/**
 * From handout:
 * Part of a wall, actors cannot move onto those tiles.
 *
 * @author Benjamin Doornbos 300487256
 */
public class Wall extends Tile {

    /**
     * Creates a new wall
     *
     * @param col col in the maze array
     * @param row row in the maze array
     */
    public Wall(int col, int row) {
        super("data/wall.png", col, row, false, false);
    }
}
