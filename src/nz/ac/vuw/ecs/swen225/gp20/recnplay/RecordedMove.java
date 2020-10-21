package nz.ac.vuw.ecs.swen225.gp20.recnplay;

import nz.ac.vuw.ecs.swen225.gp20.commons.Direction;

/**
 * A representation a move for recording/playback purposes, maps the index of this move with various things.
 *
 * @author Callum McKay 300496765
 */
public class RecordedMove implements Comparable<RecordedMove> {
    private final Direction direction;
    private final int timeLeft;
    private final int moveIndex;
    private final int level;

    /**
     * @param direction direction of said move
     * @param timeLeft  time left as this move was made
     * @param moveIndex index this move was made on
     * @param level     the level this move was made on
     */
    public RecordedMove(Direction direction, int timeLeft, int moveIndex, int level) {
        this.direction = direction;
        this.timeLeft = timeLeft;
        this.moveIndex = moveIndex;
        this.level = level;
    }

    /**
     * Returns a move which is the inverse of this one, ie opposite direction
     *
     * @return the inverse direction move
     */
    public RecordedMove getInverse() {
        var newDir = getOppositeDirection(direction);
        return new RecordedMove(newDir, timeLeft, moveIndex, level);
    }

    @Override
    public String toString() {
        return "RecordedMove{" +
                "direction=" + direction +
                ", timeLeft=" + timeLeft +
                ", moveIndex=" + moveIndex +
                '}';
    }

    /**
     * @return the direction of this recorded move
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
        if (this.moveIndex > o.moveIndex) {
            return 1;
        } else if (this.moveIndex < o.moveIndex) {
            return -1;
        } else {
            return Integer.compare(o.timeLeft, this.timeLeft);
        }
    }

    /**
     * @return the level this move was made on
     */
    public int getLevel() {
        return level;
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
}
