package ap.photo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User {
    private final String userId;
    private String username;
    private String password;
    private String emailOrPhone;
    private final LocalDateTime createdTime;
    private boolean isBanned;
    private List<String> images;

    public User(String username, String password, String emailOrPhone) {
        this.userId = UUID.randomUUID().toString();
        this.username = username;
        this.password = password;
        this.emailOrPhone = emailOrPhone;
        this.createdTime = LocalDateTime.now();
        this.isBanned = false;
        this.images = new ArrayList<>();
    }

    // Getters
    public String getUserId() { 
        return userId;
    }
    public String getUsername() { 
        return username;
    }
    public String getPassword() { 
        return password;
    }
    public String getEmailOrPhone() { 
        return emailOrPhone; 
    }
    public LocalDateTime getCreatedTime() { 
        return createdTime; 
    }
    public boolean isBanned() { 
        return isBanned; 
    }
    public List<String> getImages() { 
        return new ArrayList<>(images);
    }

    // Setters
    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty())
            throw new IllegalArgumentException("Username cannot be empty");
        this.username = username;
    }

    public void setEmailOrPhone(String emailOrPhone) {
        if (emailOrPhone == null || emailOrPhone.trim().isEmpty())
            throw new IllegalArgumentException("Email/phone cannot be empty");
        this.emailOrPhone = emailOrPhone;
    }

    public void changePassword(String oldPassword, String newPassword) {
    if (!this.password.equals(oldPassword)) {
        throw new PasswordIncorrectException("password is incorrect");
    }
    if (newPassword == null || newPassword.length() < 8) {
        throw new IllegalArgumentException("New password must be at least 8 characters");
    }
    this.password = newPassword;
}

    public void banUser() { 
        this.isBanned = true;
    }
    public void unbanUser() { 
        this.isBanned = false;
    }

    public int getNumberOfImages() { 
        return images.size();
    }

    public void addImage(String imageId) {
        if (imageId != null && !images.contains(imageId))
            images.add(imageId);
    }

    public void removeImage(String imageId) {
        images.remove(imageId);
    }
}