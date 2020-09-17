package nz.ac.vuw.ecs.swen225.gp20.maze;

import java.util.Arrays;
import java.util.List;

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
    private Player chap;
    private List<Actor> actors;
    private int treasures;
    private boolean levelOver;

    /**
     * New maze which contains the Tile array and controls logic.
     * (new maze needs to be initialised for each level)
     *
     * @param cols amount of cols for the entire level
     * @param rows amount of rows for the entire level
     */
    public Maze(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
        tiles = new Tile[cols][rows];
    }

    /**
     * Constructor with pre-constructed tile array
     *
     * @param tiles the tiles that make up the maze
     */
    public Maze(Tile[][] tiles) {
        this.cols = tiles.length;
        this.rows = tiles[0].length;
        this.tiles = tiles;
    }

    private void initMaze() {
        //TODO: discuss method for loading walls and stuff from file
        //-----------TEMPORARY-------------
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                tiles[x][y] = new Free(x, y);
            }
        }
        chap = new Player(tiles[cols / 2][rows / 2]);
        //---------------------------------
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
        Tile newLoc = null;
        switch (dir) {
            case UP:
                newLoc = tiles[a.getLocation().getCol()][a.getLocation().getRow() - 1];
                break;
            case DOWN:
                newLoc = tiles[a.getLocation().getCol()][a.getLocation().getRow() + 1];
                break;
            case LEFT:
                newLoc = tiles[a.getLocation().getCol() - 1][a.getLocation().getRow()];
                break;
            case RIGHT:
                newLoc = tiles[a.getLocation().getCol() + 1][a.getLocation().getRow()];
                break;
        }
        //TODO: better error handling
        if (newLoc == null) throw new IllegalArgumentException("Chap tried to walk onto null square");
        if (!newLoc.isAccessible()) return;

        a.getLocation().onExit();
        if (newLoc instanceof Exit)
            if (a == chap) interactWithTile(newLoc);
        newLoc.onEntry(a);
        a.setLocation(newLoc);

    }

    private void interactWithTile(Tile loc) {
        if (loc instanceof Treasure) {
            chap.incrementTreasures();
            treasures--;
            if (treasures == 0) flipExitLock();
        } else if (loc instanceof Key) {
            Key k = (Key) loc;
            chap.addToBackPack(k.getColour());
        } else if (loc instanceof LockedDoor) {
            LockedDoor ld = (LockedDoor) loc;
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
        Tile[][] toRet = new Tile[tiles.length][];
        for (int i = 0; i < tiles.length; i++) {
            toRet[i] = Arrays.copyOf(tiles[i], tiles[i].length);
        }
        return toRet;
    }

    /**
     * @return the Player object that represents Chap
     */
    public Player getChap() {
        return chap;
    }

    enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT,
    }
}
