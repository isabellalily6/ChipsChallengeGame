package nz.ac.vuw.ecs.swen225.gp20.recnplay;

import nz.ac.vuw.ecs.swen225.gp20.application.GUI;
import nz.ac.vuw.ecs.swen225.gp20.application.Main;
import nz.ac.vuw.ecs.swen225.gp20.maze.Actor;
import nz.ac.vuw.ecs.swen225.gp20.maze.Maze;
import nz.ac.vuw.ecs.swen225.gp20.maze.Player;
import nz.ac.vuw.ecs.swen225.gp20.persistence.LevelLoader;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a class with static methods for starting a recording, and loading/saving a recording to a file in JSON format
 *
 * @author callum mckay
 */
public class RecordAndPlay {
    private static boolean isRecording = false;
    private static final List<RecordedMove> recordedMoves = new ArrayList<>();
    private static final List<RecordedMove> loadedMoves = new ArrayList<>();
    private static JsonObjectBuilder gameState;
    private static GUI parentComponent;


    /**
     * saves a recorded game in Json format to a file for replaying later
     *
     * @author callum mckay
     */
    public static void saveRecording() {
        if (!isRecording) {
            return;
        }

        //Build a json representation of the game and moves that have been performed
        var jsonToSave = buildJson();

        //Save this to a file
        saveToFile(jsonToSave);

        //reset the recording state
        resetRecordingState();
    }

    /**
     * Loads a recording from the file
     *
     * @param m is the main class, this is used to access the player, and the gui which is the parent to the filechooser
     * @author callum mckay
     */
    public static void loadRecording(Main m) {
        File jsonFile = getJsonFileToLoad(m.getGui());

        if (jsonFile == null) return;

        try {
            var parser = Json.createReader(new FileReader(jsonFile, StandardCharsets.UTF_8));
            var jsonArr = parser.readArray();

            var moves = jsonArr.getJsonObject(1);
            loadedMoves.addAll(loadMoves(moves, m.getMaze().getChap()));
            var i = 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * @param a actor who performed this move
     * @param d direction of this move
     * @return true if the move was recorded, false if not
     * @author callum mckay
     */
    public static boolean addMove(Actor a, Maze.Direction d) {
        if (isRecording) {
            recordedMoves.add(new RecordedMove(a, d));
            return true;
        }

        return false;
    }

    /**
     * @return if the game is being recorded
     * @author callum mckay
     */
    public static boolean isRecording() {
        return isRecording;
    }

    /**
     * Starts recording this game
     *
     * @param m current maze that we are recording
     * @author callum mckay
     */
    public static void startRecording(Main m) {
        isRecording = true;
        gameState = LevelLoader.getGameState(m);
        parentComponent = m.getGui();
    }

    private static List<RecordedMove> loadMoves(JsonObject movesJson, Player p) {
        var toReturn = new ArrayList<RecordedMove>();

        for (var move : movesJson.getJsonArray("moves")) {
            var loadedMove = move.asJsonObject();

            var actorName = loadedMove.get("actor").toString();

            //this is due to the value being stored as a string, so it would come out as ""player""
            actorName = turnJsonStringToString(actorName);
            var dir = getDirection(loadedMove.get("dir").toString());

            RecordedMove recordedMove = null;
            if (actorName.equals("player")) {
                //TODO: make this not get the mazes chap but instead the chap from the new maze
                // once that has been loaded
                recordedMove = new RecordedMove(p, dir);
            } else {
                //TODO support for other mobs in lvl 2
            }

            toReturn.add(recordedMove);
        }

        return toReturn;
    }

    private static JsonArray buildJson() {
        var gameJson = Json.createArrayBuilder();

        gameJson.add(gameState.build());

        var movesArray = Json.createArrayBuilder();

        for (var move : recordedMoves) {
            var obj = Json.createObjectBuilder()
                    .add("actor", move.getActor().getName())
                    .add("dir", move.getDirection().toString());
            movesArray.add(obj);
        }

        var movesArrayObj = Json.createObjectBuilder().add("moves", movesArray);

        gameJson.add(movesArrayObj.build());
        return gameJson.build();
    }

    private static File getJsonFileToLoad(GUI g) {
        var fileChooser = new JFileChooser(Paths.get(".", "recordings").toAbsolutePath().normalize().toString());
        fileChooser.setFileFilter(new FileNameExtensionFilter("json files only", "json"));
        var result = fileChooser.showOpenDialog(g);

        if (result == JFileChooser.APPROVE_OPTION) {
            File jsonFile = fileChooser.getSelectedFile();
            if (!jsonFile.getName().endsWith(".json")) return null;

            return jsonFile;
        }

        return null;
    }

    private static void saveToFile(JsonArray jsonArray) {
        var fileChooser = new JFileChooser(Paths.get(".", "recordings").toAbsolutePath().normalize().toString());
        var result = fileChooser.showOpenDialog(parentComponent);

        if (result == JFileChooser.APPROVE_OPTION) {
            var writer = new StringWriter();
            Json.createWriter(writer).write(jsonArray);
            try {
                var bw = new BufferedWriter(new FileWriter(fileChooser.getSelectedFile() + ".json", StandardCharsets.UTF_8));
                bw.write(writer.toString());
                bw.close();
            } catch (IOException e) {
                throw new Error("Game was not able to be saved due to an exception");
            }
        }
    }

    private static void resetRecordingState() {
        recordedMoves.clear();
        gameState = null;
        isRecording = false;
    }

    private static String turnJsonStringToString(String s) {
        if (s == null) return null;
        if (s.length() < 2) return "";

        return s.substring(1, s.length() - 1);
    }

    private static Maze.Direction getDirection(String dir) {
        dir = turnJsonStringToString(dir);
        return Maze.Direction.valueOf(dir);
    }


    /**
     * A internal class which can represent a move, simply maps an Actor to a Direction
     *
     * @author callum mckay
     */
    static class RecordedMove {
        private final Actor actor;
        private final Maze.Direction direction;

        /**
         * @param actor     actor who this move has been done by
         * @param direction direction of said move
         * @author callum mckay
         */
        RecordedMove(Actor actor, Maze.Direction direction) {
            this.actor = actor;
            this.direction = direction;
        }

        /**
         * @return the actor of this recorded move
         * @author callum mckay
         */
        public Actor getActor() {
            return actor;
        }

        /**
         * @return the direction of this recorded move
         * @author callum mckay
         */
        public Maze.Direction getDirection() {
            return direction;
        }
    }
}
