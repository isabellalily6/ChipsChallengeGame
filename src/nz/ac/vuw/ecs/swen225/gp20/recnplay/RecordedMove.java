package nz.ac.vuw.ecs.swen225.gp20.recnplay;

import nz.ac.vuw.ecs.swen225.gp20.maze.Actor;
import nz.ac.vuw.ecs.swen225.gp20.maze.Maze;

/**
 * A internal class which can represent a move, simply maps an Actor to a Direction
 *
 * @author callum mckay
 */
class RecordedMove implements Comparable<RecordedMove> {
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

    @Override
    public String toString() {
        return "RecordedMove{" +
                "actor=" + actor.getName() +
                ", direction=" + direction +
                ", timeLeft=" + timeLeft +
                ", moveIndex=" + moveIndex +
                '}';
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
    public int hashCode() {
        return (int) Math.pow(timeLeft, moveIndex);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RecordedMove) {
            var move = (RecordedMove) obj;
            return this.actor.equals(move.actor) &&
                    this.direction.equals(move.direction) &&
                    this.moveIndex == move.moveIndex &&
                    this.timeLeft == move.timeLeft;
        }

        return super.equals(obj);
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
