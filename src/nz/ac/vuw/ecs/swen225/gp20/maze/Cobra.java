package nz.ac.vuw.ecs.swen225.gp20.maze;

import nz.ac.vuw.ecs.swen225.gp20.commons.Direction;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * The cobra is an enemy of chap. They will move in a loop around the level.
 *
 * @author Benjamin Doornbos 300487256
 */
public class Cobra extends Actor {
    private final Queue<Direction> moves;
    private boolean testMode;

    /**
     * Creates a new Actor
     *
     * @param location the tile this actor is standing on
     * @param moves    a queue of moves that the cobra will take
     */
    public Cobra(Tile location, Queue<Direction> moves) {
        super(location, "cobra");
        this.moves = moves;
        this.testMode = false;
    }

    /**
     * -----TEST CONSTRUCTOR-----
     * creates a cobra in test mode - no moves in the queue
     *
     * @param location the tile this actor is standing on
     * @param testMode whether this cobra is in test mode
     */
    public Cobra(Tile location, boolean testMode) {
        this(location, new ArrayDeque<>());
        this.testMode = testMode;
    }


    /**
     * Will take a move off of the queue then re-offer it, so that the queue will loop infinitely
     *
     * @return the next move this cobra needs to take
     */
    public Direction nextMove() {
        checkArgument(!testMode && !moves.isEmpty(), "This cobra has no moves");
        Direction move = moves.poll();
        moves.offer(move);
        return move;
    }

    /**
     * @return a list equivalent to the queue of cobra moves
     */
    public List<Direction> getListOfMoves() {
        return new ArrayList<>(moves);
    }

    /**
     * @return a copy of the move queue
     */
    public Queue<Direction> getMoves() {
        return new ArrayDeque<>(moves);
    }

    /**
     * @return if the moves of this cobra contains null, then it is a test cobra that needs no thread
     */
    public boolean inTestMode() {
        return testMode;
    }
}
