import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

/** Manejador de cliente del sistema JSMS (protocolo + salas + resiliencia). */
public class ClientHandler implements Runnable {
    private static final int TIMEOUT_MS = 30000; // Ejercicio 5

    private final Socket socket;
    private final RoomManager rooms;
    private final ProtocolParser parser = new ProtocolParser();
    private PrintWriter out;
    private volatile String username;
    private volatile String salaActual;

    public ClientHandler(Socket socket, RoomManager rooms) {
        this.socket = socket;
        this.rooms = rooms;
    }

    /** Envía una línea terminada en \n directamente a este cliente. */
    public void enviarMensajeDirecto(String mensaje) {
        PrintWriter w = out;
        if (w != null) w.println(mensaje);
    }

    public void cerrar() {
        try { socket.close(); } catch (IOException ignored) {}
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter writer = new PrintWriter(
                     new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            this.out = writer;
            socket.setSoTimeout(TIMEOUT_MS);
            socket.setKeepAlive(true);

            String linea;
            while ((linea = in.readLine()) != null) {          // null = EOF
                Command cmd = parser.parse(linea);
                if (!procesar(cmd)) break;
            }
        } catch (SocketTimeoutException e) {
            System.err.println("Inactividad de " + username + ". Cerrando por timeout.");
            enviarMensajeDirecto("408 TIMEOUT Conexion cerrada por inactividad");
        } catch (SocketException e) {
            System.err.println("Cliente " + username + " cerró abruptamente: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error de E/S con " + username + ": " + e.getMessage());
        } finally {
            limpiar();
        }
    }

    /** @return false si la sesión debe terminar. */
    private boolean procesar(Command cmd) {
        switch (cmd.getType()) {
            case CONNECT:
                if (username != null) { out.println("401 ERROR Ya estas conectado como " + username); break; }
                if (rooms.registrarUsuario(cmd.getArgument(), this)) {
                    username = cmd.getArgument();
                    out.println("200 OK");
                } else {
                    out.println("401 ERROR Username en uso");
                }
                break;
            case JOIN:
                if (username == null) { out.println("401 ERROR Debes hacer CONNECT primero"); break; }
                rooms.salirDeSala(salaActual, this);
                salaActual = cmd.getArgument();
                rooms.unirseASala(salaActual, this);
                out.println("JOINED " + salaActual);
                break;
            case MSG:
                if (username == null) { out.println("401 ERROR Debes hacer CONNECT primero"); break; }
                if (salaActual == null) { out.println("ERROR Debes hacer JOIN a una sala primero"); break; }
                out.println("MSG " + username + ": " + cmd.getArgument());
                rooms.broadcast(salaActual, "BROADCAST [" + username + "]: " + cmd.getArgument(), this);
                break;
            case QUIT:
                out.println("BYE");
                return false;
            default: // INVALID: error descriptivo sin tumbar el socket
                out.println("ERROR " + cmd.getError());
        }
        return true;
    }

    /** Libera salas y registro de usuarios (evita mensajes fantasma). */
    private void limpiar() {
        rooms.salirDeSala(salaActual, this);
        rooms.removerUsuario(username);
        cerrar();
        System.out.println("Sesión liberada: " + (username != null ? username : "(anónimo)"));
    }
}
