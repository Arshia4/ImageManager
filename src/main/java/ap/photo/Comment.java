package ap.photo;

import java.time.LocalDateTime;
import java.util.UUID;

public class Comment{
    private String commentId ;
    private String userId ;
    private String imageId ;
    private String commentText ;
    private LocalDateTime sendTime ;

    public Comment(String userId , String imageId , String commentText){
        this.userId = userId ;
        this.imageId= imageId ;
        this.commentText=commentText;
        this.commentId = UUID.randomUUID().toString();
        this.sendTime = LocalDateTime.now();
    }

    public String getCommentId(){
        return commentId ;
    }
    public String getUserId(){
        return userId ;
    }
    public String getImageId(){
        return imageId ;
    }
    public String getCommentText(){
        return commentText ;
    }
    public LocalDateTime getSendTime(){
        return sendTime ;
    }

    public void setCommentText(String commentText){
        this.commentText=commentText ;
    }
    
}