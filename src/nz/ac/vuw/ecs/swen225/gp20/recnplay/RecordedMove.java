package nz.ac.vuw.ecs.swen225.gp20.recnplay;

import nz.ac.vuw.ecs.swen225.gp20.commons.Direction;

/**
 * A internal class which can represent a move, simply maps an Actor to a Direction
 *
 * @author callum mckay
 */
public class RecordedMove implements Comparable<RecordedMove> {
    private final Direction direction;
    private final int timeLeft;
    private final int moveIndex;

    /**
     * @param direction direction of said move
     * @param timeLeft  time left as this move was made
     * @param moveIndex index this move was made on
     * @author callum mckay
     */
    public RecordedMove(Direction direction, int timeLeft, int moveIndex) {
        this.direction = direction;
        this.timeLeft = timeLeft;
        this.moveIndex = moveIndex;
    }

    /**
     * Returns a move which is the inverse of this one, ie opposite direction
     *
     * @return the inverse direction move
     */
    public RecordedMove getInverse() {
        var newDir = getOppositeDirection(direction);
        return new RecordedMove(newDir, timeLeft, moveIndex);
    }

    private Direction getOppositeDirection(Direction direction) {
        switch (direction) {
            case UP:
                return Direction.DOWN;
            case DOWN:
                return Direction.UP;
            case LEFT:
                return Direction.RIGHT;
            case RIGHT:
                return Direction.LEFT;
            default:
                throw new IllegalStateException("Unexpected value: " + direction);
        }
    }

    @Override
    public String toString() {
        return "RecordedMove{" +
                ", direction=" + direction +
                ", timeLeft=" + timeLeft +
                ", moveIndex=" + moveIndex +
                '}';
    }

    /**
     * @return the direction of this recorded move
     * @author callum mckay
     */
    public Direction getDirection() {
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
            return this.direction.equals(move.direction) &&
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
