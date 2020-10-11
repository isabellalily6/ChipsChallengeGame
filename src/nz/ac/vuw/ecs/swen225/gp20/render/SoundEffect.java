package nz.ac.vuw.ecs.swen225.gp20.render;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

/**
 * Objects of this class are sound effects to be used in the game. * * @author Seth Patel
 **/
public class SoundEffect {
    private static Clip unlockDoorClip;
    private static Clip hitByMobClip;
    private static Clip pickUpItemClip;
    private static Clip exitClip;
    private static Clip infoFieldClip;
    private static Clip stepClip;
    private static Clip cobraClip;

    static {
        try {
            unlockDoorClip = AudioSystem.getClip();
            unlockDoorClip.open(AudioSystem.getAudioInputStream(new File(Sound.UNLOCK_DOOR.filename)));
            hitByMobClip = AudioSystem.getClip();
            hitByMobClip.open(AudioSystem.getAudioInputStream(new File(Sound.HIT_BY_MOB.filename)));
            pickUpItemClip = AudioSystem.getClip();
            pickUpItemClip.open(AudioSystem.getAudioInputStream(new File(Sound.PICK_UP_ITEM.filename)));
            exitClip = AudioSystem.getClip();
            exitClip.open(AudioSystem.getAudioInputStream(new File(Sound.EXIT.filename)));
            infoFieldClip = AudioSystem.getClip();
            infoFieldClip.open(AudioSystem.getAudioInputStream(new File(Sound.INFO_FIELD.filename)));
            stepClip = AudioSystem.getClip();
            stepClip.open(AudioSystem.getAudioInputStream(new File(Sound.STEP.filename)));
            cobraClip = AudioSystem.getClip();
            cobraClip.open(AudioSystem.getAudioInputStream(new File(Sound.COBRA.filename)));
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private static void playSoundClip(Clip clip) {
        if (clip.isRunning()) {
            clip.stop();
        }
        clip.setFramePosition(0);
        clip.start();
    }

    /**
     * Create and play a sound effect once.     *     * @param sound the sound to use
     **/
    public static void play(Sound sound) {
        new Thread(() -> {
            switch (sound) {
                case UNLOCK_DOOR:
                    playSoundClip(unlockDoorClip);
                    break;
                case HIT_BY_MOB:
                    playSoundClip(hitByMobClip);
                    break;
                case PICK_UP_ITEM:
                    playSoundClip(pickUpItemClip);
                    break;
                case EXIT:
                    playSoundClip(exitClip);
                    break;
                case INFO_FIELD:
                    playSoundClip(infoFieldClip);
                    break;
                case STEP:
                    playSoundClip(stepClip);
                    break;
                case COBRA:
                    playSoundClip(cobraClip);
                    break;
            }
        }).start();
    }
}