package nz.ac.vuw.ecs.swen225.gp20.persistence;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

import nz.ac.vuw.ecs.swen225.gp20.application.Main;
import nz.ac.vuw.ecs.swen225.gp20.maze.Exit;
import nz.ac.vuw.ecs.swen225.gp20.maze.ExitLock;
import nz.ac.vuw.ecs.swen225.gp20.maze.Free;
import nz.ac.vuw.ecs.swen225.gp20.maze.InfoField;
import nz.ac.vuw.ecs.swen225.gp20.maze.Key;
import nz.ac.vuw.ecs.swen225.gp20.maze.Lava;
import nz.ac.vuw.ecs.swen225.gp20.maze.LockedDoor;
import nz.ac.vuw.ecs.swen225.gp20.maze.Maze;
import nz.ac.vuw.ecs.swen225.gp20.maze.Player;
import nz.ac.vuw.ecs.swen225.gp20.maze.Tile;
import nz.ac.vuw.ecs.swen225.gp20.maze.Treasure;
import nz.ac.vuw.ecs.swen225.gp20.maze.Wall;
import nz.ac.vuw.ecs.swen225.gp20.maze.Block;

public class LevelLoader {
	
	/**
	 * Reads a JSON file describing the level and creates a new Level object
	 * TODO: Don't hard code height and width
	 * 
	 * @param levelNumber
	 * @return Level
	 */
	public static Level load(int levelNumber) {
		String filename = "levels/level" + levelNumber + ".json";
		
		int mapWidth;
		int mapHeight;
		
		if(levelNumber == 1) {
			mapWidth = 15;
			mapHeight = 15;
		} else {
			mapWidth = 18;
			mapHeight = 11;
		}
		
		Tile[][] map = new Tile[mapWidth][mapHeight]; 
		int treasures = 0; //total treasures in the level
		Player chap = null;
		ArrayList<Block> blocks = new ArrayList<Block>();
		
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
					
				} else if(tileType.equals("Lava")) {
					map[col][row] = new Lava(col, row);
					
				} else if(tileType.equals("Block")) {
					blocks.add(new Block(col, row));
					map[col][row] = new Free(col, row);
					
				}else if(tileType.equals("Player")) {
					Tile playerTile = new Free(col, row);
					chap = new Player(playerTile);
					map[col][row] = playerTile;
					
				}
				
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		if(levelNumber == 1) {
			return new Level(map, treasures, chap);
		} else {
			return new Level(map, treasures, chap, blocks);
		}
	}
	
	/**
	 * Gets the current game state as a JsonObectBuilder
	 * @param game
	 * @return JsonObjectBuilder
	 */
	public static JsonObjectBuilder getGameState(Main application) {
		Maze game = application.getMaze();
		int timeLeft = application.getTimeLeft();
		
		JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
		
		
		String gameState = "{";
		
		Tile[][] map = game.getTiles();
		
		//level
		gameState += "\"level\": \"" + game.getLevel() + "\",";
		objectBuilder.add("level", game.getLevel());
		
		//timeLeft
		gameState += "\"timeLeft\": \"" + timeLeft + "\",";
		objectBuilder.add("timeLeft", timeLeft);
		
		//cols
		gameState += "\"cols\": \"" + map.length + "\",";
		objectBuilder.add("cols", map.length);
		
		//rows
		gameState += "\"rows\": \"" + map[0].length + "\",";
		objectBuilder.add("rows", map[0].length);
		
		//map
		JsonArrayBuilder mapArrayBuilder = Json.createArrayBuilder();
		gameState += "\"map\": [";	
		for(int col = 0; col < map.length; col++) {
			for(int row = 0; row < map[0].length; row++) {
				gameState += map[col][row].toString();
				mapArrayBuilder.add(map[col][row].getJson());
			}
		}
		objectBuilder.add("map", mapArrayBuilder);
		gameState += ",";
				
		//treasuresLeft
		gameState += "\"treasuresLeft\": \"" + game.getTreasuresLeft() + "\",";
		objectBuilder.add("treasuresLeft", game.getTreasuresLeft());
		
		//chapCol
		gameState += "\"chapCol\": \"" + game.getChap().getLocation().getCol() + "\",";
		objectBuilder.add("chapCol", game.getChap().getLocation().getCol());
		
		//chapRow
		gameState += "\"chapRow\": \"" + game.getChap().getLocation().getRow() + "\",";
		objectBuilder.add("chapRow", game.getChap().getLocation().getRow());
		
		//keysCollected
		JsonArrayBuilder keysArrayBuilder = Json.createArrayBuilder();
		gameState += "\"keysCollected\": [";
		for(Key.Colour colour : game.getChap().getBackpack()) {
			gameState += "{\"color\": \"" + colour + "\"},";
			JsonObjectBuilder keyObjectBuilder = Json.createObjectBuilder();
			keyObjectBuilder.add("color", colour.toString());
			mapArrayBuilder.add(keyObjectBuilder);
		}
		objectBuilder.add("keysCollected", keysArrayBuilder);
		if(!game.getChap().getBackpack().isEmpty()) {
			gameState = gameState.substring(0, gameState.length() - 1);
		}
		gameState += "]";
		
		gameState += "}";
		
		return objectBuilder;
		
		/**
		*/
	}
	
	/**
	 * Saves the game state to levels/gameState.json
	 * @param toSave 
	 */
	public static void saveGameState(JsonObjectBuilder toSave) {
		try {
			StringWriter writer = new StringWriter();
			Json.createWriter(writer).write(toSave.build());
			
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("levels/gameState.json"));
		    bufferedWriter.write(writer.toString());
		    bufferedWriter.close();
		} catch (IOException e) {
		      System.out.println("Error saving game state" + e);
		}
	}
	
	public static Maze loadGameState(JsonObject gameStateJson) {
        var rows = gameStateJson.getJsonNumber("rows").intValue();
        var cols = gameStateJson.getJsonNumber("cols").intValue();
        Tile[][] tiles = new Tile[cols][rows];
 
        for (var tileValue : gameStateJson.getJsonArray("map")) {
            var tileJsonObj = tileValue.asJsonObject();
            int col = tileJsonObj.asJsonObject().getInt("col");
            int row = tileJsonObj.asJsonObject().getInt("row");
 
            tiles[col][row] = makeTileFromName(tileJsonObj.asJsonObject(), col, row);
        }
 
        var maze = new Maze(tiles, gameStateJson.getJsonNumber("treasuresLeft").intValue());
        var chapCol = gameStateJson.getJsonNumber("chapCol");
        var chapRow = gameStateJson.getJsonNumber("chapRow");
        maze.getChap().setLocation(tiles[chapCol.intValue()][chapRow.intValue()]);
        return maze;
    }
	
	private static Tile makeTileFromName(JsonObject tile, int col, int row) {
        String name = tile.getString("type");
        switch (name) {
            case "Free":
                return new Free(col, row);
            case "Exit":
                return new Exit(col, row);
            case "ExitLock":
                return new ExitLock(col, row);
            case "InfoField":
                return new InfoField(col, row, tile.getString("info"));
            case "Key":
                return new Key(col, row, Key.Colour.valueOf(tile.getString("color")));
            case "Lava":
                return new Lava(col, row);
            case "LockedDoor":
                return new LockedDoor(col, row, Key.Colour.valueOf(tile.getString("color")));
            case "Treasure":
                return new Treasure(col, row);
            case "Wall":
                return new Wall(col, row);
            default:
                throw new IllegalArgumentException("Incorrect tile!");
        }
	}
}
