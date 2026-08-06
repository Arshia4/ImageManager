package ap.photo;

import java.time.LocalDateTime;
import java.util.UUID;

public class Comment{
    private String commentId ;
    private String userId ;
    private String imageId ;
    private String commentText ;
    private String  sendTime ;

    public Comment(String commentId ,String userId , String imageId , String commentText, String sendTime){
        this.userId = userId ;
        this.imageId= imageId ;
        this.commentText=commentText;
        this.commentId = commentId ;
        this.sendTime = sendTime ;
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
    public String getSendTime(){
        return sendTime ;
    }

    public void setCommentText(String commentText){
        this.commentText=commentText ;
    }
    
}