package nz.ac.vuw.ecs.swen225.gp20.recnplay;

import nz.ac.vuw.ecs.swen225.gp20.application.GUI;
import nz.ac.vuw.ecs.swen225.gp20.application.Main;
import nz.ac.vuw.ecs.swen225.gp20.maze.Actor;
import nz.ac.vuw.ecs.swen225.gp20.maze.Maze;
import nz.ac.vuw.ecs.swen225.gp20.maze.Player;
import nz.ac.vuw.ecs.swen225.gp20.persistence.LevelLoader;
import nz.ac.vuw.ecs.swen225.gp20.recnplay.replayConstants.AutoPlayDialogCreator;
import nz.ac.vuw.ecs.swen225.gp20.recnplay.replayConstants.ReplayModes;
import nz.ac.vuw.ecs.swen225.gp20.recnplay.replayConstants.ReplayOptionsCreator;
import nz.ac.vuw.ecs.swen225.gp20.recnplay.replayConstants.StepByStepDialogCreator;

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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This is a class with static methods for starting a recording, and loading/saving a recording to a file in JSON format
 *
 * @author callum mckay
 */
public class RecordAndPlay {
    private static final List<RecordedMove> recordedMoves = new ArrayList<>();
    private static JsonObjectBuilder gameState;
    private static GUI parentComponent;
    static PlayerThread playRecordingThread = new PlayerThread(null, null, 0);

    static final List<RecordedMove> loadedMoves = new ArrayList<>();
    static final Lock lock = new ReentrantLock();
    static int replaySpeed = 0;
    static boolean isRecording = false;
    static AtomicBoolean playingRecording = new AtomicBoolean(false);
    static ReplayModes replayMode;
    private static ReplayOptionDialog dialog;

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
     * @return See if a recording is paused
     */
    public static boolean isRecordingPaused() {
        return playRecordingThread.isRecordingPaused();
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

            var gameStateJson = jsonArr.getJsonObject(0);
            var maze = LevelLoader.loadGameState(gameStateJson);

            m.setMaze(maze);
            m.getGui().setMaze(maze);
            m.getGui().getCanvas().setMaze(maze);
            m.getGui().getCanvas().refreshComponents();
            m.getGui().getCanvas().repaint();

            var movesFromJson = jsonArr.getJsonObject(1);
            var moves = loadMoves(movesFromJson, maze.getChap());
            loadedMoves.clear();
            loadedMoves.addAll(moves);
            loadedMoves.sort(RecordedMove::compareTo);
            dialog = new ReplayOptionsCreator().createDialog(m);
            dialog.setVisible(true);
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
        try {
            lock.lock();
            if (isRecording && !playingRecording.get()) {
                recordedMoves.add(new RecordedMove(a, d, timeLeft, recordedMoves.size()));
                return true;
            }

            return false;
        } finally {
            lock.unlock();
        }
    }

    /**
     * @return if the game is being recorded
     * @author callum mckay
     */
    public static boolean isRecording() {
        return isRecording;
    }

    /**
     * Pause the playing recording
     */
    public static void pauseRecording() {
        if (playRecordingThread != null && playRecordingThread.isAlive() && !playRecordingThread.isInterrupted()) {
            playRecordingThread.pauseRecording();
        }
    }

    /**
     * Resume the playing recording
     */
    public static void resumeRecording() {
        if (playRecordingThread != null && playRecordingThread.isAlive() && !playRecordingThread.isInterrupted()) {
            playRecordingThread.resumeRecording();
        }
    }

    /**
     * Stop the recording from playing
     */
    public static void endPlayingRecording() {
        playingRecording.set(false);
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
     * Steps the recording forwards by 1 step
     */
    public static void stepForward() {
        if (playRecordingThread != null && playRecordingThread.isAlive() && !playRecordingThread.isInterrupted()) {
            playRecordingThread.stepThroughRecording(true);
        }
    }

    /**
     * Steps the recording backwards by 1 step
     */
    public static void stepBackward() {
        if (playRecordingThread != null && playRecordingThread.isAlive() && !playRecordingThread.isInterrupted()) {
            playRecordingThread.stepThroughRecording(false);
        }
    }

    /**
     * @param m is the main which will be playing this recording
     * @author callum mckay
     */
    public static void playRecording(Main m) {
        if (m == null) {
            return;
        }
        dialog.setVisible(false);
        if (playRecordingThread.isRealThread()) {
            playRecordingThread.interrupt();

            if (!lock.tryLock()) {
                lock.unlock();
            }
        }
        playRecordingThread = new PlayerThread(m, replayMode, replaySpeed);
        playRecordingThread.start();
        if (replayMode == ReplayModes.AUTO_PLAY) {
            dialog = new AutoPlayDialogCreator().createDialog(m);
            dialog.setVisible(true);
        } else if (replayMode == ReplayModes.STEP_BY_STEP) {
            dialog = new StepByStepDialogCreator().createDialog(m);
            dialog.setVisible(true);
        }
    }

    private static List<RecordedMove> loadMoves(JsonObject movesJson, Player p) {
        var toReturn = new ArrayList<RecordedMove>();

        for (var move : movesJson.getJsonArray("moves")) {
            var loadedMove = move.asJsonObject();

            var actorName = loadedMove.getString("actor");
            var dir = Maze.Direction.valueOf(loadedMove.getString("dir"));
            var timeLeft = loadedMove.getInt("timeLeft");

            RecordedMove recordedMove = null;
            if (actorName.equals("player")) {
                //TODO: make this not get the mazes chap but instead the chap from the new maze
                // once that has been loaded
                var moveIndex = loadedMove.getInt("moveIndex");
                recordedMove = new RecordedMove(p, dir, timeLeft, Math.max(moveIndex, 0));
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
                    .add("dir", move.getDirection().toString())
                    .add("moveIndex", move.getMoveIndex());
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

    /**
     * @param mode  sets the replay mode
     * @param speed speed to play recording at
     */
    public static void setRecordingMode(ReplayModes mode, int speed) {
        replayMode = mode;
        replaySpeed = speed;
    }
}
