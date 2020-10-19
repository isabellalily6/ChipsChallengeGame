package nz.ac.vuw.ecs.swen225.gp20.maze;

import nz.ac.vuw.ecs.swen225.gp20.commons.Direction;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This runs all the tests to ensure the basic logic of the game works
 *
 * @author Benjamin Doornbos 300487256
 */
public class MazeTest {
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
         | w | w | el| i |
         | t | bd| C | t |
         | w | rd| rk| bk|
    ex - exit
    el - exit lock
    t - treasure
    bd/rd - blue/red door
    C - chap
    bk/rk - blue/red key
     */
    private Tile[][] createGeneralTestMaze() {
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
        tiles[3][1] = new InfoField(3, 1, "Test");
        tiles[3][2] = new Treasure(3, 2);
        tiles[3][3] = new Key(3, 3, Key.Colour.BLUE);

        return tiles;
    }

    /*
        | w | w | w | w | w | w | w |
        | w | w |   |   | w | w | w |
        | w |   | B | c | w | w | w |
        | w |   |   |   | w | w | w |
        | w | w | w | w | w | w | w |

        B - block
        c - chap
        w - wall
          - free

     */
    private Tile[][] createBlockTestMaze() {
        Tile[][] tiles = new Tile[7][5];
        //set everything to wall by default
        for (int x = 0; x < 7; x++) {
            for (int y = 0; y < 5; y++) {
                tiles[x][y] = new Wall(x, y);
            }
        }

        tiles[1][2] = new Free(1, 2);
        tiles[1][3] = new Free(1, 3);
        tiles[2][1] = new Free(2, 1);
        tiles[2][2] = new Free(2, 2);
        tiles[2][3] = new Free(2, 3);
        tiles[3][1] = new Free(3, 1);
        tiles[3][2] = new Free(3, 2);
        tiles[3][3] = new Free(3, 3);

        return tiles;
    }

    /*
        | L | B | C | L |

        L - lava
        B - block
        C - chap
     */
    private Tile[][] createLavaTestMaze() {
        Tile[][] tiles = new Tile[5][1];

        tiles[0][0] = new Lava(0, 0);
        tiles[1][0] = new Free(1, 0);
        tiles[2][0] = new Free(2, 0);
        tiles[3][0] = new Lava(3, 0);
        tiles[4][0] = new Wall(4, 0);

        return tiles;
    }

    /*
           | w | ld| w | w |
           | w |   |   | w |
           | w | c |   | w |
           | w | w | w | w |

           c - cobra
           w = wall
           ld - lockedDoor
     */
    private Tile[][] createCobraTestMaze() {
        Tile[][] tiles = new Tile[4][4];

        //set everything to wall by default
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                tiles[x][y] = new Wall(x, y);
            }
        }

        tiles[1][1] = new Free(1, 1);
        tiles[1][2] = new Free(1, 2);
        tiles[2][1] = new Free(2, 1);
        tiles[2][2] = new Free(2, 2);
        tiles[1][0] = new LockedDoor(1, 0, Key.Colour.BLUE);

        return tiles;
    }

    private Maze initGeneralTestMaze() {
        return new Maze(createGeneralTestMaze(), 2);
    }

    private Maze initBlockTestMaze() {
        return new Maze(createBlockTestMaze(), 1, new ArrayList<>() {{
            add(new Block(2, 2));
        }}, null);
    }

    private Maze initLavaTestMaze() {
        return new Maze(createLavaTestMaze(), 1, new ArrayList<>() {{
            add(new Block(1, 0));
        }}, null);
    }

    private Maze initCobraTestMaze() {
        List<Cobra> cobras = new ArrayList<>() {{
            add(new Cobra(new Free(1, 2), true));
        }};
        return new Maze(createCobraTestMaze(), 1, null, cobras);
    }

    private Location getChapLocation(Maze m) {
        return new Location(m.getChap().getLocation().getCol(), m.getChap().getLocation().getRow());
    }

    private Location getFirstBlockLocation(Maze m) {
        var b = m.getBlocks().get(0);
        return new Location(b.getCol(), b.getRow());
    }

    private Location getFirstCobraLocation(Maze m) {
        var cobra = m.getCobras().get(0);
        return new Location(cobra.getLocation().getCol(), cobra.getLocation().getRow());
    }

    private void assertChapPos(Maze m, Location loc) {
        assert (m.getChap().getLocation().getCol() == loc.x && m.getChap().getLocation().getRow() == loc.y);
    }

    @Test
    public void test2dArrayShallowClone() {
        Tile[][] tiles = createEmptyMazeArray();
        var maze = new Maze(tiles, 0);
        assertNotSame(tiles, maze.getTiles());
    }

    @Test
    public void test2dArrayCloneShallowImmutable() {
        Tile[][] tiles = createEmptyMazeArray();
        var maze = new Maze(tiles, 0);
        maze.getTiles()[0][0] = new Key(0, 0, Key.Colour.RED);
        assertFalse(maze.getTiles()[0][0] instanceof Key);
    }

    @Test
    public void moveUp() {
        var maze = new Maze(createEmptyMazeArray(), 0);
        var oldY = maze.getChap().getLocation().getRow();
        maze.moveChap(Direction.UP);
        assert (maze.getChap().getLocation().getRow() - oldY == -1);
    }

    @Test
    public void moveDown() {
        var maze = new Maze(createEmptyMazeArray(), 0);
        var oldY = maze.getChap().getLocation().getRow();
        maze.moveChap(Direction.DOWN);
        assert (maze.getChap().getLocation().getRow() - oldY == 1);
    }

    @Test
    public void moveLeft() {
        var maze = new Maze(createEmptyMazeArray(), 0);
        var oldX = maze.getChap().getLocation().getCol();
        maze.moveChap(Direction.LEFT);
        assert (maze.getChap().getLocation().getCol() - oldX == -1);
    }

    @Test
    public void moveRight() {
        var maze = new Maze(createEmptyMazeArray(), 0);
        var oldX = maze.getChap().getLocation().getCol();
        maze.moveChap(Direction.RIGHT);
        assert (maze.getChap().getLocation().getCol() - oldX == 1);
    }

    @Test
    public void moveUpIntoNull() {
        var maze = new Maze(createEmptyMazeArray(), 0);
        assertThrows(IllegalArgumentException.class, () -> {
            for (int i = 0; i < 3; i++) {
                maze.moveChap(Direction.UP);
            }
        });
    }

    @Test
    public void moveDownIntoNull() {
        var maze = new Maze(createEmptyMazeArray(), 0);
        assertThrows(IllegalArgumentException.class, () -> {
            for (int i = 0; i < 3; i++) {
                maze.moveChap(Direction.DOWN);
            }
        });
    }

    @Test
    public void moveLeftIntoNull() {
        var maze = new Maze(createEmptyMazeArray(), 0);
        assertThrows(IllegalArgumentException.class, () -> {
            for (int i = 0; i < 3; i++) {
                maze.moveChap(Direction.LEFT);
            }
        });
    }

    @Test
    public void moveRightIntoNull() {
        var maze = new Maze(createEmptyMazeArray(), 0);
        assertThrows(IllegalArgumentException.class, () -> {
            for (int i = 0; i < 3; i++) {
                maze.moveChap(Direction.RIGHT);
            }
        });
    }

    @Test
    public void ensureTreasureCellAccessible() {
        var maze = initGeneralTestMaze();
        maze.moveChap(Direction.RIGHT);
        assertChapPos(maze, new Location(3, 2));
    }

    @Test
    public void grabTreasureCellBecomesFree() {
        var maze = initGeneralTestMaze();
        maze.moveChap(Direction.RIGHT);
        assert (maze.getChap().getLocation() instanceof Free);
    }

    @Test
    public void grabOneTreasureCellExitLockDoesntFlip() {
        var maze = initGeneralTestMaze();
        maze.moveChap(Direction.RIGHT);
        assert (maze.getTiles()[2][1] instanceof ExitLock);
    }

    @Test
    public void grabTreasureChapsTreasuresIncrements() {
        var maze = initGeneralTestMaze();
        assert (maze.getChap().getTreasuresCollected() == 0);
        maze.moveChap(Direction.RIGHT);
        assert (maze.getChap().getTreasuresCollected() == 1);
    }

    @Test
    public void moveIntoWall() {
        var maze = initGeneralTestMaze();
        maze.moveChap(Direction.RIGHT);
        maze.moveChap(Direction.UP);
        var beforeMove = getChapLocation(maze);
        maze.moveChap(Direction.UP);
        assertChapPos(maze, beforeMove);
    }

    @Test
    public void moveIntoLockedDoorNoKey() {
        var maze = initGeneralTestMaze();
        var beforeMove = getChapLocation(maze);
        maze.moveChap(Direction.LEFT);
        assertChapPos(maze, beforeMove);
    }

    @Test
    public void moveIntoExitLock() {
        var maze = initGeneralTestMaze();
        var beforeMove = getChapLocation(maze);
        maze.moveChap(Direction.UP);
        assertChapPos(maze, beforeMove);
    }

    @Test
    public void getBlueKeyCellBecomesFree() {
        var maze = initGeneralTestMaze();
        maze.moveChap(Direction.RIGHT);
        maze.moveChap(Direction.DOWN);
        assert (maze.getChap().getLocation() instanceof Free);
    }

    @Test
    public void getBlueKeyGoesIntoBackpack() {
        var maze = initGeneralTestMaze();
        maze.moveChap(Direction.RIGHT);
        maze.moveChap(Direction.DOWN);
        assert (maze.getChap().backpackContains(Key.Colour.BLUE));
    }

    @Test
    public void getBlueKeyBlueDoorOpens() {
        var maze = initGeneralTestMaze();
        maze.moveChap(Direction.RIGHT);
        maze.moveChap(Direction.DOWN);
        maze.moveChap(Direction.LEFT);
        maze.moveChap(Direction.UP);
        var beforeMove = getChapLocation(maze);
        maze.moveChap(Direction.LEFT);
        assert (maze.getChap().getLocation().getCol() - beforeMove.x == -1);
    }

    @Test
    public void getRedKeyBlueDoorDoesntOpen() {
        var maze = initGeneralTestMaze();
        maze.moveChap(Direction.DOWN);
        maze.moveChap(Direction.UP);
        var beforeMove = getChapLocation(maze);
        maze.moveChap(Direction.LEFT);
        assertChapPos(maze, beforeMove);
    }

    @Test
    public void getRedKeyGoesIntoBackpack() {
        var maze = initGeneralTestMaze();
        maze.moveChap(Direction.DOWN);
        assert (maze.getChap().backpackContains(Key.Colour.RED));
    }

    @Test
    public void collectAllTreasuresExitLockFlips() {
        var maze = initGeneralTestMaze();
        maze.moveChap(Direction.RIGHT);
        maze.moveChap(Direction.DOWN);
        maze.moveChap(Direction.UP);
        maze.moveChap(Direction.LEFT);
        maze.moveChap(Direction.LEFT);
        maze.moveChap(Direction.LEFT);
        assert (maze.getTiles()[2][1] instanceof Free);
    }

    @Test
    public void goToExitLevelOver() {
        var maze = initGeneralTestMaze();
        maze.moveChap(Direction.RIGHT);
        maze.moveChap(Direction.DOWN);
        maze.moveChap(Direction.UP);
        maze.moveChap(Direction.LEFT);
        maze.moveChap(Direction.LEFT);
        maze.moveChap(Direction.LEFT);
        maze.moveChap(Direction.RIGHT);
        maze.moveChap(Direction.RIGHT);
        maze.moveChap(Direction.UP);
        maze.moveChap(Direction.UP);
        assert (maze.isLevelOver());
    }

    @Test
    public void infoFieldDisplaysInfo() {
        var maze = initGeneralTestMaze();
        maze.moveChap(Direction.RIGHT);
        maze.moveChap(Direction.UP);
        assertEquals("Test", ((InfoField) maze.getChap().getLocation()).getInfo());
    }

    @Test
    public void invalidActorNameThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            Actor a = new Actor(new Free(0, 0), "") {
            };
        });
    }

    @Test
    public void negativeTotalTreasuresThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            Maze m = new Maze(new Tile[][]{{new Free(0, 0)}}, -1);
        });
    }

    @Test
    public void moveBlockLeft() {
        var maze = initBlockTestMaze();
        var initialLoc = getFirstBlockLocation(maze);
        maze.moveChap(Direction.LEFT);
        assert (getFirstBlockLocation(maze).x - initialLoc.x == -1);
    }

    @Test
    public void moveBlockRight() {
        var maze = initBlockTestMaze();
        var initialLoc = getFirstBlockLocation(maze);
        maze.moveChap(Direction.DOWN);
        maze.moveChap(Direction.LEFT);
        maze.moveChap(Direction.LEFT);
        maze.moveChap(Direction.UP);
        maze.moveChap(Direction.RIGHT);
        assert (getFirstBlockLocation(maze).x - initialLoc.x == 1);
    }

    @Test
    public void moveBlockUp() {
        var maze = initBlockTestMaze();
        var initialLoc = getFirstBlockLocation(maze);
        maze.moveChap(Direction.DOWN);
        maze.moveChap(Direction.LEFT);
        maze.moveChap(Direction.UP);
        assert (getFirstBlockLocation(maze).y - initialLoc.y == -1);
    }

    @Test
    public void moveBlockDown() {
        var maze = initBlockTestMaze();
        var initialLoc = getFirstBlockLocation(maze);
        maze.moveChap(Direction.UP);
        maze.moveChap(Direction.LEFT);
        maze.moveChap(Direction.DOWN);
        assert (getFirstBlockLocation(maze).y - initialLoc.y == 1);
    }

    @Test
    public void moveBlockLeftIntoWall() {
        var maze = initBlockTestMaze();
        maze.moveChap(Direction.LEFT);
        var locAfterMove = getFirstBlockLocation(maze);
        maze.moveChap(Direction.LEFT);
        assertEquals(locAfterMove, getFirstBlockLocation(maze));
    }

    @Test
    public void moveBlockRightIntoWall() {
        var maze = initBlockTestMaze();
        maze.moveChap(Direction.DOWN);
        maze.moveChap(Direction.LEFT);
        maze.moveChap(Direction.LEFT);
        maze.moveChap(Direction.UP);
        maze.moveChap(Direction.RIGHT);
        var locAfterMove = getFirstBlockLocation(maze);
        maze.moveChap(Direction.RIGHT);
        assertEquals(locAfterMove, getFirstBlockLocation(maze));
    }

    @Test
    public void moveBlockUpIntoWall() {
        var maze = initBlockTestMaze();
        maze.moveChap(Direction.DOWN);
        maze.moveChap(Direction.LEFT);
        maze.moveChap(Direction.UP);
        var locAfterMove = getFirstBlockLocation(maze);
        maze.moveChap(Direction.UP);
        assertEquals(locAfterMove, getFirstBlockLocation(maze));
    }

    @Test
    public void moveBlockDownIntoWall() {
        var maze = initBlockTestMaze();
        maze.moveChap(Direction.UP);
        maze.moveChap(Direction.LEFT);
        maze.moveChap(Direction.DOWN);
        var locAfterMove = getFirstBlockLocation(maze);
        maze.moveChap(Direction.DOWN);
        assertEquals(locAfterMove, getFirstBlockLocation(maze));
    }

    @Test
    public void chapDiesInLava() {
        var maze = initLavaTestMaze();
        maze.moveChap(Direction.RIGHT);
        assert (maze.isLevelOver());
    }

    @Test
    public void moveBlockIntoLavaWorks() {
        var maze = initLavaTestMaze();
        assert (maze.getTiles()[0][0] instanceof Lava);
        maze.moveChap(Direction.LEFT);
        assert (maze.getTiles()[0][0] instanceof Free);
    }

    @Test
    public void moveBlockIntoLavaWalkable() {
        var maze = initLavaTestMaze();
        maze.moveChap(Direction.LEFT);
        var locBeforeMove = getChapLocation(maze);
        maze.moveChap(Direction.LEFT);
        assert (getChapLocation(maze).x - locBeforeMove.x == -1);
    }

    @Test
    public void cobraMoves() {
        var maze = initCobraTestMaze();
        var cobra = maze.getCobras().get(0);
        maze.moveActor(cobra, Direction.UP);
    }

    @Test
    public void cobraLoops() {
        var maze = initCobraTestMaze();
        var cobra = maze.getCobras().get(0);
        maze.moveActor(cobra, Direction.UP);
        maze.moveActor(cobra, Direction.RIGHT);
        maze.moveActor(cobra, Direction.DOWN);
        maze.moveActor(cobra, Direction.LEFT);
    }

    @Test
    public void cobraCantMoveIntoWall() {
        var maze = initCobraTestMaze();
        var cobra = maze.getCobras().get(0);
        var beforeMove = getFirstCobraLocation(maze);
        maze.moveActor(cobra, Direction.DOWN);
        assertEquals(beforeMove, getFirstCobraLocation(maze));
        assert (maze.getCobras().get(0).getDir() == Direction.DOWN);
    }

    @Test
    public void cobraCantMoveIntoLockedDoor() {
        var maze = initCobraTestMaze();
        var cobra = maze.getCobras().get(0);
        maze.moveActor(cobra, Direction.UP);
        var beforeMove = getFirstCobraLocation(maze);
        maze.moveActor(cobra, Direction.UP);
        assertEquals(beforeMove, getFirstCobraLocation(maze));
        assert (maze.getCobras().get(0).getDir() == Direction.UP);
    }

    @Test
    public void cobraMoveIntoChapKillsChap() {
        var maze = initCobraTestMaze();
        var cobra = maze.getCobras().get(0);
        assertFalse(maze.isLevelOver());
        assertEquals(maze.getChap().getLocation(), maze.getTiles()[2][2]);
        maze.moveActor(cobra, Direction.RIGHT);
        assert (maze.isLevelOver());
    }

    @Test
    public void cobraMoveIntoChapCobraMoves() {
        var maze = initCobraTestMaze();
        var cobra = maze.getCobras().get(0);
        assertFalse(maze.isLevelOver());
        assertEquals(maze.getChap().getLocation(), maze.getTiles()[2][2]);
        var locBeforeMove = getFirstCobraLocation(maze);
        maze.moveActor(cobra, Direction.RIGHT);
        assert (getFirstCobraLocation(maze).x - locBeforeMove.x == 1);
    }

    @Test
    public void chapMovesIntoCobraChapDies() {
        var maze = initCobraTestMaze();
        maze.moveChap(Direction.LEFT);
        assert (maze.isLevelOver());
    }

    @Test
    public void chapMovesIntoCobraChapDoesntMove() {
        var maze = initCobraTestMaze();
        var locBeforeMove = getChapLocation(maze);
        maze.moveChap(Direction.LEFT);
        assertEquals(locBeforeMove, getChapLocation(maze));
    }

    @Test
    public void cantMoveBlockOnTileWithNoBlock() {
        var tiles = createLavaTestMaze();
        tiles[1][0].setHasBlock(true);
        var maze = new Maze(tiles, 1, new ArrayList<>(), null);
        assertThrows(IllegalArgumentException.class, () -> maze.moveChap(Direction.LEFT));
    }

   /* @Test
    public void testMovementThread() {
        var tiles = createCobraTestMaze();
        var cobra = new Cobra(new Free(1, 2),
                new ArrayDeque<>(Arrays.asList(Maze.Direction.UP, Maze.Direction.RIGHT, Maze.Direction.DOWN)));
        var maze = new Maze(tiles, 1, null, new ArrayList<>() {{
            add(cobra);
        }});
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assert (maze.isLevelOver());
        assert (maze.cobraThreadStopped());
    }*/


    static class Location {
        private final int x;
        private final int y;

        public Location(int x, int y) {
            this.x = x;
            this.y = y;
        }

        //-----Generated by Intellij------

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Location location = (Location) o;
            return x == location.x &&
                    y == location.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

}