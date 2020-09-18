package nz.ac.vuw.ecs.swen225.gp20.application;

public class Main {
    // initialize the game variables
    GUI gui;

    // initialize GUI fields

    public Main(){
      gui = new GUI(this);
      gui.setUpGui();

    }


    public static void main(String[] args) {
	    System.out.println("Welcome to Chaps Challenge!");
	    new Main();
    }
}
