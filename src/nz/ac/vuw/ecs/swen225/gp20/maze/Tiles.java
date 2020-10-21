package nz.ac.vuw.ecs.swen225.gp20.maze;

import nz.ac.vuw.ecs.swen225.gp20.commons.Direction;

import java.util.Arrays;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This class controls the 2D array of tiles for the maze an associated logic
 *
 * @author Benjamin Doornbos 300487256
 */
public class Tiles {
    final Tile[][] tileArray;
    private final int cols;
    private final int rows;

    /**
     * Creates a new Tiles object based off a given tile array
     *
     * @param tileArray tile array that represents the maze
     */
    public Tiles(Tile[][] tileArray) {
        this.cols = tileArray.length;
        this.rows = tileArray[0].length;
        this.tileArray = copy2dTileArray(tileArray);
    }

    Tile getTileInDirection(Direction dir, Tile loc) {
        checkNotNull(loc);
        Tile newLoc = null;
        switch (dir) {
            case UP:
                checkArgument(loc.getRow() > 0, "Cannot move any higher!");
                newLoc = tileArray[loc.getCol()][loc.getRow() - 1];
                break;
            case DOWN:
                checkArgument(loc.getRow() < rows - 1, "Cannot move any lower!");

                newLoc = tileArray[loc.getCol()][loc.getRow() + 1];
                break;
            case LEFT:
                checkArgument(loc.getCol() > 0, "Cannot move any further left!");

                newLoc = tileArray[loc.getCol() - 1][loc.getRow()];
                break;
            case RIGHT:
                checkArgument(loc.getCol() < cols - 1, "Cannot move any further right!");

                newLoc = tileArray[loc.getCol() + 1][loc.getRow()];
                break;
        }
        return newLoc;
    }

    private Tile[][] copy2dTileArray(Tile[][] toCopy) {
        var toRet = new Tile[toCopy.length][];
        for (int i = 0; i < toCopy.length; i++) {
            toRet[i] = Arrays.copyOf(toCopy[i], toCopy[i].length);
        }
        return toRet;
    }

    void setFree(Tile loc) {
        tileArray[loc.getCol()][loc.getRow()] = new Free(loc.getCol(), loc.getRow());
    }


    void flipExitLock() {
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                if (tileArray[x][y] instanceof ExitLock) setFree(tileArray[x][y]);
            }
        }
    }

    /**
     * @return the underlying 2D tile array for this class
     */
    public Tile[][] getTileArray() {
        return copy2dTileArray(tileArray);
    }
}
