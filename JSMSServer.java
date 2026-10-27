/**
 * Ejercicio 3 - Analiza una línea de texto y devuelve un Command.
 * Si la sintaxis es inválida devuelve un Command INVALID con mensaje descriptivo
 * (nunca lanza excepción, por lo que el socket no se cae).
 */
public class ProtocolParser {

    public Command parse(String linea) {
        if (linea == null || linea.trim().isEmpty())
            return Command.invalid("Línea vacía. Comandos: CONNECT <username>, JOIN <sala>, MSG <texto>, QUIT");

        String[] partes = linea.trim().split("\\s+", 2);
        String comando = partes[0].toUpperCase();
        String arg = partes.length > 1 ? partes[1].trim() : "";

        switch (comando) {
            case "CONNECT":
                if (arg.isEmpty()) return Command.invalid("Sintaxis: CONNECT <username>");
                if (arg.contains(" ")) return Command.invalid("El username no puede contener espacios");
                return Command.valid(Command.Type.CONNECT, arg);
            case "JOIN":
                if (arg.isEmpty()) return Command.invalid("Sintaxis: JOIN <nombre_sala>");
                if (arg.contains(" ")) return Command.invalid("El nombre de sala no puede contener espacios");
                return Command.valid(Command.Type.JOIN, arg);
            case "MSG":
                if (arg.isEmpty()) return Command.invalid("Sintaxis: MSG <texto>");
                return Command.valid(Command.Type.MSG, arg);
            case "QUIT":
                return Command.valid(Command.Type.QUIT, "");
            default:
                return Command.invalid("Comando no reconocido: " + comando);
        }
    }
}
