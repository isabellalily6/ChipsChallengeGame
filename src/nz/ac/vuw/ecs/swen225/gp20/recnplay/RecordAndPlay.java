package nz.ac.vuw.ecs.swen225.gp20.recnplay;

import javax.json.Json;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a class with static methods for starting a recording, and loading/saving a recording to a file in JSON format
 */
public class RecordAndPlay {
    private static boolean isRecording = false;
    private static final List<String> playerMoves = new ArrayList<>();
    private static final List<String> mobMoves = new ArrayList<>();

    /**
     * saves a recording to a file
     */
    public static void saveRecording() {
        var saveFileName = "chapsChallengeRecording.txt";

        var gameJson = Json.createArrayBuilder();

        var playerMovesArray = Json.createArrayBuilder(playerMoves);
        var mobMovesArray = Json.createArrayBuilder(mobMoves);

        gameJson.add(playerMovesArray.build());
        gameJson.add(mobMovesArray.build());

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
     * @param m player move to add to the list of mvoes
     * @return true if the move was recorded
     */
    public static boolean addPlayerMove(String m) {
        if (isRecording) {
            playerMoves.add(m);
            return true;
        }

        return false;
    }

    /**
     * @param m mob move to add to the list of mvoes
     * @return true if the move was recorded
     */
    public static boolean addMobMove(String m) {
        if (isRecording) {
            mobMoves.add(m);
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
     * @param isRecording, sets the game to be recorded or not
     */
    public static void setIsRecording(boolean isRecording) {
        RecordAndPlay.isRecording = isRecording;
    }

    private static void resetRecordingState() {
        playerMoves.clear();
        mobMoves.clear();
    }
}
