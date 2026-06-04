package ap.photo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet ;

public class Image{
    private int imageId ;
    private int userId ;
    private String title ;
    private String caption ;
    private LocalDateTime uploadDate;
    private String filePath ;
    private Set<Integer> likedUserIds;
    private List<String> tags ;
    private List<Integer> albumIds ;

    public Image(int imageId , int userId , String title , String caption , String filePath){
        this.imageId=imageId;
        this.userId=userId ;
        this.title=title ;
        this.caption=caption ;
        this.filePath= filePath ;
        this.likedUserIds= new HashSet<>();
        this.uploadDate = LocalDateTime.now();
        this.tags = new ArrayList<>() ;
        this.albumIds = new ArrayList<>();
    }

    public int getImageId(){
        return imageId ;
    }
    public int getUserId(){
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
    public String getFilePath(){
        return filePath ;
    }
    public List<String> getTags(){
        return tags ;
    }
    public List<Integer> getAlbumIds(){
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

    public void addLike(int userId){
        likedUserIds.add(userId);
    }
    public void removeLike(int userId){
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

    public void addToAlbum(int albumId){
        if(!albumIds.contains(albumId)){
            albumIds.add(albumId) ;
        }        
    }
    public void removeFromAlbum(int albumId){
        albumIds.remove(albumId) ;
    } 
}