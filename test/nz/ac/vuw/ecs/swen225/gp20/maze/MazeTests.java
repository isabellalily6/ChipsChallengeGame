package nz.ac.vuw.ecs.swen225.gp20.maze;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/*
TODO: Tests to make:
    - grab 1st treasure
        - ensure cell becomes free
        - ensure treasures changes by 1
    - grab correct key
        - ensure tile becomes free
        - ensure backpack contains key
    - grab incorrect key
        - ensure backpack contains correct key
    - open door with key
        - ensure correct key works
        - ensure incorrect key doesn't
        - ensure tile becomes free
    - collect last treasure
        - ensure exitLock flips
    - go to exit
        - ensure level is over
 */
class MazeTests {
    private Tile[][] createEmptyMazeArray() {
        Tile[][] tiles = new Tile[5][5];
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                tiles[x][y] = new Free(x, y);
            }
        }
        return tiles;
    }

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
        tiles[1][2] = new LockedDoor(1, 2, Key.Colour.TOPAZ);
        tiles[2][0] = new Exit(2, 0);
        tiles[2][1] = new ExitLock(2, 1);
        tiles[2][2] = new Free(2, 2);
        tiles[2][3] = new Key(2, 3, Key.Colour.SAPPHIRE);
        tiles[3][2] = new Treasure(3, 2);
        tiles[3][3] = new Key(3, 3, Key.Colour.TOPAZ);
        //TODO: add a door of a different type to ensure the wrong key can't open it

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
        var maze = new Maze(tiles);
        assertNotSame(tiles, maze.getTiles());
    }

    @Test
    void test2dArrayCloneShallowImmutable() {
        Tile[][] tiles = createEmptyMazeArray();
        var maze = new Maze(tiles);
        maze.getTiles()[0][0] = new Key(0, 0, Key.Colour.AMETHYST);
        assertFalse(maze.getTiles()[0][0] instanceof Key);
    }

    //----------Movement Tests-------------

    @Test
    void moveUp() {
        var maze = new Maze(createEmptyMazeArray());
        var oldY = maze.getChap().getLocation().getRow();
        maze.moveChap(Maze.Direction.UP);
        assert (maze.getChap().getLocation().getRow() - oldY == -1);
    }

    @Test
    void moveDown() {
        var maze = new Maze(createEmptyMazeArray());
        var oldY = maze.getChap().getLocation().getRow();
        maze.moveChap(Maze.Direction.DOWN);
        assert (maze.getChap().getLocation().getRow() - oldY == 1);
    }

    @Test
    void moveLeft() {
        var maze = new Maze(createEmptyMazeArray());
        var oldX = maze.getChap().getLocation().getCol();
        maze.moveChap(Maze.Direction.LEFT);
        assert (maze.getChap().getLocation().getCol() - oldX == -1);
    }

    @Test
    void moveRight() {
        var maze = new Maze(createEmptyMazeArray());
        var oldX = maze.getChap().getLocation().getCol();
        maze.moveChap(Maze.Direction.RIGHT);
        assert (maze.getChap().getLocation().getCol() - oldX == 1);
    }

    @Test
    void moveUpIntoNull() {
        var maze = new Maze(createEmptyMazeArray());
        assertThrows(IllegalArgumentException.class, () -> {
            for (int i = 0; i < 3; i++) {
                maze.moveChap(Maze.Direction.UP);
            }
        });
    }

    @Test
    void moveDownIntoNull() {
        var maze = new Maze(createEmptyMazeArray());
        assertThrows(IllegalArgumentException.class, () -> {
            for (int i = 0; i < 3; i++) {
                maze.moveChap(Maze.Direction.DOWN);
            }
        });
    }

    @Test
    void moveLeftIntoNull() {
        var maze = new Maze(createEmptyMazeArray());
        assertThrows(IllegalArgumentException.class, () -> {
            for (int i = 0; i < 3; i++) {
                maze.moveChap(Maze.Direction.LEFT);
            }
        });
    }

    @Test
    void moveRightIntoNull() {
        var maze = new Maze(createEmptyMazeArray());
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

    static class Location {
        private final int x;
        private final int y;

        public Location(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

}