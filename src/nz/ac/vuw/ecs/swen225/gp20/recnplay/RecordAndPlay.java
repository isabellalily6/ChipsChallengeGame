package nz.ac.vuw.ecs.swen225.gp20.recnplay;

import nz.ac.vuw.ecs.swen225.gp20.maze.Actor;
import nz.ac.vuw.ecs.swen225.gp20.maze.Maze;
import nz.ac.vuw.ecs.swen225.gp20.persistence.LevelLoader;

import javax.json.Json;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a class with static methods for starting a recording, and loading/saving a recording to a file in JSON format
 */
public class RecordAndPlay {
    private static boolean isRecording = false;
    private static final List<RecordedMove> moves = new ArrayList<>();
    private static String gameState;


    /**
     * saves a recording to a file
     */
    public static void saveRecording() {
        var saveFileName = "chapsChallengeRecording.txt";

        var gameJson = Json.createArrayBuilder();

        gameJson.add(gameState);

        var movesArray = Json.createArrayBuilder();


        for (var move : moves) {
            var obj = Json.createObjectBuilder().add("move", move.actor.getImageURl()).add("dir", move.getDirection().getName());
            movesArray.add(obj);
        }

        gameJson.add(movesArray.build());

        try (var writer = new StringWriter()) {
            Json.createWriter(writer).write(gameJson.build());
            try {
                var bw = new BufferedWriter(new FileWriter(saveFileName));
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
    public static void startRecording(Maze m) {
        isRecording = true;
        //TODO: add the start game state (ie, where are the tiles? what does the player have in their inventory?
        gameState = new LevelLoader().saveGameState(m);
    }

    private static void resetRecordingState() {
        moves.clear();
        gameState = "";
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
