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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This is a class with static methods for starting a recording, and loading/saving a recording to a file in JSON format
 *
 * @author callum mckay
 */
public class RecordAndPlay {
    private static boolean isRecording = false;
    private static boolean playingRecording = false;
    private static boolean recordingPaused = true;
    private static int moveIndex = -1;
    private static final List<RecordedMove> recordedMoves = new ArrayList<>();
    private static final List<RecordedMove> loadedMoves = new ArrayList<>();
    private static JsonObjectBuilder gameState;
    private static GUI parentComponent;
    private static final Lock lock = new ReentrantLock();


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
     * @return See if a recording is playing
     */
    public static boolean isPlayingRecording() {
        return playingRecording;
    }

    /**
     * @param playingRecording Pauses the playing recording
     */
    public static void setPlayingRecording(boolean playingRecording) {
        RecordAndPlay.playingRecording = playingRecording;
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

            var movesFromJson = jsonArr.getJsonObject(1);
            var moves = loadMoves(movesFromJson, m.getMaze().getChap());

            loadedMoves.addAll(moves);
            loadedMoves.sort(RecordedMove::compareTo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * @param a        actor who performed this move
     * @param d        direction of this move
     * @param timeLeft time that was left in the game as this move was made
     * @return true if the move was recorded, false if not
     * @author callum mckay
     */
    public static boolean addMove(Actor a, Maze.Direction d, int timeLeft) {
        if (isRecording) {
            recordedMoves.add(new RecordedMove(a, d, timeLeft));
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
     * Stop the recording from playing
     */
    public static void endPlayingRecording() {
        playingRecording = false;
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

    /**
     * @param forward, true if we are stepping forward, false if backward
     */
    public static void stepThroughRecording(boolean forward) {
        if (recordingPaused || !playingRecording) {
            return;
        }

        if (forward) moveIndex++;
        else moveIndex--;
    }

    /**
     * @param m is the main which will be playing this recording
     * @author callum mckay
     */
    public static void playRecording(Main m) {
        if (loadedMoves.isEmpty()) return;

        //We don't want to delete the move from the real list, as the user needs to be able to step back through the list
        var movesToPlay = new ArrayList<>(loadedMoves);

        //we want to keep track of where we are, for allowing the user to step through the moves
        moveIndex = 0;
        playingRecording = true;
        recordingPaused = false;

        new Thread(() -> {


            //if this is different to the moveIndex we know that the user has stepped through
            int prevMoveIndex = 0;

            while (!movesToPlay.isEmpty()) {
                lock.lock();
                if (!playingRecording) {
                    moveIndex = -1;
                    recordingPaused = true;
                    return;
                }

                if (prevMoveIndex != moveIndex) {
                    //We have gone backwards
                    if (prevMoveIndex > moveIndex) {
                        var playedMoves = new ArrayList<>(loadedMoves);
                        playedMoves.removeAll(movesToPlay);
                        playedMoves.sort(RecordedMove::compareTo);
                        Collections.reverse(playedMoves);

                        for (int i = 0; i < prevMoveIndex - moveIndex; i++) {
                            var moveToAdd = playedMoves.get(i);
                            movesToPlay.add(moveToAdd);

                            //If the times on the next move ARE equal, we want them to both be added
                            if (moveToAdd.getTimeLeft() != playedMoves.get(i + 1).getTimeLeft()) {
                                break;
                            }
                        }
                    } else {
                        //we have gone forwards
                        var movesToSkip = new ArrayList<>(loadedMoves);
                        movesToSkip.removeAll(movesToPlay);

                        for (int i = 0; i < moveIndex - prevMoveIndex; i++) {
                            var moveToSkip = movesToSkip.get(i);
                            movesToPlay.remove(moveToSkip);

                            //If the times on the next move ARE equal, we want them to both be added
                            if (moveToSkip.getTimeLeft() != movesToSkip.get(i + 1).getTimeLeft()) {
                                break;
                            }
                        }
                    }

                    //ensure the moves are still sorted
                    movesToPlay.sort(RecordedMove::compareTo);
                    //TODO: SET TIMER ON MAIN TO BE THAT OF THE FIRST MOVE
                }

                if (recordingPaused) continue;
                lock.unlock();

                int timeLeft = m.getTimeLeft();
                var copiedList = new ArrayList<>(movesToPlay);

                for (var move : copiedList) {

                    //We want to play each move at this second
                    if (timeLeft == move.getTimeLeft()) {
                        m.getMaze().moveActor(move.getActor(), move.getDirection());
                        movesToPlay.remove(move);
                        moveIndex++;
                        prevMoveIndex = moveIndex;
                    } else break;
                }
            }
        }).start();
    }

    private static List<RecordedMove> loadMoves(JsonObject movesJson, Player p) {
        var toReturn = new ArrayList<RecordedMove>();

        for (var move : movesJson.getJsonArray("moves")) {
            var loadedMove = move.asJsonObject();

            var actorName = loadedMove.get("actor").toString();

            //this is due to the value being stored as a string, so it would come out as ""player""
            actorName = turnJsonStringToString(actorName);
            var dir = getDirection(loadedMove.get("dir").toString());
            var timeLeft = loadedMove.getInt("timeLeft");

            RecordedMove recordedMove = null;
            if (actorName.equals("player")) {
                //TODO: make this not get the mazes chap but instead the chap from the new maze
                // once that has been loaded
                recordedMove = new RecordedMove(p, dir, timeLeft);
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
                    .add("timeLeft", move.getTimeLeft())
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
    static class RecordedMove implements Comparable<RecordedMove> {
        private final Actor actor;
        private final Maze.Direction direction;
        private final int timeLeft;

        /**
         * @param actor     actor who this move has been done by
         * @param direction direction of said move
         * @param timeLeft  time left as this move was made
         * @author callum mckay
         */
        RecordedMove(Actor actor, Maze.Direction direction, int timeLeft) {
            this.actor = actor;
            this.direction = direction;
            this.timeLeft = timeLeft;
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


        /**
         * @return the time left as this move was made
         */
        public int getTimeLeft() {
            return timeLeft;
        }

        @Override
        public int compareTo(RecordedMove o) {
            return Integer.compare(this.timeLeft, o.timeLeft);
        }
    }
}
