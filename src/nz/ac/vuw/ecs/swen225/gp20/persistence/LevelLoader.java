package nz.ac.vuw.ecs.swen225.gp20.persistence;


import java.io.File;
import java.io.FileReader;

import nz.ac.vuw.ecs.swen225.gp20.maze.Tile;

public class LevelLoader {
	
	/**
	 * Reads a JSON file describing the level
	 * TODO: Don't hard code height and width
	 * TODO: Ask at help desk how to use Jackson library
	 * 
	 * @param level number
	 */
	public Level load(int levelNumber) {
		String filename = "levels\\level" + levelNumber + ".json";
		
		int mapHeight = 9;
		int mapWidth = 9;
		
		Tile[][] map = new Tile[mapHeight][mapWidth]; 
		int treasures = 0;
		
		 
		
		return new Level(map, treasures);
	}
}
