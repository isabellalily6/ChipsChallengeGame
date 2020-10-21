package nz.ac.vuw.ecs.swen225.gp20.persistence;

import nz.ac.vuw.ecs.swen225.gp20.application.Main;
import nz.ac.vuw.ecs.swen225.gp20.commons.Direction;
import nz.ac.vuw.ecs.swen225.gp20.maze.*;

import javax.json.*;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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
		ArrayList<Cobra> cobras = new ArrayList<Cobra>();
		ArrayList<Queue<Direction>> cobraMoves = new ArrayList<>();

		try {

			//Read JSON file into a list of JsonObjects
			JsonReader reader = Json.createReader(new FileReader(filename));
			JsonArray jsonArray = reader.readArray();

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
					
				}else if(tileType.equals("Cobra")) {
					Tile playerTile = new Free(col, row);
					
					Queue<Direction> moves = new LinkedList<Direction>();
					JsonArray jsonMoves = jsonTileObj.getJsonArray("moves");
					
					for(int j = 0; j < jsonMoves.size(); j++) {
						JsonObject directionObj = (JsonObject) jsonMoves.get(j);
						String direction = directionObj.getString("direction");
						
						if(direction.contentEquals("Up")) {
							moves.add(Direction.UP);
						} else if(direction.contentEquals("Down")) {
							moves.add(Direction.DOWN);
						} else if(direction.contentEquals("Left")) {
							moves.add(Direction.LEFT);
						} else if(direction.contentEquals("Right")) {
							moves.add(Direction.RIGHT);
						} 
					}
					
					cobras.add(new Cobra(playerTile, moves));
					map[col][row] = playerTile;
					
				}
				
			}
			
			reader.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		if(levelNumber == 1) {
			return new Level(map, treasures, chap);
		} else {
			return new Level(map, treasures, chap, blocks, cobras);
		}
	}
	
	/**
	 * Gets the current game state as a JsonObjectBuilder
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
		
		//cobras
		gameState += "\"cobras\": [";
		JsonArrayBuilder cobrasArrayBuilder = Json.createArrayBuilder();
		if(game.getCobras() != null) {
			for(Cobra c : game.getCobras()) {
				JsonObjectBuilder cobraObjectBuilder = Json.createObjectBuilder();
				gameState += "{\"cobraCol\": \"" + c.getLocation().getCol() + "\",";
				cobraObjectBuilder.add("cobraCol", c.getLocation().getCol());
				gameState += "\"cobraRow\": \"" + c.getLocation().getRow() + "\",";
				cobraObjectBuilder.add("cobraRow", c.getLocation().getRow());
				
				gameState += "\"moves\": [";
				JsonArrayBuilder cobraDirectionBuilder = Json.createArrayBuilder();
				for(Direction d : c.getListOfMoves()) {
					JsonObjectBuilder directionObjectBuilder = Json.createObjectBuilder();
					gameState += "{\"direction\": \"" + d.getName() + "\"},";
					directionObjectBuilder.add("direction", d.getName());
					cobraDirectionBuilder.add(directionObjectBuilder);
				}
				if(!c.getListOfMoves().isEmpty()) {
					gameState = gameState.substring(0, gameState.length() - 1);
				}
				gameState += "]},";
				cobrasArrayBuilder.add(cobraObjectBuilder);
			}
			if(!game.getCobras().isEmpty()) {
				gameState = gameState.substring(0, gameState.length() - 1);
			}
		}
		gameState += "],";
		objectBuilder.add("cobras", cobrasArrayBuilder);
		
		//blocks
		gameState += "\blocks\": [";
		JsonArrayBuilder blocksArrayBuilder = Json.createArrayBuilder();
		if(game.getBlocks() != null) {
			for(Block b : game.getBlocks()) {
				JsonObjectBuilder blockObjectBuilder = Json.createObjectBuilder();
				gameState += "{\"blockCol\": \"" + b.getCol() + "\",";
				blockObjectBuilder.add("blockCol", b.getCol());
				gameState += "\"blockRow\": \"" + b.getRow() + "\"},";
				blockObjectBuilder.add("blockRow", b.getRow());
				blocksArrayBuilder.add(blockObjectBuilder);
			}
			if(!game.getBlocks().isEmpty()) {
				gameState = gameState.substring(0, gameState.length() - 1);
			}
		}
		gameState += "],";
		objectBuilder.add("blocks", blocksArrayBuilder);
		
		//keysCollected
		JsonArrayBuilder keysArrayBuilder = Json.createArrayBuilder();
		gameState += "\"keysCollected\": [";
		for(Key.Colour colour : game.getChap().getBackpack()) {
			gameState += "{\"color\": \"" + colour + "\"},";
			JsonObjectBuilder keyObjectBuilder = Json.createObjectBuilder();
			keyObjectBuilder.add("color", colour.toString());
			keysArrayBuilder.add(keyObjectBuilder);
		}
		objectBuilder.add("keysCollected", keysArrayBuilder);
		if(!game.getChap().getBackpack().isEmpty()) {
			gameState = gameState.substring(0, gameState.length() - 1);
		}
		gameState += "]}";
		
		return objectBuilder;
		
		/**
		*/
	}
	
	/**
	 * Saves the game state to file
	 * @param toSave 
	 */
	public static void saveGameState(JsonObjectBuilder toSave, File file) {
		try {
			StringWriter writer = new StringWriter();
			Json.createWriter(writer).write(toSave.build());
			
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
		    bufferedWriter.write(writer.toString());
		    bufferedWriter.close();
		} catch (IOException e) {
		      System.out.println("Error saving game state" + e);
		}
	}
	
	public static void loadOldGame(Main main, File file) {
		JsonReader reader;
		try {
			reader = Json.createReader(new FileReader(file));
			JsonObject gameObject = reader.readObject();
			reader.close();
			
			Maze maze = loadGameState(gameObject);
			main.setMaze(maze);
			main.setTimeLeft(gameObject.getJsonNumber("timeLeft").intValue());
			main.setLevel(gameObject.getJsonNumber("level").intValue());
			
		} catch (FileNotFoundException | javax.json.stream.JsonParsingException ignored) {
		}
	}
	
	public static Maze loadGameState(JsonObject gameStateJson) {
        Maze maze;
        
        int rows = gameStateJson.getJsonNumber("rows").intValue();
        int cols = gameStateJson.getJsonNumber("cols").intValue();
        Tile[][] tiles = new Tile[cols][rows];
 
        for (JsonValue tileValue : gameStateJson.getJsonArray("map")) {
            JsonObject tileJsonObj = tileValue.asJsonObject();
            int col = tileJsonObj.asJsonObject().getInt("col");
            int row = tileJsonObj.asJsonObject().getInt("row");
 
            tiles[col][row] = makeTileFromName(tileJsonObj.asJsonObject(), col, row);
        }
        
        List<Block> blocks = new ArrayList<Block>();
        for(JsonValue bValue : gameStateJson.getJsonArray("blocks")) {
        	JsonObject blockObject = bValue.asJsonObject();
        	int col = blockObject.getInt("blockCol");
        	int row = blockObject.getInt("blockRow");
        	blocks.add(new Block(col, row));
        }
        
        List<Cobra> cobras = new ArrayList<Cobra>();
        for(JsonValue cValue : gameStateJson.getJsonArray("blocks")) {
        	JsonObject cobraObject = cValue.asJsonObject();
        	
        	int col = cobraObject.getInt("cobraCol");
        	int row = cobraObject.getInt("cobraRow");
        	
        	Queue<Direction> moves = new LinkedList<Direction>();
        	for(JsonValue mValue : cobraObject.getJsonArray("moves")) {
        		JsonObject moveObject = mValue.asJsonObject();
        		String direction = moveObject.getString("direction");
        		moves.add(Direction.valueOf(direction));
        	}
        	
        	cobras.add(new Cobra(tiles[col][row], moves));
        }
        
        if(gameStateJson.getJsonNumber("level").intValue() == 1) {
        	maze = new Maze(tiles, gameStateJson.getJsonNumber("treasuresLeft").intValue());
        } else {
        	maze = new Maze(tiles, gameStateJson.getJsonNumber("treasuresLeft").intValue(), blocks, cobras);
        }
        JsonNumber chapCol = gameStateJson.getJsonNumber("chapCol");
        JsonNumber chapRow = gameStateJson.getJsonNumber("chapRow");
        maze.getChap().getLocation().onExit();
        maze.getChap().setLocation(tiles[chapCol.intValue()][chapRow.intValue()]);
        
        for(JsonValue cValue : gameStateJson.getJsonArray("keysCollected")) {
        	String color = cValue.asJsonObject().getString("color");
        	maze.getChap().addToBackPack(Key.Colour.valueOf(color));
        }
        
        
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
