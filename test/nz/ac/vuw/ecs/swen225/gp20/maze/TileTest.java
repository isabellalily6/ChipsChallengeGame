package nz.ac.vuw.ecs.swen225.gp20.maze;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TileTest {

    @Test
    public void tileEqualsWorksForSameCoord() {
        assertEquals(new Free(0, 0), new Free(0, 0));
    }

    @Test
    public void tileEqualsWorksForNull() {
        assertNotEquals(new Free(0, 0), null);
    }

    @Test
    public void tileEqualsWorksForDifferentType() {
        assertNotEquals(new Free(0, 0), "wrong class");
    }

    @Test
    public void tileEqualsWorksForDiffCol() {
        assertNotEquals(new Free(0, 0), new Free(1, 0));
    }

    @Test
    public void tileEqualsWorksForDiffRow() {
        assertNotEquals(new Free(0, 0), new Free(0, 1));
    }

}