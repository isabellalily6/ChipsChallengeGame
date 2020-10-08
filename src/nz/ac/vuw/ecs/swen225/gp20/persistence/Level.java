package nz.ac.vuw.ecs.swen225.gp20.persistence;
import java.util.ArrayList;

import nz.ac.vuw.ecs.swen225.gp20.maze.Player;
import nz.ac.vuw.ecs.swen225.gp20.maze.Tile;

public class Level {
	
	private Tile[][] map; //array describing the level tiles using [col][row]
	private int treasures; //number of treasures in the level
	private Player chap;
	private ArrayList<Block> blocks;
	
	public Level(Tile[][] map, int treasures, Player chap) {
		this.map = map;
		this.treasures = treasures;
		this.chap = chap;
	}
	
	public Level(Tile[][] map, int treasures, Player chap, ArrayList<Block> blocks) {
		this.map = map;
		this.treasures = treasures;
		this.chap = chap;
		this.blocks = blocks;
	}
	
	public Tile[][] getMap(){
		return map;
	}

	public int getTreasures() {
		return treasures;
	}

	public Player getChap() {
		return chap;
	}

	public ArrayList<Block> getBlocks() {
		return blocks;
	}
}
