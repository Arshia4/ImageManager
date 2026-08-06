package ap.photo;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class AdminPanel {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 12345;
    private static final Scanner scanner = new Scanner(System.in);

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";

    public static void main(String[] args) {
        System.out.println("=== پنل مدیریت ادمین ===");

        if (!adminLogin()) {
            return;
        }

        while (true) {
            printMenu();
            System.out.print("انتخاب: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> listUsers();
                case "2" -> getUserStats();
                case "3" -> toggleBanStatus(true);
                case "4" -> toggleBanStatus(false);
                case "0" -> {
                    System.out.println(" خروج.");
                    return;
                }
                default -> System.out.println(" گزینه نامعتبر!");
            }
        }
    }

    private static boolean adminLogin() {
        System.out.print("👤 نام کاربری: ");
        String username = scanner.nextLine();
        System.out.print(" رمز عبور: ");
        String password = scanner.nextLine();

        if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
            System.out.println(" ورود موفق!");
            return true;
        } else {
            System.out.println(" نام کاربری یا رمز اشتباه است!");
            return false;
        }
    }

    private static void printMenu() {
        System.out.println("\n منوی مدیریت:");
        System.out.println("1  لیست کاربران");
        System.out.println("2  آمار کاربر");
        System.out.println("3  مسدودسازی کاربر (Ban)");
        System.out.println("4  رفع مسدودسازی (Unban)");
        System.out.println("0  خروج");
    }

    private static void listUsers() {
        String response = sendRequest("{\"type\":\"GET_ALL_USERS\",\"data\":{}}");
        System.out.println("\n لیست کاربران:\n" + response);
    }

    private static void getUserStats() {
        System.out.print(" نام کاربری: ");
        String username = scanner.nextLine();
        String response = sendRequest(String.format("{\"type\":\"GET_USER_STATS\",\"data\":{\"username\":\"%s\"}}", username));
        System.out.println("\n آمار کاربر:\n" + response);
    }

    private static void toggleBanStatus(boolean ban) {
        System.out.print(" نام کاربری: ");
        String username = scanner.nextLine();
        String type = ban ? "BAN_USER" : "UNBAN_USER";
        String response = sendRequest(String.format("{\"type\":\"%s\",\"data\":{\"username\":\"%s\"}}", type, username));
        System.out.println("\n نتیجه:\n" + response);
    }

    private static String sendRequest(String json) {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(json);
            return in.readLine();
        } catch (IOException e) {
            System.err.println(" خطا در ارتباط با سرور: " + e.getMessage());
            return null;
        }
    }
}