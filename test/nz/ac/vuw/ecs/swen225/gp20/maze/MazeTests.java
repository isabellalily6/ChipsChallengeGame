package nz.ac.vuw.ecs.swen225.gp20.maze;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This runs all the tests to ensure the basic logic of the game works
 *
 * @author Benjamin Doornbos 300487256
 */
public class MazeTests {
    private Tile[][] createEmptyMazeArray() {
        Tile[][] tiles = new Tile[5][5];
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                tiles[x][y] = new Free(x, y);
            }
        }
        return tiles;
    }

    /*
         | w | w | ex| w |
         | w | w | el| w |
         | t | bd| C | t |
         | w | rd| rk| bk|
    ex - exit
    el - exit lock
    t - treasure
    bd/rd - blue/red door
    C - chap
    bk/rk - blue/red key
     */
    private Tile[][] createTestMazeArray() {
        Tile[][] tiles = new Tile[4][4];
        //set everything to wall by default
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                tiles[x][y] = new Wall(x, y);
            }
        }
        //hardcode a tiny, playable board
        tiles[0][2] = new Treasure(0, 2);
        tiles[0][3] = new LockedDoor(0, 3, Key.Colour.RED);
        tiles[1][2] = new LockedDoor(1, 2, Key.Colour.BLUE);
        tiles[2][0] = new Exit(2, 0);
        tiles[2][1] = new ExitLock(2, 1);
        tiles[2][2] = new Free(2, 2);
        tiles[2][3] = new Key(2, 3, Key.Colour.RED);
        tiles[3][2] = new Treasure(3, 2);
        tiles[3][3] = new Key(3, 3, Key.Colour.BLUE);

        return tiles;
    }

    private Maze initTestMaze() {
        return new Maze(createTestMazeArray(), 2);
    }

    private Location getChapLocation(Maze m) {
        return new Location(m.getChap().getLocation().getCol(), m.getChap().getLocation().getRow());
    }

    private void assertChapPos(Maze m, Location loc) {
        assert (m.getChap().getLocation().getCol() == loc.x && m.getChap().getLocation().getRow() == loc.y);
    }

    @Test
    void test2dArrayShallowClone() {
        Tile[][] tiles = createEmptyMazeArray();
        var maze = new Maze(tiles, 0);
        assertNotSame(tiles, maze.getTiles());
    }

    @Test
    void test2dArrayCloneShallowImmutable() {
        Tile[][] tiles = createEmptyMazeArray();
        var maze = new Maze(tiles, 0);
        maze.getTiles()[0][0] = new Key(0, 0, Key.Colour.RED);
        assertFalse(maze.getTiles()[0][0] instanceof Key);
    }

    //----------Movement Tests-------------

    @Test
    void moveUp() {
        var maze = new Maze(createEmptyMazeArray(), 0);
        var oldY = maze.getChap().getLocation().getRow();
        maze.moveChap(Maze.Direction.UP);
        assert (maze.getChap().getLocation().getRow() - oldY == -1);
    }

    @Test
    void moveDown() {
        var maze = new Maze(createEmptyMazeArray(), 0);
        var oldY = maze.getChap().getLocation().getRow();
        maze.moveChap(Maze.Direction.DOWN);
        assert (maze.getChap().getLocation().getRow() - oldY == 1);
    }

    @Test
    void moveLeft() {
        var maze = new Maze(createEmptyMazeArray(), 0);
        var oldX = maze.getChap().getLocation().getCol();
        maze.moveChap(Maze.Direction.LEFT);
        assert (maze.getChap().getLocation().getCol() - oldX == -1);
    }

    @Test
    void moveRight() {
        var maze = new Maze(createEmptyMazeArray(), 0);
        var oldX = maze.getChap().getLocation().getCol();
        maze.moveChap(Maze.Direction.RIGHT);
        assert (maze.getChap().getLocation().getCol() - oldX == 1);
    }

    @Test
    void moveUpIntoNull() {
        var maze = new Maze(createEmptyMazeArray(), 0);
        assertThrows(IllegalArgumentException.class, () -> {
            for (int i = 0; i < 3; i++) {
                maze.moveChap(Maze.Direction.UP);
            }
        });
    }

    @Test
    void moveDownIntoNull() {
        var maze = new Maze(createEmptyMazeArray(), 0);
        assertThrows(IllegalArgumentException.class, () -> {
            for (int i = 0; i < 3; i++) {
                maze.moveChap(Maze.Direction.DOWN);
            }
        });
    }

    @Test
    void moveLeftIntoNull() {
        var maze = new Maze(createEmptyMazeArray(), 0);
        assertThrows(IllegalArgumentException.class, () -> {
            for (int i = 0; i < 3; i++) {
                maze.moveChap(Maze.Direction.LEFT);
            }
        });
    }

    @Test
    void moveRightIntoNull() {
        var maze = new Maze(createEmptyMazeArray(), 0);
        assertThrows(IllegalArgumentException.class, () -> {
            for (int i = 0; i < 3; i++) {
                maze.moveChap(Maze.Direction.RIGHT);
            }
        });
    }

    @Test
    void ensureTreasureCellAccessible() {
        var maze = initTestMaze();
        maze.moveChap(Maze.Direction.RIGHT);
        assertChapPos(maze, new Location(3, 2));
    }

    @Test
    void grabTreasureCellBecomesFree() {
        var maze = initTestMaze();
        maze.moveChap(Maze.Direction.RIGHT);
        assert (maze.getChap().getLocation() instanceof Free);
    }

    @Test
    void grabOneTreasureCellExitLockDoesntFlip() {
        var maze = initTestMaze();
        maze.moveChap(Maze.Direction.RIGHT);
        assert (maze.getTiles()[2][1] instanceof ExitLock);
    }

    @Test
    void grabTreasureChapsTreasuresIncrements() {
        var maze = initTestMaze();
        assert (maze.getChap().getTreasuresCollected() == 0);
        maze.moveChap(Maze.Direction.RIGHT);
        assert (maze.getChap().getTreasuresCollected() == 1);
    }

    @Test
    void moveIntoWall() {
        var maze = initTestMaze();
        maze.moveChap(Maze.Direction.RIGHT);
        var beforeMove = getChapLocation(maze);
        maze.moveChap(Maze.Direction.UP);
        assertChapPos(maze, beforeMove);
    }

    @Test
    void moveIntoLockedDoorNoKey() {
        var maze = initTestMaze();
        var beforeMove = getChapLocation(maze);
        maze.moveChap(Maze.Direction.LEFT);
        assertChapPos(maze, beforeMove);
    }

    @Test
    void moveIntoExitLock() {
        var maze = initTestMaze();
        var beforeMove = getChapLocation(maze);
        maze.moveChap(Maze.Direction.UP);
        assertChapPos(maze, beforeMove);
    }

    @Test
    void getBlueKeyCellBecomesFree() {
        var maze = initTestMaze();
        maze.moveChap(Maze.Direction.RIGHT);
        maze.moveChap(Maze.Direction.DOWN);
        assert (maze.getChap().getLocation() instanceof Free);
    }

    @Test
    void getBlueKeyGoesIntoBackpack() {
        var maze = initTestMaze();
        maze.moveChap(Maze.Direction.RIGHT);
        maze.moveChap(Maze.Direction.DOWN);
        assert (maze.getChap().backpackContains(Key.Colour.BLUE));
    }

    @Test
    void getBlueKeyBlueDoorOpens() {
        var maze = initTestMaze();
        maze.moveChap(Maze.Direction.RIGHT);
        maze.moveChap(Maze.Direction.DOWN);
        maze.moveChap(Maze.Direction.LEFT);
        maze.moveChap(Maze.Direction.UP);
        var beforeMove = getChapLocation(maze);
        maze.moveChap(Maze.Direction.LEFT);
        assert (maze.getChap().getLocation().getCol() - beforeMove.x == -1);
    }

    @Test
    void getRedKeyBlueDoorDoesntOpen() {
        var maze = initTestMaze();
        maze.moveChap(Maze.Direction.DOWN);
        maze.moveChap(Maze.Direction.UP);
        var beforeMove = getChapLocation(maze);
        maze.moveChap(Maze.Direction.LEFT);
        assertChapPos(maze, beforeMove);
    }

    @Test
    void getRedKeyGoesIntoBackpack() {
        var maze = initTestMaze();
        maze.moveChap(Maze.Direction.DOWN);
        assert (maze.getChap().backpackContains(Key.Colour.RED));
    }

    @Test
    void collectAllTreasuresExitLockFlips() {
        var maze = initTestMaze();
        maze.moveChap(Maze.Direction.RIGHT);
        maze.moveChap(Maze.Direction.DOWN);
        maze.moveChap(Maze.Direction.UP);
        maze.moveChap(Maze.Direction.LEFT);
        maze.moveChap(Maze.Direction.LEFT);
        maze.moveChap(Maze.Direction.LEFT);
        assert (maze.getTiles()[2][1] instanceof Free);
    }

    @Test
    void goToExitLevelOver() {
        var maze = initTestMaze();
        maze.moveChap(Maze.Direction.RIGHT);
        maze.moveChap(Maze.Direction.DOWN);
        maze.moveChap(Maze.Direction.UP);
        maze.moveChap(Maze.Direction.LEFT);
        maze.moveChap(Maze.Direction.LEFT);
        maze.moveChap(Maze.Direction.LEFT);
        maze.moveChap(Maze.Direction.RIGHT);
        maze.moveChap(Maze.Direction.RIGHT);
        maze.moveChap(Maze.Direction.UP);
        maze.moveChap(Maze.Direction.UP);
        assert (maze.isLevelOver());
    }

    static class Location {
        private final int x;
        private final int y;

        public Location(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

}