import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Hashtable;
import java.security.NoSuchAlgorithmException; import java.security.PrivateKey; import java.security.spec.InvalidKeySpecException;
import java.net.*;
import java.io.*;


class Main {
    public static void main(String[] args) {
        try {
            int port = 5350;
            Socket socket = new Socket("127.0.0.1", port);
            String enviar = "Hello\n";
            socket.getOutputStream().write(enviar.getBytes());


            BufferedReader input;
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String message = input.readLine();

            System.out.println(message.toString());
            socket.close();
            
        } catch (Exception e) {
            System.out.println("Exception in main: " + e.getMessage());
        }
    }
}