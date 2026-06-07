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
    private final List<String> images;

    public User(String username, String password, String emailOrPhone) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (emailOrPhone == null || emailOrPhone.isBlank()) {
            throw new IllegalArgumentException("Email or phone cannot be empty");
        }
        validatePassword(password, username);
        this.userId = UUID.randomUUID().toString();
        this.username = username;
        this.password = password;
        this.emailOrPhone = emailOrPhone;
        this.createdTime = LocalDateTime.now();
        this.isBanned = false;
        this.images = new ArrayList<>();
    }

    private void validatePassword(String password, String username) {
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        if (password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
        if (password.toLowerCase().contains(username.toLowerCase())) {
            throw new IllegalArgumentException("Password cannot contain username");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("Password must contain at least one lowercase letter");
        }
        if (!password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Password must contain at least one digit");
        }
    }

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

    public void setUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        this.username = username;
    }

    public void setEmailOrPhone(String emailOrPhone) {
        if (emailOrPhone == null || emailOrPhone.isBlank()) {
            throw new IllegalArgumentException("Email or phone cannot be empty");
        }
        this.emailOrPhone = emailOrPhone;
    }
    public void changePassword(String oldPassword,String newPassword) {
        if (!this.password.equals(oldPassword)) {
            throw new PasswordIncorrectException("Password is incorrect");
        }
        validatePassword(newPassword, username);
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
        if (imageId != null &&!imageId.isBlank() &&!images.contains(imageId)) {
            images.add(imageId);
        }
    }
    public void removeImage(String imageId) {
        images.remove(imageId);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", emailOrPhone='" + emailOrPhone + '\'' +
                ", images=" + images.size() +
                ", banned=" + isBanned +
                '}';
    }
}