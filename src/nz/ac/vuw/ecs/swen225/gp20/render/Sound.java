package nz.ac.vuw.ecs.swen225.gp20.render;

/**
 * Objects of this class represent a sound to play in the game.
 *
 * @author Seth Patel
 **/
public enum Sound {
    UNLOCK_DOOR("data/unlock_door.wav"),
    HIT_BY_MOB("data/hit_by_mob.wav"),
    PICK_UP_ITEM("data/pick_up_item.wav"),
    EXIT("data/exit.wav"),
    INFO_FIELD("data/info_field.wav"),
    STEP("data/step.wav"),
    COBRA("data/cobra.wav");

    private final String filename;

    /**
     * Construct a new sound object.
     *
     * @param filename the file of the sound
     **/
    Sound(String filename) {
        this.filename = filename;
    }

    /**
     * @return the filename for this sound
     */
    public String getFilename() {
        return filename;
    }
}
