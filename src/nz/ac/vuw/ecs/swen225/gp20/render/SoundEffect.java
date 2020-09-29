package nz.ac.vuw.ecs.swen225.gp20.render;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * Objects of this class are sound effects to be used in the game.
 *
 * @author Seth Patel
 **/
public class SoundEffect {

    private Sound sound;

    /**
     * Create and play a sound effect once.
     *
     * @param sound the sound to use
     **/
    public static void play(Sound sound) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(sound.filename));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }
}
