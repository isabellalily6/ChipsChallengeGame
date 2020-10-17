package nz.ac.vuw.ecs.swen225.gp20.maze;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A class to handle the movement of the cobras on a separate thread
 *
 * @author Benjamin Doornbos
 */
public class MovementThreadHandler extends Thread {
    private final Maze maze;

    /**
     * @param maze The maze the cobras are being moved on
     */
    public MovementThreadHandler(Maze maze) {
        checkArgument(maze.getCobras() != null && !maze.getCobras().isEmpty());
        this.maze = maze;
    }

    @Override
    public void run() {
        while (true) {
            for (Cobra c : maze.getCobras()) {
                maze.moveActor(c, c.nextMove());
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
