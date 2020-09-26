package nz.ac.vuw.ecs.swen225.gp20.persistence;

import nz.ac.vuw.ecs.swen225.gp20.maze.*;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

public class LevelLoader {

	/**
	 * Reads a JSON file describing the level
	 * TODO: Don't hard code height and width
	 *
	 * @param level number
	 */
	public Level load(int levelNumber) {
		String filename = "levels\\level" + levelNumber + ".json";

		int mapWidth = 9;
		int mapHeight = 9;

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
				int col = i / mapWidth;
				int row = i % mapWidth;


				//Determine the colour if applicable
				Key.Colour tileColor;
				if(tileType.equals("Key") || tileType.equals("LockedDoor")) {
					String colorName = jsonTileObj.getString("color");
					if(colorName.contentEquals("emerald")) {
						tileColor = Key.Colour.EMERALD;

					} else if(colorName.contentEquals("diamond")) {
						tileColor = Key.Color.DIAMOND;

					} else if(colorName.contentEquals("sapphire")) {
						tileColor = Key.Color.SAPPHIRE;

					} else if(colorName.contentEquals("amethyst")) {
						tileColor = Key.Color.AMETHYST;

					} else if(colorName.contentEquals("topaz")) {
						tileColor = Key.Color.TOPAZ;

					} else if(colorName.contentEquals("ruby")) {
						tileColor = Key.Color.RUBY;

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

}
