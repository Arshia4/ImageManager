package ap.photo;

import java.io.*;
import java.util.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Database {
    private Map<String, User> users;
    private Map<String, Image> images;
    private Map<String, Album> albums;
    private Map<String, Comment> comments;
    private Map<String, Tag> tags;
    private Map<String, List<String>> albumImages; 
    private Map<String, List<String>> imageTags; 
    private Map<String, List<String>> imageLikes; 

    public Database() {
        users = new HashMap<>();
        images = new HashMap<>();
        albums = new HashMap<>();
        comments = new HashMap<>();
        tags = new HashMap<>();
        albumImages = new HashMap<>();
        imageTags = new HashMap<>();
        imageLikes = new HashMap<>();
    }
    
    public Map<String, User> getUsers() { return users; }
    public Map<String, Image> getImages() { return images; }
    public Map<String, Album> getAlbums() { return albums; }
    public Map<String, Comment> getComments() { return comments; }
    public Map<String, Tag> getTags() { return tags; }
    public Map<String, List<String>> getAlbumImages() { return albumImages; }
    public Map<String, List<String>> getImageTags() { return imageTags; }
    public Map<String, List<String>> getImageLikes() { return imageLikes; }
    
    public void saveToFile(String filePath) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Map<String, Object> data = new HashMap<>();
        
        data.put("users", users);
        data.put("images", images);
        data.put("albums", albums);
        data.put("comments", comments);
        data.put("tags", tags);
        data.put("albumImages", albumImages);
        data.put("imageTags", imageTags);
        data.put("imageLikes", imageLikes);

        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(data, writer);
        }
    }

    @SuppressWarnings("unchecked")
    public void loadFromFile(String filePath) throws IOException {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filePath)) {
            Map<String, Object> data = gson.fromJson(reader, Map.class);
            
            users = (Map<String, User>) data.getOrDefault("users", new HashMap<>());
            images = (Map<String, Image>) data.getOrDefault("images", new HashMap<>());
            albums = (Map<String, Album>) data.getOrDefault("albums", new HashMap<>());
            comments = (Map<String, Comment>) data.getOrDefault("comments", new HashMap<>());
            tags = (Map<String, Tag>) data.getOrDefault("tags", new HashMap<>());
            albumImages = (Map<String, List<String>>) data.getOrDefault("albumImages", new HashMap<>());
            imageTags = (Map<String, List<String>>) data.getOrDefault("imageTags", new HashMap<>());
            imageLikes = (Map<String, List<String>>) data.getOrDefault("imageLikes", new HashMap<>());
        }
    }
}