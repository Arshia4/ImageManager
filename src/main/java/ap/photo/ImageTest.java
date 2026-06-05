package ap.photo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ImageTest{
    private Image image ;
    
    @BeforeEach
    void setUp(){
        image = new Image("userId" , "title" , "caption") ;
    }


    @Test
    void testCreatingImage(){
        assertNotNull(image.getImageId());
        assertEquals("userId",image.getUserId()) ;
        assertEquals("title" , image.getTitle()) ;
        assertEquals("caption" , image.getCaption()) ;
        assertNotNull(image.getUploadDate()) ;
        assertEquals(0 , image.getTags().size()) ;
        assertEquals(0 , image.getAlbumIds().size()) ;
    }

    @Test
    void testSetTitle(){
        image.setTitle("Nature photo") ;
        assertEquals("Nature photo" , image.getTitle()) ;
    }

    @Test
    void testSetCaption(){
        image.setCaption("this photo is a Nature photo") ;
        assertEquals("this photo is a Nature photo" , image.getCaption()) ;
    }

    @Test
    void testAddLike(){
        String user1 = "user1" ;
        String user2 = "user2" ;
        String user3 = "user3" ;

        assertEquals(0 , image.getLikesCount()) ;
        image.addLike(user1) ;
        assertEquals(1 , image.getLikesCount()) ;
        image.addLike(user1) ;
        assertEquals(1 , image.getLikesCount()) ;
        image.addLike(user2) ;
        image.addLike(user3) ;
        assertEquals(3 , image.getLikesCount()) ;
    }

    @Test
    void testRemoveLike(){
        String user1 = "user1" ;
        String user2 = "user2" ;

        image.addLike(user1) ;
        image.addLike(user2) ;
        assertEquals(2 , image.getLikesCount()) ;
        image.removeLike(user1) ;
        assertEquals(1 , image.getLikesCount()) ;
        image.removeLike(user1) ;
        assertEquals(1 , image.getLikesCount()) ;
        image.removeLike(user2) ;
        assertEquals(0 , image.getLikesCount()) ;
        image.removeLike(user2) ;
        assertEquals(0 , image.getLikesCount()) ;
    }

    @Test
    void testAddTag(){
        String tag1 = "tag1" ;
        String tag2 = "tag2" ;

        assertEquals(0 , image.getTags().size()) ;
        image.addTag(tag1) ;        
        assertEquals(1 , image.getTags().size()) ;
        assertEquals(true , image.getTags().contains(tag1)) ;
        image.addTag(tag1) ;
        assertEquals(1 , image.getTags().size()) ;
        image.addTag(tag2) ;
        assertEquals(2 , image.getTags().size()) ;
        assertEquals(true , image.getTags().contains(tag2)) ;
    }

    @Test
    void testRemoveTag(){
        String tag1 = "tag1" ;
        String tag2 = "tag2" ;

        assertEquals(0 , image.getTags().size()) ;
        image.addTag(tag1) ;
        image.addTag(tag2) ;
        assertEquals(2 , image.getTags().size()) ;
        image.removeTag(tag1) ;
        assertEquals(1 , image.getTags().size()) ;
        image.removeTag(tag1) ;
        assertEquals(1 , image.getTags().size()) ;
        image.removeTag(tag2) ;
        assertEquals(0 , image.getTags().size()) ;
        image.removeTag(tag2) ;
        assertEquals(0 , image.getTags().size()) ;
    }

    @Test
    void testAddToAlbum(){
        String album1 = "album1" ;
        String album2 = "album2" ;

        assertEquals(0 , image.getAlbumIds().size()) ;
        image.addToAlbum(album1) ;
        assertEquals(1 , image.getAlbumIds().size()) ;
        image.addToAlbum(album1) ;
        assertEquals(1 , image.getAlbumIds().size()) ;
        image.addToAlbum(album2) ;
        assertEquals(2 , image.getAlbumIds().size()) ;
        assertEquals(true , image.getAlbumIds().contains(album1)) ;
        assertEquals(true , image.getAlbumIds().contains(album2)) ;
    }

    @Test
    void testRemoveFromAlbum(){
        String album1 = "album1" ;
        String album2 = "album2" ;

        assertEquals(0 , image.getAlbumIds().size()) ;
        image.addToAlbum(album1) ;
        image.addToAlbum(album2) ;
        assertEquals(2 , image.getAlbumIds().size()) ;
        image.removeFromAlbum(album1) ;
        assertEquals(1 , image.getAlbumIds().size()) ;
        image.removeFromAlbum(album1) ;
        assertEquals(1 , image.getAlbumIds().size()) ;
        image.removeFromAlbum(album2) ;
        assertEquals(0 , image.getAlbumIds().size()) ;
        image.removeFromAlbum(album2) ;
        assertEquals(0 , image.getAlbumIds().size()) ;
    }
}