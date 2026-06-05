package ap.photo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AlbumTest {
    private Album album;

    @BeforeEach
    void setUp() {
        album = new Album("arshia123", "sbu pictures");
    }

    @Test
    void testAlbumCreation() {
        assertNotNull(album.getAlbumId());
        assertEquals("arshia123", album.getUserId());
        assertEquals("sbu pictures", album.getName());
        assertNotNull(album.getCreatedTime());
        assertEquals(0, album.getImageCount());
        assertTrue(album.getImages().isEmpty());
    }

    @Test
    void testSetNameValid() {
        album.setName("New Album");
        assertEquals("New Album", album.getName());
    }

    @Test
    void testSetNameInvalid() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> album.setName(""));
        assertEquals("Album name cannot be empty", exception.getMessage());
    }

    @Test
    void testAddImage() {
        assertTrue(album.addImage("img1"));
        assertEquals(1, album.getImageCount());
        assertTrue(album.containsImage("img1"));
    }

    @Test
    void testAddDuplicateImage() {
        album.addImage("img1");
        assertFalse(album.addImage("img1"));
        assertEquals(1, album.getImageCount());
    }

    @Test
    void testRemoveImage() {
        album.addImage("img1");
        album.addImage("img2");
        assertEquals(2, album.getImageCount());
        
        album.removeImage("img1");
        assertEquals(1, album.getImageCount());
        assertFalse(album.containsImage("img1"));
        assertTrue(album.containsImage("img2"));
    }
}