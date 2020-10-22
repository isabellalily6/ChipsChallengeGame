package nz.ac.vuw.ecs.swen225.gp20.commons;

/**
 * Objects of this class represent a sound to play in the game.
 *
 * @author Seth Patel 300488677
 **/
public enum Sound {
    /**
     * Unlock door sound.
     **/
    UNLOCK_DOOR("data/unlock_door.wav"),
    /**
     * Hit by mob sound.
     **/
    HIT_BY_MOB("data/hit_by_mob.wav"),
    /**
     * Pick up item sound.
     **/
    PICK_UP_ITEM("data/pick_up_item.wav"),
    /**
     * Level finished sound.
     **/
    EXIT("data/exit.wav"),
    /**
     * Info field sound.
     **/
    INFO_FIELD("data/info_field.wav"),
    /**
     * Player step sound.
     **/
    STEP("data/step.wav"),
    /**
     * Cobra sound.
     **/
    COBRA("data/cobra.wav"),
    /**
     * Sound for block movement.
     **/
    MOVE_BLOCK("data/move_block.wav");

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
