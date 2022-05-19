import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

class Main {
    public static void main(String[] args) {
		
		ServerSocket serverSocket = null;
		Socket socket = null;
		try {
			serverSocket = new ServerSocket(8081);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
        while (true) {
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Connection accepted");
            InputStream input = null;
            BufferedReader buffer = null;
            try {
                input = socket.getInputStream();
                buffer = new BufferedReader(new InputStreamReader(input));
                String receivedText = buffer.readLine();
                System.out.println(receivedText);
                
            } catch (IOException e) {
                return;
            }
        }
    }
}