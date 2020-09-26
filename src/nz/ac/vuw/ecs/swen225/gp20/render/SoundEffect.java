package nz.ac.vuw.ecs.swen225.gp20.render;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Objects of this class are sound effects to be used in the game.
 *
 * @author Seth Patel
 **/
public class SoundEffect {

    enum Sound {
        UNLOCK_DOOR(),
        HIT_BY_MOB(),
        PICK_UP_ITEM(),
        EXIT(),
        INFO_FIELD(),
        STEP(),
        COBRA();
    }

    private static final Map<Sound, String> sounds = new HashMap<Sound, String>() {{
        put(Sound.UNLOCK_DOOR, "data/unlock_door.wav");
        put(Sound.HIT_BY_MOB, "data/hit_by_mob.wav");
        put(Sound.PICK_UP_ITEM, "data/pick_up_item.wav");
        put(Sound.EXIT, "data/exit.wav");
        put(Sound.INFO_FIELD, "data/info_field.wav");
        put(Sound.STEP, "data/step.wav");
        put(Sound.COBRA, "data/cobra.wav");
    }};

    /**
     * Create and play a sound effect once.
     *
     * @param sound the sound to use
     **/
    public SoundEffect(Sound sound) {
        try {
            URL url = getClass().getResource(sounds.get(sound));
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
            if (clip.getFramePosition() == clip.getFrameLength()) {
                clip.stop();
            }
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }
}
