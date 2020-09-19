package nz.ac.vuw.ecs.swen225.gp20.application;

import nz.ac.vuw.ecs.swen225.gp20.maze.Maze;

public class Main {
    // initialize the game variables
    GUI gui;
    Maze maze;

    // game information
    int level = 1;

    public Main(){
      maze = new Maze(level);
      gui = new GUI(this, maze);
      gui.setUpGui();

    }


    public static void main(String[] args) {
	    System.out.println("Welcome to Chaps Challenge!");
	    new Main();
    }
}
