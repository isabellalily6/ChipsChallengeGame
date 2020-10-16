package nz.ac.vuw.ecs.swen225.gp20.recnplay;

import nz.ac.vuw.ecs.swen225.gp20.application.Main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

class PlayerThread extends Thread {
    private final Main main;
    private final Lock lock = new ReentrantLock();
    private final AtomicBoolean recordingPaused = new AtomicBoolean(false);
    private int timeAtPause;
    private int moveIndex;
    private int moveIndexAtPause;
    private int prevMoveIndex;
    private int timeLeft;
    private int lastMoveTime;


    /**
     * @param main main class which is running the replay
     *             Passing in null allows this to be used as a fake, where the methods do nothing
     */
    public PlayerThread(Main main) {
        this.main = main;
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

        try {
            lock.lock();
            if (!recordingPaused.getAndSet(true)) {
                main.pauseGame();
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

        if (recordingPaused.getAndSet(false)) {
            try {
                lock.lock();
                moveIndex = moveIndexAtPause;
                main.setTimeLeft(timeAtPause);
                main.getGui().setTimer(timeAtPause);
                timeLeft = timeAtPause;
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

        if (!RecordAndPlay.playingRecording) {
            return;
        }

        if (forward) moveIndex++;
        else moveIndex--;
    }

    @Override
    public void run() {
        if (main == null) return;

        if (RecordAndPlay.loadedMoves.isEmpty()) return;

        //We don't want to delete the move from the real list, as the user needs to be able to step back through the list
        var movesToPlay = new ArrayList<>(RecordAndPlay.loadedMoves);

        //we want to keep track of where we are, for allowing the user to step through the moves
        moveIndex = 0;
        RecordAndPlay.playingRecording = true;
        recordingPaused.set(false);
        main.getTimer().cancel();
        main.getTimer().purge();
        main.startTimer();
        main.setTimeLeft(Math.max(movesToPlay.get(0).getTimeLeft() + 2, 100));
        main.getGui().setTimer(Math.max(movesToPlay.get(0).getTimeLeft() + 2, 100));

        //if this is different to the moveIndex we know that the user has stepped through
        prevMoveIndex = -1;
        timeLeft = main.getTimeLeft();
        while (!movesToPlay.isEmpty()) {
            if (!RecordAndPlay.playingRecording) {
                moveIndex = -1;
                break;
            }

            if (recordingPaused.get()) {
                continue;
            }

            if (Math.abs(prevMoveIndex - moveIndex) != 1) {
                //We have gone backwards
                if (prevMoveIndex > moveIndex) {
                    var playedMoves = new ArrayList<>(RecordAndPlay.loadedMoves);
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
                    var movesToSkip = new ArrayList<>(RecordAndPlay.loadedMoves);
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

                //make sure indexes now match up
                // in case the recording is paused we dont want to repeat the skip/step back
                prevMoveIndex = moveIndex - 1;
                main.setTimeLeft(movesToPlay.get(0).getTimeLeft());
                main.getGui().setTimer(movesToPlay.get(0).getTimeLeft());
                timeLeft = movesToPlay.get(0).getTimeLeft();
            }

            int finalTimeLeft = timeLeft;
            var movesWithCorrectMoveIndices = new ArrayList<>(movesToPlay).stream()
                    .filter(move -> move.getMoveIndex() > prevMoveIndex).collect(Collectors.toList());

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
                    lock.unlock();
                    if (recordingPaused.get()) {
                        break;
                    }
                    System.out.println("Processing: " + move.toString());
                    //We want to play each move at this second
                    main.getGui().getMaze().moveActor(move.getActor(), move.getDirection());
                    main.getGui().getCanvas().refreshComponents();

                    movesToPlay.remove(move);

                    prevMoveIndex = moveIndex;
                    moveIndex = move.getMoveIndex() + 1;
                    lastMoveTime = move.getTimeLeft();
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
                        Thread.sleep(1000 / copiedList.size());
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
                    Thread.sleep(1000);
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
                timeLeft--;
                main.setTimeLeft(timeLeft);
                main.getGui().setTimer(timeLeft);
            }
        }
        System.out.println("Recording is over, no moves left");
        main.setTimeLeft(timeLeft);
        main.getGui().setTimer(timeLeft);
    }

    private void updatePausedRecording() {
        timeAtPause = lastMoveTime;
        moveIndexAtPause = moveIndex;
        System.out.println("Pausing at move: " + moveIndexAtPause + " at time: " + timeAtPause);
        System.out.println("Last move completed: " + prevMoveIndex);
    }
}
