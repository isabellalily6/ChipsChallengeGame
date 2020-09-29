package nz.ac.vuw.ecs.swen225.gp20.maze;

/**
 * From handout:
 * If Chap steps onto the tile, the treasure (chip) is picked up and added to the treasure chest.
 * Then the tile turns into a free tile
 *
 * @author Benjamin Doornbos 300487256
 */
public class Treasure extends Tile {
    /**
     * Creates new Treasure tile
     *
     * @param col col in the maze array
     * @param row row in the maze array
     */
    public Treasure(int col, int row) {
        super("data/treasure.png", col, row, true, true);
    }

}
