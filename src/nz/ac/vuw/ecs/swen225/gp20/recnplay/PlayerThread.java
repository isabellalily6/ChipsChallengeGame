package nz.ac.vuw.ecs.swen225.gp20.recnplay;

import nz.ac.vuw.ecs.swen225.gp20.application.Main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

class PlayerThread extends Thread {
    private final Main main;
    private final Lock lock = new ReentrantLock();
    private boolean recordingPaused;
    private int timeAtPause;
    private int moveIndexAtPause;
    private int prevMoveIndex;
    private int timeLeft;
    private int lastMoveTime;

    /**
     * @param main main class which is running the replay
     */
    public PlayerThread(Main main) {
        this.main = main;
    }

    /**
     * @return See if a recording is paused
     */
    public boolean isRecordingPaused() {
        return recordingPaused;
    }

    /**
     * * Pause the playing recording
     */
    public void pauseRecording() {
        lock.lock();
        if (!recordingPaused) {
            recordingPaused = true;
            main.pauseGame();
            updatePausedRecording();
        }
        lock.unlock();
    }

    private void updatePausedRecording() {
        timeAtPause = lastMoveTime;
        moveIndexAtPause = RecordAndPlay.moveIndex;
        System.out.println("Pausing at move: " + moveIndexAtPause + " at time: " + timeAtPause);
        System.out.println("Last move completed: " + prevMoveIndex);
    }

    /**
     * Resume the playing recording
     */
    public void resumeRecording() {
        if (recordingPaused) {
            lock.lock();
            RecordAndPlay.moveIndex = moveIndexAtPause;
            main.setTimeLeft(timeAtPause);
            main.getGui().setTimer(timeAtPause);
            timeLeft = timeAtPause;
            System.out.println("Resuming at move: " + moveIndexAtPause + " at time: " + timeAtPause);
            main.playGame();
            recordingPaused = false;
            lock.unlock();
        }
    }

    @Override
    public void run() {
        if (RecordAndPlay.loadedMoves.isEmpty()) return;

        //We don't want to delete the move from the real list, as the user needs to be able to step back through the list
        var movesToPlay = new ArrayList<>(RecordAndPlay.loadedMoves);

        //we want to keep track of where we are, for allowing the user to step through the moves
        RecordAndPlay.moveIndex = 0;
        RecordAndPlay.playingRecording = true;
        recordingPaused = false;
        main.getTimer().cancel();
        main.getTimer().purge();
        main.startTimer();
        main.setTimeLeft(Math.max(movesToPlay.get(0).getTimeLeft() + 2, 100));
        main.getGui().setTimer(Math.max(movesToPlay.get(0).getTimeLeft() + 2, 100));

        //if this is different to the moveIndex we know that the user has stepped through
        prevMoveIndex = -1;
        timeLeft = main.getTimeLeft();
        var iter = 0;
        while (!movesToPlay.isEmpty()) {
            if (!RecordAndPlay.playingRecording) {
                RecordAndPlay.moveIndex = -1;
                recordingPaused = true;
                break;
            }

            if (recordingPaused) {
                continue;
            } else {
                System.out.println("iteration: " + iter++);
            }

            if (Math.abs(prevMoveIndex - RecordAndPlay.moveIndex) != 1) {
                //We have gone backwards
                if (prevMoveIndex > RecordAndPlay.moveIndex) {
                    var playedMoves = new ArrayList<>(RecordAndPlay.loadedMoves);
                    playedMoves.removeAll(movesToPlay);
                    playedMoves.sort(RecordedMove::compareTo);
                    Collections.reverse(playedMoves);

                    for (int i = 0; i < prevMoveIndex - RecordAndPlay.moveIndex; i++) {
                        var moveToAdd = playedMoves.get(i);
                        movesToPlay.add(moveToAdd);

                        //If the times on the next move ARE equal, we want them to both be added
                        if (moveToAdd.getTimeLeft() != playedMoves.get(i + 1).getTimeLeft()) {
                            break;
                        }
                    }
                } else {
                    //we have gone forwards
                    var movesToSkip = new ArrayList<>(RecordAndPlay.loadedMoves);
                    movesToSkip.removeAll(movesToPlay);

                    for (int i = 0; i < RecordAndPlay.moveIndex - prevMoveIndex; i++) {
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

                //make sure indexes now match up
                // in case the recording is paused we dont want to repeat the skip/step back
                prevMoveIndex = RecordAndPlay.moveIndex - 1;
                main.setTimeLeft(movesToPlay.get(0).getTimeLeft());
                main.getGui().setTimer(movesToPlay.get(0).getTimeLeft());
                timeLeft = movesToPlay.get(0).getTimeLeft();
            }

            int finalTimeLeft = timeLeft;
            var movesWithCorrectMoveIndices = new ArrayList<>(movesToPlay).stream().filter(move -> move.getMoveIndex() > prevMoveIndex).collect(Collectors.toList());
            var copiedList = new ArrayList<>(movesWithCorrectMoveIndices).stream().filter(move -> move.getTimeLeft() == finalTimeLeft).collect(Collectors.toList());

            for (var move : copiedList) {
                if (recordingPaused) {
                    break;
                }
                if (lock.tryLock()) {
                    lock.unlock();
                    if (recordingPaused) {
                        break;
                    }
                    System.out.println("Processing: " + move.toString());
                    //We want to play each move at this second
                    main.getGui().getMaze().moveActor(move.getActor(), move.getDirection());
                    main.getGui().getCanvas().refreshComponents();

                    movesToPlay.remove(move);

                    prevMoveIndex = RecordAndPlay.moveIndex;
                    RecordAndPlay.moveIndex = move.getMoveIndex() + 1;
                    lastMoveTime = move.getTimeLeft();
                    if (recordingPaused) {
                        System.out.println("Updating pausing var for move: " + move.toString());
                        updatePausedRecording();
                        break;
                    }

                    try {
                        //do these moves over 1sec
                        Thread.sleep(1000 / copiedList.size());
                    } catch (InterruptedException e) {
                        if (Thread.holdsLock(RecordAndPlay.lock)) {
                            RecordAndPlay.lock.unlock();
                        }
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
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    if (Thread.holdsLock(RecordAndPlay.lock)) {
                        RecordAndPlay.lock.unlock();
                    }
                    RecordAndPlay.playRecordingThread.interrupt();
                    return;
                }
            }

            if (!recordingPaused) {
                timeLeft--;
                main.setTimeLeft(timeLeft);
                main.getGui().setTimer(timeLeft);
            }
        }

        main.setTimeLeft(timeLeft);
        main.getGui().setTimer(timeLeft);
    }
}
