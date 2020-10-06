package nz.ac.vuw.ecs.swen225.gp20.monkey;

import nz.ac.vuw.ecs.swen225.gp20.application.GUI;
import nz.ac.vuw.ecs.swen225.gp20.application.Main;
import nz.ac.vuw.ecs.swen225.gp20.maze.*;
import org.junit.jupiter.api.Test;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.Lock;

/**
 * class made to test application of game
 * built of specific and random testing
 */
public class MonkeyTesting {

    /**
     * tests monkey test without GUI
     */
    @Test
    public void monkeyTestWithoutGUI () {
        Random random = new Random();
        // fill up direction ArrayList
        ArrayList<Maze.Direction> directions = new ArrayList<>();
        directions.add(Maze.Direction.UP);
        directions.add(Maze.Direction.DOWN);
        directions.add(Maze.Direction.LEFT);
        directions.add(Maze.Direction.RIGHT);
        Main main = new Main();
        Maze maze = main.getMaze();
        int x = 0;

        while (x++ < 999 && main.getTimeLeft() > 0) {
            try {
                Thread.sleep(100, 0);
                Tile[][] tiles = maze.getTiles();
                HashMap<Tile, Integer> candidates = new HashMap<>();

                Tile up = tiles[maze.getChap().getLocation().getCol()]
                        [Math.max(maze.getChap().getLocation().getRow() - 1, 0)];

                // check if tile is a valuable tile
                if (up instanceof Treasure || up instanceof Key || (up instanceof LockedDoor
                        && maze.getChap().backpackContains(((LockedDoor) up).getLockColour()))) {
                    candidates.put(up, 1); // tile is valuable
                }
                else if (up.isAccessible()) candidates.put(up, 0); // tile is not valuable

                Tile down = tiles[maze.getChap().getLocation().getCol()]
                        [Math.min(maze.getChap().getLocation().getRow() + 1, tiles.length - 1)];
                if (down instanceof Treasure || down instanceof Key || (down instanceof LockedDoor
                        && maze.getChap().backpackContains(((LockedDoor) down).getLockColour()))) {
                    candidates.put(down, 1);
                }
                else if (down.isAccessible()) candidates.put(down, 0);

                Tile left = tiles[Math.max(maze.getChap().getLocation().getCol() - 1, 0)]
                        [maze.getChap().getLocation().getRow()];
                if (left instanceof Treasure || left instanceof Key || (left instanceof LockedDoor
                        && maze.getChap().backpackContains(((LockedDoor) left).getLockColour()))) {
                    candidates.put(left, 1);
                }
                else if (left.isAccessible()) candidates.put(left, 0);

                Tile right = tiles[Math.min(maze.getChap().getLocation().getCol() + 1, tiles[0].length)]
                        [maze.getChap().getLocation().getRow()];
                if (right instanceof Treasure || right instanceof Key || (right instanceof LockedDoor
                        && maze.getChap().backpackContains(((LockedDoor) right).getLockColour()))) {
                    candidates.put(right, 1);
                }
                else if (right.isAccessible()) candidates.put(right, 0);

                // final candidate
                Tile candidate;
                // modify map into a list
                ArrayList<Tile> updatedCandidates = new ArrayList<>(candidates.keySet());
                if (candidates.containsValue(1)) { // a valuable tile exists
                    for (Map.Entry entry : candidates.entrySet()) {
                        // remove non-valuable tiles
                        if ((int) entry.getValue() == 0) updatedCandidates.remove(entry.getKey());
                    }
                }
                // get random input from remaining candidates
                candidate = updatedCandidates.get(random.nextInt(updatedCandidates.size()));

                // move chap
                if (candidate == up) maze.moveChap(directions.get(0));
                else if (candidate == down) maze.moveChap(directions.get(1));
                else if (candidate == left) maze.moveChap(directions.get(2));
                else maze.moveChap(directions.get(3));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * tests monkey test with GUI
     * comments same as previous test
     */
    @Test
    public void monkeyTestWithGUI () {
        Random random = new Random();
        Main main = new Main();
        Maze maze = main.getMaze();
        GUI gui = main.getGui();
        ArrayList<KeyEvent> keyEvents = new ArrayList<>();
        // up
        keyEvents.add(new KeyEvent(gui, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, 38));
        // down
        keyEvents.add(new KeyEvent(gui, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, 40));
        // left
        keyEvents.add(new KeyEvent(gui, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, 37));
        // right
        keyEvents.add(new KeyEvent(gui, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, 39));
        int x = 0;

        while (x++ < 999) {
            try {
                Tile[][] tiles = maze.getTiles();
                HashMap<Tile, Integer> candidates = new HashMap<>();

                Tile up = tiles[maze.getChap().getLocation().getCol()][maze.getChap().getLocation().getRow() - 1];
                if (up instanceof Treasure || up instanceof Key || (up instanceof LockedDoor
                        && maze.getChap().backpackContains(((LockedDoor) up).getLockColour()))) {
                    candidates.put(up, 1);
                }
                else if (up.isAccessible()) candidates.put(up, 0);

                Tile down = tiles[maze.getChap().getLocation().getCol()][maze.getChap().getLocation().getRow() + 1];
                if (down instanceof Treasure || down instanceof Key || (down instanceof LockedDoor
                        && maze.getChap().backpackContains(((LockedDoor) down).getLockColour()))) {
                    candidates.put(down, 1);
                }
                else if (down.isAccessible()) candidates.put(down, 0);

                Tile left = tiles[maze.getChap().getLocation().getCol() - 1][maze.getChap().getLocation().getRow()];
                if (left instanceof Treasure || left instanceof Key || (left instanceof LockedDoor
                        && maze.getChap().backpackContains(((LockedDoor) left).getLockColour()))) {
                    candidates.put(left, 1);
                }
                else if (left.isAccessible()) candidates.put(left, 0);

                Tile right = tiles[maze.getChap().getLocation().getCol() + 1][maze.getChap().getLocation().getRow()];
                if (right instanceof Treasure || right instanceof Key || (right instanceof LockedDoor
                        && maze.getChap().backpackContains(((LockedDoor) right).getLockColour()))) {
                    candidates.put(right, 1);
                }
                else if (right.isAccessible()) candidates.put(right, 0);

                Tile candidate;
                ArrayList<Tile> updatedCandidates = new ArrayList<>(candidates.keySet());
                if (candidates.containsValue(1)) {
                    for (Map.Entry entry : candidates.entrySet()) {
                        if ((int) entry.getValue() == 0) updatedCandidates.remove(entry.getKey());
                    }
                }
                candidate = updatedCandidates.get(random.nextInt(updatedCandidates.size()));

                if (candidate == up) gui.dispatchEvent(keyEvents.get(0));
                else if (candidate == down) gui.dispatchEvent(keyEvents.get(1));
                else if (candidate == left) gui.dispatchEvent(keyEvents.get(2));
                else gui.dispatchEvent(keyEvents.get(3));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}