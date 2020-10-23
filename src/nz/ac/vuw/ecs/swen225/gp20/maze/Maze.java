package nz.ac.vuw.ecs.swen225.gp20.maze;

import nz.ac.vuw.ecs.swen225.gp20.commons.Direction;
import nz.ac.vuw.ecs.swen225.gp20.commons.Sound;
import nz.ac.vuw.ecs.swen225.gp20.persistence.LevelLoader;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * From Handout:
 * The game state is primarily made up of the maze itself, the current location of Chap on the maze, the treasure
 * chest and other items Chap has collected, such as keys. The game logic controls what events may, or may not happen
 * in the game world (e.g. “Can Chap go through this door?”, “Can Chap pick up this object?”, “Does this key open that
 * door?”, etc.).
 *
 * <p>The core logic of the game is that the player moves Chap around the maze until he reaches the exit and then
 * advances to the next level (if there is another level).
 * This module should make extensive use of contracts to ensure the integrity of the maze.
 *
 * @author Benjamin Doornbos
 */
public class Maze {
    private static final ReentrantLock moveLock = new ReentrantLock();
    private final Tiles tiles;
    private final int totalTreasures;
    private final Player chap;
    private List<Cobra> cobras;
    private List<Block> blocks;
    private int treasuresLeft;
    private int level;
    private LevelState state;

    /**
     * Creates a maze from specified tile array
     *
     * @param tiles          the tiles that make up the maze
     * @param totalTreasures the total treasures that are in this level
     */
    public Maze(Tile[][] tiles, int totalTreasures) {
        this.tiles = new Tiles(tiles);
        checkArgument(totalTreasures >= 0, "amount of treasures must not be negative");
        this.totalTreasures = treasuresLeft = totalTreasures;
        chap = new Player(tiles[tiles.length / 2][tiles[0].length / 2]);
        this.state = LevelState.RUNNING;
        this.level = 1;
    }

    /**
     * (For level 2) Creates a maze from tiles, blocks and cobras provided
     *
     * @param tiles          the tiles that make up the maze
     * @param totalTreasures the total treasures that are in this level
     * @param blocks         list of blocks to be placed on the level
     * @param cobras         list of cobras to be placed on the level
     */
    public Maze(Tile[][] tiles, int totalTreasures, List<Block> blocks, List<Cobra> cobras) {
        this(tiles, totalTreasures);
        if (blocks != null) {
            this.blocks = blocks;
            setBlocks();
        }
        if (cobras != null) {
            this.cobras = cobras;
            setCobras();
        }
        if (cobras != null && blocks != null) this.level = 2;
    }

    private Maze(Tile[][] tiles, int totalTreasures, Player chap) {
        checkArgument(tiles[0].length > 0, "Tiles cannot be empty");
        this.tiles = new Tiles(tiles);
        checkArgument(totalTreasures >= 0, "amount of treasures must not be negative");
        this.totalTreasures = treasuresLeft = totalTreasures;
        this.chap = chap;
        // this is so getLocation will point to the right object
        checkArgument(chap.getLocation().getCol() > 0 && chap.getLocation().getRow() > 0 &&
                        chap.getLocation().getRow() < tiles[0].length && chap.getLocation().getCol() < tiles.length,
                "Chap's location is not valid!");
        this.chap.setLocation(this.tiles.tileArray[chap.getLocation().getCol()][chap.getLocation().getRow()]);
        this.state = LevelState.RUNNING;
    }


    /**
     * Generates a Maze from JSON file corresponding to the level number provided
     *
     * @param level the level for this Maze to load
     */
    public Maze(int level) {
        this(LevelLoader.load(level).getMap(), LevelLoader.load(level).getTreasures(), LevelLoader.load(level).getChap());
        checkArgument(level == 1 || level == 2, "Level has to be 1 or 2");
        this.level = level;
        if (level == 2) {
            this.blocks = LevelLoader.load(level).getBlocks();
            this.cobras = LevelLoader.load(level).getCobras();
            setCobras();
            setBlocks();
        }
    }

    private void setBlocks() {
        for (Block b : blocks) {
            b.setLocation(tiles.tileArray[b.getCol()][b.getRow()]);
            b.getLocation().setHasBlock(true);
        }
    }

    private void setCobras() {
        for (Cobra c : cobras) {
            c.setLocation(tiles.tileArray[c.getLocation().getCol()][c.getLocation().getRow()]);
        }
    }

    /**
     * Moves Chap in given direction. This is a special case of moveActor but for only Chap.
     *
     * @param dir Direction to move
     * @return the sound that should be played, null if there is no sound to be played
     */
    public Sound moveChap(Direction dir) {
        moveLock.lock();
        try {
            Sound sound = moveActor(chap, dir);
            moveCobras();
            return sound;
        } finally {
            moveLock.unlock();
        }
    }

    private void moveCobras() {
        if (level == 2) {
            //don't move cobras in test mode
            if (cobras.get(0).inTestMode()) return;
            for (Cobra cobra : cobras) {
                moveActor(cobra, cobra.nextMove());
            }
        }
    }

    /**
     * Moves the given actor one square in the given direction
     *
     * @param a   Actor to move
     * @param dir Direction to move
     * @return the sound that should be played, null if there is no sound to be played
     */
    public Sound moveActor(Actor a, Direction dir) {
        checkNotNull(a);
        Tile newLoc = tiles.getTileInDirection(dir, a.getLocation());
        checkNotNull(newLoc);

        a.setDir(dir);

        if (!(newLoc instanceof LockedDoor) && !newLoc.isAccessible()) return null;

        a.getLocation().onExit();

        Sound sound = null;

        if (a == chap) {
            if (newLoc instanceof Exit) {
                state = LevelState.WON;
            } else if (newLoc.hasBlock()) {
                if (moveBlock(newLoc, dir)) return Sound.MOVE_BLOCK;
                else return null;
            } else if (newLoc.isOccupied()) {
                state = LevelState.DIED;
                return Sound.HIT_BY_MOB;
            } else {
                //if this method returns null, chap is not allowed to move to newLoc
                sound = interactWithTile(newLoc);
                if (sound == null) return null;
                // this tile may have been updated in the 2d array so we need to reset the newLoc pointer
                newLoc = tiles.tileArray[newLoc.getCol()][newLoc.getRow()];
            }
        } else {
            if (!newLoc.isAccessible()) return null;
            if (newLoc.isOccupied()) {
                state = LevelState.DIED;
            }
        }
        a.setLocation(newLoc);

        //if a sound has already been determined, play it. Else, determine the sound
        if (sound != null) return sound;
        sound = playSound(newLoc);
        checkNotNull(sound);
        return sound;
    }

    private Sound interactWithTile(Tile loc) {
        checkNotNull(loc);
        if (loc instanceof Treasure) {
            chap.incrementTreasures();
            treasuresLeft--;
            if (treasuresLeft == 0) tiles.flipExitLock();
            assert (treasuresLeft + chap.getTreasuresCollected() == totalTreasures);
        } else if (loc instanceof Key) {
            var k = (Key) loc;
            chap.addToBackPack(k.getColour());
            assert (chap.getBackpack().size() < 4);
        } else if (loc instanceof LockedDoor) {
            var ld = (LockedDoor) loc;
            if (!chap.backpackContains(ld.getLockColour())) return null;
        } else if (loc instanceof Lava) {
            state = LevelState.DIED;
            return Sound.HIT_BY_MOB;
        }

        if (loc.isFreeOnEntry()) {
            Sound sound = playSound(loc);
            tiles.setFree(loc);
            return sound;
        }
        return playSound(loc);
    }

    private Sound playSound(Tile t) {
        if (t instanceof Exit) return Sound.EXIT;
        if (t instanceof Treasure || t instanceof Key) return Sound.PICK_UP_ITEM;
        if (t instanceof InfoField) return Sound.INFO_FIELD;
        if (t instanceof LockedDoor) return Sound.UNLOCK_DOOR;
        else return Sound.STEP;
    }

    private boolean moveBlock(Tile loc, Direction dir) {
        Optional<Block> opt = blocks.stream().filter(b -> b.getLocation().equals(loc)).findFirst();
        if (opt.isEmpty()) throw new IllegalArgumentException("No block at this location");
        Block b = opt.get();

        Tile newLoc = tiles.getTileInDirection(dir, loc);

        checkNotNull(newLoc);

        if (newLoc.isAccessible() && !newLoc.hasBlock()) {
            b.getLocation().setHasBlock(false);
            if (newLoc instanceof Lava) {
                blocks.remove(b);
                tiles.setFree(newLoc);
            } else {
                b.setLocation(newLoc);
                b.getLocation().setHasBlock(true);
            }
        } else return false;
        return true;
    }

    /**
     * Whether the level is over (chap died or reached exit)
     *
     * @return Whether the level is over (chap died or reached exit)
     */
    public boolean isLevelOver() {
        return state != LevelState.RUNNING;
    }

    /**
     * Which state this level is in
     *
     * @return Which state this level is in
     */
    public LevelState getState() {
        return state;
    }

    /**
     * Gets a shallow copy of 2d array of Tiles that represents the maze
     *
     * @return Shallow copy of 2d array of Tiles that represents the maze
     */
    public Tile[][] getTiles() {
        return tiles.getTileArray();
    }

    /**
     * Gets the amount of uncollected treasures in this level
     *
     * @return the amount of uncollected treasures in this level
     */
    public int getTreasuresLeft() {
        return treasuresLeft;
    }

    /**
     * Gets the Player object that represents Chap
     *
     * @return the Player object that represents Chap
     */
    public Player getChap() {
        return chap;
    }

    /**
     * Gets the level that is currently being played
     *
     * @return the level that is currently being played
     */
    public int getLevel() {
        return level;
    }

    /**
     * Gets a list of all the cobras in this game
     *
     * @return a list of all the cobras in this game
     */
    public List<Cobra> getCobras() {
        return cobras;
    }

    /**
     * Gets a list of all the blocks in the game
     *
     * @return a list of all the blocks in the game
     */
    public List<Block> getBlocks() {
        return blocks;
    }

    /**
     * a enum that determines what state the level is in
     *
     * @author Benjamin Doornbos 300487256
     */
    public enum LevelState {
        /**
         * the game is running
         */
        RUNNING,
        /**
         * chap collected all the treasures
         */
        WON,
        /**
         * chap has died :(
         */
        DIED,
    }
}