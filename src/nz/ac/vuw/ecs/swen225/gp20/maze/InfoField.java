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
     * @param col  col in the maze array
     * @param row  row in the maze array
     * @param info the information to be printed to the screen when this tile is stepped on
     */
    public InfoField(int col, int row, String info) {
        super("data/infoField.png", col, row, true, false);
        this.info = info;
    }

    /**
     * The info this tile should show the user when they walk on this tile
     *
     * @return info to display
     */
    public String getInfo() {
        return info;
    }

    @Override
    public String toString() {
        return "{\"type\": \""+this.getClass().getSimpleName()+"\", \"info\": \""+info+"\"}";
    }
}
