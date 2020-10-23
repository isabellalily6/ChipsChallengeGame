package persistence;


import org.junit.Test;

import nz.ac.vuw.ecs.swen225.gp20.maze.Exit;
import nz.ac.vuw.ecs.swen225.gp20.maze.ExitLock;
import nz.ac.vuw.ecs.swen225.gp20.maze.Free;
import nz.ac.vuw.ecs.swen225.gp20.maze.InfoField;
import nz.ac.vuw.ecs.swen225.gp20.maze.Key;
import nz.ac.vuw.ecs.swen225.gp20.maze.LockedDoor;
import nz.ac.vuw.ecs.swen225.gp20.maze.Player;
import nz.ac.vuw.ecs.swen225.gp20.maze.Tile;
import nz.ac.vuw.ecs.swen225.gp20.maze.Treasure;
import nz.ac.vuw.ecs.swen225.gp20.maze.Wall;
import nz.ac.vuw.ecs.swen225.gp20.persistence.Level;
import nz.ac.vuw.ecs.swen225.gp20.persistence.LevelLoader;

/**
 * Persistence test contains all the tests for the persistence module.
 * Tests loading a game and a level, and tests saving a game
 * 
 * @author Matt Rowling 300487163
 */
public class PersistenceTest {
	
	private Tile level1ChapTile = new Free(6, 7);
	
	//THE TEST FAILS BECAUSE THE COLS AND ROWS ARE ENTERED WRONG HERE
	private Tile[][] level1Map = {
			{ new Free(0, 0), new Free(0, 1), new Wall(0, 2), new Wall(0, 3), new Wall(0, 4), new Wall(0, 5), new Wall(0, 6), new Free(0, 7), new Wall(0, 8), new Wall(0, 9), new Wall(0, 10), new Wall(0, 11), new Wall(0, 12), new Free(0, 13), new Free(0, 14)},
			{ new Free(1, 0), new Free(1, 1), new Wall(1, 2), new Free(1, 3), new Free(1, 4), new Free(1, 5), new Wall(1, 6), new Wall(1, 7), new Wall(1, 8), new Free(1, 9), new Free(1, 10), new Free(1, 11), new Wall(1, 12), new Free(1,13 ), new Free(1, 14)},
			{ new Free(2, 0), new Free(2, 1), new Wall(2, 2), new Free(2, 3), new Treasure(2, 4), new Free(2, 5), new Wall(2, 6), new Exit(2, 7), new Wall(2, 8), new Free(2, 9), new Treasure(2, 10), new Free(2, 11), new Wall(2, 12), new Free(2, 13), new Free(2, 14)},
			{ new Wall(3, 0), new Wall(3, 1), new Wall(3, 2), new Wall(3, 3), new Wall(3, 4), new LockedDoor(3, 5, Key.Colour.GREEN), new Wall(3, 6), new ExitLock(3, 7), new Wall(3, 8), new LockedDoor(3, 9, Key.Colour.GREEN), new Wall(3, 10), new Wall(3, 11), new Wall(3, 12), new Wall(3, 13), new Wall(3, 14)},
			{ new Wall(4, 0), new Free(4, 1), new Key(4, 2, Key.Colour.GREEN), new Free(4, 3), new LockedDoor(4, 4, Key.Colour.BLUE), new Free(4, 5),  new Free(4, 6), new Free(4, 7), new Free(4, 8), new Free(4, 9),  new LockedDoor(4, 10, Key.Colour.RED), new Free(4, 11), new Key(4, 12, Key.Colour.GREEN), new Free(4, 13), new Wall(4, 14)},
			{ new Wall(5, 0), new Free(5, 1), new Treasure(5, 2),  new Free(5, 3), new Wall(5, 4), new Key(5, 5, Key.Colour.BLUE), new Free(5, 6), new InfoField(5, 7, "Collect chips to get past the chip socket. Use keys to open doors."), new Free(5, 8), new Key(5, 9, Key.Colour.RED), new Wall(5, 10), new Free(5, 11), new Treasure(5, 12), new Free(5, 13), new Wall(5, 14)},
			{ new Wall(6, 0), new Wall(6, 1), new Wall(6, 2), new Wall(6, 3), new Wall(6, 4), new Treasure(6, 5), new Free(6, 6), level1ChapTile, new Free(6, 8), new Treasure(6, 9), new Wall(6, 10), new Wall(6, 11), new Wall(6, 12), new Wall(6, 13), new Wall(6, 14)},
			{ new Wall(7, 0), new Free(7, 1), new Treasure(7, 2),  new Free(7, 3), new Wall(7, 4), new Key(7, 5, Key.Colour.BLUE), new Free(7, 6), new Free(7, 7), new Free(7, 8), new Key(7, 9, Key.Colour.RED), new Wall(7, 10), new Free(7, 11), new Treasure(7, 12), new Free(7, 13), new Wall(7, 14)},
			{ new Wall(8, 0), new Free(8, 1), new Free(8, 2),  new Free(8, 3), new Wall(8, 4), new Key(8, 5, Key.Colour.RED), new Free(8, 6), new Free(8, 7), new Free(8, 8), new Key(8, 9, Key.Colour.BLUE), new Wall(8, 10), new Free(8, 11), new Free(8, 12), new Free(8, 13), new Wall(8, 14)},
			{ new Wall(9, 0), new Wall(9, 1), new Wall(9, 2), new Wall(9, 3), new Wall(9, 4), new Wall(9, 5), new LockedDoor(9, 6, Key.Colour.GREEN), new Wall(9, 7), new LockedDoor(9, 8, Key.Colour.GREEN), new Wall(9, 9), new Wall(9, 10), new Wall(9, 11), new Wall(9, 12), new Wall(9, 13), new Wall(9, 14)},
			{ new Free(10, 0), new Free(10, 1), new Free(10, 2), new Free(10, 3), new Wall(10, 4), new Free(10, 5), new Free(10, 6), new Wall(10, 7), new Free(10, 8), new Free(10, 9), new Wall(10, 10), new Free(10, 11), new Free(10, 12), new Free(10, 13), new Free(10, 14)},
			{ new Free(11, 0), new Free(11, 1), new Free(11, 2), new Free(11, 3), new Wall(11, 4), new Free(11, 5), new Treasure(11, 6), new Wall(11, 7), new Treasure(11, 8), new Free(11, 9), new Wall(11, 10), new Free(11, 11), new Free(11, 12), new Free(11, 13), new Free(11, 14)},
			{ new Free(12, 0), new Free(12, 1), new Free(12, 2), new Free(12, 3), new Wall(12, 4), new Free(12, 5), new Free(12, 6), new Wall(12, 7), new Key(12, 8, Key.Colour.GREEN), new Free(12, 9), new Wall(12, 10), new Free(12, 11), new Free(12, 12), new Free(12, 13), new Free(12, 14)},
			{ new Free(13, 0), new Free(13, 1), new Free(13, 2), new Free(13, 3), new Wall(13, 4), new Wall(13, 5), new Wall(13, 6), new Wall(13, 7), new Wall(13, 8), new Wall(13, 9), new Wall(13, 10), new Free(13, 11), new Free(13, 12), new Free(13, 13), new Free(13, 14)},
			{ new Free(14, 0), new Free(14, 1), new Free(14, 2), new Free(14, 3), new Free(14, 4), new Free(14, 5), new Free(14, 6), new Free(14, 7), new Free(14, 8), new Free(14, 9), new Free(14, 10), new Free(14, 11), new Free(14, 12), new Free(14, 13), new Free(14, 14)}
	};

	@Test
	public void loadLevel1() {
		Level level1 = new Level(level1Map, 11, new Player(level1ChapTile));
		Level level1Loaded = LevelLoader.load(1);
		
		for(int c = 0; c < level1Map.length; c++) {
			for(int r = 0; r < level1Map[0].length; r++) {
				if(level1Map[c][r].getCol() != level1.getMap()[c][r].getCol()) {
					System.out.println("c = " + c + "\n"+ "r = " + r + "\n" + "actual col: " + level1Map[c][r].getCol() + "\n" + "produced r: " + level1.getMap()[c][r].getRow() + "\n");
				}
				assert(level1.getMap()[c][r].getCol() == level1Loaded.getMap()[c][r].getCol());
				assert(level1.getMap()[c][r].getRow() == level1Loaded.getMap()[c][r].getRow());
				if(!level1.getMap()[c][r].getClass().equals(level1Loaded.getMap()[r][c].getClass())) {
					System.out.println("c = " + c + "| r = " + r);
					System.out.println("level1 class: " + level1.getMap()[c][r].getClass());
					System.out.println("level1Loaded class: " + level1Loaded.getMap()[c][r].getClass());
				}
				assert(level1.getMap()[c][r].getClass().equals(level1Loaded.getMap()[c][r].getClass()));
			}
		}
	}
}
