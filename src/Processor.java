import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Processor extends Thread {
    private int port;
    private final int ID;
    private  final String ANSI_BLUE = "\033[34m";
    private final String ANSI_RESET = "\u001B[0m";

    public Processor(int id) {
        ID = id;
    }

    public void run() {
        startServer();
    }

    private void startServer() {
//      Random port in range 30000-40000
        port = randomNumber(30000,40000);
//      Configure Processor connection
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println(ANSI_BLUE + "Processor " + ID + " listening at port " + port + ANSI_RESET);

            // Wait for clients to connect and configure In / Out client messages
            while (true) {
                Socket clientSocket = serverSocket.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                System.out.println(ANSI_BLUE + "Processor " + ID + " received connection from client." + ANSI_RESET);
//              Read the API content from client
                String clientResponse = in.readLine();
//              Return to client the count chars from the API content
                out.println(countCharacters(clientResponse));
//              Return to the client the end message
                out.println("end data");
//              Close the connection with the client
                clientSocket.close();
                System.out.println(ANSI_BLUE+ "Processor " + ID + " END" + ANSI_RESET);
            }
        } catch (IOException e) {
            System.out.println(ANSI_BLUE + "Port used" + ANSI_RESET);
//          Start server again to a new port
            startServer();
//            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.out.println(ANSI_BLUE + "Could not close port: " + port + ANSI_RESET);
                e.printStackTrace();
            }
        }
    }

    //  Return random number
    private static int randomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt((max + 1) - min) + (min);
    }

    public static String countCharacters(String str) {
//      Transform the letter to lower case
        str = str.toLowerCase();
        Map<Character, Integer> charCount = new HashMap<>();
//      Count the chars from [a-z]
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c >= 'a' && c <= 'z') {
                if (charCount.containsKey(c)) {
                    charCount.put(c, charCount.get(c) + 1);
                } else {
                    charCount.put(c, 1);
                }
            }
        }
//      Convert the Hashmap to a string
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Character, Integer> entry : charCount.entrySet()) {
            sb.append(entry.getKey() + ": " + entry.getValue() + "\n");
        }
        return sb.toString();
    }

    public int getPORT() {
        return port;
    }
}
