import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
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
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Hashtable;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.PrintWriter;
import java.util.Timer;
import java.util.TimerTask;
import java.time.*;
//import pair.pair;

class Main {
    static Hashtable<Integer, String> Usuarios = new Hashtable<Integer, String>(){{
        put(1,"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAupS0gFZ9rvAXQxwQwL/v+8VL8HpCvlndiyojFuZO5qjvSQN8abl6zuqHBh4SkuV7vXz+S87UYHhpOJBlzGMwRdcfQ16Bwn3ENtUVGTthEVIPjgN3UQTXLdvO11+d+RVDvsotyLWsQVEmi3cR1kvrbEjd7VpRnkxpt4MyfuSm+OGDfrVGW88xkR2BAjHsE7A6+EQ9HxRIoox/OC3RgxyL0H/+Ai0FBzYnGHFYLQQnygsP+YVVv/dMNzQdcynRyuS5+lK4iibFBQJjk/JMou24L2EXhYrPvq3RM1/htu0x0MxbIMixhU5pqyDDSYmXSBwb+RgiCWI3Qg6Dw4l+Ie8N0QIDAQAB");
        put(2,"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvBNmWjxpNgquBBf4X4fTCV/5y4uCfU4hjvnwuXExsu7hmxbDCnnHkVNixNyrY6cfPTElYePQty/wtyOggr2eN0lj88fX29j+OI+jcwjoD4XYzr+W4TIDtPdsvKxB9XBI4kSFgeNCtcwbHhO3VcXIUJnFJ8izoYRiK4KrmXu5szgqt03RRrl9lKgQV3q1g3oRAIu9ii/ZofA9PhyYBYB//hhpUYPYE9vLY96fdDPcmahFeKxSvWYu6f/cJPEia6mFXffO0atN3B3PjJwlAQLwsRPDIu0wLoKS4FscoQtaLGgqfwjfxp03vfv8zjZN/8q6xzbFf7jR45SQ5mTnck+YqQIDAQAB");
        put(3,"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2RQQYtAasAv8UKudiRFgcfZn+U3r0CjBD19bk/ibCsEgaXtuF7jxwnbVEuYFitZpo+yVemB6NuR2cth6XplJyww5TP1HSSZdGYSWxPsOI93fGSUNp0n/e5T+pGBA/ldPZ601DZbAJiCV93P00A9/CiFPQsAAi6oYVHkeTIffNAFivGq78+WYC99iavRAfGc+Fvf036Tp9MTZ6EbhF1lWOUG8yc+788sYaYOmntugOWMyWgKCQ0U26iW+0DEUyUTRVQ53oruVEHO6XLI962WE++lO2aVMvqnn1bsZi73mA5fdiWI3ECb25mILP9R3MK8LHwo3sWWg0GFdItYDqmQM4QIDAQAB");
    }};

    //Diccionario número de meses, nombre
    static Hashtable<Integer, String> Meses = new Hashtable<Integer, String>(){{
        put(1,"Enero");
        put(2,"Febrero");
        put(3,"Marzo");
        put(4,"Abril");
        put(5,"Mayo");
        put(6,"Junio");
        put(7,"Julio");
        put(8,"Agosto");
        put(9,"Septiembre");
        put(10,"Octubre");
        put(11,"Noviembre");
        put(12,"Diciembre");
    }};    

    public static void main(String[] args) throws SignatureException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {
        
        ArrayList<ArrayList<String>> ips = new ArrayList<ArrayList<String>>();

        new Timer().scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run(){
                for(int i = 0; i < ips.size(); i++){
                    LocalDateTime stamp = LocalDateTime.parse(ips.get(i).get(1));
                    System.out.println(stamp);
                    if(Duration.between(stamp, LocalDateTime.now()).getSeconds() > 10L){
                        ips.remove(i);
                    }
                }
            }
        },0,5000);

        ArrayList<Float> ratios = new ArrayList<Float>();

        Integer totalPedidos = 0;
        Integer pedidosCorrectos = 0;
        Integer numeroDeMes = 5;
        Integer numeroDeAnyo = 2022;
        
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
                /*
                if(noSimulacro){
                    String ipCliente = socker.getInetAddress().getHostAddress();
                    LocalDateTime ahora = LocalDateTime.now();
                    Boolean encontrado = false;
                    for(int i = 0; i < ips.size(); i++){
                        if(ips.get(i).get(0).equals(ipCliente)){
                            socket.close();
                        }
                    }
                    if(!encontrado){
                        ips.add(new ArrayList<String>(Arrays.asList(ipCliente, ahora.toString())));
                    }
                }
                */
                
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("\nConnection accepted");

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
                String numeroPedidosMaxMes = Campos[3];
                System.out.println(numeroPedidosMaxMes);
                
                if (Integer.parseInt(numeroPedidosMaxMes) == 0) {
                    String[] pedidosArray = pedido.split(" ");
                    for (String objeto : pedidosArray) {
                        Integer obj = Integer.parseInt(objeto);
                        if (obj < 0 || obj > 300) {
                            System.out.println("Número de objetos incorrecto");
                            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                            out.println("Número de objetos incorrecto");
                            out.close();
                        }
                    }
                }

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
                    if (Integer.parseInt(numeroPedidosMaxMes) != 0) {
                        pedidosCorrectos++;
                    }
                    if (Integer.parseInt(numeroPedidosMaxMes) == 0) {
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        out.println("Firma correcta");
                        out.close();
                    }
                } else {
                    System.out.println("Firma incorrecta");
                    if (Integer.parseInt(numeroPedidosMaxMes) == 0) {
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        out.println("Firma incorrecta");
                        out.close();
                    }
                }
                if (Integer.parseInt(numeroPedidosMaxMes) != 0) {
                    totalPedidos++;
                    System.out.println("Pedidos correctos: " + pedidosCorrectos + " de " + totalPedidos);
                }


                if (totalPedidos == Integer.parseInt(numeroPedidosMaxMes) && Integer.parseInt(numeroPedidosMaxMes) != 0) {
                    System.out.println("\n\nNumero de mes: " + numeroDeMes);
                    System.out.println("Total de pedidos: " + totalPedidos);
                    System.out.println("Pedidos correctos: " + pedidosCorrectos);
                    System.out.println("Pedidos incorrectos: " + (totalPedidos - pedidosCorrectos));
                    Float ratioMes = (float) pedidosCorrectos / (float) totalPedidos;
                    System.out.println("Ratio: " + ratioMes);

                    String log = "Mes: " + Meses.get(numeroDeMes) + " Anyo: " + numeroDeAnyo.toString() + " Ratio Mensual: " + ratioMes.toString();

                    if (ratios.size() >= 2) {
                        Float p1 = ratios.get(ratios.size()-2);
                        Float p2 = ratios.get(ratios.size()-1);

                        if (ratioMes == p1 && ratioMes == p2) {
                            log = log + " Tendencia: 0";
                        } else if (ratioMes < p1 || ratioMes < p2) {
                            log = log + " Tendencia: -";
                        } else {
                            log = log + " Tendencia: +";
                        }
                    } else {
                        log = log + " Tendencia: 0";
                    }

                    System.out.println("\n" + log);

                    String data = "";
                    try {
                        File myObj = new File("logs.txt");
                        Scanner myReader = new Scanner(myObj);
                        while (myReader.hasNextLine()) {
                          data = data + myReader.nextLine() + "\n";
                        }
                        myReader.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    try {
                        FileWriter myWriter = new FileWriter("logs.txt");
                        myWriter.write(data + log);
                        myWriter.close();
                      } catch (IOException e) {
                        e.printStackTrace();
                      }
                    
                    ratios.add(ratioMes);

                    numeroDeMes++;
                    if (numeroDeMes == 13) {
                        numeroDeMes = 1;
                        numeroDeAnyo++;
                    }
                    
                    totalPedidos = 0;
                    pedidosCorrectos = 0;
                }

            } catch (IOException e) {
                return;
            }
        }
    }
}