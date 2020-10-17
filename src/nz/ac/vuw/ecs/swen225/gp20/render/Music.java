package nz.ac.vuw.ecs.swen225.gp20.render;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * Objects of this class represent music to play in the game.
 *
 * @author Seth Patel 300488677
 **/
public class Music {

    private static final String musicFile = "data/music.wav";
    private Clip clip;

    /**
     * Create a music object.
     **/
    public Music() {
        Runnable runnable = () -> {
            try {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(musicFile));
                clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                clip.start();
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
                e.printStackTrace();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
}
