package ap.photo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TagTest {
    private Tag tag ;

    @BeforeEach
    void setUp(){
        tag = new Tag("nature");
    }
    
    @Test
    void testCreatingTag(){
        assertNotNull(tag.getTagId());
        assertEquals("nature", tag.getName());
    }

    @Test
    void testSetName(){
        tag.setName("home");
        assertEquals("home" , tag.getName()) ;
    }

}
