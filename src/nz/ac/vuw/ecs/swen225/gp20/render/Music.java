package nz.ac.vuw.ecs.swen225.gp20.render;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

/**
 * Objects of this class represent music to play in the game.
 *
 * @author Seth Patel
 **/
public class Music {

    private static final String musicFile = "music.wav";
    private Clip clip;

    /**
     * Create a music object.
     **/
    public Music() {
        try {
            URL url = getClass().getResource(musicFile);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Play the music.
     **/
    public void play() {
        if (clip.isRunning()) clip.stop();
        clip.setFramePosition(0);
        clip.start();
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    /**
     * Stop the music.
     **/
    public void stop() {
        if (!clip.isRunning()) return;
        clip.stop();
        clip.setFramePosition(0);
    }
}
