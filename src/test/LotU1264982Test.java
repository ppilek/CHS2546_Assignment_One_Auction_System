package test;

import entries.LotU1264982;
import javaspace.SpaceUtils;
import net.jini.space.JavaSpace05;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LotU1264982Test {

    private static LotU1264982 lot;

    @BeforeEach
    void setUp() {
        System.out.println("@BeforeAll - Check connection with JavaSpace:");
        JavaSpace05 space = (JavaSpace05) SpaceUtils.getSpace();
        if (space == null) {
            System.err.println("Failed to find the JavaSpace");

        } else {
            System.out.println("Connected to JavaSpace");
            try {
                lot = new LotU1264982(1, "userOne", "mobile", "mobile phone good price", 55.99, false, 0.0, "sold", "userTwo");
                space.write(lot, null, 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @DisplayName("test if we can get index filed")
    @Test
    void getIndex() {
        assertEquals(1, lot.getIndex());
    }

    @DisplayName("test if we can set index filed")
    @Test
    void setIndex() {
        lot.setIndex(23);
        assertEquals(1, lot.getIndex());
    }

    @DisplayName("test if we can get userSeller filed")
    @Test
    void getUserSeller() {
        assertEquals("userOne", lot.getUserSeller());
    }

    @DisplayName("test if we can set userSeller filed")
    @Test
    void setUserSeller() {
        lot.setUserSeller("userTwo");
        assertEquals("userOnw", lot.getUserSeller());
    }

    @DisplayName("test if we can get title filed")
    @Test
    void getTitle() {
        assertEquals("mobile", lot.getTitle());
    }

    @DisplayName("test if we can set title filed")
    @Test
    void setTitle() {
        lot.setUserSeller("monitor");
        assertEquals("monitor", lot.getTitle());
    }

    @DisplayName("test if we can get description filed")
    @Test
    void getDescription() {
        assertEquals("mobile phone good price", lot.getDescription());
    }

    @DisplayName("test if we can set description filed")
    @Test
    void setDescription() {
        lot.setDescription("good price");
        assertEquals("good price", lot.getDescription());
    }

    @DisplayName("test if we can get original price filed")
    @Test
    void getOriginalPrice() {
        assertEquals(66.66, lot.getOriginalPrice());
    }

    @DisplayName("test if we can set original price filed")
    @Test
    void setOriginalPrice() {
        lot.setOriginalPrice(99.99);
        assertEquals(99.99, lot.getOriginalPrice());
    }

    @DisplayName("test if we can get sold price filed")
    @Test
    void getSoldPrice() {
        assertEquals(12.34, lot.getSoldPrice());
    }

    @DisplayName("test if we can set sold price filed")
    @Test
    void setSoldPrice() {
        lot.setSoldPrice(34.56);
        assertEquals(56.78, lot.getSoldPrice());
    }

    @DisplayName("test if we can get status filed")
    @Test
    void getStatus() {
        assertEquals("sold", lot.getStatus());
    }

    @DisplayName("test if we can set status filed")
    @Test
    void setStatus() {
        lot.setStatus("null");
        assertEquals("null", lot.getStatus());
    }

    @DisplayName("test if we can get userBuyer filed")
    @Test
    void getUserBuyer() {
        assertEquals("userTwo", lot.getUserBuyer());
    }

    @DisplayName("test if we can set userBuyer filed")
    @Test
    void setUserBuyer() {
        lot.setUserBuyer("lukasz");
        assertEquals("lukasz", lot.getUserBuyer());
    }

    @Test
    void testToString() {
        String string = "Lot{" +
                "index=" + 1 +
                ", userSeller='" + "userSeller" + '\'' +
                ", title='" + "title" + '\'' +
                ", description='" + "description" + '\'' +
                ", originalPrice=" + 23.45 +
                ", soldPrice=" + 62.51 +
                ", status='" + "status" + '\'' +
                ", userBuyer='" + "userBuyer" + '\'' +
                '}';

        assertEquals(string, lot.toString());
    }
}