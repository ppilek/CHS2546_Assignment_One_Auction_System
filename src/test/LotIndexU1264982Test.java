package test;

import entries.LotIndexU1264982;
import javaspace.SpaceUtils;
import net.jini.space.JavaSpace05;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LotIndexU1264982Test {

    private static JavaSpace05 space;

    @BeforeEach
    void setUp() {
        System.out.println("@BeforeAll - Check connection with JavaSpace:");
        space = (JavaSpace05) SpaceUtils.getSpace();
        if (space == null) {
            System.err.println("Failed to find the JavaSpace");

        } else {
            System.out.println("Connected to JavaSpace");
        }
    }

    @DisplayName("test if we can set index field")
    @Test
    void getIndex() {
        try {
            LotIndexU1264982 lotIndex = new LotIndexU1264982(1);
            space.write(lotIndex, null, 1000);

            assertEquals(1, lotIndex.getIndex());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @DisplayName("test if we can set index field")
    @Test
    void setIndex() {
        try {
            LotIndexU1264982 lotIndex = new LotIndexU1264982();
            space.write(lotIndex, null, 1000);
            lotIndex.setIndex(3);

            assertEquals(10, lotIndex.getIndex());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @DisplayName("test if we can increment by one index field")
    @Test
    void testIncrement() {
        try {
            LotIndexU1264982 lotIndex = new LotIndexU1264982();
            space.write(lotIndex, null, 1000);
            lotIndex.setIndex(5);
            lotIndex.increment();

            assertEquals(6, lotIndex.getIndex());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @DisplayName("test if we can get toStrting string.")
    @Test
    void testToString() {

        try {
            LotIndexU1264982 lotIndex = new LotIndexU1264982();
            space.write(lotIndex, null, 1000);
            lotIndex.setIndex(5);

            String string = "Lot{" + "index=" + 5 + '}';
            assertEquals(string, lotIndex.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}