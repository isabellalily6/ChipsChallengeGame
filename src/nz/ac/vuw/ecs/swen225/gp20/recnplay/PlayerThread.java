package nz.ac.vuw.ecs.swen225.gp20.recnplay;

import nz.ac.vuw.ecs.swen225.gp20.application.Main;
import nz.ac.vuw.ecs.swen225.gp20.recnplay.replayConstants.ReplayModes;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * A Thread which plays back a recorded game
 *
 * @author Callum McKay 300496765
 */
class PlayerThread extends Thread {
    private final Main main;
    private final Lock lock = new ReentrantLock();
    private List<RecordedMove> movesToPlay = new ArrayList<>();
    private final AtomicInteger moveIndex = new AtomicInteger(0);
    private final AtomicInteger prevMoveIndex = new AtomicInteger(0);
    private final AtomicInteger timeLeft = new AtomicInteger(100);
    private final AtomicInteger lastMoveTime = new AtomicInteger(100);
    private final ReplayModes replayMode;
    private final int sleepTime;
    private boolean levelChange;


    /**
     * Creates a thread for playing back a recording
     *
     * @param main        main class which is running the replay
     *                    Passing in null allows this to be used as a fake, where the methods do nothing
     * @param mode        the mode of replay, ie: autoplay, step by step
     * @param speed       the speed the replay plays at
     * @param levelChange if the level changes once completed, ie level 1 -> complete -> level 2
     */
    public PlayerThread(Main main, ReplayModes mode, int speed, boolean levelChange) {
        this.main = main;
        this.replayMode = mode;
        //1000 is 1 sec, if speed is 100%, then it will run at 1000 / (100/100) = 1000
        this.sleepTime = speed == 0 ? 1000 : (int) (1000 / (speed / 100.0));
        this.levelChange = levelChange;
    }

    /**
     * Check if this is a real thread, or a proxy thread
     *
     * @return If this is a real thread or a fake
     */
    public boolean isRealThread() {
        return main != null;
    }

    /**
     * Steps through the recording
     *
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

    /**
     * Gets the main class associated with this thread
     *
     * @return the main class associated with this thread
     */
    public Main getMain() {
        return main;
    }

    /**
     * This runs the PlayerThread. This will run through the moves, and play them on the maze.
     * This runs in either auto play or step by step mode
     */
    @Override
    public void run() {
        if (main == null) return;

        if (RecordAndPlay.loadedMoves.isEmpty()) return;

        //We don't want to delete the move from the real list, as the user needs to be able to step back through the list
        movesToPlay = new ArrayList<>(RecordAndPlay.loadedMoves);
        movesToPlay.sort(RecordedMove::compareTo);

        //we want to keep track of where we are, for allowing the user to step through the moves
        moveIndex.set(0);
        RecordAndPlay.playingRecording.set(true);
        main.getTimer().cancel();
        main.getTimer().purge();
        var isLevelOne = movesToPlay.stream().filter(m -> m.getLevel() == 1).findFirst();
        if (isLevelOne.isPresent()) {
            main.setLevel(1);
        } else {
            main.setLevel(2);
        }

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

                int finalTimeLeft = timeLeft.get();
                var movesWithCorrectMoveIndices = new ArrayList<>(movesToPlay).stream()
                        .filter(move -> move.getMoveIndex() > prevMoveIndex.get()).collect(Collectors.toList());

                //get the moves for the current second
                //or the previous moves in the case where some moves were not processed properly (can happen if you pause mid second)
                var copiedList = new ArrayList<>(movesWithCorrectMoveIndices).stream()
                        .filter(move -> (move.getTimeLeft() == finalTimeLeft || move.getTimeLeft() == finalTimeLeft + 1)
                                && move.getLevel() == main.getLevel())
                        .collect(Collectors.toList());

                for (var move : copiedList) {
                    playMove(move);

                    movesToPlay.remove(move);

                    prevMoveIndex.set(moveIndex.get());
                    moveIndex.set(move.getMoveIndex() + 1);

                    //used in case the pause has happened while processing this move
                    lastMoveTime.set(move.getTimeLeft());

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
                        return;
                    }

                    if (main.isLevelWon() && levelChange) {
                        incrementLevelToPlay();
                    } else if (main.isLevelWon()) {
                        main.playGame();
                        main.startGame(1);
                        main.setLevel(1);
                        return;
                    }

                }

                if (copiedList.isEmpty()) {
                    try {
                    /*
                        This is not busy waiting, even though this is in a loop
                        This is what is simulating the timer, so that the moves happen in real time, not all at once
                    */
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        if (Thread.holdsLock(RecordAndPlay.lock)) {
                            RecordAndPlay.lock.unlock();
                        }
                        return;
                    }
                }

                timeLeft.decrementAndGet();
                updateTime(timeLeft.get());
            }
        } else if (replayMode == ReplayModes.STEP_BY_STEP) {
            prevMoveIndex.set(-1);
            moveIndex.set(-1);

            main.pauseGame(false);
            updateTime(movesToPlay.get(0).getTimeLeft());
            main.getGui().updateGui(true);
            while (RecordAndPlay.playingRecording.get()) {
                if (prevMoveIndex.get() != moveIndex.get()) {
                    lock.lock();
                    try {
                        //Moved forwards
                        if (prevMoveIndex.get() < moveIndex.get()) {
                            var move = movesToPlay.get(moveIndex.get());

                            playMove(move);
                            prevMoveIndex.set(moveIndex.get());

                            updateTime(move.getTimeLeft());

                            if (main.isLevelWon() && levelChange) {
                                incrementLevelToPlay();
                            }
                        } else {
                            var move = movesToPlay.get(moveIndex.get() + 1);

                            var inverseMove = move.getInverse();
                            playMove(inverseMove);

                            //make sure chap is facing the right direction
                            main.getGui().setChapDirection(move.getDirection());
                            main.getGui().updateGui(true);
                            prevMoveIndex.set(moveIndex.get());
                            updateTime(move.getTimeLeft());
                        }
                    } finally {
                        lock.unlock();
                    }
                }
            }

            return;
        } else {
            //invalid state
            return;
        }
    }

    private void incrementLevelToPlay() {
        if (main.getLevel() != 2) {
            main.startGame(2);
            main.setLevel(2);
            main.getTimer().cancel();
            main.getTimer().purge();
            updateTime(100);
            timeLeft.set(100);
            levelChange = false;
        }
    }

    private void updateTime(int time) {
        main.setTimeLeft(time);
        main.getGui().setTimer(time);
    }

    private void playMove(RecordedMove move) {
        var sound = main.getMaze().moveChap(move.getDirection());
        if (sound != null) {
            main.getGui().playSound(sound);
        }
        //Thread safe repainting
        main.getGui().updateGui(true);
    }
}
