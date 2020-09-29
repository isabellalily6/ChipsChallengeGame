package nz.ac.vuw.ecs.swen225.gp20.maze;

import nz.ac.vuw.ecs.swen225.gp20.render.Sound;
import nz.ac.vuw.ecs.swen225.gp20.render.SoundEffect;

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

    @Override
    public void onEntry(Actor actor) {
        SoundEffect.play(Sound.PICK_UP_ITEM);
        setOccupied(true);
    }
}
