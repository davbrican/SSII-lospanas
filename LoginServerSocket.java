import java.io.*;
import java.net.*;

import javax.net.ServerSocketFactory;
public class LoginServerSocket {
    private static final String CORRECT_USER_NAME = "vgomezalv";
    private static final String CORRECT_PASSWORD = "H34arga$tenK";

    /**
    * @param args
    * @throws IOException
    * @throws InterruptedException
    */

    public static void main(String[] args) throws IOException, InterruptedException {
        // wait for client connection and check login information
        ServerSocketFactory socketFactory = (ServerSocketFactory)
        ServerSocketFactory.getDefault();

        // create Socket from factory
        ServerSocket serverSocket = (ServerSocket)
        socketFactory.createServerSocket(7070);

        while (true) {
            try {
                System.err.println("Waiting for connection...");
                Socket socket = serverSocket.accept();

                // open BufferedReader for reading data from client
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // open PrintWriter for writing data to client
                PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                String userName = input.readLine();
                String password = input.readLine();

                if (userName.equals(CORRECT_USER_NAME)&&password.equals(CORRECT_PASSWORD))
                {
                    output.println("Welcome, " + userName);
                } else {
                    output.println("Login Failed.");
                }
                
                output.close();
                input.close();
                socket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        } // end while
    }
    //serverSocket.close();
}