package nz.ac.vuw.ecs.swen225.gp20.maze;

/**
 * From Handout:
 * Actors can move onto those tiles. If Chap moves onto such a tile, he picks up the key with this colour,
 * once this is done, the tile turns into a free tile.
 *
 * @author Benjamin Doornbos 300487256
 */
public class Key extends Tile {
    private final Colour colour;

    /**
     * Creates a new Key (should only be used when initialising game
     *
     * @param col    col in the maze array
     * @param row    row in the maze array
     * @param colour the colour that this particular key is going to be (determined by json)
     */
    public Key(int col, int row, Colour colour) {
        super("data/" + colour.getName() + "Key.png", col, row, true, true);
        this.colour = colour;
    }

    /**
     * @return The colour of this key
     */
    public Colour getColour() {
        return colour;
    }

    public enum Colour {
        /**
         * Red coloured Keys/Doors
         */
        RED("Red"),
        /**
         * Green coloured Keys/Doors
         */
        GREEN("Green"),
        /**
         * Blue coloured Keys/Doors
         */
        BLUE("Blue");

        private final String name;


        Colour(String name) {
            this.name = name;
        }

        /**
         * @return properly formatted name of this key
         */
        public String getName() {
            return name;
        }
    }
}


