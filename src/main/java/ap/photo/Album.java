package ap.photo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Album {
    private final String albumId;
    private final String userId;
    private String name;
    private final LocalDateTime createdTime;
    private List<String> images;

    public Album(String userId, String name) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User id cannot be empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Album name cannot be empty");
        }
        this.albumId = UUID.randomUUID().toString();
        this.userId = userId;
        this.name = name;
        this.createdTime = LocalDateTime.now();
        this.images = new ArrayList<>();
    }

    public String getAlbumId() {
        return albumId;
    }
    public String getUserId() {
        return userId;
    }
    public String getName() {
        return name;
    }
    public LocalDateTime getCreatedTime() {
        return createdTime;
    }
    public List<String> getImages() {
        return new ArrayList<>(images);
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Album name cannot be empty");
        }
        this.name = name;
    }

    public boolean addImage(String imageId) {
        if (imageId == null || imageId.trim().isEmpty()) {
            return false;
        }
        if (images.contains(imageId)) {
            return false;
        }
        images.add(imageId);
        return true;
    }

    public void removeImage(String imageId) {
        images.remove(imageId);
    }

    public boolean containsImage(String imageId) {
        return images.contains(imageId);
    }

    public int getImageCount() {
        return images.size();
    }

    public void clearImages() {
        images.clear();
    }

    @Override
    public String toString() {
        return "Album{" +
                "albumId='" + albumId + '\'' +
                ", userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", createdTime=" + createdTime +
                ", imageCount=" + images.size() +
                '}';
    }
}