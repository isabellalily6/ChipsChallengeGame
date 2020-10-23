package nz.ac.vuw.ecs.swen225.gp20.persistence;

import java.util.ArrayList;
import nz.ac.vuw.ecs.swen225.gp20.maze.Block;
import nz.ac.vuw.ecs.swen225.gp20.maze.Cobra;
import nz.ac.vuw.ecs.swen225.gp20.maze.Player;
import nz.ac.vuw.ecs.swen225.gp20.maze.Tile;

/**
 * Level stores information regarding the details of the level of the game. A
 * level object is used to send data when loading a game.
 *
 * @author Matt Rowling 300487163
 */
public class Level {

  private Tile[][] map; // 2d array describing the level tiles using [col][row]
  private int treasures; // number of treasures in the level
  private Player chap;
  private ArrayList<Block> blocks;
  private ArrayList<Cobra> cobras;

  /**
   * Creates a level object without blocks and cobras.
   * 
   * @param map 2d array of tiles
   * @param treasures number of treasure in the level
   * @param chap player object
   */
  public Level(Tile[][] map, int treasures, Player chap) {
    this.map = map;
    this.treasures = treasures;
    this.chap = chap;
  }

  /**
   * Creates a level object with blocks and cobras.
   * 
   * @param map 2d array of tiles
   * @param treasures number of treasure in the level
   * @param chap player object
   * @param blocks list of Block objects
   * @param cobras list of Cobra objects
   */
  public Level(Tile[][] map, int treasures, Player chap, ArrayList<Block> blocks,
      ArrayList<Cobra> cobras) {
    this.map = map;
    this.treasures = treasures;
    this.chap = chap;
    this.blocks = blocks;
    this.cobras = cobras;
  }

  /**
   * Gets the map.
   * 
   * @return Tile[][]
   */
  public Tile[][] getMap() {
    return map;
  }

  /**
   * Gets the number of treasures.
   * 
   * @return int
   */
  public int getTreasures() {
    return treasures;
  }

  /**
   * Gets Chap.
   * 
   * @return Player
   */
  public Player getChap() {
    return chap;
  }

  /**
   * Gets the list of Blocks.
   * 
   * @return ArrayList<Block>
   */
  public ArrayList<Block> getBlocks() {
    return blocks;
  }

  /**
   * Gets the list of Cobras.
   * 
   * @return ArrayList<Cobra>
   */
  public ArrayList<Cobra> getCobras() {
    return cobras;
  }
}
