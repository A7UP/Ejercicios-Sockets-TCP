import java.io.*;
import java.net.*;

/** Ejercicio 1 - Cliente: lee de System.in y muestra la respuesta del servidor. */
public class EchoClient {
    public static void main(String[] args) {
        String host = "localhost";
        int puerto = 8080;
        try (Socket socket = new Socket(host, puerto);
             BufferedReader consola = new BufferedReader(new InputStreamReader(System.in));
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println("Conectado a " + host + ":" + puerto + " (escribe CHAO para salir)");
            String linea;
            while ((linea = consola.readLine()) != null) {
                out.println(linea);
                if ("CHAO".equalsIgnoreCase(linea)) break;
                String respuesta = in.readLine();
                if (respuesta == null) { System.out.println("Servidor cerró la conexión."); break; }
                System.out.println(respuesta);
            }
        } catch (IOException e) {
            System.err.println("Error de cliente: " + e.getMessage());
        }
    }
}
