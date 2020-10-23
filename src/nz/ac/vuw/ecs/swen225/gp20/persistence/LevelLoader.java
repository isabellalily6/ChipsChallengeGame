package nz.ac.vuw.ecs.swen225.gp20.persistence;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;
import nz.ac.vuw.ecs.swen225.gp20.application.Main;
import nz.ac.vuw.ecs.swen225.gp20.commons.Direction;
import nz.ac.vuw.ecs.swen225.gp20.maze.Block;
import nz.ac.vuw.ecs.swen225.gp20.maze.Cobra;
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

/**
 * LevelLoader contains static methods for loading a level, saving a game, and
 * loading a saved game.
 *
 * @author Matt Rowling 300487163
 */
public class LevelLoader {

  /**
   * Reads a JSON file describing the level map and creates a new Level object.
   * 
   * @param levelNumber which level it is
   * @return Level
   */
  public static Level load(int levelNumber) {
    String filename = "levels/level" + levelNumber + ".json";

    // Create the map with its size depending on the level
    int mapWidth;
    int mapHeight;
    if (levelNumber == 1) {
      mapWidth = 15;
      mapHeight = 15;
    } else {
      mapWidth = 18;
      mapHeight = 11;
    }
    Tile[][] map = new Tile[mapWidth][mapHeight];

    // Elements to return in Level object
    int treasures = 0;
    Player chap = null;
    ArrayList<Block> blocks = new ArrayList<Block>();
    ArrayList<Cobra> cobras = new ArrayList<Cobra>();

    // Read the level JSON file
    try {

      // Read JSON file into a list of JsonObjects
      JsonReader reader = Json.createReader(new FileReader(filename));
      JsonArray jsonArray = reader.readArray();
      List<JsonObject> jsonTiles = jsonArray.getValuesAs(JsonObject.class);
      reader.close();

      // Iterate through the List of JsonObjects to create new tile objects and put
      // them in the map
      for (int i = 0; i < jsonTiles.size(); i++) {

        // Get JsonObject of the tile and store its type
        JsonObject jsonTileObj = jsonTiles.get(i);
        String tileType = jsonTileObj.getString("type");

        // Calculate the location of the current tile
        int row = i / mapWidth;
        int col = i % mapWidth;

        // Make the relevant tile and store it in the map
        if (tileType.equals("Cobra")) {
          Tile cobraTile = new Free(col, row); // A cobra sits on a free tile

          // Store the cobra's directions of moves in a queue
          Queue<Direction> moves = new LinkedList<Direction>();
          JsonArray jsonMoves = jsonTileObj.getJsonArray("moves");
          for (int j = 0; j < jsonMoves.size(); j++) {
            JsonObject directionObj = (JsonObject) jsonMoves.get(j);
            moves.add(Direction.valueOf(directionObj.getString("direction")));
          }

          cobras.add(new Cobra(cobraTile, moves));
          map[col][row] = cobraTile;

        } else if (tileType.equals("Block")) {
          blocks.add(new Block(col, row));
          map[col][row] = new Free(col, row); // A block sits on a free tile

        } else if (tileType.equals("Player")) {
          Tile playerTile = new Free(col, row); // The player sits on a free tile
          chap = new Player(playerTile);
          map[col][row] = playerTile;

        } else if (tileType.equals("Treasure")) {
          treasures++;
          map[col][row] = new Treasure(col, row);

        } else {
          map[col][row] = makeTileFromName(jsonTileObj, col, row);
        }

      }

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    // Return a level object using a different constructor depending on what level
    // it is
    if (levelNumber == 1) {
      return new Level(map, treasures, chap);
    } else {
      return new Level(map, treasures, chap, blocks, cobras);
    }
  }

  /**
   * Gets the current game state as a JsonObjectBuilder.
   * 
   * @param application a Main object from the application package to get the
   *                     game from
   * @return JsonObjectBuilder
   */
  public static JsonObjectBuilder getGameState(Main application) {
    Maze game = application.getMaze();
    Tile[][] map = game.getTiles();

    JsonObjectBuilder objectBuilder = Json.createObjectBuilder();

    // level
    objectBuilder.add("level", game.getLevel());

    // timeLeft
    objectBuilder.add("timeLeft", application.getTimeLeft());

    // cols
    objectBuilder.add("cols", map.length);

    // rows
    objectBuilder.add("rows", map[0].length);

    // map
    JsonArrayBuilder mapArrayBuilder = Json.createArrayBuilder();
    for (int col = 0; col < map.length; col++) {
      for (int row = 0; row < map[0].length; row++) {
        mapArrayBuilder.add(map[col][row].getJson());
      }
    }
    objectBuilder.add("map", mapArrayBuilder);

    // treasuresLeft
    objectBuilder.add("treasuresLeft", game.getTreasuresLeft());

    // chapCol
    objectBuilder.add("chapCol", game.getChap().getLocation().getCol());

    // chapRow
    objectBuilder.add("chapRow", game.getChap().getLocation().getRow());

    // cobras
    JsonArrayBuilder cobrasArrayBuilder = Json.createArrayBuilder();
    if (game.getCobras() != null) {
      for (Cobra c : game.getCobras()) {
        JsonObjectBuilder cobraObjectBuilder = Json.createObjectBuilder();

        // cobraCol
        cobraObjectBuilder.add("cobraCol", c.getLocation().getCol());

        // cobraRow
        cobraObjectBuilder.add("cobraRow", c.getLocation().getRow());

        // moves
        JsonArrayBuilder cobraDirectionBuilder = Json.createArrayBuilder();
        for (Direction d : c.getListOfMoves()) {
          // direction
          JsonObjectBuilder directionObjectBuilder = Json.createObjectBuilder();
          directionObjectBuilder.add("direction", d.toString());
          cobraDirectionBuilder.add(directionObjectBuilder);
        }
        cobraObjectBuilder.add("moves", cobraDirectionBuilder);
        cobrasArrayBuilder.add(cobraObjectBuilder);
      }
    }
    objectBuilder.add("cobras", cobrasArrayBuilder);

    // blocks
    JsonArrayBuilder blocksArrayBuilder = Json.createArrayBuilder();
    if (game.getBlocks() != null) {
      for (Block b : game.getBlocks()) {
        JsonObjectBuilder blockObjectBuilder = Json.createObjectBuilder();

        // blockCol
        blockObjectBuilder.add("blockCol", b.getCol());

        // blockRow
        blockObjectBuilder.add("blockRow", b.getRow());

        blocksArrayBuilder.add(blockObjectBuilder);
      }
    }
    objectBuilder.add("blocks", blocksArrayBuilder);

    // keysCollected
    JsonArrayBuilder keysArrayBuilder = Json.createArrayBuilder();
    for (Key.Colour colour : game.getChap().getBackpack()) {
      JsonObjectBuilder keyObjectBuilder = Json.createObjectBuilder();

      // color
      keyObjectBuilder.add("color", colour.toString());
      keysArrayBuilder.add(keyObjectBuilder);
    }
    objectBuilder.add("keysCollected", keysArrayBuilder);

    return objectBuilder;
  }

  /**
   * Saves the game state from a JsonObjectBuilder to a file passed in as a
   * parameter.
   * 
   * @param toSave the JSON object builder to write the file from
   * @param file the file to write to
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

  /**
   * Loads an old game from a game state file and edits a main object.
   * 
   * @param main main object to edit
   * @param file file to load from
   */
  public static void loadOldGame(Main main, File file) {
    try {
      JsonReader reader = Json.createReader(new FileReader(file));
      JsonObject gameObject = reader.readObject();
      reader.close();

      Maze maze = loadGameState(gameObject);
      main.setMaze(maze);
      main.setTimeLeft(gameObject.getJsonNumber("timeLeft").intValue());
      main.setLevel(gameObject.getJsonNumber("level").intValue());

    } catch (FileNotFoundException | javax.json.stream.JsonParsingException ignored) {

      System.out.println("Error loading old game" + ignored);
    }
  }

  /**
   * Loads a maze from a game state JsonObject.
   * 
   * @param gameStateJson game state as a JsonObject
   * @return Maze
   */
  public static Maze loadGameState(JsonObject gameStateJson) {
    Maze maze; // maze object to return

    // Create the map of tiles
    int rows = gameStateJson.getJsonNumber("rows").intValue();
    int cols = gameStateJson.getJsonNumber("cols").intValue();
    Tile[][] tiles = new Tile[cols][rows];

    // Set the tiles in the map
    for (JsonValue tileValue : gameStateJson.getJsonArray("map")) {
      JsonObject tileJsonObj = tileValue.asJsonObject();

      int col = tileJsonObj.asJsonObject().getInt("col");
      int row = tileJsonObj.asJsonObject().getInt("row");

      tiles[col][row] = makeTileFromName(tileJsonObj.asJsonObject(), col, row);
    }

    // Get the list of Blocks
    List<Block> blocks = new ArrayList<Block>();
    for (JsonValue blockValue : gameStateJson.getJsonArray("blocks")) {
      JsonObject blockObject = blockValue.asJsonObject();

      int col = blockObject.getInt("blockCol");
      int row = blockObject.getInt("blockRow");
      blocks.add(new Block(col, row));
    }

    // Get the list of Cobras
    List<Cobra> cobras = new ArrayList<Cobra>();
    for (JsonValue cobraValue : gameStateJson.getJsonArray("cobras")) {
      JsonObject cobraObject = cobraValue.asJsonObject();

      int col = cobraObject.getInt("cobraCol");
      int row = cobraObject.getInt("cobraRow");

      // Get the Cobra's queue of moves
      Queue<Direction> moves = new LinkedList<Direction>();
      for (JsonValue moveValue : cobraObject.getJsonArray("moves")) {
        JsonObject moveObject = moveValue.asJsonObject();
        String direction = moveObject.getString("direction");
        moves.add(Direction.valueOf(direction));
      }
      cobras.add(new Cobra(tiles[col][row], moves));
    }

    // Construct the maze to return
    if (gameStateJson.getJsonNumber("level").intValue() == 1) {
      maze = new Maze(tiles, gameStateJson.getJsonNumber("treasuresLeft").intValue());
    } else {
      maze = new Maze(tiles, gameStateJson.getJsonNumber("treasuresLeft").intValue(), blocks,
          cobras);
    }

    // Reset Chap's location
    JsonNumber chapCol = gameStateJson.getJsonNumber("chapCol");
    JsonNumber chapRow = gameStateJson.getJsonNumber("chapRow");
    maze.getChap().getLocation().onExit();
    maze.getChap().setLocation(tiles[chapCol.intValue()][chapRow.intValue()]);

    // Reset the cobras' locations
    List<Cobra> mazeCobras = maze.getCobras();
    if (mazeCobras != null) {
      for (int i = 0; i < mazeCobras.size(); i++) {
        mazeCobras.get(i).getLocation().onExit();
        mazeCobras.get(i).setLocation(cobras.get(i).getLocation());
      }
    }

    // Add the keys to Chap's backpack
    for (JsonValue colorValue : gameStateJson.getJsonArray("keysCollected")) {
      String color = colorValue.asJsonObject().getString("color");
      maze.getChap().addToBackPack(Key.Colour.valueOf(color));
    }

    return maze;
  }

  /**
   * Helper method that takes a JsonObject and a location and returns a new tile.
   * 
   * @param tile JsonObject
   * @param col  column location of the tile
   * @param row  row location of the tile
   * @return Tile
   */
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
