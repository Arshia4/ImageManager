package ap.photo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CommentTest{
    private Comment comment ;

    @BeforeEach
    void setUp(){
        comment = new Comment("userId" , "imageId" , "its a good photo") ;
    }

    @Test
    void testCreatingComment(){
        assertNotNull(comment.getCommentId()) ;
        assertEquals("userId" , comment.getUserId()) ;
        assertEquals("imageId" , comment.getImageId()) ;
        assertEquals("its a good photo" , comment.getCommentText()) ;
        assertNotNull(comment.getSendTime()) ;
    }

    @Test
    void testSetCommentText(){
        assertEquals("its a good photo" , comment.getCommentText()) ;
        comment.setCommentText("its a good photo i like it") ;
        assertEquals("its a good photo i like it" , comment.getCommentText()) ;
    }
}