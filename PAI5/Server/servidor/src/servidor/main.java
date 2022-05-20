package servidor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

class Main {
	// Diccionario número de meses, nombre
	static Hashtable<Integer, String> Meses = new Hashtable<Integer, String>() {
		{
			put(1, "Enero");
			put(2, "Febrero");
			put(3, "Marzo");
			put(4, "Abril");
			put(5, "Mayo");
			put(6, "Junio");
			put(7, "Julio");
			put(8, "Agosto");
			put(9, "Septiembre");
			put(10, "Octubre");
			put(11, "Noviembre");
			put(12, "Diciembre");
		}
	};

	public static void main(String[] args)
			throws SignatureException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException {

		ArrayList<ArrayList<String>> ips = new ArrayList<ArrayList<String>>();

		new Timer().scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				for (int i = 0; i < ips.size(); i++) {
					LocalDateTime stamp = LocalDateTime.parse(ips.get(i).get(1));
					if (Duration.between(stamp, LocalDateTime.now()).getSeconds() > 4800L) {
						ips.remove(i);
					}
				}
			}
		}, 0, 5000);

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
			Boolean pedidoFallido;
			try {
				socket = serverSocket.accept();
				pedidoFallido = false;
				/*
				 * if(noSimulacro){ String ipCliente = socker.getInetAddress().getHostAddress();
				 * LocalDateTime ahora = LocalDateTime.now(); Boolean encontrado = false;
				 * for(int i = 0; i < ips.size(); i++){ if(ips.get(i).get(0).equals(ipCliente)){
				 * socket.close(); pedidoFallido = true; } } if(!encontrado){ ips.add(new
				 * ArrayList<String>(Arrays.asList(ipCliente, ahora.toString()))); } }
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

				if (Integer.parseInt(numeroPedidosMaxMes) == 0) {
					String[] pedidosArray = pedido.split(" ");
					for (String objeto : pedidosArray) {
						Integer obj = Integer.parseInt(objeto);
						if (obj < 0 || obj > 300) {
							System.out.println("Número de objetos incorrecto");
							PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
							out.println("Número de objetos incorrecto");
							out.close();
							pedidoFallido = true;
						}
					}
				}

				Connection c = null;
				String url = "jdbc:sqlite:ejemplo.db";
				String publicKey = "";
				try {
					Class.forName("org.sqlite.JDBC");
					c = DriverManager.getConnection(url);
					c.createStatement();
					Statement sentencia = c.createStatement();
					String sql = "SELECT publicKey FROM Usuarios Where id like " + id;
					ResultSet rs = sentencia.executeQuery(sql);

					while (rs.next()) {
						publicKey = rs.getString("publicKey");
					}

					c.close();
					System.out.println("Connection closed");
				} catch (Exception e) {
					System.out.println(e.toString());
				}

				byte[] publicKeyBytes = Base64.getDecoder().decode(publicKey);
				byte[] firmaBytes = Base64.getDecoder().decode(firma);

				X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
				KeyFactory keyFactory = KeyFactory.getInstance("RSA");
				PublicKey publicKey2 = keyFactory.generatePublic(spec);

				Signature sg = Signature.getInstance("SHA256withRSA");
				sg.initVerify(publicKey2);
				sg.update(pedido.getBytes());
				// Verification de firma
				if (sg.verify(firmaBytes)) {
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

				if (totalPedidos == Integer.parseInt(numeroPedidosMaxMes)
						&& Integer.parseInt(numeroPedidosMaxMes) != 0) {
					System.out.println("\n\nNumero de mes: " + numeroDeMes);
					System.out.println("Total de pedidos: " + totalPedidos);
					System.out.println("Pedidos correctos: " + pedidosCorrectos);
					System.out.println("Pedidos incorrectos: " + (totalPedidos - pedidosCorrectos));
					Float ratioMes = (float) pedidosCorrectos / (float) totalPedidos;
					System.out.println("Ratio: " + ratioMes);

					String log = "Mes: " + Meses.get(numeroDeMes) + " Anyo: " + numeroDeAnyo.toString()
							+ " Ratio Mensual: " + ratioMes.toString();

					if (ratios.size() >= 2) {
						Float p1 = ratios.get(ratios.size() - 2);
						Float p2 = ratios.get(ratios.size() - 1);

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