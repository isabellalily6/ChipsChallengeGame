package nz.ac.vuw.ecs.swen225.gp20.persistence;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import nz.ac.vuw.ecs.swen225.gp20.application.Main;
import nz.ac.vuw.ecs.swen225.gp20.maze.Exit;
import nz.ac.vuw.ecs.swen225.gp20.maze.ExitLock;
import nz.ac.vuw.ecs.swen225.gp20.maze.Free;
import nz.ac.vuw.ecs.swen225.gp20.maze.InfoField;
import nz.ac.vuw.ecs.swen225.gp20.maze.Key;
import nz.ac.vuw.ecs.swen225.gp20.maze.LockedDoor;
import nz.ac.vuw.ecs.swen225.gp20.maze.Maze;
import nz.ac.vuw.ecs.swen225.gp20.maze.Tile;
import nz.ac.vuw.ecs.swen225.gp20.maze.Treasure;
import nz.ac.vuw.ecs.swen225.gp20.maze.Wall;

public class LevelLoader {
	
	/**
	 * Reads a JSON file describing the level
	 * TODO: Don't hard code height and width
	 * 
	 * @param levelNumber
	 */
	public static Level load(int levelNumber) {
		String filename = "levels/level" + levelNumber + ".json";
		
		int mapWidth = 15;
		int mapHeight = 15;
		
		Tile[][] map = new Tile[mapWidth][mapHeight]; 
		int treasures = 0; //total treasures in the level
		
		try {
			
			//Read JSON file into a list of JsonObjects
			JsonReader reader = Json.createReader(new FileReader(filename));
			JsonArray jsonArray = reader.readArray();
			reader.close();
			List<JsonObject> jsonTiles = jsonArray.getValuesAs(JsonObject.class);
			
			//Iterate through the JsonArray to create new tile objects and put them in the map
			for(int i = 0; i < jsonTiles.size(); i++) {
				
				JsonObject jsonTileObj = jsonTiles.get(i);
				String tileType = jsonTileObj.getString("type");
				int row = i / mapWidth;
				int col = i % mapWidth;
				
				
				//Determine the colour if applicable
				Key.Colour tileColor = null;
				if(tileType.equals("Key") || tileType.equals("LockedDoor")) {
					String colorName = jsonTileObj.getString("color");
					if(colorName.contentEquals("red")) {
						tileColor = Key.Colour.RED;
						
					} else if(colorName.contentEquals("green")) {
						tileColor = Key.Colour.GREEN;
						
					} else if(colorName.contentEquals("blue")) {
						tileColor = Key.Colour.BLUE;
						
					}
				}
				
				
				//Put a new tile object into the map
				if(tileType.equals("Wall")) {
					map[col][row] = new Wall(col, row);
					
				} else if(tileType.equals("Free")) {
					map[col][row] = new Free(col, row);
					
				} else if(tileType.equals("Key")) {
					map[col][row] = new Key(col, row, tileColor);
					
				} else if(tileType.equals("LockedDoor")) {
					map[col][row] = new LockedDoor(col, row, tileColor);
					
				} else if(tileType.equals("InfoField")) {
					map[col][row] = new InfoField(col, row, jsonTileObj.getString("info"));
					
				} else if(tileType.equals("Treasure")) {
					treasures++;
					map[col][row] = new Treasure(col, row);
					
				} else if(tileType.equals("ExitLock")) {
					map[col][row] = new ExitLock(col, row);
					
				} else if(tileType.equals("Exit")) {
					map[col][row] = new Exit(col, row);
					
				}
				
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return new Level(map, treasures);
	}
	
	/**
	 * Gets the current game state and returns a string
	 * @param game
	 */
	public String getGameState(Main application) {
		Maze game = application.getMaze();
		int timeLeft = application.getTimeLeft();
		
		String gameState = "{";
		
		Tile[][] map = game.getTiles();
		
		//level
		gameState += "\"level\": \"" + game.getLevel() + "\",";
		
		//timeLeft
		gameState += "\"timeLeft\": \"" + timeLeft + "\",";
		
		//cols
		gameState += "\"cols\": \"" + map.length + "\",";
		
		//rows
		gameState += "\"rows\": \"" + map[0].length + "\",";
		
		//map
		gameState += "\"map\": [";	
		for(int col = 0; col < map.length; col++) {
			for(int row = 0; row < map[0].length; row++) {
				gameState += map[col][row].toString();
			}
		}
		gameState += ",";
				
		//treasuresLeft
		gameState += "\"treasuresLeft\": \"" + game.getTreasuresLeft() + "\",";
		
		//chapCol
		gameState += "\"chapCol\": \"" + game.getChap().getLocation().getCol() + "\",";
		
		//chapRow
		gameState += "\"chapRow\": \"" + game.getChap().getLocation().getRow() + "\",";
		
		//keysCollected
		gameState += "\"keysCollected\": [";
		for(Key.Colour colour : game.getChap().getBackpack()) {
			gameState += "{\"colour\": \"" + colour + "\"},";
		}
		if(!game.getChap().getBackpack().isEmpty()) {
			gameState = gameState.substring(0, gameState.length() - 1);
		}
		gameState += "]";
		
		gameState += "}";
		
		return gameState;
		
		/**
		*/
	}
	
	/**
	 * Saves the game state string to a json file
	 * @param toSave 
	 */
	public void saveGameState(String toSave) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("levels/gameState.json"));
		      writer.write(toSave);
		      writer.close();
		} catch (IOException e) {
		      System.out.println("Error saving game state" + e);
		}
	}
	
	
	
}
