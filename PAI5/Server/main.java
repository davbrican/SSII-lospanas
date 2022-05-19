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
			serverSocket = new ServerSocket(8080);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			socket = serverSocket.accept();
		} catch (IOException e) {
			e.printStackTrace();
		}

		InputStream input = null;
        BufferedReader buffer = null;
        DataOutputStream output = null;
        try {
            input = socket.getInputStream();
            buffer = new BufferedReader(new InputStreamReader(input));
            output = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            return;
        }
        String line;
        while (true) {
            try {
                line = buffer.readLine();
                if ((line == null) || line.equalsIgnoreCase("QUIT")) {
                    socket.close();
                    return;
                } else {
                    output.writeBytes(line + "\n\r");
                    output.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}