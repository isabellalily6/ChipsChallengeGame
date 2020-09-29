package nz.ac.vuw.ecs.swen225.gp20.monkey;

import nz.ac.vuw.ecs.swen225.gp20.application.Main;
import nz.ac.vuw.ecs.swen225.gp20.maze.Maze;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

/**
 * class made to test application of game
 * built of specific and random testing
 */
public class MonkeyTesting {

    @Test
    public void monkeyTest () {
        Random random = new Random();
        ArrayList<Maze.Direction> directions = new ArrayList<>();
        directions.add(Maze.Direction.UP);
        directions.add(Maze.Direction.DOWN);
        directions.add(Maze.Direction.LEFT);
        directions.add(Maze.Direction.RIGHT);
        Main main = new Main();
        Maze maze = main.getMaze();
        int x = 0;

        while (x++ < 999) {
            try {
                maze.moveChap(directions.get(random.nextInt(4)));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
