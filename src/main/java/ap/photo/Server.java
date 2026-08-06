package ap.photo;

import java.io.*;
import java.net.*;

public class Server {
    private static final int PORT = 12345;
    private ServerSocket serverSocket;
    private DatabaseManager dbManager;
    private FileManager fileManager;

    public Server(DatabaseManager dbManager, FileManager fileManager) {
        this.dbManager = dbManager;
        this.fileManager = fileManager;
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Server started on port " + PORT);

        while (true) {
            Socket client = serverSocket.accept();
            System.out.println("Client connected: " + client.getInetAddress());

            new Thread(new ClientHandler(client, dbManager, fileManager)).start();
        }
    }

    public void stop() throws IOException {
        if (serverSocket != null)
            serverSocket.close();
    }

    public static void main(String[] args) {
        try {
            Database database = new Database();
            FileManager fileManager = new FileManager();
            DatabaseManager dbManager = new DatabaseManager(database, fileManager);

            try {
                database.loadFromFile("database.json");
                System.out.println("Database loaded.");
            } catch (IOException e) {
                System.out.println("New database created.");
            }

            new Server(dbManager, fileManager).start();

        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}