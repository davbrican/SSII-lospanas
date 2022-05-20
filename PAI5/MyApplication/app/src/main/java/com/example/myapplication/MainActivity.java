package com.example.myapplication;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Hashtable;
import java.util.Random;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;



public class MainActivity extends AppCompatActivity {

    // Setup Server information
    protected static String server = "10.0.2.2";
    protected static int port = 5350;
    protected static final Pair<Integer, String> UsuarioN1 = new Pair<>(1, "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC6lLSAVn2u8BdDHBDAv+/7xUvwekK+Wd2LKiMW5k7mqO9JA3xpuXrO6ocGHhKS5Xu9fP5LztRgeGk4kGXMYzBF1x9DXoHCfcQ21RUZO2ERUg+OA3dRBNct287XX535FUO+yi3ItaxBUSaLdxHWS+tsSN3tWlGeTGm3gzJ+5Kb44YN+tUZbzzGRHYECMewTsDr4RD0fFEiijH84LdGDHIvQf/4CLQUHNicYcVgtBCfKCw/5hVW/90w3NB1zKdHK5Ln6UriKJsUFAmOT8kyi7bgvYReFis++rdEzX+G27THQzFsgyLGFTmmrIMNJiZdIHBv5GCIJYjdCDoPDiX4h7w3RAgMBAAECggEAAtMkR0f38RLLDrMsCO0G/OVJahTscdhnX3dgzrSSgnu8MVtAsj2ShPOISIuNQq/VKiLhsI5gs6wIWh4WinarI7g3GGnOgLOEfwOGGqAQucadJsG5mAPg9Eg+XGSi/hhy3Sk6B6Ps0r7VUhnNrsnEC6Xv7+C047basrfFPJD+kFNGvLCDs6wokse0Bcj22jsxr+gtMwMF25XdHFVW6rskOV1MUALrapJ7jgC2eauln8y5Ju6N5O36ThoQhbNbBXrxDRhY6jtrIBie3W3A5djAkNMy095qCVwPPVpZeu340yt0eDBlBcvQZqjcFAzqdf8ZPUcmYB2xQRo+vlt0CnaDuQKBgQDFC8DLAaes7MJtqhlhJ+fbU2cNaM0aN5PcAsD3BN4Rnz8TveUu9xQWbnr9thX0mNuBTFY881hI3Vn2DMOTJar/7XAs3PM8U8c+5E6MD2VI5A5kJq9WN33Kj5GqCufqV9iYIk6csfElPPzq3YOvuM16urvJ6RDIqCqxLIy8SbvSqQKBgQDyZ2idUXuIqDKTasMS0g5AP4iK1nuXUGoOx8iefM9XFziXc5kNhs/H4yOsDuY2LEpp4Xl94Qa3N6gL3/8tQuaeKj0CBLAzUcHrtPO5+J+f4IOLIsO4HGZ5Bok14//soIRO7XC0w6r2qx0bpWZM+wAXQu0MaCgOCjipZmX5+P8C6QKBgQCiQZFaGVzHapaX0y/e7wtfcYg0ZI4v/oAE9UeMoTdz71vzl7U3PUIPTp58eQoPRifKit+ghQm3xn4jvFL3wlbTM+PhLXglvx8czMdZUfwnT5QCKFDNgBhXSm26Rgy+zeOPMwDkyyo1bckeZZXghx685zNnikHF2aR+DJ8/FMBTMQKBgQDPLVy3YKYRpZgccNorYsb1WJfN+gUFzZ/n5A7ujkPz7o/aud0tN3StJdAeRVmzIWW0WxaoZMSFoZcKFzeUqqJKulUMPY+PPShd2XmaEAn17kDfkSXwXK2kcNai7ayVxJdwkfWLOdUMIDGvqEzoLHrmZrgNq5PYdBn7ht5PqOXjyQKBgGWI59NYWy/y1Vq0bMy1oVBkMpR5DJpXLFSGJXCrQ/tTdCVyfmdTQNiOs65XgDei5SJXy6X7nR+Ofd/zXYkripks0+3L1+x3+b4lsqqxAkQXJzcTiUS8WvDxaBL8Px1zeJ0nfrXDXhTgh71xlE+HLeMbqMJcDflM+0ZPn6nflckM");
    protected static final Pair<Integer, String> UsuarioN2 = new Pair<>(2, "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQC8E2ZaPGk2Cq4EF/hfh9MJX/nLi4J9TiGO+fC5cTGy7uGbFsMKeceRU2LE3Ktjpx89MSVh49C3L/C3I6CCvZ43SWPzx9fb2P44j6NzCOgPhdjOv5bhMgO092y8rEH1cEjiRIWB40K1zBseE7dVxchQmcUnyLOhhGIrgquZe7mzOCq3TdFGuX2UqBBXerWDehEAi72KL9mh8D0+HJgFgH/+GGlRg9gT28tj3p90M9yZqEV4rFK9Zi7p/9wk8SJrqYVd987Rq03cHc+MnCUBAvCxE8Mi7TAugpLgWxyhC1osaCp/CN/GnTe9+/zONk3/yrrHNsV/uNHjlJDmZOdyT5ipAgMBAAECggEADcpJBvTHAh9mWFvUP6zwhw9yfBTj9yweFHhfHjnJMHAzj/DrUX6nE2YkfSrGUQLqrMydtmpt3gC14GJ3B8m/gBjxgJPL+rgTwTHxr3hHtluqD1Qc9rDOyhtvmFCecCX7h3EJbldWMjOftA+IsdFUDr+M+o3MyLBcQl8ia0Psfyd5SSBauleuM6zTK1wJ6yOEXdVOld9Jp/5WcPwiAkeOjzP45Pd9Rop9y2oESFXlzwEHTWcfTn599cQigbOypOCCTAtRjPX3hkK+U1M6wEr80gER6+olQlwyJbYzXpiXUWJhHL8uVL5YAlaSHviwx6jAU2dpbRVKoAcKVP/+yXc4wQKBgQC9dbGk9eAHziXFh810yGZiXV1X3jvyfH9NsMOYNZ+W1QCb+5EWg3qoo54kkF7sQPPDYZN+D7dVBieV3ZLwjW7Ft9VtgxG3O78CV8nv0BdJLev+WFyzm4eoBgq5GUynzAFg0p9OWHnRDwJ+UYdNIhaNVfCY5SWxAqguaPfPkVeNvwKBgQD+IUYpMFSZ0rUSHiN0D1DL1KwsQBjdYdGfdsOsFCDLiP5eDJ9jDdWugwg9G9TA64UBv2KYbZZSIj3WBWdW6pAsXqkvbXQg6fscuSRsaJaRy2+6cGvFf0w44vYezLghgfr4UTEBKvoXYA/8oQtbxMLPmcbE8QSnRbNKjMmEeu5DlwKBgF/mNB5Qyq6IvXvH6sqDKbf+lGIGvodWV0XnBIqGEhrSBHXwF5eyw6Ka91CAt1uU4Q5z1KmJoP3rmJv2RQt4O6rfC6xcHNqH5n50G2ZFCZRkJ0FeTsYnIrp9HG5nDPMeg1AULMkGZdrZYyS3deooKAwwhRGPY82+j4y2W0F4yz2XAoGAezJwS+l1Kfke9MCNmWcPWTEpom4UX8ZbE/5ET7iWSXWJMtjVr/R2AZreNJm4YyoKGdXJG3IM7JZS+d02wskFyay+QhLCuG4V4U/T24Y8cEN6T5zOcjkLH4zPmhDOttfHbfgWVKWcBhb4yRTdZ8iUVIYM+U6KXfkJOPvVeZcKyNMCgYBoza2ysac8VaBidA4+gj5Ygl0kFdmyKiuf+CVQaXXkV7gJc7ElIDfFDAaDNjD1fbjSea8eDTDrD0acQniUOFWA7Vtxdb4nOGI62rb9qGgHMp6cCjfpfiey8BHow21pXPbVD6KscmNg3Dcy9pIG1DUpiwJaK4HL2SInJxnRWCAhVw==");
    protected static final Pair<Integer, String> UsuarioN3 = new Pair<>(3, "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDZFBBi0BqwC/xQq52JEWBx9mf5TevQKMEPX1uT+JsKwSBpe24XuPHCdtUS5gWK1mmj7JV6YHo25HZy2HpemUnLDDlM/UdJJl0ZhJbE+w4j3d8ZJQ2nSf97lP6kYED+V09nrTUNlsAmIJX3c/TQD38KIU9CwACLqhhUeR5Mh980AWK8arvz5ZgL32Jq9EB8Zz4W9/TfpOn0xNnoRuEXWVY5QbzJz7vzyxhpg6ae26A5YzJaAoJDRTbqJb7QMRTJRNFVDneiu5UQc7pcsj3rZYT76U7ZpUy+qefVuxmLveYDl92JYjcQJvbmYgs/1HcwrwsfCjexZaDQYV0i1gOqZAzhAgMBAAECggEAAoAzrB+/nvWUY42GlBSqKsXu7rVSGSTPDJFJqcu8ZYRSDkUIwKc1ycq6q6wQtkfAEc5g7nb8EXPSVYJe7oYPjpJGereUQah6fBfF8OD/ibQ9pLmDjcDRe6f8wwIE3ak91fDt4okxD39imuYwFNGI4iH+GsPBfdpoy1hV3AXYsW2pOaUCfnXCoNgUtIxOrMvbg80mkBONOIt8d9JcD3mehBWm5y5OQ3CY+2LQwQwBzABNPLZQdxIyTNXe2YxUNAJzmIkiPwf+BDeONfvF1b9QWzYdbOUFd/7XwEXiwYf96X5CbdXsNX8YLTmNV3vHqIqPPZTEP0qc2D3n/8jcBHVGzQKBgQDxgm1LuejCNvtAVsEtONrc1++robvcvdnOBmXCMCF8+3u6sgm1WzHnz7hng5HxpHxz2OeXwICqpC73yNv+MHx1Fxt8HF4DbhNmGu4X75o7GGWuX2kHfCnvF4quVrjfdTnXeX4/CGsmWh68PIWa01Q53aL5LadOPX9FXIzbXUnd9wKBgQDmGmBmkr0PROJAYdfJ/S4DGYI6lbEA+aUxaj6+qZ2S0Ij6sJ/mIaGGDXyIu2lEycNmVzZSIuzFkNdZtIm6l+HssPyM7mFIUtbcCteJfqAw9VNDOq1lzhefhwscZpwaDtFVtkGJsH0yboQ38aKQTa77QLkE2kEok/weqSxnQtSV5wKBgQCkwLkWw8iJVUCpb4Vw+Cw2JAkYKMkjmVAQQEUC6BqwTE7n2bTNx1yQKyA9XYAePHu5++phl60uu7pexuNs0F7W8eCKFj/8TwdkzFJIeefZEJetEOFxfb6NoJ22uOp9ZlcDK5p4HaIbE7eL6i2qpSf8IbqgCgjsUv+TrcNZkpZlmQKBgGV4FDKnWr+0/KCvhN0JzJSJVyhGgnuPmw0jcO/bFCV92CnUYW62PehDYjtZiZ5P6t7ibo3h4M9ug8iHGIU0HOinU3dCV5vxC9aU4V88e5+bT1BCO9y8+SXcA0ZO6V+EUUOez1/MeCkZGy0gXTONTjB15iEBreIa//71UyNmESDZAoGBAIXhigFXhzDTDDrzQS6nzAAqZAMKFN8MIfRdJw+jXg5RUy9aMh10i5Srx+qhixlejoVtjM4dg0wslx3KTvWROoxt2HYW+VooykZ+nzPzj8RS1AdyjOFZBoH0ihVkgSBxlkMNtoQde2qA51LuA2TV5GMoWhH46InoVgbehmQshf4g");

    private static final Hashtable<String, Pair<Integer, String>> UsuariosID = new Hashtable<String, Pair<Integer, String>>(){{
       put("Usuario 1", UsuarioN1);
       put("Usuario 2", UsuarioN2);
       put("Usuario 3", UsuarioN3);
    }};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) { StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); StrictMode.setThreadPolicy(policy); }

        // Capturamos el boton de Enviar
        View button = findViewById(R.id.button_send);

        // Llama al listener del boton Enviar
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String pedidoDePrueba(String pedido, Spinner usuario) {
        String Nusuario = usuario.getSelectedItem().toString();
        final Pair UsuarioIdClave = UsuariosID.get(Nusuario);
        byte[] privateKeyBytes = Base64.getDecoder().decode(UsuarioIdClave.second.toString());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory;
        byte[] pedidoFirmado = new byte[0];
        String pruebaPedido = new String();

        try {
            keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(spec);
            Signature sg = Signature.getInstance("SHA256withRSA");
            sg.initSign(privateKey);
            sg.update(pedido.getBytes());
            // Firma
            pedidoFirmado = sg.sign();
            pruebaPedido = new String(Base64.getEncoder().encode(pedidoFirmado));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }

        return pruebaPedido;
    }

    // Creación de un cuadro de dialogo para confirmar pedido
    private void showDialog() throws Resources.NotFoundException {

        EditText camas = (EditText) findViewById(R.id.camas);
        EditText mesas = (EditText) findViewById(R.id.mesas);
        EditText sillas = (EditText) findViewById(R.id.sillas);
        EditText sillones = (EditText) findViewById(R.id.sillones);
        Spinner usuario = (Spinner) findViewById(R.id.usuario);
        Switch simularMes = (Switch) findViewById(R.id.switch1);
        Boolean switchState = simularMes.isChecked();

        Boolean condicion = TextUtils.isEmpty(camas.getText().toString()) || TextUtils.isEmpty(mesas.getText().toString()) || TextUtils.isEmpty(sillas.getText().toString()) || TextUtils.isEmpty(sillones.getText().toString());

        if(condicion){
            // Mostramos un mensaje emergente;
            Toast.makeText(getApplicationContext(), "Todos los campos deben estar rellenos", Toast.LENGTH_SHORT).show();
        }else {

            final Integer nCamas = Integer.parseInt(camas.getText().toString());
            final Integer nMesas = Integer.parseInt(mesas.getText().toString());
            final Integer nSillas = Integer.parseInt(sillas.getText().toString());
            final Integer nSillones = Integer.parseInt(sillones.getText().toString());


            Boolean condicion2 = 0 > nCamas || nCamas > 300 || 0 > nMesas || nMesas > 300 || 0 > nSillas || nSillas > 300 || 0 > nSillones || nSillones > 300;

            if (condicion2) {
                // Mostramos un mensaje emergente;
                Toast.makeText(getApplicationContext(), "Las cantidades deben estar comprendidas entre 0 y 300", Toast.LENGTH_SHORT).show();

            } else {
                String Nusuario = usuario.getSelectedItem().toString();
                final Pair UsuarioIdClave = UsuariosID.get(Nusuario);
                new AlertDialog.Builder(this)
                        .setTitle("Enviar")
                        .setMessage("Se va a proceder al envio")
                        .setIcon(R.drawable.ic_launcher_background)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {


                                    // Catch ok button and send information
                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                    public void onClick(DialogInterface dialog, int whichButton) {

                                        // 1. Pedido
                                        String pedido = nCamas +" "+ nMesas+ " "+ nSillas+ " "+ nSillones;
                                        // 2. Firmar los datos
                                        String pruebaPedido = pedidoDePrueba(pedido, usuario);
                                        // 3. Enviar los datos
                                        try {
                                            if (switchState) {
                                                int random_int = (int)Math.floor(Math.random()*(30-15+1)+15);
                                                Log.d("Random_int: ", String.valueOf(random_int));
                                                int i = 0;
                                                while(i<random_int) {
                                                    Socket socket = new Socket(server, port);
                                                    String id = UsuarioIdClave.first.toString();
                                                    int maxPed = 300;
                                                    int minPed = 0;
                                                    int nCam = (int)Math.floor(Math.random()*(maxPed-minPed+1)+minPed);
                                                    int nMes = (int)Math.floor(Math.random()*(maxPed-minPed+1)+minPed);
                                                    int nSil = (int)Math.floor(Math.random()*(maxPed-minPed+1)+minPed);
                                                    int nSill = (int)Math.floor(Math.random()*(maxPed-minPed+1)+minPed);
                                                    pedido = nCam +" "+ nMes+ " "+ nSil+ " "+ nSill;
                                                    Log.d("pedido: ", pedido);
                                                    boolean randomError = ((int)Math.floor(Math.random()*(100-0+1)+0) <= 7);
                                                    Log.d("randomError: ", String.valueOf(randomError));
                                                    Log.d("id",id);
                                                    if (randomError) {
                                                        if (id.equals("1")) id = "2";
                                                        else if (id.equals("2")) id = "3";
                                                        else if (id.equals("3")) id = "1";
                                                    }
                                                    pruebaPedido = pedidoDePrueba(pedido, usuario);
                                                    String enviar = pedido + "campo:"+id +"campo:"+ pruebaPedido+"campo:"+random_int;
                                                    Log.d("enviar: ", enviar);
                                                    socket.getOutputStream().write(enviar.getBytes());
                                                    socket.close();
                                                    i++;
                                                }
                                            } else {
                                                Socket socket = new Socket(server, port);
                                                String id = UsuarioIdClave.first.toString();
                                                String enviar = pedido + "campo:"+id +"campo:"+ pruebaPedido+"campo:0";
                                                socket.getOutputStream().write(enviar.getBytes());
                                                
                                                socket.close();
                                            }

                                        } catch (Exception e) {
                                            Log.i("probando",e.toString());
                                        }
                                        Toast.makeText(MainActivity.this, "Petición enviada correctamente", Toast.LENGTH_SHORT).show();
                                    }
                                }

                        )
                                .

                        setNegativeButton(android.R.string.no, null)

                                .

                        show();
            }
        }
    }



}