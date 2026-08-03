package ap.photo;

import java.io.*;
import java.nio.file.*;

public class FileManager {
     private static final String STORAGE_PATH = "C:/uploads/";

      public FileManager() {
        try {
            Files.createDirectories(Paths.get(STORAGE_PATH));
        } catch (IOException e) {
            System.err.println(" خطا در ایجاد پوشه ذخیره‌سازی " + e.getMessage());
        }
    }

    public String savePhoto(byte[] imageData, String fileName) throws IOException {
        Path path = Paths.get(STORAGE_PATH + fileName);
        Files.write(path, imageData);
        return path.toString();
    }
    
    public byte[] getPhoto(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readAllBytes(path);
    }


    public boolean deletePhoto(String filePath) {
        try {
            return Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException e) {
            System.err.println(" خطا در حذف فایل " + e.getMessage());
            return false;
        }
    }

    public boolean photoExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }

}
