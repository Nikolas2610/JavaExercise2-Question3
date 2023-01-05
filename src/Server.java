import java.io.*;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;
import java.util.Random;

public class Server extends Thread {

    private final Socket clientSocket;
    private static final int PROCESSORS_COUNT = 4;
    private static List<Processor> processors = new ArrayList<>();
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final int N = 25;

    public static void main(String[] args) throws IOException {
//        Configure main server socket at port 10000
        ServerSocket serverSocket = null;
        final int PORT = 10000;
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println(ANSI_GREEN + "Server listening at port " + PORT + ANSI_RESET);

            // Create the Processor objects and start them
            for (int i = 0; i < PROCESSORS_COUNT; i++) {
                Processor processor = new Processor(i + 1);
                processor.start();
                processors.add(processor);
            }

            // Wait for clients to connect
            while (true) {
                System.out.println(ANSI_GREEN + "Waiting for clients to connect..." + ANSI_RESET);
                new Server(serverSocket.accept());
            }
        } catch (IOException e) {
            System.err.println(ANSI_GREEN + "Could not listen on port: " + PORT + "." + ANSI_RESET);
            System.exit(1);
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.out.println(ANSI_GREEN + "Cound not close port: " + PORT + ANSI_RESET);
                System.exit(1);
            }
        }
    }

    public Server(Socket clientSocket) {
        this.clientSocket = clientSocket;
        start();
    }

    public void run() {
        try {
//            Configure In / Out messages with clients
            PrintWriter out = new PrintWriter(this.clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String inputLine;
//          Run N times the reader functionality
            for (int i = 0; i < N; i++) {
                String message = getApiContent();   // Get Api Content
                out.println(message);   // Send the content to the client
                inputLine = in.readLine();  // Read response from client to return one available Processor
                out.println(processors.get(randomNumber(0, PROCESSORS_COUNT - 1)).getPORT());
            }
//          Return exit message to close the connection at the client
            System.out.println(ANSI_GREEN + "Close client connection" + ANSI_RESET);
            out.println("exit");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getApiContent() throws IOException {
        // Set the API URL
        String apiUrl = "http://metaphorpsum.com/paragraphs/"
                + String.valueOf(randomNumber(1, 10)) + "/" + String.valueOf(randomNumber(1, 10));

        // Make the API call and get the response
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // Return the response
        return response.toString();
    }

    //  Return random number
    private static int randomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt((max + 1) - min) + (min);
    }
}