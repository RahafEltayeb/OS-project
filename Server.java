//package osproject;



import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Server {
    static final int PORT = 1300;

    // List to store client requests
    private static final List<String> clientRequests = new ArrayList<>();
    // List to store connected client IPs
    private static final List<String> connectedClients = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) { // create a new server socket
            System.out.println("Server is listening on port " + PORT);

            while (true) {
                // Accept a client connection
                Socket socket = serverSocket.accept();
                InetAddress clientAddress = socket.getInetAddress();
                String clientIP = clientAddress.getHostAddress();
                System.out.println("Client connected: " + clientIP);
                
                // Run Network.sh on client connection
                
                NetworkShell(clientIP);

                // Add client to the connected list if not already present
                synchronized (connectedClients) {
                    if (!connectedClients.contains(clientIP)) {
                        connectedClients.add(clientIP);
                    }
                }

                // Print connected client information
                System.out.println("Client connected: " + clientIP);
                displayConnectedClients();

                // Handle client requests in a separate thread
                ClientHandler clientHandler = new ClientHandler(socket, clientIP);
                clientHandler.start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void NetworkShell(String clientIP) {
        try {
            // Execute network shell file (taking client IP as argument)
            Process process = Runtime.getRuntime().exec("sh /home/maya/Network.sh " + clientIP);

            // Create an input stream from the network shell file
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            // Output (Network.sh) on console
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Wait for the process to complete to avoid zombie processes
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            System.err.println("Error executing Network.sh: " + e.getMessage());
        }
    }

    // Method to print all client requests
    private static void printClientRequests() {
        System.out.println("\n--- Client Requests ---");
        synchronized (clientRequests) {
            for (String request : clientRequests) {
                System.out.println(request);
            }
        }
        System.out.println("-----------------------\n");
    }

    // Method to display all connected clients
    private static void displayConnectedClients() {
        System.out.println("\n--- Connected Clients ---");
        synchronized (connectedClients) {
            if (connectedClients.isEmpty()) {
                System.out.println("(No clients connected)");
            } else {
                for (String clientIP : connectedClients) {
                    System.out.println("Client IP: " + clientIP);
                }
            }
        }
        System.out.println("--------------------------\n");
    }

    // Inner class to handle client requests in a separate thread
    static class ClientHandler extends Thread {
        private final Socket socket;
        private final String clientIP;

        public ClientHandler(Socket socket, String clientIP) {
            this.socket = socket;
            this.clientIP = clientIP;
        }

        @Override
        public void run() {
            try (
                BufferedReader fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter toClient = new PrintWriter(socket.getOutputStream(), true)) {

                // Continuously read messages from the client
                String clientMessage;
                while ((clientMessage = fromClient.readLine()) != null) {
                    System.out.println("Message from client (" + clientIP + "): " + clientMessage);
                    
                    if (clientMessage.equals("REQUEST_SYSTEM_INFO")) { 
                    
    	                System.out.println("Processing system info request...");

    	                // Generate the file at the server side by executing system.sh
    	                File systemInfoFile = generateSystemInfoFile();

    	                // Send the file to the client
    	                sendFileToClient(systemInfoFile);
    	                
    	            } else if (clientMessage.equals("exit")) {
    	                System.out.println("Client disconnected.");
    	                break; // Exit loop if client sends "exit"
    	            } else {
    	                System.out.println("Invalid client request...");
    	            }

                    // Store client request in the list
                    synchronized (clientRequests) {
                        clientRequests.add("Client IP: " + clientIP + ", Request: " + clientMessage);
                    }

                    // Print the updated list of client requests
                    printClientRequests();

                    // Send a response back to the client
                    toClient.println("Message received: " + clientMessage);
                }
            } catch (IOException e) {
                System.err.println("Connection error with client (" + clientIP + "): " + e.getMessage());
            } finally {
                // Remove the client from the connected list on disconnect
                synchronized (connectedClients) {
                    connectedClients.remove(clientIP);
                    System.out.println("Client disconnected: " + clientIP);
                    displayConnectedClients();
                }

                // Close the socket
                try {
                    if (!socket.isClosed()) {
                        socket.close();
                    }
                } catch (IOException e) {
                    System.err.println("Error closing socket for client (" + clientIP + "): " + e.getMessage());
                }
            }
        }
        // Method to execute system.sh and save its output to a file
        private File generateSystemInfoFile() throws IOException {
            File outputFile = new File("system_info.txt"); // create file to store script output

            Process process = Runtime.getRuntime().exec("sh /home/maya/System.sh"); 
            	
            	BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                PrintWriter writer = new PrintWriter(new FileWriter(outputFile));
            	

                String line;
                //Read system.sh output
                while ((line = reader.readLine()) != null) {
                	//write the output to the created file
                    writer.println(line);
                }
                try {
    				process.waitFor();
    				//writer.close();
    			} catch (InterruptedException e) {
    				
    				e.printStackTrace();
    			}
                
            return outputFile;
        }

        //send a file to the client
    
private void sendFileToClient(File file) throws IOException {
    try (BufferedInputStream fileReader = new BufferedInputStream(new FileInputStream(file));
         BufferedOutputStream fileWriter = new BufferedOutputStream(socket.getOutputStream());
         PrintWriter writer = new PrintWriter(fileWriter, true)) {

        // Reading data from the file and sending it to the client
        byte[] buffer = new byte[4096];
        int bytes;
        while ((bytes = fileReader.read(buffer)) != -1) {
            fileWriter.write(buffer, 0, bytes);
        }
        fileWriter.flush();

        // Send EOF marker
        writer.println("END_OF_FILE");
        System.out.println("File sent to client successfully.");
    } catch (IOException e) {
        System.err.println("Error sending file to client: " + e.getMessage());
        throw e;
    }
}


    }
}
