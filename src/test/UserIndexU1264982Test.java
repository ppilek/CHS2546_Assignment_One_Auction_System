package test;

import entries.UserIndexU1264982;
import javaspace.SpaceUtils;
import net.jini.space.JavaSpace05;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserIndexU1264982Test {

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
            UserIndexU1264982 userIndex = new UserIndexU1264982(1);
            space.write(userIndex, null, 1000);

            assertEquals(2, userIndex.getIndex());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @DisplayName("test if we can set index field")
    @Test
    void setIndex() {
        try {
            UserIndexU1264982 userIndex = new UserIndexU1264982();
            space.write(userIndex, null, 1000);
            userIndex.setIndex(3);

            assertEquals(3, userIndex.getIndex());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @DisplayName("test if we can increment by one index field")
    @Test
    void testIncrement() {
        try {
            UserIndexU1264982 userIndex = new UserIndexU1264982();
            space.write(userIndex, null, 1000);
            userIndex.setIndex(5);
            userIndex.increment();

            assertEquals(6, userIndex.getIndex());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @DisplayName("test if we can get toStrting string.")
    @Test
    void testToString() {

        try {
            UserIndexU1264982 userIndex = new UserIndexU1264982();
            space.write(userIndex, null, 1000);
            userIndex.setIndex(5);

            String string = "UserIndex{" + "index=" + 5 + '}';
            assertEquals(string, userIndex.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}