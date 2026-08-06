package ap.photo;

import java.util.*;
import java.io.IOException;
import java.time.LocalDateTime; 

public class DatabaseManager {
    private Database database;
    private FileManager fileManager;
    
    public DatabaseManager(Database database , FileManager fileManager){
        this.database=database ;
        this.fileManager = fileManager ;
    }

  
    public User registerUser(String username, String password) {
        for (User user : database.getUsers().values()) {
            if (user.getUsername().equals(username)) {
                return null;
            }
        }
        String userId = UUID.randomUUID().toString();
        User user = new User(userId, username, password);
        database.getUsers().put(userId, user);
        return user;
    }

    public User loginUser(String username, String password) {
        for (User user : database.getUsers().values()) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    public Image addImage(String userId, String title, String caption, List<String> tags, byte[] imageData, String fileName) throws IOException {
        User user = database.getUsers().get(userId);
        if (user == null) return null;

        String imageId = UUID.randomUUID().toString();
        String filePath = fileManager.savePhoto(imageData, fileName);
        
        Image image = new Image(imageId, userId, title, caption, LocalDateTime.now().toString(), filePath);
        database.getImages().put(imageId, image);
        
        for (String tagName : tags) {
            addTagToImage(imageId, tagName);
        }
        
        return image;
    }

    public List<Image> getUserImages(String userId) {
        List<Image> result = new ArrayList<>();
        for (Image image : database.getImages().values()) {
            if (image.getUserId().equals(userId)) {
                result.add(image);
            }
        }
        return result;
    }

    public Image getImageById(String imageId) {
        return database.getImages().get(imageId);
    }

    public void addLike(String imageId, String userId) {
        List<String> likes = database.getImageLikes().getOrDefault(imageId, new ArrayList<>());
        if (!likes.contains(userId)) {
            likes.add(userId);
            database.getImageLikes().put(imageId, likes);
        }
    }

    public void removeLike(String imageId, String userId) {
        List<String> likes = database.getImageLikes().get(imageId);
        if (likes != null) {
            likes.remove(userId);
            if (likes.isEmpty()) {
                database.getImageLikes().remove(imageId);
            }
        }
    }

    public int getLikeCount(String imageId) {
        return database.getImageLikes().getOrDefault(imageId, new ArrayList<>()).size();
    }

   
    public boolean isImageLikedByUser(String imageId, String userId) {
        List<String> likes = database.getImageLikes().get(imageId);
        return likes != null && likes.contains(userId);
    }

    public Comment addComment(String imageId, String userId, String text) {
        if (database.getImages().get(imageId) == null) return null;
        
        String commentId = UUID.randomUUID().toString();
        Comment comment = new Comment(commentId, userId, imageId, text, LocalDateTime.now().toString());
        database.getComments().put(commentId, comment);
        return comment;
    }

    public List<Comment> getCommentsForImage(String imageId) {
        List<Comment> result = new ArrayList<>();
        for (Comment comment : database.getComments().values()) {
            if (comment.getImageId().equals(imageId)) {
                result.add(comment);
            }
        }
        return result;
    }

    public void addTagToImage(String imageId, String tagName) {
        String tagId = null;
        for (Tag tag : database.getTags().values()) {
            if (tag.getName().equals(tagName)) {
                tagId = tag.getTagId();
                break;
            }
        }
        if (tagId == null) {
            tagId = UUID.randomUUID().toString();
            Tag newTag = new Tag(tagId, tagName);
            database.getTags().put(tagId, newTag);
        }
        
        List<String> tags = database.getImageTags().getOrDefault(imageId, new ArrayList<>());
        if (!tags.contains(tagId)) {
            tags.add(tagId);
            database.getImageTags().put(imageId, tags);
        }
    }

    
    public List<String> getImageTagIds(String imageId) {
        return database.getImageTags().getOrDefault(imageId, new ArrayList<>());
    }

    
    public Tag getTagById(String tagId) {
        return database.getTags().get(tagId);
    }

   
    public List<String> getImageAlbumIds(String imageId) {
        List<String> result = new ArrayList<>();
        for (Album album : database.getAlbums().values()) {
            if (album.containsImage(imageId)) {
                result.add(album.getAlbumId());
            }
        }
        return result;
    }

    public List<Image> searchImages(String query) {
        List<Image> result = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        for (Image image : database.getImages().values()) {
            if (image.getTitle().toLowerCase().contains(lowerQuery) ||
                image.getCaption().toLowerCase().contains(lowerQuery)) {
                result.add(image);
            }
        }
        return result;
    }

    public boolean deleteImage(String imageId, String userId) {
        Image image = database.getImages().get(imageId);
        if (image == null || !image.getUserId().equals(userId)) {
            return false;
        }
        fileManager.deletePhoto(image.getFilePath());
        database.getImages().remove(imageId);
        database.getImageLikes().remove(imageId);
        database.getImageTags().remove(imageId);
        
        List<String> commentsToRemove = new ArrayList<>();
        for (Comment comment : database.getComments().values()) {
            if (comment.getImageId().equals(imageId)) {
                commentsToRemove.add(comment.getCommentId());
            }
        }
        for (String commentId : commentsToRemove) {
            database.getComments().remove(commentId);
        }
        
        return true;
    }


    public boolean deleteImages(List<String> imageIds, String userId) {
        boolean allDeleted = true;
        for (String imageId : imageIds) {
            if (!deleteImage(imageId, userId)) {
                allDeleted = false;
            }
        }
        return allDeleted;
    }

    public Album createAlbum(String userId, String name) {
        if (database.getUsers().get(userId) == null) return null;
        
        String albumId = UUID.randomUUID().toString();
        Album album = new Album(albumId, name, userId);
        database.getAlbums().put(albumId, album);
        return album;
    }

    public void addImageToAlbum(String imageId, String albumId) {
        Album album = database.getAlbums().get(albumId);
        if (album != null) {
            album.addImage(imageId);
        }
    }

   
    public List<String> getAlbumImageIds(String albumId) {
        Album album = database.getAlbums().get(albumId);
        if (album != null) {
            return album.getImages();
        }
        return new ArrayList<>();
    }

    public List<Image> getAlbumImages(String albumId) {
        List<Image> result = new ArrayList<>();
        Album album = database.getAlbums().get(albumId);
        if (album != null) {
            for (String imageId : album.getImages()) {
                Image image = database.getImages().get(imageId);
                if (image != null) {
                    result.add(image);
                }
            }
        }
        return result;
    }

    public List<Album> getUserAlbums(String userId) {
        List<Album> result = new ArrayList<>();
        for (Album album : database.getAlbums().values()) {
            if (album.getUserId().equals(userId)) {
                result.add(album);
            }
        }
        return result;
    }

    
    public boolean deleteAlbum(String albumId, String userId) {
        Album album = database.getAlbums().get(albumId);
        if (album == null || !album.getUserId().equals(userId)) {
            return false;
        }
        database.getAlbums().remove(albumId);
        return true;
    }

   
    public void removeImageFromAlbum(String imageId, String albumId) {
        Album album = database.getAlbums().get(albumId);
        if (album != null) {
            album.removeImage(imageId);
        }
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(database.getUsers().values());
    }

    public User getUserByUsername(String username) {
        for (User user : database.getUsers().values()) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    } 

    public void banUser(String username) {
        User user = getUserByUsername(username);
        if (user != null) {
            user.setBanned(true);
        }
    }

    public void unbanUser(String username) {
        User user = getUserByUsername(username);
        if (user != null) {
            user.setBanned(false);
        }
    }

    public Map<String, Integer> getUserStats(String username) {
        User user = getUserByUsername(username);
        if (user == null) return null;
        
        Map<String, Integer> stats = new HashMap<>();
        stats.put("imageCount", getUserImages(user.getUserId()).size());
        stats.put("albumCount", getUserAlbums(user.getUserId()).size());
        return stats;
    }

    public void editImageTitle(String imageId, String newTitle) {
        Image image = database.getImages().get(imageId);
        if (image != null) {
            image.setTitle(newTitle);
        }
    }

    public void editImageCaption(String imageId, String newCaption) {
        Image image = database.getImages().get(imageId);
        if (image != null) {
            image.setCaption(newCaption);
        }
    }

    public void editImageTags(String imageId, List<String> newTags) {
        database.getImageTags().remove(imageId);
        
        for (String tagName : newTags) {
            addTagToImage(imageId, tagName);
        }
    }

    public void editComment(String commentId, String newText) {
        Comment comment = database.getComments().get(commentId);
        if (comment != null) {
            comment.setCommentText(newText);
        }
    }
}