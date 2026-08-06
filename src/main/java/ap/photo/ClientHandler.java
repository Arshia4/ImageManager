package ap.photo;

import java.io.*;
import java.net.*;
import java.util.*;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private DatabaseManager dbManager;
    private FileManager fileManager;
    private BufferedReader in;
    private PrintWriter out;

    public ClientHandler(Socket socket, DatabaseManager dbManager, FileManager fileManager) {
        this.clientSocket = socket;
        this.dbManager = dbManager;
        this.fileManager = fileManager;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            String jsonRequest;
            while ((jsonRequest = in.readLine()) != null) {
                System.out.println("📩 دریافت درخواست: " + jsonRequest);
                
                Map<String, Object> request = RequestParser.parse(jsonRequest);
                String type = (String) request.get("type");
                Map<String, Object> data = (Map<String, Object>) request.get("data");

                String response = handleRequest(type, data);
                out.println(response);
                System.out.println("📤 ارسال پاسخ: " + response);
            }
        } catch (Exception e) {
            System.err.println("❌ خطا در ارتباط با کلاینت: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("❌ خطا در بستن سوکت: " + e.getMessage());
            }
        }
    }

    private String handleRequest(String type, Map<String, Object> data) {
        try {
            switch (type) {
                case "REGISTER": return handleRegister(data);
                case "LOGIN": return handleLogin(data);
                case "ADD_IMAGE": return handleAddImage(data);
                case "GET_IMAGES": return handleGetImages(data);
                case "ADD_LIKE": return handleAddLike(data);
                case "REMOVE_LIKE": return handleRemoveLike(data);
                case "ADD_COMMENT": return handleAddComment(data);
                case "GET_COMMENTS": return handleGetComments(data);
                case "SEARCH_IMAGES": return handleSearchImages(data);
                case "DELETE_IMAGE": return handleDeleteImage(data);
                case "DELETE_IMAGES": return handleDeleteImages(data); // 👈 اضافه شد
                case "CREATE_ALBUM": return handleCreateAlbum(data);
                case "ADD_TO_ALBUM": return handleAddToAlbum(data);
                case "GET_ALBUMS": return handleGetAlbums(data);
                case "DELETE_ALBUM": return handleDeleteAlbum(data); // 👈 اضافه شد
                case "REMOVE_FROM_ALBUM": return handleRemoveFromAlbum(data); // 👈 اضافه شد
                case "EDIT_IMAGE_TITLE": return handleEditImageTitle(data); // 👈 اضافه شد
                case "EDIT_IMAGE_CAPTION": return handleEditImageCaption(data); // 👈 اضافه شد
                case "EDIT_IMAGE_TAGS": return handleEditImageTags(data); // 👈 اضافه شد
                case "GET_ALL_USERS": return handleGetAllUsers(data);
                case "GET_USER_STATS": return handleGetUserStats(data);
                case "BAN_USER": return handleBanUser(data);
                case "UNBAN_USER": return handleUnbanUser(data);
                default: return ResponseBuilder.error("نوع درخواست نامعتبر: " + type);
            }
        } catch (Exception e) {
            return ResponseBuilder.error("خطا در پردازش: " + e.getMessage());
        }
    }

    // ========== هندلرهای ادمین ==========
    private String handleGetAllUsers(Map<String, Object> data) {
        List<User> users = dbManager.getAllUsers();
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (User user : users) {
            Map<String, Object> u = new HashMap<>();
            u.put("userId", user.getUserId());
            u.put("username", user.getUsername());
            u.put("isBanned", user.isBanned());
            u.put("imageCount", dbManager.getUserImages(user.getUserId()).size());
            u.put("albumCount", dbManager.getUserAlbums(user.getUserId()).size());
            result.add(u);
        }
        
        return ResponseBuilder.success("لیست کاربران", result);
    }

    private String handleGetUserStats(Map<String, Object> data) {
        String username = (String) data.get("username");
        Map<String, Integer> stats = dbManager.getUserStats(username);
        
        if (stats == null) {
            return ResponseBuilder.error("کاربر یافت نشد");
        }
        
        return ResponseBuilder.success("آمار کاربر", stats);
    }

    private String handleBanUser(Map<String, Object> data) {
        String username = (String) data.get("username");
        dbManager.banUser(username);
        return ResponseBuilder.success("کاربر با موفقیت مسدود شد", null);
    }

    private String handleUnbanUser(Map<String, Object> data) {
        String username = (String) data.get("username");
        dbManager.unbanUser(username);
        return ResponseBuilder.success("مسدودیت کاربر برداشته شد", null);
    }

    // ========== احراز هویت ==========
    private String handleRegister(Map<String, Object> data) {
        String username = (String) data.get("username");
        String password = (String) data.get("password");
        
        if (username == null || password == null) {
            return ResponseBuilder.error("نام کاربری و رمز عبور الزامی است");
        }
        
        User user = dbManager.registerUser(username, password);
        if (user == null) {
            return ResponseBuilder.error("نام کاربری تکراری است");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("userId", user.getUserId());
        result.put("username", user.getUsername());
        return ResponseBuilder.success("ثبت‌نام موفق", result);
    }

    private String handleLogin(Map<String, Object> data) {
        String username = (String) data.get("username");
        String password = (String) data.get("password");
        
        User user = dbManager.loginUser(username, password);
        if (user == null) {
            return ResponseBuilder.error("نام کاربری یا رمز عبور اشتباه است");
        }
        
        // 👈 چک کردن وضعیت Ban
        if (user.isBanned()) {
            return ResponseBuilder.error("حساب کاربری شما مسدود شده است");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("userId", user.getUserId());
        result.put("username", user.getUsername());
        result.put("imageCount", dbManager.getUserImages(user.getUserId()).size());
        result.put("albumCount", dbManager.getUserAlbums(user.getUserId()).size());
        return ResponseBuilder.success("ورود موفق", result);
    }

    // ========== مدیریت عکس‌ها ==========
    private String handleAddImage(Map<String, Object> data) {
        String userId = (String) data.get("userId");
        String title = (String) data.get("title");
        String caption = (String) data.get("caption");
        List<String> tags = (List<String>) data.get("tags");
        String imageDataBase64 = (String) data.get("imageData");
        
        if (imageDataBase64 == null || imageDataBase64.isEmpty()) {
            return ResponseBuilder.error("داده‌های عکس ارسال نشده است");
        }
        
        try {
            byte[] imageData = Base64.getDecoder().decode(imageDataBase64);
            String fileName = userId + "_" + System.currentTimeMillis() + ".jpg";
            
            Image image = dbManager.addImage(userId, title, caption, tags, imageData, fileName);
            if (image == null) {
                return ResponseBuilder.error("کاربر یافت نشد");
            }
            
            // 👈 ذخیره Base64 در آبجکت
            image.setImageData(imageDataBase64);
            
            Map<String, Object> result = new HashMap<>();
            result.put("imageId", image.getImageId());
            result.put("title", image.getTitle());
            return ResponseBuilder.success("عکس با موفقیت آپلود شد", result);
        } catch (Exception e) {
            return ResponseBuilder.error("خطا در ذخیره عکس: " + e.getMessage());
        }
    }

    private String handleGetImages(Map<String, Object> data) {
        String userId = (String) data.get("userId");
        
        List<Image> images = dbManager.getUserImages(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Image image : images) {
            Map<String, Object> img = new HashMap<>();
            img.put("imageId", image.getImageId());
            img.put("userId", image.getUserId());
            img.put("title", image.getTitle());
            img.put("caption", image.getCaption());
            img.put("uploadDate", image.getUploadDate());
            img.put("likeCount", dbManager.getLikeCount(image.getImageId()));
            img.put("imageData", image.getImageData()); // 👈 اضافه شد
            
            // 👈 اضافه کردن تگ‌ها
            List<String> tagNames = new ArrayList<>();
            for (String tagId : dbManager.getImageTagIds(image.getImageId())) {
                Tag tag = dbManager.getTagById(tagId);
                if (tag != null) {
                    tagNames.add(tag.getName());
                }
            }
            img.put("tags", tagNames);
            
            // 👈 اضافه کردن آلبوم‌ها
            List<String> albumIds = dbManager.getImageAlbumIds(image.getImageId());
            img.put("albumIds", albumIds);
            
            // 👈 چک کردن لایک کاربر فعلی
            boolean isLiked = dbManager.isImageLikedByUser(image.getImageId(), userId);
            img.put("isLiked", isLiked);
            
            result.add(img);
        }
        
        return ResponseBuilder.success("دریافت عکس‌ها موفق", result);
    }

    private String handleAddLike(Map<String, Object> data) {
        String imageId = (String) data.get("imageId");
        String userId = (String) data.get("userId");
        
        dbManager.addLike(imageId, userId);
        return ResponseBuilder.success("لایک اضافه شد", null);
    }

    private String handleRemoveLike(Map<String, Object> data) {
        String imageId = (String) data.get("imageId");
        String userId = (String) data.get("userId");
        
        dbManager.removeLike(imageId, userId);
        return ResponseBuilder.success("لایک برداشته شد", null);
    }

    // ========== مدیریت کامنت‌ها ==========
    private String handleAddComment(Map<String, Object> data) {
        String imageId = (String) data.get("imageId");
        String userId = (String) data.get("userId");
        String text = (String) data.get("text");
        
        Comment comment = dbManager.addComment(imageId, userId, text);
        if (comment == null) {
            return ResponseBuilder.error("عکس یافت نشد");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("commentId", comment.getCommentId());
        result.put("text", comment.getCommentText());
        result.put("sendTime", comment.getSendTime());
        return ResponseBuilder.success("کامنت اضافه شد", result);
    }

    private String handleGetComments(Map<String, Object> data) {
        String imageId = (String) data.get("imageId");
        
        List<Comment> comments = dbManager.getCommentsForImage(imageId);
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Comment comment : comments) {
            Map<String, Object> c = new HashMap<>();
            c.put("commentId", comment.getCommentId());
            c.put("userId", comment.getUserId());
            c.put("text", comment.getCommentText());
            c.put("sendTime", comment.getSendTime());
            result.add(c);
        }
        
        return ResponseBuilder.success("دریافت کامنت‌ها موفق", result);
    }

    private String handleSearchImages(Map<String, Object> data) {
        String query = (String) data.get("query");
        
        List<Image> images = dbManager.searchImages(query);
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Image image : images) {
            Map<String, Object> img = new HashMap<>();
            img.put("imageId", image.getImageId());
            img.put("title", image.getTitle());
            img.put("caption", image.getCaption());
            img.put("uploadDate", image.getUploadDate());
            img.put("likeCount", dbManager.getLikeCount(image.getImageId()));
            img.put("imageData", image.getImageData());
            
            List<String> tagNames = new ArrayList<>();
            for (String tagId : dbManager.getImageTagIds(image.getImageId())) {
                Tag tag = dbManager.getTagById(tagId);
                if (tag != null) {
                    tagNames.add(tag.getName());
                }
            }
            img.put("tags", tagNames);
            
            result.add(img);
        }
        
        return ResponseBuilder.success("نتیجه جستجو", result);
    }

    private String handleDeleteImage(Map<String, Object> data) {
        String imageId = (String) data.get("imageId");
        String userId = (String) data.get("userId");
        
        boolean success = dbManager.deleteImage(imageId, userId);
        if (!success) {
            return ResponseBuilder.error("حذف عکس ناموفق");
        }
        
        return ResponseBuilder.success("عکس با موفقیت حذف شد", null);
    }

    // 👈 اضافه شد: حذف چند عکس
    private String handleDeleteImages(Map<String, Object> data) {
        List<String> imageIds = (List<String>) data.get("imageIds");
        String userId = (String) data.get("userId");
        
        boolean success = dbManager.deleteImages(imageIds, userId);
        if (!success) {
            return ResponseBuilder.error("حذف عکس‌ها ناموفق");
        }
        
        return ResponseBuilder.success("عکس‌ها با موفقیت حذف شدند", null);
    }

    // ========== مدیریت آلبوم‌ها ==========
    private String handleCreateAlbum(Map<String, Object> data) {
        String userId = (String) data.get("userId");
        String name = (String) data.get("name");
        
        Album album = dbManager.createAlbum(userId, name);
        if (album == null) {
            return ResponseBuilder.error("کاربر یافت نشد");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("albumId", album.getAlbumId());
        result.put("name", album.getName());
        return ResponseBuilder.success("آلبوم ساخته شد", result);
    }

    private String handleAddToAlbum(Map<String, Object> data) {
        String imageId = (String) data.get("imageId");
        String albumId = (String) data.get("albumId");
        
        dbManager.addImageToAlbum(imageId, albumId);
        return ResponseBuilder.success("عکس به آلبوم اضافه شد", null);
    }

    private String handleGetAlbums(Map<String, Object> data) {
        String userId = (String) data.get("userId");
        
        List<Album> albums = dbManager.getUserAlbums(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Album album : albums) {
            Map<String, Object> a = new HashMap<>();
            a.put("albumId", album.getAlbumId());
            a.put("name", album.getName());
            a.put("userId", album.getUserId());
            
            // 👈 برگردوندن لیست imageIds به جای count
            List<String> imageIds = dbManager.getAlbumImageIds(album.getAlbumId());
            a.put("imageIds", imageIds);
            a.put("imageCount", imageIds.size());
            
            result.add(a);
        }
        
        return ResponseBuilder.success("دریافت آلبوم‌ها موفق", result);
    }

    // 👈 اضافه شد: حذف آلبوم
    private String handleDeleteAlbum(Map<String, Object> data) {
        String albumId = (String) data.get("albumId");
        String userId = (String) data.get("userId");
        
        boolean success = dbManager.deleteAlbum(albumId, userId);
        if (!success) {
            return ResponseBuilder.error("حذف آلبوم ناموفق");
        }
        
        return ResponseBuilder.success("آلبوم با موفقیت حذف شد", null);
    }

    // 👈 اضافه شد: خروج عکس از آلبوم
    private String handleRemoveFromAlbum(Map<String, Object> data) {
        String imageId = (String) data.get("imageId");
        String albumId = (String) data.get("albumId");
        
        dbManager.removeImageFromAlbum(imageId, albumId);
        return ResponseBuilder.success("عکس از آلبوم خارج شد", null);
    }

    // 👈 اضافه شد: ویرایش عنوان
    private String handleEditImageTitle(Map<String, Object> data) {
        String imageId = (String) data.get("imageId");
        String newTitle = (String) data.get("newTitle");
        
        dbManager.editImageTitle(imageId, newTitle);
        return ResponseBuilder.success("عنوان عکس ویرایش شد", null);
    }

    // 👈 اضافه شد: ویرایش کپشن
    private String handleEditImageCaption(Map<String, Object> data) {
        String imageId = (String) data.get("imageId");
        String newCaption = (String) data.get("newCaption");
        
        dbManager.editImageCaption(imageId, newCaption);
        return ResponseBuilder.success("کپشن عکس ویرایش شد", null);
    }

    // 👈 اضافه شد: ویرایش تگ‌ها
    private String handleEditImageTags(Map<String, Object> data) {
        String imageId = (String) data.get("imageId");
        List<String> newTags = (List<String>) data.get("newTags");
        
        dbManager.editImageTags(imageId, newTags);
        return ResponseBuilder.success("تگ‌های عکس ویرایش شدند", null);
    }
}