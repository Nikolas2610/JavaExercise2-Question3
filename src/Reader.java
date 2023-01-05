import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Reader {
    static String serverHostname = "127.0.0.1";
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

        String serverMessage;

        while ((serverMessage = in.readLine()) != null) {
//      If response from main server is exit close connection
            if (serverMessage.equals("exit")) {
                break;
            }
//      Save Api message
            String apiMessage = serverMessage;
            System.out.println(apiMessage);
//          Request to server to get the port from a available Processor
            out.println("Connect me to a processor to calc the data");
//          Save Processor port
            String port = in.readLine();
            connectToProcessor(Integer.parseInt(port), apiMessage);
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