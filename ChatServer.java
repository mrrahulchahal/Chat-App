import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
    private static List<ClientHandler> clients = new ArrayList<>();
  
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(5000);
        System.out.println("Server started. Waiting for clients...");
  
        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket);
  
            ClientHandler clientThread = new ClientHandler(clientSocket, clients);
            clients.add(clientThread);
            new Thread(clientThread).start();
  
            // Update the clients list for all existing clients
            for (ClientHandler aClient : clients) {
                aClient.updateClients(clients);
            }
        }
    }
  }
  
  class ClientHandler implements Runnable {
    private Socket clientSocket;
    private List<ClientHandler> clients;
    private PrintWriter out;
    private BufferedReader in;
  
    public ClientHandler(Socket socket, List<ClientHandler> clients) throws IOException {
        this.clientSocket = socket;
        this.clients = clients;
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }
  
    public void updateClients(List<ClientHandler> clients) {
        this.clients = clients;
    }
  
    public void run() {
        try {
            String inputLine;
            while ((inputLine = in.readLine())!= null) {
                if (inputLine.startsWith("join ")) {
                    String name = inputLine.substring(5); // Extract the client's name
                    String joinMessage = name + " has joined the chat.";
                    for (ClientHandler aClient : clients) {
                        aClient.out.println(joinMessage);
                    }
                } else {
                    for (ClientHandler aClient : clients) {
                        aClient.out.println(inputLine);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());
        } finally {
            try {
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
  }