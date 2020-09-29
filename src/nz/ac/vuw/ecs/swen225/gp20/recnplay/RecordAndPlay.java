package nz.ac.vuw.ecs.swen225.gp20.recnplay;

import nz.ac.vuw.ecs.swen225.gp20.application.Main;
import nz.ac.vuw.ecs.swen225.gp20.maze.Actor;
import nz.ac.vuw.ecs.swen225.gp20.maze.Maze;
import nz.ac.vuw.ecs.swen225.gp20.persistence.LevelLoader;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a class with static methods for starting a recording, and loading/saving a recording to a file in JSON format
 */
public class RecordAndPlay {
    private static boolean isRecording = false;
    private static final List<RecordedMove> moves = new ArrayList<>();
    private static JsonObjectBuilder gameState;


    /**
     * saves a recorded game to a file for replaying later
     */
    public static void saveRecording() {
        var saveFileName = "chapsChallengeRecording.json";

        var gameJson = Json.createArrayBuilder();

        gameJson.add(gameState.build());

        var movesArray = Json.createArrayBuilder();

        for (var move : moves) {
            var obj = Json.createObjectBuilder()
                    .add("move", move.actor.getName())
                    .add("dir", move.getDirection().toString());
            movesArray.add(obj);
        }

        var movesArrayObj = Json.createObjectBuilder().add("moves", movesArray);

        gameJson.add(movesArrayObj.build());

        try (var writer = new StringWriter()) {
            Json.createWriter(writer).write(gameJson.build());
            try {
                var bw = new BufferedWriter(new FileWriter(saveFileName, StandardCharsets.UTF_8));
                bw.write(writer.toString());
                bw.close();
            } catch (IOException e) {
                throw new Error("Game was not able to be saved due to an exception");
            }
        } catch (IOException e) {
            throw new Error("Game was not able to be saved due to an exception");
        }

        resetRecordingState();
    }

    /**
     * Loads a recording from the file
     */
    public static void loadRecording() {

    }


    /**
     * @param a actor who performed this move
     * @param d direction of this move
     * @return true if the move was recorded, false if not
     */
    public static boolean addMove(Actor a, Maze.Direction d) {
        if (isRecording) {
            moves.add(new RecordedMove(a, d));
            return true;
        }

        return false;
    }

    /**
     * @return if the game is being recorded
     */
    public static boolean isRecording() {
        return isRecording;
    }

    /**
     * Starts recording this game
     *
     * @param m current maze that we are recording
     */
    public static void startRecording(Main m) {
        isRecording = true;
        gameState = LevelLoader.getGameState(m);
    }

    private static void resetRecordingState() {
        moves.clear();
        gameState = null;
        isRecording = false;
    }

    static class RecordedMove {
        private final Actor actor;
        private final Maze.Direction direction;

        /**
         * @param actor     actor who this move has been done by
         * @param direction direction of said move
         */
        RecordedMove(Actor actor, Maze.Direction direction) {
            this.actor = actor;
            this.direction = direction;
        }

        /**
         * @return the actor of this recorded move
         */
        public Actor getActor() {
            return actor;
        }

        /**
         * @return the direction of this recorded move
         */
        public Maze.Direction getDirection() {
            return direction;
        }
    }
}
