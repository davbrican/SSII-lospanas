import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Hashtable;



class Main {
    static Hashtable<Integer, String> Usuarios = new Hashtable<Integer, String>(){{
        put(1,"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAupS0gFZ9rvAXQxwQwL/v+8VL8HpCvlndiyojFuZO5qjvSQN8abl6zuqHBh4SkuV7vXz+S87UYHhpOJBlzGMwRdcfQ16Bwn3ENtUVGTthEVIPjgN3UQTXLdvO11+d+RVDvsotyLWsQVEmi3cR1kvrbEjd7VpRnkxpt4MyfuSm+OGDfrVGW88xkR2BAjHsE7A6+EQ9HxRIoox/OC3RgxyL0H/+Ai0FBzYnGHFYLQQnygsP+YVVv/dMNzQdcynRyuS5+lK4iibFBQJjk/JMou24L2EXhYrPvq3RM1/htu0x0MxbIMixhU5pqyDDSYmXSBwb+RgiCWI3Qg6Dw4l+Ie8N0QIDAQAB");
        put(2,"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvBNmWjxpNgquBBf4X4fTCV/5y4uCfU4hjvnwuXExsu7hmxbDCnnHkVNixNyrY6cfPTElYePQty/wtyOggr2eN0lj88fX29j+OI+jcwjoD4XYzr+W4TIDtPdsvKxB9XBI4kSFgeNCtcwbHhO3VcXIUJnFJ8izoYRiK4KrmXu5szgqt03RRrl9lKgQV3q1g3oRAIu9ii/ZofA9PhyYBYB//hhpUYPYE9vLY96fdDPcmahFeKxSvWYu6f/cJPEia6mFXffO0atN3B3PjJwlAQLwsRPDIu0wLoKS4FscoQtaLGgqfwjfxp03vfv8zjZN/8q6xzbFf7jR45SQ5mTnck+YqQIDAQAB");
        put(3,"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2RQQYtAasAv8UKudiRFgcfZn+U3r0CjBD19bk/ibCsEgaXtuF7jxwnbVEuYFitZpo+yVemB6NuR2cth6XplJyww5TP1HSSZdGYSWxPsOI93fGSUNp0n/e5T+pGBA/ldPZ601DZbAJiCV93P00A9/CiFPQsAAi6oYVHkeTIffNAFivGq78+WYC99iavRAfGc+Fvf036Tp9MTZ6EbhF1lWOUG8yc+788sYaYOmntugOWMyWgKCQ0U26iW+0DEUyUTRVQ53oruVEHO6XLI962WE++lO2aVMvqnn1bsZi73mA5fdiWI3ECb25mILP9R3MK8LHwo3sWWg0GFdItYDqmQM4QIDAQAB");
    }};

    public static void main(String[] args) throws SignatureException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
		
		ServerSocket serverSocket = null;
		Socket socket = null;
		try {
		    serverSocket = new ServerSocket(5350);
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
                String[] Campos = receivedText.split("campo:");
                String pedido = Campos[0];
                String id = Campos[1];
                String firma = Campos[2];
                System.out.println(firma);

                String publicKey = Usuarios.get(Integer.parseInt(id));

                byte[] publicKeyBytes = Base64.getDecoder().decode(publicKey);
                byte[] firmaBytes = Base64.getDecoder().decode(firma);

                X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                PublicKey publicKey2 = keyFactory.generatePublic(spec);
                
                Signature sg = Signature.getInstance("SHA256withRSA");
                sg.initVerify(publicKey2);
                sg.update(pedido.getBytes());
                // Verification de firma
                if(sg.verify(firmaBytes)){
                    System.out.println("Firma correcta");
                    System.out.println(pedido);
                };


            } catch (IOException e) {
                return;
            }
        }
    }
}