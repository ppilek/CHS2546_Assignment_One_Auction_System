package test;

import entries.NotificationU1264982;
import javaspace.SpaceUtils;
import net.jini.space.JavaSpace05;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationU1264982Test {

    private static NotificationU1264982 notification;

    @BeforeEach
    void setUp() {
        System.out.println("@BeforeAll - Check connection with JavaSpace:");
        JavaSpace05 space = (JavaSpace05) SpaceUtils.getSpace();
        if (space == null) {
            System.err.println("Failed to find the JavaSpace");

        } else {
            System.out.println("Connected to JavaSpace");
            try {
                notification = new NotificationU1264982(1, "userOne", "mobile", "mobile phone good price", 55.99, false, "userTwo");
                space.write(notification, null, 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @DisplayName("test if we can get LotIndex filed")
    @Test
    void getLotSeller() {
        assertEquals(1, notification.getLotIndex());
    }

    @DisplayName("test if we can set LotIndex filed")
    @Test
    void setLotSeller() {
        notification.setLotIndex(23);
        assertEquals(23, notification.getLotIndex());
    }

    @DisplayName("test if we can get LotTitle filed")
    @Test
    void getLotTitle() {
        assertEquals("mouse", notification.getLotTitle());
    }

    @DisplayName("test if we can set LotIndex filed")
    @Test
    void setLotTitle() {
        notification.setLotTitle("monitor");
        assertEquals("monitor", notification.getLotTitle());
    }

    @DisplayName("test if we can get LotDescription filed")
    @Test
    void getLotDescription() {
        assertEquals("new model", notification.getLotDescription());
    }

    @DisplayName("test if we can set LotDescription filed")
    @Test
    void setLotDescription() {
        notification.setLotDescription("23 inch monitor");
        assertEquals("description", notification.getLotDescription());
    }

    @DisplayName("test if we can get LotPrice filed")
    @Test
    void getLotPrice() {
        assertEquals(56.25, notification.getLotPrice());
    }

    @DisplayName("test if we can set LotPrice filed")
    @Test
    void setLotPrice() {
        notification.setLotPrice(65.12);
        assertEquals(32.14, notification.getLotPrice());
    }

    @DisplayName("test if we can get BidAccepted filed")
    @Test
    void getBidAccepted() {
        assertTrue(notification.getBidAccepted());
    }

    @DisplayName("test if we can set BidAccepted filed")
    @Test
    void setBidAccepted() {
        notification.setBidAccepted(false);
        assertFalse(notification.getBidAccepted());
    }

    @DisplayName("test if we can get LotBuyer filed")
    @Test
    void getLotBuyer() {
        assertEquals("userTwo", notification.getLotBuyer());
    }

    @DisplayName("test if we can set LotBuyer filed")
    @Test
    void setLotBuyer() {
        notification.setLotBuyer("userOne");
        assertEquals("userTwo", notification.getLotBuyer());
    }

    @Test
    void testToString() {
        String string = "Notification{" +
                "lotIndex=" + 56 +
                ", lotSeller='" + "lotSeller" + '\'' +
                ", lotTitle='" + "lotTitle" + '\'' +
                ", lotDescription='" + "lotDescription" + '\'' +
                ", lotPrice=" + 56.45 +
                ", isBidAccepted=" + true +
                ", lotBuyer='" + "lotBuyer" + '\'' +
                '}';

        assertEquals(string, notification.toString());
    }
}