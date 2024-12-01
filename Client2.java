package osproject;


import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client2 {
    static final String SERVER_IP = "172.20.10.3"; 
    static final int SERVER_PORT = 1300;
    static long lastRequestTime = 0;  // Last time system info was requested
    static final long REQUEST_DELAY = 300000;  // 5 minutes in milliseconds

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT)) { // Keep the socket open for the entire session
            System.out.println("Connected to server at " + SERVER_IP + ":" + SERVER_PORT);

            BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter toServer = new PrintWriter(socket.getOutputStream(), true); // Auto-flush

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.print("Enter your request (type 'exit' to quit): ");
                String userInput = scanner.nextLine();

                long currentTime = System.currentTimeMillis();

                if ("exit".equalsIgnoreCase(userInput)) {
                    toServer.println("exit"); // Notify the server to close the connection
                    System.out.println("Exiting...");
                    break;
                }

                if ("REQUEST_SYSTEM_INFO".equalsIgnoreCase(userInput)) {
                    if (currentTime - lastRequestTime < REQUEST_DELAY) {
                        long waitTime = (REQUEST_DELAY - (currentTime - lastRequestTime)) / 1000; 
                        System.out.println("Next system info request cannot be before " + waitTime + " seconds.");
                    } else {
                        toServer.println("REQUEST_SYSTEM_INFO");
                        lastRequestTime = currentTime; // Update the last request time
                        saveFileFromServer(socket, "received_system_info2.txt");

                        // Display the received file content
                        try (BufferedReader fileReader = new BufferedReader(new FileReader("received_system_info2.txt"))) {
                            String fileLine;
                            System.out.println("Contents of the received file:");
                            while ((fileLine = fileReader.readLine()) != null) {
                                System.out.println(fileLine);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    System.out.println("Unknown request. Please try again.");
                }

                // Execute and display search.sh
                System.out.println("Executing search.sh...");
                runSearchScript();

                // Execute and display clientInfo.sh
                System.out.println("Executing clientInfo.sh...");
                runClientInfoScript();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveFileFromServer(Socket socket, String fileName) throws IOException {
        try (BufferedInputStream fileReader = new BufferedInputStream(socket.getInputStream());
             BufferedOutputStream fileWriter = new BufferedOutputStream(new FileOutputStream(fileName))) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fileReader.read(buffer)) != -1) {
                fileWriter.write(buffer, 0, bytesRead);
            }
            fileWriter.flush();
            System.out.println("File saved as: " + fileName);

            // Display the file content immediately after saving
            try (BufferedReader fileReader2 = new BufferedReader(new FileReader(fileName))) {
                String fileLine;
                System.out.println("Contents of the received file:");
                while ((fileLine = fileReader2.readLine()) != null) {
                    System.out.println(fileLine);
                }
            } catch (IOException e) {
                System.err.println("Error reading the saved file: " + e.getMessage());
            }
        }
    }

    private static void runSearchScript() {
        try {
            Process process = new ProcessBuilder("sh", "/home/client2/Desktop/Search.sh").start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line); // Display output of the search script
            }
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void runClientInfoScript() {
        try {
            Process process = new ProcessBuilder("sh", "/home/client2/Desktop/ClientInfo updated.sh").start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line); // Display output of the clientInfo script
            }
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
