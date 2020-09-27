package nz.ac.vuw.ecs.swen225.gp20.render;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * Objects of this class are sound effects to be used in the game.
 *
 * @author Seth Patel
 **/
public enum SoundEffect {
    UNLOCK_DOOR("data/unlock_door.wav"),
    HIT_BY_MOB("data/hit_by_mob.wav"),
    PICK_UP_ITEM("data/pick_up_item.wav"),
    EXIT("data/exit.wav"),
    INFO_FIELD("data/info_field.wav"),
    STEP("data/step.wav"),
    COBRA("data/cobra.wav");

    private String filename;

    /**
     * Create and play a sound effect once.
     *
     * @param filename the sound file to use
     **/
    SoundEffect(String filename) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filename));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }
}
