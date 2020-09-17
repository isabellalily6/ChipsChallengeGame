package nz.ac.vuw.ecs.swen225.gp20.maze;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class MazeTests {
    private Tile[][] initMaze() {
        Tile[][] tiles = new Tile[10][10];
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                tiles[x][y] = new Free(x, y);
            }
        }
        return tiles;
    }

    @Test
    void test2dArrayShallowClone() {
        Tile[][] tiles = initMaze();
        Maze maze = new Maze(tiles);
        assertNotSame(tiles, maze.getTiles());
    }

    @Test
    void test2dArrayCloneShallowImmutable() {
        Tile[][] tiles = initMaze();
        Maze maze = new Maze(tiles);
        maze.getTiles()[0][0] = new Key(0, 0, Key.Colour.AMETHYST);
        assertFalse(maze.getTiles()[0][0] instanceof Key);
    }

}