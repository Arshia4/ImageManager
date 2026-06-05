package ap.photo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.HashSet ;

public class Image{
    private String imageId ;
    private String userId ;
    private String title ;
    private String caption ;
    private LocalDateTime uploadDate;
    private Set<String> likedUserIds;
    private List<String> tags ;
    private List<String> albumIds ;

    public Image(String userId ,String title , String caption){
        this.imageId= UUID.randomUUID().toString();
        this.userId= userId;
        this.title=title ;
        this.caption=caption ;
        this.likedUserIds= new HashSet<>();
        this.uploadDate = LocalDateTime.now();
        this.tags = new ArrayList<>() ;
        this.albumIds = new ArrayList<>();
    }

    public String getImageId(){
        return imageId ;
    }
    public String getUserId(){
        return userId ;
    }
    public String getTitle(){
        return title ;
    }
    public String getCaption(){
        return caption;
    }
    public LocalDateTime getUploadDate(){
        return uploadDate ;
    }
    public List<String> getTags(){
        return tags ;
    }
    public List<String> getAlbumIds(){
        return albumIds ;
    }
    public int getLikesCount(){
        return likedUserIds.size();
    }

    public void setTitle(String title){
        this.title = title ;
    }
    public void setCaption(String caption){
        this.caption=caption ;
    }

    public void addLike(String userId){
        likedUserIds.add(userId);
    }
    public void removeLike(String userId){
        likedUserIds.remove(userId);
    }

    public void addTag(String tag){
        if (!tags.contains(tag)) {
            tags.add(tag) ;
        }
    }
    public void removeTag(String tag){
        tags.remove(tag) ;
    }

    public void addToAlbum(String albumId){
        if(!albumIds.contains(albumId)){
            albumIds.add(albumId) ;
        }        
    }
    public void removeFromAlbum(String albumId){
        albumIds.remove(albumId) ;
    } 
}