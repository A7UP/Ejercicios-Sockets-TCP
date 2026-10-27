import java.util.*;
import java.util.concurrent.*;

/** Ejercicios 4 y 5 - Estado global thread-safe: usuarios, salas y broadcast. */
public class RoomManager {
    // NombreSala -> miembros (lecturas/broadcast >> escrituras)
    private final Map<String, List<ClientHandler>> salas = new ConcurrentHashMap<>();
    // username -> handler (unicidad de usuarios)
    private final Map<String, ClientHandler> usuarios = new ConcurrentHashMap<>();

    public boolean registrarUsuario(String nick, ClientHandler h) {
        return usuarios.putIfAbsent(nick, h) == null;
    }
    public void removerUsuario(String nick) {
        if (nick != null) usuarios.remove(nick);
    }

    public void unirseASala(String nombreSala, ClientHandler cliente) {
        salas.computeIfAbsent(nombreSala, k -> new CopyOnWriteArrayList<>()).add(cliente);
    }

    public void salirDeSala(String nombreSala, ClientHandler cliente) {
        if (nombreSala == null) return;
        List<ClientHandler> miembros = salas.get(nombreSala);
        if (miembros != null) miembros.remove(cliente);
    }

    public void broadcast(String nombreSala, String mensaje, ClientHandler emisor) {
        List<ClientHandler> miembros = salas.get(nombreSala);
        if (miembros != null) {
            for (ClientHandler miembro : miembros) {
                if (miembro != emisor) { // Evitar eco al propio emisor
                    miembro.enviarMensajeDirecto(mensaje);
                }
            }
        }
    }

    /** Ejercicio 5 - Notifica a todos los usuarios conectados (shutdown). */
    public void notificarATodos(String mensaje) {
        for (ClientHandler h : usuarios.values()) h.enviarMensajeDirecto(mensaje);
    }

    public Collection<ClientHandler> usuariosConectados() { return usuarios.values(); }
}
