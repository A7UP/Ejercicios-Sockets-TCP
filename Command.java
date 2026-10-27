import java.io.*;
import java.net.*;

/** Cliente CLI con recepción asíncrona: un hilo escucha al servidor, el principal lee System.in. */
public class ChatClient {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int puerto = args.length > 1 ? Integer.parseInt(args[1]) : 8080;

        try (Socket socket = new Socket(host, puerto);
             BufferedReader consola = new BufferedReader(new InputStreamReader(System.in));
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println("Conectado. Comandos: CONNECT <user> | JOIN <sala> | MSG <texto> | QUIT");

            Thread receptor = new Thread(() -> {
                try {
                    String linea;
                    while ((linea = in.readLine()) != null) System.out.println(linea);
                } catch (IOException e) {
                    // socket cerrado
                }
                System.out.println("[Conexión cerrada por el servidor]");
                System.exit(0);
            }, "receptor");
            receptor.setDaemon(true);
            receptor.start();

            String linea;
            while ((linea = consola.readLine()) != null) {
                out.println(linea);
                if ("QUIT".equalsIgnoreCase(linea.trim())) break;
            }
            Thread.sleep(300); // deja llegar el "BYE"
        } catch (IOException | InterruptedException e) {
            System.err.println("Error de cliente: " + e.getMessage());
        }
    }
}
