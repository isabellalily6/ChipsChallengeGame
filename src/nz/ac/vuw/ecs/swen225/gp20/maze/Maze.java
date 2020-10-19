package nz.ac.vuw.ecs.swen225.gp20.maze;

import nz.ac.vuw.ecs.swen225.gp20.commons.Direction;
import nz.ac.vuw.ecs.swen225.gp20.commons.Sound;
import nz.ac.vuw.ecs.swen225.gp20.persistence.LevelLoader;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
    private final int cols;
    private final int rows;
    private final Tile[][] tiles;
    private final int totalTreasures;
    private final Player chap;
    private List<Cobra> cobras;
    private List<Block> blocks;
    private Thread cobraThread;
    private int treasuresLeft;
    private int level;
    private LevelState state;

    /**
     * TEST CONSTRUCTOR - Do not use in production code
     * a real maze needs many more fields than this
     *
     * @param tiles          the tiles that make up the maze
     * @param totalTreasures the total treasures that are in this level
     */
    public Maze(Tile[][] tiles, int totalTreasures) {
        this.cols = tiles.length;
        this.rows = tiles[0].length;
        this.tiles = copy2dTileArray(tiles);
        checkArgument(totalTreasures >= 0, "amount of treasures must not be negative");
        this.totalTreasures = treasuresLeft = totalTreasures;
        chap = new Player(tiles[cols / 2][rows / 2]);
        this.state = LevelState.RUNNING;
    }

    /**
     * ------TEST CONSTRUCTOR---------
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
            if (!cobras.get(0).inTestMode()) {
                cobraThread = new MovementThreadHandler(this);
                cobraThread.start();
            }
        }
    }

    /**
     * @param chap           the protagonist
     * @param tiles          the tiles that make up the maze
     * @param totalTreasures the total treasures that are in this level
     */
    public Maze(Tile[][] tiles, int totalTreasures, Player chap) {
        this.cols = tiles.length;
        this.rows = tiles[0].length;
        this.tiles = copy2dTileArray(tiles);
        checkArgument(totalTreasures >= 0, "amount of treasures must not be negative");
        this.totalTreasures = treasuresLeft = totalTreasures;
        this.chap = chap;
        // this is so getLocation will point to the right object
        this.chap.setLocation(this.tiles[chap.getLocation().getCol()][chap.getLocation().getRow()]);
        this.state = LevelState.RUNNING;
    }


    /**
     * Generates a Maze from JSON file corresponding to the level number provided
     *
     * @param level the level for this Maze to load
     */
    public Maze(int level) {
        this(LevelLoader.load(level).getMap(), LevelLoader.load(level).getTreasures(), LevelLoader.load(level).getChap());
        this.level = level;
        if (level == 2) {
            this.blocks = LevelLoader.load(level).getBlocks();
            this.cobras = LevelLoader.load(level).getCobras();
            setCobras();
            setBlocks();
            this.cobraThread = new MovementThreadHandler(this);
            this.cobraThread.start();
        }
    }

    private void setBlocks() {
        for (Block b : blocks) {
            b.setLocation(tiles[b.getCol()][b.getRow()]);
            b.getLocation().setHasBlock(true);
        }
    }

    private void setCobras() {
        for (Cobra c : cobras) {
            c.setLocation(tiles[c.getLocation().getCol()][c.getLocation().getRow()]);
        }
    }

    /**
     * Moves Chap in given direction. This is a special case of moveActor but for only Chap.
     *
     * @param dir Direction to move
     * @return the sound that should be played, null if there is no sound to be played
     */
    public Sound moveChap(Direction dir) {
        return moveActor(chap, dir);
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
        Tile newLoc = null;
        switch (dir) {
            case UP:
                checkArgument(a.getLocation().getRow() > 0, "Actor cannot move any higher!");
                newLoc = tiles[a.getLocation().getCol()][a.getLocation().getRow() - 1];
                break;
            case DOWN:
                checkArgument(a.getLocation().getRow() < rows - 1, "Actor cannot move any lower!");
                newLoc = tiles[a.getLocation().getCol()][a.getLocation().getRow() + 1];
                break;
            case LEFT:
                checkArgument(a.getLocation().getCol() > 0, "Actor cannot move any further left!");
                newLoc = tiles[a.getLocation().getCol() - 1][a.getLocation().getRow()];
                break;
            case RIGHT:
                checkArgument(a.getLocation().getCol() < cols - 1, "Actor cannot move any further right!");
                newLoc = tiles[a.getLocation().getCol() + 1][a.getLocation().getRow()];
                break;
        }
        //TODO: better error handling
        checkNotNull(newLoc);

        a.setDir(dir);

        if (!(newLoc instanceof LockedDoor) && !newLoc.isAccessible()) return null;

        a.getLocation().onExit();

        Sound sound = null;

        if (a == chap) {
            if (newLoc instanceof Exit) {
                state = LevelState.WON;
                if (cobraThread != null) cobraThread.interrupt();
            } else if (newLoc.hasBlock()) {
                if (!moveBlock(newLoc, dir)) return null;
            } else if (newLoc.isOccupied()) {
                state = LevelState.DIED;
                if (cobraThread != null) cobraThread.interrupt();
                return null; //TODO: death sound?
            } else {
                //if this method returns null, chap is not allowed to move to newLoc
                sound = interactWithTile(newLoc);
                if (sound == null) return null;
                // this tile may have been updated in the 2d array so we need to reset the newLoc pointer
                newLoc = tiles[newLoc.getCol()][newLoc.getRow()];
            }
        } else {
            if (!newLoc.isAccessible()) return null;
            if (newLoc.isOccupied()) {
                state = LevelState.DIED;
                if (cobraThread != null) cobraThread.interrupt();
            }
        }
        //newLoc.onEntry(a);
        a.setLocation(newLoc);

        //if a sound has already been determined, play it. Else, determine the sound
        if (sound != null) return sound;
        return playSound(newLoc);
    }

    private Sound interactWithTile(Tile loc) {
        if (loc instanceof Treasure) {
            chap.incrementTreasures();
            treasuresLeft--;
            if (treasuresLeft == 0) flipExitLock();
            assert (treasuresLeft + chap.getTreasuresCollected() == totalTreasures);
        } else if (loc instanceof Key) {
            var k = (Key) loc;
            chap.addToBackPack(k.getColour());
        } else if (loc instanceof LockedDoor) {
            var ld = (LockedDoor) loc;
            //TODO: better error handling
            if (!chap.backpackContains(ld.getLockColour())) return null;
        } else if (loc instanceof Lava) {
            //TODO: potentially make levelOver an int 0=not over 1=win 2=die
            state = LevelState.DIED;
            if (cobraThread != null) cobraThread.interrupt();
            return null;
        }
        if (loc.isFreeOnEntry()) {
            Sound sound = playSound(loc);
            setFree(loc);
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


    private void setFree(Tile loc) {
        tiles[loc.getCol()][loc.getRow()] = new Free(loc.getCol(), loc.getRow());
    }

    private void flipExitLock() {
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                if (tiles[x][y] instanceof ExitLock) setFree(tiles[x][y]);
            }
        }
    }

    private boolean moveBlock(Tile loc, Direction dir) {
        Optional<Block> opt = blocks.stream().filter(b -> b.getLocation().equals(loc)).findFirst();
        if (opt.isEmpty()) throw new IllegalArgumentException("No block at this location");
        Block b = opt.get();

        Tile newLoc = null;
        switch (dir) {
            case UP:
                newLoc = tiles[loc.getCol()][loc.getRow() - 1];
                break;
            case DOWN:
                newLoc = tiles[loc.getCol()][loc.getRow() + 1];
                break;
            case LEFT:
                newLoc = tiles[loc.getCol() - 1][loc.getRow()];
                break;
            case RIGHT:
                newLoc = tiles[loc.getCol() + 1][loc.getRow()];
                break;
        }

        checkNotNull(newLoc);

        if (newLoc.isAccessible() && !newLoc.hasBlock()) {
            b.getLocation().setHasBlock(false);
            if (newLoc instanceof Lava) {
                blocks.remove(b);
                setFree(newLoc);
            } else {
                b.setLocation(newLoc);
                b.getLocation().setHasBlock(true);
            }
        } else return false;
        return true;
    }

    private Tile[][] copy2dTileArray(Tile[][] toCopy) {
        var toRet = new Tile[toCopy.length][];
        for (int i = 0; i < toCopy.length; i++) {
            toRet[i] = Arrays.copyOf(toCopy[i], toCopy[i].length);
        }
        return toRet;
    }

    /**
     * @return Whether the level is over (chap died or reached exit)
     */
    public boolean isLevelOver() {
        return state != LevelState.RUNNING;
    }

    /**
     * @return Which state this level is in
     */
    public LevelState getState() {
        return state;
    }

    /**
     * @return Shallow copy of 2d array of Tiles that represents the maze
     */
    public Tile[][] getTiles() {
        return copy2dTileArray(tiles);
    }

    /**
     * @return the amount of uncollected treasures in this level
     */
    public int getTreasuresLeft() {
        return treasuresLeft;
    }

    /**
     * @return the Player object that represents Chap
     */
    public Player getChap() {
        return chap;
    }

    /**
     * @return the level that is currently being played
     */
    public int getLevel() {
        return level;
    }

    /**
     * @return a list of all the cobras in this game
     */
    public List<Cobra> getCobras() {
        return cobras;
    }

    /**
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