package ap.photo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    private User user;

    @BeforeEach
    void setUp() {
        user = new User( "arshia","Strong404A","arshia@gmail.com");
    }

    @Test
    void testUserCreation() {
        assertNotNull(user.getUserId());
        assertNotNull(user.getCreatedTime());
        assertFalse(user.isBanned());
        assertEquals("arshia", user.getUsername());
        assertEquals("Strong404A", user.getPassword());
        assertEquals("arshia@gmail.com",user.getEmailOrPhone());
        assertEquals(0,user.getNumberOfImages());
    }

    @Test
    void testChangePasswordSuccess() {
        user.changePassword("Strong404A","NewPass123A");
        assertEquals("NewPass123A",user.getPassword());
    }

    @Test
    void testChangePasswordWrongOld() {

        Exception exception = assertThrows(PasswordIncorrectException.class,
            () -> user.changePassword("wrong","NewPass123A"));
        assertEquals("Password is incorrect",exception.getMessage());
        assertEquals("Strong404A",user.getPassword());
    }

    @Test
    void testWeakPasswordTooShort() {
        Exception exception =assertThrows(IllegalArgumentException.class,
                        () -> user.changePassword("Strong404A","Ab1"));
        assertEquals("Password must be at least 8 characters",exception.getMessage());
    }

    @Test
    void testWeakPasswordNoUpperCase() {
        Exception exception =assertThrows(IllegalArgumentException.class,
            () -> user.changePassword("Strong404A","newpass123"));
        assertEquals("Password must contain at least one uppercase letter",exception.getMessage());
    }

    @Test
    void testWeakPasswordNoLowerCase() {
        Exception exception =assertThrows(IllegalArgumentException.class,
                        () -> user.changePassword("Strong404A","NEWPASS123"));
        assertEquals("Password must contain at least one lowercase letter",exception.getMessage());
    }

    @Test
    void testWeakPasswordNoDigit() {
        Exception exception =assertThrows(IllegalArgumentException.class,
                        () -> user.changePassword("Strong404A","NewPassword"));
        assertEquals("Password must contain at least one digit",exception.getMessage());
    }

    @Test
    void testPasswordContainsUsername() {
        Exception exception =assertThrows(IllegalArgumentException.class,
                        () -> user.changePassword("Strong404A","Arshia123A"));
        assertEquals("Password cannot contain username",exception.getMessage());
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
        assertEquals(1,user.getNumberOfImages());
        user.removeImage("img-001");
        assertEquals(0,user.getNumberOfImages());
    }

    @Test
    void testDuplicateImageNotAdded() {
        user.addImage("img-001");
        user.addImage("img-001");
        assertEquals(1,user.getNumberOfImages());
    }

    @Test
    void testInvalidUsernameInConstructor() {

        assertThrows(IllegalArgumentException.class,
                () -> new User("","Strong404A","test@gmail.com"));
    }

    @Test
    void testInvalidEmailOrPhoneInConstructor() {
        assertThrows(IllegalArgumentException.class,
                () -> new User("arshia","Strong404A",""));
    }
}