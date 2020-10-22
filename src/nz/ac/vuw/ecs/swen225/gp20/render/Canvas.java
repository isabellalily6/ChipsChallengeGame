package nz.ac.vuw.ecs.swen225.gp20.render;

import nz.ac.vuw.ecs.swen225.gp20.commons.Direction;
import nz.ac.vuw.ecs.swen225.gp20.maze.Cobra;
import nz.ac.vuw.ecs.swen225.gp20.maze.Maze;
import nz.ac.vuw.ecs.swen225.gp20.maze.Tile;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;


/**
 * Objects of this class provide a simple 2D view of the maze
 * to be embedded in the application. It is updated when any
 * actor moves. It only uses a certain focus region of the maze
 * which is determined based on the position of the player.
 *
 * @author Seth Patel
 **/
public class Canvas extends JLayeredPane {
    private static final int VIEW_SIZE = 9;
    private static final int VIEW_SIDE = (VIEW_SIZE - 1) / 2;
    private static final int TILE_SIZE = 50;
    private Maze maze;
    private final JLabel[][] components;
    private final JPanel boardPanel;
    private final JPanel transitionPanel;
    private static final ReentrantLock lock = new ReentrantLock();

    /**
     * New canvas to render the game.
     *
     * @param maze the maze to be rendered
     **/
    public Canvas(Maze maze) {
        setPreferredSize(new Dimension(VIEW_SIZE * TILE_SIZE, VIEW_SIZE * TILE_SIZE));
        setMinimumSize(new Dimension(VIEW_SIZE * TILE_SIZE, VIEW_SIZE * TILE_SIZE));
        setMaximumSize(new Dimension(VIEW_SIZE * TILE_SIZE, VIEW_SIZE * TILE_SIZE));
        boardPanel = new JPanel();
        transitionPanel = new JPanel();
        this.maze = maze;
        components = new JLabel[VIEW_SIZE][VIEW_SIZE];
        boardPanel.setPreferredSize(new Dimension(VIEW_SIZE * TILE_SIZE, VIEW_SIZE * TILE_SIZE));
        boardPanel.setMinimumSize(new Dimension(VIEW_SIZE * TILE_SIZE, VIEW_SIZE * TILE_SIZE));
        boardPanel.setMaximumSize(new Dimension(VIEW_SIZE * TILE_SIZE, VIEW_SIZE * TILE_SIZE));
        transitionPanel.setPreferredSize(new Dimension(VIEW_SIZE * TILE_SIZE, VIEW_SIZE * TILE_SIZE));
        transitionPanel.setMinimumSize(new Dimension(VIEW_SIZE * TILE_SIZE, VIEW_SIZE * TILE_SIZE));
        transitionPanel.setMaximumSize(new Dimension(VIEW_SIZE * TILE_SIZE, VIEW_SIZE * TILE_SIZE));
        boardPanel.setLayout(new GridLayout(VIEW_SIZE, VIEW_SIZE, 0, 0));
        boardPanel.setBounds(0, 0, VIEW_SIZE * TILE_SIZE, VIEW_SIZE * TILE_SIZE);
        transitionPanel.setBounds(0, 0, VIEW_SIZE * TILE_SIZE, VIEW_SIZE * TILE_SIZE);
        transitionPanel.setLayout(null);
        transitionPanel.setOpaque(false);
        createComponents();
        add(boardPanel, JLayeredPane.DEFAULT_LAYER);
        add(transitionPanel, JLayeredPane.PALETTE_LAYER);
    }

    private void createComponents() {
        boardPanel.removeAll();
        Tile centre = maze.getChap().getLocation();
        for (int row = centre.getRow() - VIEW_SIDE, y = 0; row <= centre.getRow() + VIEW_SIDE; row++, y++) {
            for (int col = centre.getCol() - VIEW_SIDE, x = 0; col <= centre.getCol() + VIEW_SIDE; col++, x++) {
                if (row < 0 || row > maze.getTiles()[0].length - 1 || col < 0 || col > maze.getTiles().length - 1) {
                    components[x][y] = new JLabel(makeImageIcon("data/free.png"));
                } else {
                    components[x][y] = new JLabel(makeImageIcon(maze.getTiles()[col][row].getImageURl()));
                }
                boardPanel.add(components[x][y]);
            }
        }
        components[VIEW_SIDE][VIEW_SIDE].setIcon(makeImageIcon(maze.getChap().getImageURl()));
    }

    /**
     * Refresh the components for the canvas.
     **/
    public void refreshComponents() {
        if (maze == null) return;
        Tile centre = maze.getChap().getLocation();
        for (int row = centre.getRow() - VIEW_SIDE, y = 0; row <= centre.getRow() + VIEW_SIDE; row++, y++) {
            for (int col = centre.getCol() - VIEW_SIDE, x = 0; col <= centre.getCol() + VIEW_SIDE; col++, x++) {
                if (row < 0 || row > maze.getTiles()[0].length - 1 || col < 0 || col > maze.getTiles().length - 1) {
                    components[x][y].setIcon(makeImageIcon("data/free.png"));
                } else {
                    int finalCol = col;
                    int finalRow = row;
                    List<Cobra> cobras = new ArrayList<>();
                    if (maze.getCobras() != null) {
                        cobras = maze.getCobras().stream().filter(c -> c.getLocation().equals(maze.getTiles()[finalCol][finalRow])).collect(Collectors.toList());
                    }
                    if (maze != null && maze.getBlocks() != null) {
                        if (maze.getBlocks().stream().anyMatch(b -> b.getLocation().equals(maze.getTiles()[finalCol][finalRow]))) {
                            components[x][y].setIcon(makeImageIcon("data/block.png"));
                        } else if (!cobras.isEmpty()) {
                            components[x][y].setIcon(makeImageIcon(cobras.get(0).getImageURl()));
                        } else {
                            ImageIcon icon = makeImageIcon(maze.getTiles()[col][row].getImageURl());
                            components[x][y].setIcon(icon);
                        }
                    } else if (maze != null) {
                        ImageIcon icon = makeImageIcon(maze.getTiles()[col][row].getImageURl());
                        components[x][y].setIcon(icon);
                    }
                }
            }
        }
        if (maze != null && !maze.getState().equals(Maze.LevelState.DIED)) {
            components[VIEW_SIDE][VIEW_SIDE].setIcon(makeImageIcon(maze.getChap().getImageURl()));
        }
    }

    /**
     * Draws the player between moving tiles for smoother feel.
     *
     * @param direction the direction to move
     **/
    public void movePlayer(Direction direction) {
        lock.lock();
        try {
            Point origin = components[VIEW_SIDE][VIEW_SIDE].getLocation();
            int x = (int) origin.getX();
            int y = (int) origin.getY();
            ImageIcon image = getImage(direction);
            int i = 0;
            while (i < 25) {
                Graphics g = transitionPanel.getGraphics().create(transitionPanel.getX(), transitionPanel.getY(), transitionPanel.getWidth(), transitionPanel.getHeight());
                drawUnderlyingTiles(g, direction);
                g.drawImage(image.getImage(), x, y, null);
                switch (direction) {
                    case UP:
                        y -= 2;
                        break;
                    case DOWN:
                        y += 2;
                        break;
                    case LEFT:
                        x -= 2;
                        break;
                    case RIGHT:
                        x += 2;
                        break;
                }
                transitionPanel.repaint();
                i++;
            }
        } finally {
            lock.unlock();
        }
    }

    private void drawUnderlyingTiles(Graphics g, Direction direction) {
        Point chapPos = new Point(maze.getChap().getLocation().getCol(), maze.getChap().getLocation().getRow());
        var label = components[VIEW_SIDE][VIEW_SIDE];
        ImageIcon icon = makeImageIcon(maze.getTiles()[chapPos.x][chapPos.y].getImageURl());
        if(maze.getTiles()[chapPos.x][chapPos.y].getImageURl().equals("data/exit.png")) {
            icon = makeImageIcon("data/free.png");
        }
        g.drawImage(icon.getImage(), label.getX(), label.getY(), null);
        switch (direction) {
            case UP:
                label = components[VIEW_SIDE][VIEW_SIDE-1];
                break;
            case DOWN:
                label = components[VIEW_SIDE][VIEW_SIDE+1];
                break;
            case LEFT:
                label = components[VIEW_SIDE-1][VIEW_SIDE];
                break;
            case RIGHT:
                label = components[VIEW_SIDE+1][VIEW_SIDE];
                break;
        }
        g.drawImage(((ImageIcon) label.getIcon()).getImage(), label.getX(), label.getY(), null);
    }

    private ImageIcon getImage(Direction direction) {
        switch (direction) {
            case UP:
                return makeImageIcon("data/playerUpClear.png");
            case DOWN:
                return makeImageIcon("data/playerDownClear.png");
            case LEFT:
                return makeImageIcon("data/playerLeftClear.png");
            case RIGHT:
                return makeImageIcon("data/playerRightClear.png");
            default:
                throw new IllegalArgumentException();
        }
    }

    private static ImageIcon scaleImage(ImageIcon baseImage) {
        return new ImageIcon(baseImage.getImage().getScaledInstance(TILE_SIZE, TILE_SIZE, Image.SCALE_SMOOTH));
    }

    /**
     * Convert a given image to an ImageIcon.
     *
     * @param filename the image file e.g. "data/image.png"
     * @return the converted image
     **/
    public static ImageIcon makeImageIcon(String filename) {
        return scaleImage(new ImageIcon(filename));
    }

    /**
     * Set the value of the maze.
     *
     * @param maze the value to set
     **/
    public void setMaze(Maze maze) {
        this.maze = maze;
    }
}
