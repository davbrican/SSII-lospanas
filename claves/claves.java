
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.security.KeyFactory;
import java.security.KeyPair;

class HelloWorldApp {
    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyPairGenerator kgen = KeyPairGenerator.getInstance("RSA");
        kgen.initialize(2048);
        KeyPair keys = kgen.generateKeyPair();
        KeyPair keys2 = kgen.generateKeyPair();
        KeyPair keys3 = kgen.generateKeyPair();
        PublicKey publicKey = keys.getPublic();
        PublicKey publicKey2 = keys2.getPublic();
        PublicKey publicKey3 = keys3.getPublic();
        PrivateKey privateKey = keys.getPrivate();
        PrivateKey privateKey2 = keys2.getPrivate();
        PrivateKey privateKey3 = keys3.getPrivate();
        String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String publicKeyString2 = Base64.getEncoder().encodeToString(publicKey2.getEncoded());
        String publicKeyString3 = Base64.getEncoder().encodeToString(publicKey3.getEncoded());
        System.out.println("public : " + publicKeyString);
        System.out.println("public : " + publicKeyString2);
        System.out.println("public : " + publicKeyString3);
        String privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        String privateKeyString2 = Base64.getEncoder().encodeToString(privateKey2.getEncoded());
        String privateKeyString3 = Base64.getEncoder().encodeToString(privateKey3.getEncoded());

        System.out.println("private : " + privateKeyString);
        System.out.println("private : " + privateKeyString2);
        System.out.println("private : " + privateKeyString3);


        //En caso de que quieras convertir de String a PublicKey

        // byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
        // X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
        // KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        // PublicKey publicKey2 = keyFactory.generatePublic(spec);
        // System.out.println(publicKey.equals(publicKey2));

        //En caso de que quieras convertir de String a PrivateKey

        // byte[] privataKeyBytes = Base64.getDecoder().decode(privateKeyString);
        // X509EncodedKeySpec spec = new X509EncodedKeySpec(privateKeyBytes);
        // KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        // privateKey privateKey2 = keyFactory.generateprivate(spec);
        // System.out.println(privateKey.equals(privateKey2));
    }
}