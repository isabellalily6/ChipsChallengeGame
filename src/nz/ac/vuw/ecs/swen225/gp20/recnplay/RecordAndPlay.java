package nz.ac.vuw.ecs.swen225.gp20.recnplay;

import nz.ac.vuw.ecs.swen225.gp20.application.GUI;
import nz.ac.vuw.ecs.swen225.gp20.application.Main;
import nz.ac.vuw.ecs.swen225.gp20.maze.*;
import nz.ac.vuw.ecs.swen225.gp20.persistence.LevelLoader;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

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
    private static Thread playRecordingThread;
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
     * @return See if a recording is paused
     */
    public static boolean isRecordingPaused() {
        return recordingPaused;
    }

    /**
     * @param recordingPaused Pauses the playing recording
     */
    public static void setRecordingPaused(boolean recordingPaused) {
        RecordAndPlay.recordingPaused = recordingPaused;
    }

    /**
     * Loads a recording from the file
     *
     * @param m is the main class, this is used to access the player, and the gui which is the parent to the filechooser
     * @author callum mckay
     */
    public static void loadRecording(Main m) {
        if (playRecordingThread != null) {
            playRecordingThread.interrupt();
        }

        File jsonFile = getJsonFileToLoad(m.getGui());

        if (jsonFile == null) return;

        try {
            var parser = Json.createReader(new FileReader(jsonFile, StandardCharsets.UTF_8));
            var jsonArr = parser.readArray();

            var gameStateJson = jsonArr.getJsonObject(0);
            var maze = loadGameState(gameStateJson);

            m.setMaze(maze);
            m.getGui().getCanvas().setMaze(maze);
            m.getGui().setMaze(maze);
            m.getGui().getCanvas().refreshComponents();
            m.getGui().getCanvas().repaint();

            var movesFromJson = jsonArr.getJsonObject(1);
            var moves = loadMoves(movesFromJson, maze.getChap());

            loadedMoves.addAll(moves);
            loadedMoves.sort(RecordedMove::compareTo);
            playRecording(m);
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
        lock.lock();
        if (isRecording && !playingRecording) {
            recordedMoves.add(new RecordedMove(a, d, timeLeft, recordedMoves.size() - 1));
            return true;
        }
        lock.unlock();
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
        if (!playingRecording) {
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
        m.getTimer().cancel();
        m.getTimer().purge();
        m.startTimer();
        m.setTimeLeft(Math.max(movesToPlay.get(0).timeLeft + 1, 100));
        m.getGui().setTimer(Math.max(movesToPlay.get(0).timeLeft + 1, 100));

        new Thread(() -> {
            //if this is different to the moveIndex we know that the user has stepped through
            int prevMoveIndex = 0;

            int timeLeft = m.getTimeLeft();

            while (!movesToPlay.isEmpty()) {
                lock.lock();
                if (!playingRecording) {
                    moveIndex = -1;
                    recordingPaused = true;
                    return;
                }

//                if (prevMoveIndex != moveIndex) {
//                    //We have gone backwards
//                    if (prevMoveIndex > moveIndex) {
//                        var playedMoves = new ArrayList<>(loadedMoves);
//                        playedMoves.removeAll(movesToPlay);
//                        playedMoves.sort(RecordedMove::compareTo);
//                        Collections.reverse(playedMoves);
//
//                        for (int i = 0; i < prevMoveIndex - moveIndex; i++) {
//                            var moveToAdd = playedMoves.get(i);
//                            movesToPlay.add(moveToAdd);
//
//                            //If the times on the next move ARE equal, we want them to both be added
//                            if (moveToAdd.getTimeLeft() != playedMoves.get(i + 1).getTimeLeft()) {
//                                break;
//                            }
//                        }
//                    } else {
//                        //we have gone forwards
//                        var movesToSkip = new ArrayList<>(loadedMoves);
//                        movesToSkip.removeAll(movesToPlay);
//
//                        for (int i = 0; i < moveIndex - prevMoveIndex; i++) {
//                            var moveToSkip = movesToSkip.get(i);
//                            movesToPlay.remove(moveToSkip);
//
//                            //If the times on the next move ARE equal, we want them to both be added
//                            if (moveToSkip.getTimeLeft() != movesToSkip.get(i + 1).getTimeLeft()) {
//                                break;
//                            }
//                        }
//                    }
//
//                    //ensure the moves are still sorted
//                    movesToPlay.sort(RecordedMove::compareTo);
//
//                    //make sure indexes now match up
//                    // in case the recording is paused we dont want to repeat the skip/step back
//                    prevMoveIndex = moveIndex;
//                    m.setTimeLeft(movesToPlay.get(0).timeLeft);
//                    m.getGui().setTimer(movesToPlay.get(0).timeLeft);
//                }
//
//                if (recordingPaused) continue;
//                lock.unlock();
//
//                if (timeLeft == m.getTimeLeft()) {
//                  continue;
//                }

                timeLeft = m.getTimeLeft();
                var copiedList = new ArrayList<>(movesToPlay).stream().filter(move -> move.getTimeLeft() == m.getTimeLeft()).collect(Collectors.toList());

                for (var move : copiedList) {
                    //We want to play each move at this second
                    m.getGui().dispatchEvent(keyEventFromDirection(move.getDirection(), m.getGui()));
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    movesToPlay.remove(move);
                    moveIndex++;
                    prevMoveIndex = moveIndex;
                }
                timeLeft--;
                m.setTimeLeft(timeLeft);
                m.getGui().setTimer(timeLeft);
            }
        }).start();
    }

    private static Maze loadGameState(JsonObject gameStateJson) {
        var level = gameStateJson.getJsonNumber("level").intValue();
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
                recordedMove = new RecordedMove(p, dir, timeLeft, loadedMove.getInt("moveIndex"));
            } else {
                //TODO support for other mobs in lvl 2
            }

            toReturn.add(recordedMove);
        }

        return toReturn;
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

    private static KeyEvent keyEventFromDirection(Maze.Direction d, GUI gui) {
        switch (d) {
            case UP:
                return new KeyEvent(gui, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, KeyEvent.VK_UP, KeyEvent.CHAR_UNDEFINED);
            case DOWN:
                return new KeyEvent(gui, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, KeyEvent.VK_DOWN, KeyEvent.CHAR_UNDEFINED);
            case LEFT:
                return new KeyEvent(gui, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, KeyEvent.VK_LEFT, KeyEvent.CHAR_UNDEFINED);
            case RIGHT:
                return new KeyEvent(gui, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, KeyEvent.VK_RIGHT, KeyEvent.CHAR_UNDEFINED);
            default:
                throw new IllegalStateException("Unexpected value: " + d);
        }
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
     * A internal class which can represent a move, simply maps an Actor to a Direction
     *
     * @author callum mckay
     */
    static class RecordedMove implements Comparable<RecordedMove> {
        private final Actor actor;
        private final Maze.Direction direction;
        private final int timeLeft;
        private final int moveIndex;

        /**
         * @param actor     actor who this move has been done by
         * @param direction direction of said move
         * @param timeLeft  time left as this move was made
         * @param moveIndex index this move was made on
         * @author callum mckay
         */
        RecordedMove(Actor actor, Maze.Direction direction, int timeLeft, int moveIndex) {
            this.actor = actor;
            this.direction = direction;
            this.timeLeft = timeLeft;
            this.moveIndex = moveIndex;
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

        /**
         * @return gets the move index
         */
        public int getMoveIndex() {
            return moveIndex;
        }

        @Override
        public int compareTo(RecordedMove o) {
            if (this.timeLeft > o.timeLeft) {
                return -1;
            } else if (this.timeLeft < o.timeLeft) {
                return 1;
            } else {
                return Integer.compare(this.moveIndex, o.moveIndex);
            }
        }
    }
}
