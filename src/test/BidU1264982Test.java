package test;

import entries.BidU1264982;
import javaspace.SpaceUtils;
import net.jini.space.JavaSpace05;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BidU1264982Test {

    private static BidU1264982 bid;

    @BeforeEach
    void setUp() {
        System.out.println("@BeforeAll - Check connection with JavaSpace:");
        JavaSpace05 space = (JavaSpace05) SpaceUtils.getSpace();
        if (space == null) {
            System.err.println("Failed to find the JavaSpace");

        } else {
            System.out.println("Connected to JavaSpace");
            try {
                bid = new BidU1264982(1, 10, 23.30, "user");
                space.write(bid, null, 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @DisplayName("test if we can get index filed")
    @Test
    void getIndex() {
        assertEquals(1, bid.getIndex());
    }

    @DisplayName("test if we can set index filed")
    @Test
    void setIndex() {
        bid.setIndex(23);
        assertEquals(1, bid.getIndex());
    }

    @DisplayName("test if we can get lotIndex filed")
    @Test
    void getLotIndex() {
        assertEquals(10, bid.getLotIndex());
    }

    @DisplayName("test if we can set lotIndex filed")
    @Test
    void setLotIndex() {
        bid.setLotIndex(10);
        assertEquals(10, bid.getLotIndex());
    }

    @DisplayName("test if we can get bidValue filed")
    @Test
    void getBidValue() {
        assertEquals(23.30, bid.getBidValue());
    }

    @DisplayName("test if we can set bidValue filed")
    @Test
    void setBidValue() {
        bid.setBidValue(99.99);
        assertEquals(99.99, bid.getBidValue());
    }

    @DisplayName("test if we can get userBuyer filed")
    @Test
    void getUserBuyer() {
        assertEquals("patryk", bid.getUserBuyer());
    }

    @DisplayName("test if we can set userBuyer filed")
    @Test
    void setUserBuyer() {
        bid.setUserBuyer("userOne");
        assertEquals("userOne", bid.getUserBuyer());
    }

    @DisplayName("test if we can get toStrting string.")
    @Test
    void testToString() {
        String string = "Bid{" +
                "index=" + 1 +
                ", lotIndex=" + 10 +
                ", bidValue=" + 23.30 +
                ", userBuyer='" + "userBuyer" + '\'' +
                '}';

        assertEquals(string, bid.toString());
    }
}