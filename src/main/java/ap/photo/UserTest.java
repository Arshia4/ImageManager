package ap.photo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    private User user;

    @BeforeEach
    void setUp() {
        user = new User("arshia", "arshia404", "arshia@gmail.com");
    }

    @Test
    void testUserCreation() {
        assertNotNull(user.getUserId());
        assertNotNull(user.getCreatedTime());
        assertFalse(user.isBanned());
        assertEquals("arshia", user.getUsername());
        assertEquals("arshia404", user.getPassword());
        assertEquals("arshia@gmail.com", user.getEmailOrPhone());
        assertEquals(0, user.getNumberOfImages());
    }

    @Test
    void testChangePasswordSuccess() {
        user.changePassword("arshia404", "hossein404");
        assertEquals("hossein404", user.getPassword());
    }

    @Test
    void testChangePasswordWrongOld() {
        Exception exception = assertThrows(PasswordIncorrectException.class,
                () -> user.changePassword("wrong", "newpas"));
        assertEquals("password is incorrect", exception.getMessage());
        assertEquals("arshia404", user.getPassword());
    }

    @Test
    void testWeakPassword() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> user.changePassword("arshia404", "short"));
        assertEquals("New password must be at least 8 characters", exception.getMessage());
        assertEquals("arshia404", user.getPassword());
    }

    @Test
    void testBanAndUnban() {
        user.banUser();
        assertTrue(user.isBanned());
        user.unbanUser();
        assertFalse(user.isBanned());
    }

    @Test
    void testAddAndRemoveImage() {
        user.addImage("img-001");
        assertEquals(1, user.getNumberOfImages());
        user.removeImage("img-001");
        assertEquals(0, user.getNumberOfImages());
    }
}