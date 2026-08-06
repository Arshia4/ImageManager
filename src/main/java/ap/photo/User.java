package ap.photo;

import java.time.LocalDateTime;

public class User {
    private final String userId;
    private String username;
    private String password;
    private final LocalDateTime createdTime;
    private boolean isBanned;
    public User(String userId , String username, String password) {
        this.userId = userId ;
        this.username = username;
        this.password = password;
        this.createdTime = LocalDateTime.now();
        this.isBanned = false;
    }
    public void setBanned(boolean isBanned) {
        this.isBanned = isBanned;
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
    public LocalDateTime getCreatedTime() { 
        return createdTime; 
    }
    public boolean isBanned() { 
        return isBanned; 
    }

    
    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty())
            throw new IllegalArgumentException("Username cannot be empty");
        this.username = username;
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
}