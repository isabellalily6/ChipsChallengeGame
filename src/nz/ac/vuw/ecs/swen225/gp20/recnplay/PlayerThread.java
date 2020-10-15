package nz.ac.vuw.ecs.swen225.gp20.recnplay;

import nz.ac.vuw.ecs.swen225.gp20.application.Main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

class PlayerThread extends Thread {
    private final Main main;

    /**
     * @param main main class which is running the replay
     */
    public PlayerThread(Main main) {
        this.main = main;
    }


    @Override
    public void run() {
        if (RecordAndPlay.loadedMoves.isEmpty()) return;

        //We don't want to delete the move from the real list, as the user needs to be able to step back through the list
        var movesToPlay = new ArrayList<>(RecordAndPlay.loadedMoves);

        //we want to keep track of where we are, for allowing the user to step through the moves
        RecordAndPlay.moveIndex = 0;
        RecordAndPlay.playingRecording = true;
        RecordAndPlay.recordingPaused = false;
        main.getTimer().cancel();
        main.getTimer().purge();
        main.startTimer();
        main.setTimeLeft(Math.max(movesToPlay.get(0).getTimeLeft() + 1, 100));
        main.getGui().setTimer(Math.max(movesToPlay.get(0).getTimeLeft() + 1, 100));


        //if this is different to the moveIndex we know that the user has stepped through
        int prevMoveIndex = 0;

        int timeLeft = main.getTimeLeft();

        while (!movesToPlay.isEmpty()) {
            if (!RecordAndPlay.playingRecording) {
                RecordAndPlay.moveIndex = -1;
                RecordAndPlay.recordingPaused = true;
                return;
            }

            if (prevMoveIndex != RecordAndPlay.moveIndex) {
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
                prevMoveIndex = RecordAndPlay.moveIndex;
                main.setTimeLeft(movesToPlay.get(0).getTimeLeft());
                main.getGui().setTimer(movesToPlay.get(0).getTimeLeft());
            }

            if (RecordAndPlay.recordingPaused) continue;

            timeLeft = main.getTimeLeft();
            var copiedList = new ArrayList<>(movesToPlay).stream().filter(move -> move.getTimeLeft() == main.getTimeLeft()).collect(Collectors.toList());

            for (var move : copiedList) {
                //We want to play each move at this second
                main.getGui().getMaze().moveActor(move.getActor(), move.getDirection());
                main.getGui().getCanvas().refreshComponents();
                // m.getGui().dispatchEvent(keyEventFromDirection(move.getDirection(), m.getGui()));
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    if (Thread.holdsLock(RecordAndPlay.lock)) {
                        RecordAndPlay.lock.unlock();
                    }
                    RecordAndPlay.playRecordingThread.interrupt();
                    return;
                }
                main.getGui().getCanvas().repaint();
                movesToPlay.remove(move);
                RecordAndPlay.moveIndex++;
                prevMoveIndex = RecordAndPlay.moveIndex;
            }
            timeLeft--;
            main.setTimeLeft(timeLeft);
            main.getGui().setTimer(timeLeft);
        }
    }

}
