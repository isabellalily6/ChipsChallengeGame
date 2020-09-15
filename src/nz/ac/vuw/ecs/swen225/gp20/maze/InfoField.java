package nz.ac.vuw.ecs.swen225.gp20.maze;

/**
 * From handout:
 * Like a free tile, but when Chap steps on this field, a help text will be displayed.
 *
 * @author Benjamin Doornbos 300487256
 */
public class InfoField extends Tile {
    private final String info;

    /**
     * Creates new InfoField
     *
     * @param row  row in the maze array
     * @param col  col in the maze array
     * @param info the information to be printed to the screen when this tile is stepped on
     */
    public InfoField(int row, int col, String info) {
        super("data/infoField.png", row, col, true);
        this.info = info;
    }
}
