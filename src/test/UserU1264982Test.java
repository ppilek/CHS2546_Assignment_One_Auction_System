package test;

import entries.NotificationU1264982;
import entries.UserU1264982;
import javaspace.SpaceUtils;
import net.jini.space.JavaSpace05;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserU1264982Test {

    private static UserU1264982 user;

    @BeforeEach
    void setUp() {
        System.out.println("@BeforeAll - Check connection with JavaSpace:");
        JavaSpace05 space = (JavaSpace05) SpaceUtils.getSpace();
        if (space == null) {
            System.err.println("Failed to find the JavaSpace");

        } else {
            System.out.println("Connected to JavaSpace");
            try {
                user = new UserU1264982(1, "userOne", "password",  false, 0.0);
                space.write(user, null, 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @DisplayName("test if we can get index filed")
    @Test
    void getIndex() {
        assertEquals(101, user.getIndex());
    }

    @DisplayName("test if we can set index filed")
    @Test
    void setIndex() {
        user.setIndex(1);
        assertEquals(1, user.getIndex());
    }

    @DisplayName("test if we can get username filed")
    @Test
    void getUsername() {
        assertEquals("patryk", user.getUsername());
    }

    @DisplayName("test if we can set username filed")
    @Test
    void setUsername() {
        user.setUsername("username");
        assertEquals("username", user.getUsername());
    }

    @DisplayName("test if we can get password filed")
    @Test
    void getPassword() {
        assertEquals("abc123", user.getPassword());
    }

    @DisplayName("test if we can set password filed")
    @Test
    void setPassword() {
        user.setPassword("ulaulala");
        assertEquals("ulaulala", user.getPassword());
    }

    @DisplayName("test if we can get isSignIn filed")
    @Test
    void getIsSingIn() {
        assertEquals(true, user.getSingIn());
    }

    @DisplayName("test if we can set isSignIn filed")
    @Test
    void setIsSingIn() {
        user.setSingIn(false);
        assertEquals(false, user.getSingIn());
    }

    @DisplayName("test if we can get balance filed")
    @Test
    void getBalance() {
        assertEquals(52.14, user.getBalance());
    }

    @DisplayName("test if we can set balance filed")
    @Test
    void setBalance() {
        user.setBalance(100.00);
        assertEquals(100.00, user.getBalance());
    }

    @Test
    void testToString() {
        String string = "User{" +
                "index=" + 1 +
                ", username='" + "userOne" + '\'' +
                ", password='" + "password" + '\'' +
                ", isSingIn=" + false +
                ", balance=" + 0.0 +
                '}';

        assertEquals(string, user.toString());
    }
}