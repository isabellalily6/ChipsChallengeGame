package nz.ac.vuw.ecs.swen225.gp20.persistence;
import nz.ac.vuw.ecs.swen225.gp20.maze.Tile;

public class Level {
	
	private Tile[][] map; //array describing the level tiles using [col][row]
	private int treasures; //number of treasures in the level
	
	public Level(Tile[][] map, int treasures) {
		this.map = map;
		this.treasures = treasures;
	}
	
	public Tile[][] getMap(){
		return map;
	}

	public int getTreasures() {
		return treasures;
	}
}
