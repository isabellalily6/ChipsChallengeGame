package nz.ac.vuw.ecs.swen225.gp20.recnplay;

import nz.ac.vuw.ecs.swen225.gp20.application.GUI;
import nz.ac.vuw.ecs.swen225.gp20.application.Main;
import nz.ac.vuw.ecs.swen225.gp20.maze.Direction;
import nz.ac.vuw.ecs.swen225.gp20.maze.Player;
import nz.ac.vuw.ecs.swen225.gp20.recnplay.replayConstants.ReplayModes;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

class PlayerThread extends Thread {
    private final Main main;
    private final Lock lock = new ReentrantLock();
    private final AtomicBoolean recordingPaused = new AtomicBoolean(false);
    private List<RecordedMove> movesToPlay = new ArrayList<>();
    private final AtomicInteger timeAtPause = new AtomicInteger(100);
    private final AtomicInteger moveIndex = new AtomicInteger(0);
    private final AtomicInteger moveIndexAtPause = new AtomicInteger(0);
    private final AtomicInteger prevMoveIndex = new AtomicInteger(0);
    private final AtomicInteger timeLeft = new AtomicInteger(100);
    private final AtomicInteger lastMoveTime = new AtomicInteger(100);
    private final ReplayModes replayMode;
    private final int sleepTime;


    /**
     * @param main  main class which is running the replay
     *              Passing in null allows this to be used as a fake, where the methods do nothing
     * @param mode  the mode of replay, ie: autoplay, step by step
     * @param speed the speed the replay plays at
     */
    public PlayerThread(Main main, ReplayModes mode, int speed) {
        this.main = main;
        this.replayMode = mode;
        //1000 is 1 sec, if speed is 100%, then it will run at 1000 / (100/100) = 1000
        this.sleepTime = speed == 0 ? 1000 : (int) (1000 / (speed / 100.0));
    }

    /**
     * @return If this is a real thread or a fake
     */
    public boolean isRealThread() {
        return main != null;
    }

    /**
     * @return See if a recording is paused
     */
    public boolean isRecordingPaused() {
        return recordingPaused.get();
    }

    /**
     * * Pause the playing recording
     */
    public void pauseRecording() {
        if (main == null) return;
        if (replayMode != ReplayModes.AUTO_PLAY) return;

        try {
            lock.lock();
            if (!recordingPaused.getAndSet(true)) {
                main.pauseGame(false);
                updatePausedRecording();
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Resume the playing recording
     */
    public void resumeRecording() {
        if (main == null) return;
        if (replayMode != ReplayModes.AUTO_PLAY) return;
        if (recordingPaused.getAndSet(false)) {
            try {
                lock.lock();
                moveIndex.set(moveIndexAtPause.get());
                updateTime(timeAtPause.get());
                timeLeft.set(timeAtPause.get());
                System.out.println("Resuming at move: " + moveIndexAtPause + " at time: " + timeAtPause);
                main.playGame();
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * @param forward, true if we are stepping forward, false if backward
     */
    public void stepThroughRecording(boolean forward) {
        if (main == null) return;

        if (!RecordAndPlay.playingRecording.get()) {
            return;
        }

        if (replayMode != ReplayModes.STEP_BY_STEP) return;

        if (lock.tryLock()) {
            try {
                if (forward) {
                    if (moveIndex.get() + 1 >= movesToPlay.size()) {
                        return;
                    }
                    moveIndex.getAndIncrement();
                } else {
                    if (moveIndex.get() - 1 < -1) {
                        return;
                    }
                    moveIndex.getAndDecrement();
                }
            } finally {
                lock.unlock();
            }
        }
    }

    private void updateTime(int time) {
        main.setTimeLeft(time);
        main.getGui().setTimer(time);
    }

    private static KeyEvent keyEventFromDirection(Direction d, GUI gui) {
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

    @Override
    public void run() {
        if (main == null) return;

        if (RecordAndPlay.loadedMoves.isEmpty()) return;

        //We don't want to delete the move from the real list, as the user needs to be able to step back through the list
        movesToPlay = new ArrayList<>(RecordAndPlay.loadedMoves);

        //we want to keep track of where we are, for allowing the user to step through the moves
        moveIndex.set(0);
        RecordAndPlay.playingRecording.set(true);
        recordingPaused.set(false);
        main.getTimer().cancel();
        main.getTimer().purge();

        updateTime(Math.max(movesToPlay.get(0).getTimeLeft() + 2, 100));

        //if this is different to the moveIndex we know that the user has stepped through
        prevMoveIndex.set(-1);
        timeLeft.set(main.getTimeLeft());
        if (replayMode == ReplayModes.AUTO_PLAY) {
            while (!movesToPlay.isEmpty()) {
                if (!RecordAndPlay.playingRecording.get()) {
                    moveIndex.set(-1);
                    break;
                }

                if (recordingPaused.get()) {
                    continue;
                }

                int finalTimeLeft = timeLeft.get();
                var movesWithCorrectMoveIndices = new ArrayList<>(movesToPlay).stream()
                        .filter(move -> move.getMoveIndex() > prevMoveIndex.get()).collect(Collectors.toList());

                //get the moves for the current second
                //or the previous moves in the case where some moves were not processed properly (can happen if you pause mid second)
                var copiedList = new ArrayList<>(movesWithCorrectMoveIndices).stream()
                        .filter(move -> move.getTimeLeft() == finalTimeLeft || move.getTimeLeft() == finalTimeLeft + 1)
                        .collect(Collectors.toList());

                for (var move : copiedList) {
                    if (recordingPaused.get()) {
                        break;
                    }
                    if (lock.tryLock()) {
                        try {
                            if (recordingPaused.get()) {
                                break;
                            }
                            System.out.println("Processing: " + move.toString());

                            playMove(move);

                            movesToPlay.remove(move);

                            prevMoveIndex.set(moveIndex.get());
                            moveIndex.set(move.getMoveIndex() + 1);

                            //used in case the pause has happened while processing this move
                            lastMoveTime.set(move.getTimeLeft());
                        } finally {
                            lock.unlock();
                        }
                        if (recordingPaused.get()) {
                            System.out.println("Updating pausing var for move: " + move.toString());
                            updatePausedRecording();
                            break;
                        }

                        try {
                        /*
                          This is not busy waiting, even though this is in a loop
                          This is what is simulating the timer, so that the moves happen in real time, not all at once
                         */
                            Thread.sleep(sleepTime / copiedList.size());
                        } catch (InterruptedException e) {
                            if (Thread.holdsLock(RecordAndPlay.lock)) {
                                RecordAndPlay.lock.unlock();
                            }
                            System.out.println("Exception thrown");
                            RecordAndPlay.playRecordingThread.interrupt();
                            return;
                        }
                    } else {
                        break;
                    }
                }

                if (copiedList.isEmpty()) {
                    try {
                        System.out.println("No moves found for: " + prevMoveIndex + " at time: " + timeLeft);
                    /*
                        This is not busy waiting, even though this is in a loop
                        This is what is simulating the timer, so that the moves happen in real time, not all at once
                    */
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        if (Thread.holdsLock(RecordAndPlay.lock)) {
                            RecordAndPlay.lock.unlock();
                        }
                        System.out.println("Exception thrown");
                        RecordAndPlay.playRecordingThread.interrupt();
                        return;
                    }
                }

                if (!recordingPaused.get()) {
                    timeLeft.decrementAndGet();
                    updateTime(timeLeft.get());
                }
            }
        } else if (replayMode == ReplayModes.STEP_BY_STEP) {
            prevMoveIndex.set(-1);
            moveIndex.set(-1);

            main.pauseGame(false);
            while (RecordAndPlay.playingRecording.get()) {
                if (prevMoveIndex.get() != moveIndex.get()) {
                    if (lock.tryLock()) {
                        try {
                            //Moved forwards
                            if (prevMoveIndex.get() < moveIndex.get()) {
                                var move = movesToPlay.get(moveIndex.get());
                                playMove(move);
                                prevMoveIndex.set(moveIndex.get());
                                updateTime(move.getTimeLeft());
                            } else {
                                var move = movesToPlay.get(moveIndex.get() + 1);
                                var inverseMove = move.getInverse();
                                playMove(inverseMove);

                                //make sure chap is facing the right direction
                                main.getMaze().getChap().setDir(move.getDirection());
                                main.getGui().getCanvas().refreshComponents();
                                prevMoveIndex.set(moveIndex.get());
                                updateTime(move.getTimeLeft());
                            }
                        } finally {
                            lock.unlock();
                        }
                    }
                }
            }

            return;
        } else {
            //invalid state
            return;
        }
        System.out.println("Recording is over, no moves left");
        main.startGame(1);
        main.playGame();
        RecordAndPlay.endPlayingRecording();
    }

    private void playMove(RecordedMove move) {
        if (move.getActor() instanceof Player) {
            main.getGui().dispatchEvent(keyEventFromDirection(move.getDirection(), main.getGui()));
        } else {
            main.getMaze().moveActor(move.getActor(), move.getDirection());

            //repaint the gui
            main.getGui().getCanvas().refreshComponents();
            main.getGui().getCanvas().repaint();
            main.getGui().repaint();
        }
    }

    private void updatePausedRecording() {
        timeAtPause.set(lastMoveTime.get());
        moveIndexAtPause.set(moveIndex.get());
        System.out.println("Pausing at move: " + moveIndexAtPause + " at time: " + timeAtPause);
        System.out.println("Last move completed: " + prevMoveIndex);
    }
}
