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
     * @param row    row in the maze array
     * @param col    col in the maze array
     * @param colour the colour that this particular key is going to be (determined by json)
     */
    public Key(int row, int col, Colour colour) {
        super("data/key.png", row, col, true);
        this.colour = colour;
    }

    enum Colour {
        EMERALD, DIAMOND, SAPPHIRE, AMETHYST, TOPAZ, RUBY
    }
}


