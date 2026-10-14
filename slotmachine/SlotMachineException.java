/**
 * Excepcion del simulador SlotMachine. Cada constante es el mensaje de una
 * situacion de error distinta.
 */
public class SlotMachineException extends Exception
{
    /** La maquina no tiene ruedas. */
    public static final String NO_WHEELS = "La maquina no tiene ruedas.";

    /** El numero de ruedas debe ser mayor que cero. */
    public static final String INVALID_WHEEL_COUNT = "El numero de ruedas debe ser mayor que cero.";

    /** La posicion indicada esta fuera de rango. */
    public static final String POSITION_OUT_OF_RANGE = "La posicion indicada esta fuera de rango.";

    /** El tipo de rueda no existe. */
    public static final String UNKNOWN_WHEEL_TYPE = "Tipo de rueda desconocido.";

    /** El tipo de simbolo no existe. */
    public static final String UNKNOWN_SYMBOL_TYPE = "Tipo de simbolo desconocido.";

    /** El color es nulo o no es soportado. */
    public static final String INVALID_COLOR = "Color no valido.";

    /** La rueda esta fija. */
    public static final String WHEEL_LOCKED = "La rueda esta fija.";

    /** La rueda no se deja bloquear. */
    public static final String WHEEL_CANNOT_LOCK = "Esta rueda no se deja bloquear.";

    /** La rueda no se deja intercambiar. */
    public static final String WHEEL_CANNOT_SWAP = "Esta rueda no se deja intercambiar.";

    /** La rueda no se deja eliminar. */
    public static final String WHEEL_CANNOT_DELETE = "Esta rueda no se deja eliminar.";

    /** La rueda no tiene simbolos. */
    public static final String EMPTY_WHEEL = "La rueda no tiene simbolos.";

    /** No existe un simbolo de ese color en la rueda. */
    public static final String SYMBOL_NOT_FOUND = "No existe un simbolo de ese color en la rueda.";

    /** No existe un simbolo de ese color en ninguna rueda. */
    public static final String SYMBOL_NOT_IN_ANY_WHEEL = "No existe un simbolo de ese color en ninguna rueda.";

    /** La configuracion no coincide con el numero de ruedas. */
    public static final String CONFIGURATION_MISMATCH = "La configuracion no coincide con el numero de ruedas.";

    /** La maquina no tiene solucion. */
    public static final String NO_SOLUTION = "La maquina no tiene solucion.";

    /** Se pidio simular sin haber ejecutado solve(). */
    public static final String NOT_SOLVED = "Debe ejecutar solve() antes de simulate().";

    /** La solucion encontrada ya no se puede alcanzar. */
    public static final String SOLUTION_UNREACHABLE = "La solucion no se puede alcanzar.";

    /**
     * Crea una excepcion con el mensaje dado.
     * @param message mensaje de error, normalmente una de las constantes de esta clase
     */
    public SlotMachineException(String message)
    {
        super(message);
    }
}
