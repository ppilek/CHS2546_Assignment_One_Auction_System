package test;

import controllers.UserController;
import entries.UserU1264982;
import javaspace.SpaceUtils;
import net.jini.space.JavaSpace05;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private static JavaSpace05 space;
    private static UserController userController;
    private static UserU1264982 user;

    @BeforeEach
    void setUp() {
        System.out.println("@BeforeAll - Check connection with JavaSpace:");
        space = (JavaSpace05) SpaceUtils.getSpace();
        if (space == null) {
            System.err.println("Failed to find the JavaSpace");

        } else {
            System.out.println("Connected to JavaSpace");
            System.out.println("Connected to JavaSpace");
            try {
                user = new UserU1264982(1, "userOne", "password",  false, 0.0);
                space.write(user, null, 2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        userController  = new UserController();
    }

    @Test
    void addSoldLotPriceToAccount() {
        userController.addSoldLotPriceToAccount("userOne", 23.45);
        assertEquals(23.45, user.getBalance());
    }

    @Test
    void getSpace() {
        JavaSpace05 testSpace = (JavaSpace05) SpaceUtils.getSpace();

        assertEquals(testSpace, space);
    }

    @Test
    void singUpToSpace() {
        assertEquals("Success", userController.singUpToSpace("userOne", "password"));
    }

    @Test
    void singInToSpace() {
        assertEquals("Success", userController.singInToSpace("userOne", "password"));
    }

    @Test
    void singOutToSpace() {
        assertEquals("Success", userController.singOutToSpace("userTwo"));
    }
}