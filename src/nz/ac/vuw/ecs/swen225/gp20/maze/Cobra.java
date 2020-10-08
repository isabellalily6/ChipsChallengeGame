package nz.ac.vuw.ecs.swen225.gp20.maze;

import java.util.Queue;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * The cobra is an enemy of chap. They will move in a loop around the level.
 *
 * @author Benjamin Doornbos 300487256
 */
public class Cobra extends Actor {
    Queue<Maze.Direction> moves;

    /**
     * Creates a new Actor
     *
     * @param location the tile this actor is standing on
     * @param moves    a queue of moves that the cobra will take
     */
    public Cobra(Tile location, Queue<Maze.Direction> moves) {
        super(location, "cobra");
        checkArgument(!moves.isEmpty(), "Cobra must have some moves");
        this.moves = moves;
    }

    /**
     * Will take a move off of the queue then re-offer it, so that the queue will loop infinitely
     *
     * @return the next move this cobra needs to take
     */
    public Maze.Direction nextMove() {
        Maze.Direction move = moves.poll();
        moves.offer(move);
        return move;
    }
}
