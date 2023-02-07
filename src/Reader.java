import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Reader {
    static String serverHostname = "127.0.0.1";
    private static final int N = 25;
    public static void main(String[] args) throws IOException {
//      Set localhost address
        if (args.length > 0) {
            serverHostname = args[0];
        }

        System.out.println("Attempting to connect to host " + serverHostname + " on port 10000.");
//      Initialize Client Socket, In/Out response with server
        Socket echoSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;
        try {
//          Configure connection with main server and In/Out response with server
            echoSocket = new Socket(serverHostname, 10000);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
        } catch (UnknownHostException e) {
//          Error with address or port
            System.err.println("Don't know about host: " + serverHostname);
            System.exit(1);
        } catch (IOException e) {
//          Error with server down
            System.err.println("Couldn't get I/O for the connection to: " + serverHostname);
            System.exit(1);
        }
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String serverMessage;
        String readerInput;
        boolean exit = false;

        while ((serverMessage = in.readLine()) != null) {
//          Read the menu from the Server
            System.out.println(serverMessage);
//          Read from keyboard and send the response to the Server
            readerInput = stdIn.readLine();
            out.println(readerInput);
            switch (readerInput) {
                case "1":
//                  Read the list of available processors
                    serverMessage = in.readLine();
                    System.out.println(serverMessage);
                    // Read from keyboard and send the response to the Server
                    readerInput = stdIn.readLine();
                    out.println(readerInput);
//                  Get the port of the chosen processor
                    String port = in.readLine();
//                  Get the API message and send to the processor for N times
                    for (int i = 0; i < N; i++) {
                        serverMessage = in.readLine();
                        String apiMessage = serverMessage;
                        System.out.println(apiMessage);
                        connectToProcessor(Integer.parseInt(port), apiMessage);
                    }
                    break;
                case "2":
                    //  Read the list of available processors
                    serverMessage = in.readLine();
                    System.out.println(serverMessage);
                    // Read from keyboard and send the response to the Server
                    readerInput = stdIn.readLine();
                    out.println(readerInput);
                    break;
                case "3":
                    exit = true;
                    break;
                default:
                    System.out.println("Wrong input");
                    continue;
            }
            if (exit) {
                break;
            }
        }
//      Close connection
        out.close();
        in.close();
        echoSocket.close();
    }

    private static void connectToProcessor(int portNumber, String apiMessage) {
        try {
//          Configure a new Socket connection and In / Out server messages
            Socket processorSocket = new Socket(serverHostname, portNumber);
            PrintWriter processorOut = new PrintWriter(processorSocket.getOutputStream(), true);
            BufferedReader processorIn = new BufferedReader(new InputStreamReader(processorSocket.getInputStream()));
            String serverMessage;
//          Send to the Processor the API message to calc the chars
            processorOut.println(apiMessage);
//          Read all the response data from the Processor
            while ((serverMessage = processorIn.readLine()) != null) {
                if (serverMessage.equals("end data")) {
                    break;
                }
                System.out.println(serverMessage);
            }
        } catch (IOException e) {
            System.out.println("error");
            throw new RuntimeException(e);
        }
    }
}