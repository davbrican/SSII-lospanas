package Cliente;
// Java core packages
import java.io.*;
import java.net.*;

import javax.net.SocketFactory;
// Java extension packages
import javax.swing.*;

public class LoginClient {
    // LoginClient constructor
    public LoginClient()
    {
        // open Socket connection to server and send login
        try {
            // obtain SocketFactory for creating Sockets
            SocketFactory socketFactory = ( SocketFactory ) SocketFactory.getDefault();

            // create Socket from factory
            Socket socket = ( Socket ) socketFactory.createSocket("localhost", 7070 );

            // create PrintWriter for sending login to server
            PrintWriter output = new PrintWriter(new OutputStreamWriter( socket.getOutputStream()));

            // prompt user for user name
            String userName = JOptionPane.showInputDialog( null,"Enter User Name:" );

            // send user name to server
            output.println( userName );
            //output.println( Integer.MAX_VALUE + 1 );

            // prompt user for password
            String password = JOptionPane.showInputDialog( null,"Enter Password:" );

            // send password to server
            output.println( password );
            output.flush();

            // create BufferedReader for reading server response
            BufferedReader input = new BufferedReader(new InputStreamReader( socket.getInputStream ()) );

            // read response from server
            String response = input.readLine();

            // display response to user
            JOptionPane.showMessageDialog( null, response );

            // clean up streams and sockects
            output.close();
            input.close();
            socket.close();
        } // end try
        // handle exception with server
        catch ( IOException ioException ) {
            ioException.printStackTrace();
        }
        // exit application
        finally {
            System.exit(0);
        }
    } // end LoginClient constructor
    // execute application

    public static void main( String args[] )
    {
        new LoginClient();
    }
}