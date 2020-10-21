package nz.ac.vuw.ecs.swen225.gp20.monkey;

import nz.ac.vuw.ecs.swen225.gp20.application.GUI;
import nz.ac.vuw.ecs.swen225.gp20.application.Main;
import nz.ac.vuw.ecs.swen225.gp20.commons.Direction;
import nz.ac.vuw.ecs.swen225.gp20.maze.*;
import org.junit.jupiter.api.Test;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * class made to test application of game
 * built of specific and random testing
 */
public class MonkeyTesting {

    /**
     * tests monkey test without GUI
     */
    @Test
    public void randomMonkeyTestWithoutGUI() {
        double initialTime = System.currentTimeMillis(); // get start time
        Random random = new Random(); // initialise random number generator

        // fill up direction ArrayList
        ArrayList<Direction> directions = new ArrayList<>();
        directions.add(Direction.UP);
        directions.add(Direction.DOWN);
        directions.add(Direction.LEFT);
        directions.add(Direction.RIGHT);

        // get game elements
        Main main = new Main(false, 1); // start test game
        Maze maze = main.getMaze();

        int steps = 0;

        // run until level is complete
        while (!maze.isLevelOver()) {
            try {
                steps++; // add step
                maze.moveChap(directions.get(random.nextInt(4))); // move randomly
            } catch (Exception e) {
                e.printStackTrace(); // print exception
            }
        }
        // print time taken and steps
        double endTime = System.currentTimeMillis();
        int seconds = (int) ((endTime - initialTime) / 1000.0);
        int minutes = (int) (seconds / 60.0);
        seconds -= minutes * 60;
        System.out.println("Time taken: " + minutes + " minutes. " + seconds + " seconds." + " steps: " + steps);
    }

    /**
     * tests random monkey test with GUI
     */
    @Test
    public void randomMonkeyTestWithGUI() {
        double initialTime = System.currentTimeMillis(); // get start time
        Random random = new Random(); // initialise random number generator

        // initialise game elements
        Main main = new Main(true, 1);
        Maze maze = main.getMaze();
        GUI gui = main.getGui();

        // initialise KeyEvent's
        ArrayList<KeyEvent> keyEvents = new ArrayList<>();
        // up
        keyEvents.add(new KeyEvent(gui, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, 38,
                KeyEvent.CHAR_UNDEFINED));
        // down
        keyEvents.add(new KeyEvent(gui, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, 40,
                KeyEvent.CHAR_UNDEFINED));
        // left
        keyEvents.add(new KeyEvent(gui, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, 37,
                KeyEvent.CHAR_UNDEFINED));
        // right
        keyEvents.add(new KeyEvent(gui, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, 39,
                KeyEvent.CHAR_UNDEFINED));

        int steps = 0;

        // run until level is complete
        while (!main.getMaze().isLevelOver()) {
            try {
                Tile[][] tiles = maze.getTiles(); // get maze tiles
                HashMap<Tile, Integer> candidates = new HashMap<>(); // initialise HashMap

                // get up tile
                Tile up = tiles[maze.getChap().getLocation().getCol()][maze.getChap().getLocation().getRow() - 1];

                // get down tile
                Tile down = tiles[maze.getChap().getLocation().getCol()][maze.getChap().getLocation().getRow() + 1];

                // get left tile
                Tile left = tiles[maze.getChap().getLocation().getCol() - 1][maze.getChap().getLocation().getRow()];

                // get right tile
                Tile right = tiles[maze.getChap().getLocation().getCol() + 1][maze.getChap().getLocation().getRow()];

                // check for value of the tiles
                int tileValue;
                if ((tileValue = getValue(up, maze)) != -1) candidates.put(up, tileValue);
                if ((tileValue = getValue(down, maze)) != -1) candidates.put(down, tileValue);
                if ((tileValue = getValue(left, maze)) != -1) candidates.put(left, tileValue);
                if ((tileValue = getValue(right, maze)) != -1) candidates.put(right, tileValue);
                // wall tile is never added into the map as it is never a valuable move


                // final candidate
                Tile candidate;
                // modify map into a list
                // get random input from remaining candidates
                candidate = (Tile)candidates.keySet().toArray()[random.nextInt(candidates.size())];

                // move chap
                if (candidate == up) gui.dispatchEvent(keyEvents.get(0));
                else if (candidate == down) gui.dispatchEvent(keyEvents.get(1));
                else if (candidate == left) gui.dispatchEvent(keyEvents.get(2));
                else gui.dispatchEvent(keyEvents.get(3));
                steps++; // a step has been taken
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        double endTime = System.currentTimeMillis();
        int seconds = (int) ((endTime - initialTime) / 1000.0);
        int minutes = (int) (seconds / 60.0);
        seconds -= minutes * 60;
        System.out.println("Time taken: " + minutes + " minutes. " + seconds + " seconds." + " steps: " + steps);
    }

    /**
     * return the value of a given tile
     * only used for intelligent tests
     *
     * @param tile the tile to check
     * @param maze the state of the maze
     * @return value of the tile
     */
    public int getValue(Tile tile, Maze maze) {
        // check if tile is valuable
        if (tile instanceof Treasure || tile instanceof Key || tile instanceof Exit || (tile instanceof LockedDoor
                && maze.getChap().backpackContains(((LockedDoor) tile).getLockColour()))) {
            return 1; // tile is valuable
        } else if (tile.isAccessible() && !(tile instanceof InfoField)) return 0; // tile is not valuable
        if (tile instanceof InfoField) return -1;
        return -2;
    }

    /**
     * tests intelligent monkey test without GUI
     */
    @Test
    public void intelligentMonkeyTestWithoutGUI() {
        double initialTime = System.currentTimeMillis(); // get start time
        Random random = new Random(); // initialise random number generator

        // fill up direction ArrayList
        ArrayList<Direction> directions = new ArrayList<>();
        directions.add(Direction.UP);
        directions.add(Direction.DOWN);
        directions.add(Direction.LEFT);
        directions.add(Direction.RIGHT);

        // get game elements
        Main main = new Main(false, 1); // start test game
        Maze maze = main.getMaze();

        int steps = 0;

        // run until level is complete
        while (!maze.isLevelOver()) {
            try {
                Tile[][] tiles = maze.getTiles(); // get maze tiles
                HashMap<Tile, Integer> candidates = new HashMap<>(); // initialise HashMap

                // get up tile
                Tile up = tiles[maze.getChap().getLocation().getCol()][maze.getChap().getLocation().getRow() - 1];

                // get down tile
                Tile down = tiles[maze.getChap().getLocation().getCol()][maze.getChap().getLocation().getRow() + 1];

                // get left tile
                Tile left = tiles[maze.getChap().getLocation().getCol() - 1][maze.getChap().getLocation().getRow()];

                // get right tile
                Tile right = tiles[maze.getChap().getLocation().getCol() + 1][maze.getChap().getLocation().getRow()];

                // check for value of the tiles
                int tileValue;
                if ((tileValue = getValue(up, maze)) >= 0) candidates.put(up, tileValue);
                if ((tileValue = getValue(down, maze)) >= 0) candidates.put(down, tileValue);
                if ((tileValue = getValue(left, maze)) >= 0) candidates.put(left, tileValue);
                if ((tileValue = getValue(right, maze)) >= 0) candidates.put(right, tileValue);
                // wall tile is never added into the map as it is never a valuable move


                // final candidate
                Tile candidate;
                // modify map into a list
                ArrayList<Tile> updatedCandidates = new ArrayList<>(candidates.keySet());
                if (candidates.containsValue(1)) { // a valuable tile exists
                    candidates.forEach((key, value) -> {
                        // remove non-valuable tiles
                        if (value == 0) updatedCandidates.remove(key);
                    });
                }
                // get random input from remaining candidates
                candidate = updatedCandidates.get(random.nextInt(updatedCandidates.size()));

                // move chap
                if (candidate == up) maze.moveChap(directions.get(0));
                else if (candidate == down) maze.moveChap(directions.get(1));
                else if (candidate == left) maze.moveChap(directions.get(2));
                else maze.moveChap(directions.get(3));
                steps++; // a step has been taken
            } catch (Exception e) {
                e.printStackTrace(); // print exception
            }
        }
        // print time taken and steps
        double endTime = System.currentTimeMillis();
        int seconds = (int) ((endTime - initialTime) / 1000.0);
        int minutes = (int) (seconds / 60.0);
        seconds -= minutes * 60;
        System.out.println("Time taken: " + minutes + " minutes. " + seconds + " seconds." + " steps: " + steps);
    }

    /**
     * tests intelligent monkey test with GUI
     */
    @Test
    public void intelligentMonkeyTestWithGUI() {
        double initialTime = System.currentTimeMillis(); // get start time
        Random random = new Random(); // initialise random number generator

        // initialise game elements
        Main main = new Main(true, 1); // start test game
        Maze maze = main.getMaze(); // get the game maze
        GUI gui = main.getGui(); // get the game gui

        // initialise KeyEvent's
        ArrayList<KeyEvent> keyEvents = new ArrayList<>();
        // up
        keyEvents.add(new KeyEvent(gui, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, 38,
                KeyEvent.CHAR_UNDEFINED));
        // down
        keyEvents.add(new KeyEvent(gui, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, 40,
                KeyEvent.CHAR_UNDEFINED));
        // left
        keyEvents.add(new KeyEvent(gui, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, 37,
                KeyEvent.CHAR_UNDEFINED));
        // right
        keyEvents.add(new KeyEvent(gui, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, 39,
                KeyEvent.CHAR_UNDEFINED));

        int steps = 0;

        // run until level is complete
        while (!maze.isLevelOver()) {
            try {
                Tile[][] tiles = maze.getTiles(); // get current state of game
                HashMap<Tile, Integer> candidates = new HashMap<>(); // initialise HashMap

                // get up tile
                Tile up = tiles[maze.getChap().getLocation().getCol()][maze.getChap().getLocation().getRow() - 1];

                // get down tile
                Tile down = tiles[maze.getChap().getLocation().getCol()][maze.getChap().getLocation().getRow() + 1];

                // get left tile
                Tile left = tiles[maze.getChap().getLocation().getCol() - 1][maze.getChap().getLocation().getRow()];

                // get right tile
                Tile right = tiles[maze.getChap().getLocation().getCol() + 1][maze.getChap().getLocation().getRow()];

                // check for value of the tiles
                int tileValue;
                if ((tileValue = getValue(up, maze)) >= 0) candidates.put(up, tileValue);
                if ((tileValue = getValue(down, maze)) >= 0) candidates.put(down, tileValue);
                if ((tileValue = getValue(left, maze)) >= 0) candidates.put(left, tileValue);
                if ((tileValue = getValue(right, maze)) >= 0) candidates.put(right, tileValue);

                // move valuable candidates to an ArrayList
                Tile candidate; // initialise final candidate
                ArrayList<Tile> updatedCandidates = new ArrayList<>(candidates.keySet());
                if (candidates.containsValue(1)) { // 1 or more tiles are valuable
                    candidates.forEach((key, value) -> {
                        if (value == 0) updatedCandidates.remove(key); // remove invaluable tiles
                    });
                }
                // get random move from remaining candidates
                candidate = updatedCandidates.get(random.nextInt(updatedCandidates.size()));

                // move chap
                if (candidate == up) gui.dispatchEvent(keyEvents.get(0));
                else if (candidate == down) gui.dispatchEvent(keyEvents.get(1));
                else if (candidate == left) gui.dispatchEvent(keyEvents.get(2));
                else gui.dispatchEvent(keyEvents.get(3));
                steps++; // a move has been made
            } catch (Exception e) {
                e.printStackTrace(); // print exception
            }
        }

        // print time taken and steps
        double endTime = System.currentTimeMillis();
        int seconds = (int) ((endTime - initialTime) / 1000.0);
        int minutes = (int) (seconds / 60.0);
        seconds -= minutes * 60;
        System.out.println("Time taken: " + minutes + " minutes. " + seconds + " seconds." + " steps: " + steps);
    }
}