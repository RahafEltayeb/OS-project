package osproject;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client1 {
    static final String SERVER_IP = "172.20.10.3";  
    static final int SERVER_PORT = 1300;
    static long lastRequestTime=0; 
    static final long RequestDelay=300000; // 5 minutes in milliseconds 

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT)) { 
            // run login.sh
            System.out.println("Executing login.sh...");
            runLoginScript(); 

            // Create a socket connection
            //Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            System.out.println("Connected to server at " + SERVER_IP + ":" + SERVER_PORT);

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in);


            while (true) { //infinite request loop 
            	//user input prompt
                System.out.print("Enter your request (type 'exit' to quit): ");
                String userInput = scanner.nextLine();
                
                if ("exit".equalsIgnoreCase(userInput)) {
                	//whole connection is terminated
                    out.println("exit"); // Inform server to close the connection
                    System.out.println("Exiting...");
                    break; // Exit the loop if the user types 'exit'
                }

                else if ("REQUEST_SYSTEM_INFO".equalsIgnoreCase(userInput)) {
                    //current time
                    long currentTime = System.currentTimeMillis();

                    if (currentTime - lastRequestTime < RequestDelay) {
                    	//wait time calculation in seconds
                        long waitTime = (RequestDelay - (currentTime - lastRequestTime)) / 1000; 
                        
                        System.out.println("Next system info request cannot be before " + waitTime + " seconds.");
                    } else {
                    	//inform server of the request
                        out.println("REQUEST_SYSTEM_INFO");
                        saveFileFromServer(socket, "received_system_info.txt");

                        // Signal the end of request processing (optional)
                        out.println("END_OF_REQUEST");
                        lastRequestTime = currentTime; // Update last request time

                        // Now display the received file content
                        try (BufferedReader fileReader = new BufferedReader(new FileReader("received_system_info.txt"))) {
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

                // run check.sh
                System.out.println("Executing check.sh...");
                runCheckScript();                
            }

        } catch (IOException e) {
            e.printStackTrace();
        } 
       /* finally {
            try {
               // if (socket != null)
                 // socket.close();
                //if (out != null )
                  //  out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }

    // Method to save the received file locally
    private static void saveFileFromServer(Socket socket, String fileName) throws IOException {
        try (InputStream serverInput = socket.getInputStream();
           BufferedReader textReader = new BufferedReader(new InputStreamReader(serverInput));
           FileOutputStream fileWriter = new FileOutputStream(fileName)) {

            byte[] buffer = new byte[4096];
            int bytesRead;

            while (true) {
                // Check for the "END_OF_FILE" signal
                String serverLine = textReader.readLine();
                if ("END_OF_FILE".equals(serverLine)) {
                    break;
                }

                // Save streamed content to the file
                bytesRead = serverInput.read(buffer);
                if (bytesRead == -1) break;
                fileWriter.write(buffer, 0, bytesRead);
            }
            System.out.println("File received and saved as: " + fileName);
        }
    }

private static void runLoginScript() {
    try {
        // Command to launch login.sh in a new terminal
        String[] command = {
            "gnome-terminal", "--", "sh", "/home/olla/login.sh"
        };

        // Use ProcessBuilder to execute the command
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true); // Redirect stderr to stdout
        Process process = processBuilder.start();

        // Wait for the process to exit (optional)
     //   int exitCode = process.waitFor();
      //  System.out.println("login.sh exited with code: " + exitCode);

    } catch (IOException e) {
        e.printStackTrace();
    }
}



private static void runCheckScript(){
    try {
        Process process = new ProcessBuilder("sh", "/home/olla/Check.sh").start();
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
