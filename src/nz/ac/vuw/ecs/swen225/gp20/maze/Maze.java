package nz.ac.vuw.ecs.swen225.gp20.maze;

import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * From Handout:
 * The game state is primarily made up of the maze itself, the current location of Chap on the maze, the treasure
 * chest and other items Chap has collected, such as keys. The game logic controls what events may, or may not happen
 * in the game world (e.g. “Can Chap go through this door?”, “Can Chap pick up this object?”, “Does this key open that
 * door?”, etc.).
 *
 * <p>The core logic of the game is that the player moves Chap around the maze until he reaches the exit and then advances
 * to the next level (if there is another level).
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
    private List<Actor> actors;
    private int treasuresLeft;
    private boolean levelOver;
    private int level;


    /**
     * TEST CONSTRUCTOR - Do not use in production code
     * a real maze needs many more fields than this
     *
     * @param tiles the tiles that make up the maze
     */
    public Maze(Tile[][] tiles) {
        this.cols = tiles.length;
        this.rows = tiles[0].length;
        this.tiles = copy2dTileArray(tiles);
        this.totalTreasures = 0;
        chap = new Player(tiles[cols / 2][rows / 2]);
    }

    /**
     * New maze which contains the Tile array and controls logic.
     * (new maze needs to be initialised for each level)
     * TODO: discuss method for loading walls and stuff from file
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
    }

    /**
     * Generates a Maze from JSON file corresponding to the level number provided
     *
     * @param level the level for this Maze to load
     */
    public Maze(int level) {
        //TODO: json loader stuff here
        this.cols = -1;
        this.rows = -1;
        this.tiles = new Tile[0][0];
        this.totalTreasures = -1;
        this.chap = null;
        this.level = level;
    }

    /**
     * Moves Chap in given direction. This is a special case of moveActor but for only Chap.
     *
     * @param dir Direction to move
     */
    public void moveChap(Direction dir) {
        moveActor(chap, dir);
    }

    /**
     * Moves the given actor one square in the given direction
     *
     * @param a   Actor to move
     * @param dir Direction to move
     */
    public void moveActor(Actor a, Direction dir) {
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
        if (!newLoc.isAccessible()) return;

        a.getLocation().onExit();
        if (a == chap) {
            if (newLoc instanceof Exit) levelOver = true;
            else {
                interactWithTile(newLoc);
                // this tile may have been updated in the 2d array so we need to reset the newLoc pointer
                newLoc = tiles[newLoc.getCol()][newLoc.getRow()];
            }
        }
        newLoc.onEntry(a);
        a.setLocation(newLoc);

    }

    private void interactWithTile(Tile loc) {
        if (loc instanceof Treasure) {
            chap.incrementTreasures();
            treasuresLeft--;
            if (treasuresLeft == 0) flipExitLock();
            assert (treasuresLeft + chap.getTreasuresCollected() == totalTreasures);
        } else if (loc instanceof Key) {
            var k = (Key) loc;
            chap.addToBackPack(k.getColour());
        } else if (loc instanceof LockedDoor) {
            LockedDoor ld = (LockedDoor) loc;
            //TODO: better error handling
            if (!chap.backpackContains(ld.getLockColour())) return;
        }

        if (loc.isFreeOnEntry()) setFree(loc);
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

    private Tile[][] copy2dTileArray(Tile[][] toCopy) {
        Tile[][] toRet = new Tile[toCopy.length][];
        for (int i = 0; i < toCopy.length; i++) {
            toRet[i] = Arrays.copyOf(toCopy[i], toCopy[i].length);
        }
        return toRet;
    }

    /**
     * @return Whether the level is over (chap died or reached exit)
     */
    public boolean isLevelOver() {
        return levelOver;
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
     * Enum that determines the direction of one of chap's moves
     *
     * @author Benjamin Doornbos
     */
    public enum Direction {
        /**
         * Moving up one row
         */
        UP,
        /**
         * Moving down one row
         */
        DOWN,
        /**
         * Moving left one column
         */
        LEFT,
        /**
         * Moving right one column
         */
        RIGHT,
    }
}
