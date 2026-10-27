/** Objeto estructurado resultante del parsing de una línea del protocolo. */
public class Command {
    public enum Type { CONNECT, JOIN, MSG, QUIT, INVALID }

    private final Type type;
    private final String argument;
    private final String error;

    private Command(Type type, String argument, String error) {
        this.type = type; this.argument = argument; this.error = error;
    }
    public static Command valid(Type t, String arg) { return new Command(t, arg, null); }
    public static Command invalid(String error)     { return new Command(Type.INVALID, "", error); }

    public Type getType()       { return type; }
    public String getArgument() { return argument; }
    public String getError()    { return error; }
}
