package test;

import entries.BidIndexU1264982;
import javaspace.SpaceUtils;
import net.jini.space.JavaSpace05;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class BidIndexU1264982Test {

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

    @DisplayName("test if we can get index filed")
    @Test
    void testGetIndex() {
        try {
            BidIndexU1264982 bidIndex = new BidIndexU1264982(1);
            space.write(bidIndex, null, 1000);

            assertEquals(1, bidIndex.getIndex());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @DisplayName("test if we can set index field")
    @Test
    void testSetIndex() {
        try {
            BidIndexU1264982 bidIndex = new BidIndexU1264982();
            space.write(bidIndex, null, 1000);
            bidIndex.setIndex(5);

            assertEquals(5, bidIndex.getIndex());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @DisplayName("test if we can increment by one index field")
    @Test
    void testIncrement() {
        try {
            BidIndexU1264982 bidIndex = new BidIndexU1264982();
            space.write(bidIndex, null, 1000);
            bidIndex.setIndex(5);
            bidIndex.increment();

            assertEquals(6, bidIndex.getIndex());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @DisplayName("test if we can get toStrting string.")
    @Test
    void testToString() {

        try {
            BidIndexU1264982 bidIndex = new BidIndexU1264982();
            space.write(bidIndex, null, 1000);
            bidIndex.setIndex(5);

            String string = "BidIndex{" + "index=" + 6 + '}';
            assertEquals(string, bidIndex.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}