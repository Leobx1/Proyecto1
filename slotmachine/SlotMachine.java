import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SlotMachine
{
    private ArrayList<Wheel> wheels;
    private boolean visible;
    private boolean ok;
    private String[] solution;
    private static final int MAX_SPIN_ATTEMPTS = 1000;

    /** Crea una maquina tragamonedas vacia. */
    public SlotMachine()
    {
        wheels = new ArrayList<>();
        visible = false;
        ok = true;
        solution = null;
    }

    /**
     * Crea una maquina con n ruedas normales y n simbolos normales de colores variados.
     * @throws SlotMachineException INVALID_WHEEL_COUNT si n no es mayor que cero
     */
    public SlotMachine(int n) throws SlotMachineException
    {
        this(n, "normal", "normal");
    }

    /**
     * Crea una maquina con n ruedas del tipo dado y n simbolos del tipo dado por rueda,
     * de colores variados. Tipos de rueda: normal, lefty, rebel.
     * Tipos de simbolo: normal, ephemeral, shy, contagious.
     * @throws SlotMachineException INVALID_WHEEL_COUNT, UNKNOWN_WHEEL_TYPE o UNKNOWN_SYMBOL_TYPE
     */
    public SlotMachine(int n, String wheelType, String symbolType) throws SlotMachineException
    {
        if (n <= 0) throw new SlotMachineException(SlotMachineException.INVALID_WHEEL_COUNT);
        wheels = new ArrayList<>();
        visible = false;
        ok = true;
        solution = null;

        String[] availableColors = {"red", "blue", "green", "magenta"};

        for (int i = 1; i <= n; i++) {
            addWheel(wheelType, i);
            for (int j = 0; j < n; j++) {
                String color = availableColors[(int)(Math.random() * availableColors.length)];
                addSymbol(symbolType, i, j + 1, color);
            }
            wheels.get(i - 1).randomize();
        }
    }

    /** Marca la ultima operacion como fallida y crea la excepcion con el mensaje dado. */
    private SlotMachineException fail(String message)
    {
        ok = false;
        return new SlotMachineException(message);
    }

    /** Agrega una rueda en la posicion pos. */
    public void addWheel(int pos) throws SlotMachineException
    {
        addWheel("normal", pos);
    }

    /**
     * Agrega una rueda del tipo dado (normal, lefty, rebel) en la posicion pos.
     * @throws SlotMachineException UNKNOWN_WHEEL_TYPE si el tipo no existe
     */
    public void addWheel(String type, int pos) throws SlotMachineException
    {
        int p = clamp(pos, 1, wheels.size() + 1);
        Wheel w = createWheel(type, p, wheels.size() + 1);
        if (w == null) {
            throw fail(SlotMachineException.UNKNOWN_WHEEL_TYPE);
        }
        wheels.add(p - 1, w);
        repositionWheels();
        if (visible) w.makeVisible();
        ok = true;
    }

    /**
     * Elimina la rueda en la posicion pos.
     * @throws SlotMachineException NO_WHEELS si no hay ruedas, WHEEL_CANNOT_DELETE si la rueda no se deja eliminar
     */
    public void delWheel(int pos) throws SlotMachineException
    {
        if (wheels.isEmpty()) {
            throw fail(SlotMachineException.NO_WHEELS);
        }
        int p = clamp(pos, 1, wheels.size());
        Wheel w = wheels.get(p - 1);
        if (!w.canDelete()) {
            throw fail(SlotMachineException.WHEEL_CANNOT_DELETE);
        }
        wheels.remove(p - 1);
        if (visible) w.makeInvisible();
        repositionWheels();
        ok = true;
    }

    /**
     * Agrega un simbolo del color dado en la posicion pos de la rueda especificada.
     * @throws SlotMachineException NO_WHEELS o INVALID_COLOR
     */
    public void addSymbol(int wheel, int pos, String color) throws SlotMachineException
    {
        addSymbol("normal", wheel, pos, color);
    }

    /**
     * Agrega un simbolo del tipo dado (normal, ephemeral, shy, contagious) en la posicion pos de la rueda.
     * @throws SlotMachineException NO_WHEELS, UNKNOWN_SYMBOL_TYPE o INVALID_COLOR
     */
    public void addSymbol(String type, int wheel, int pos, String color) throws SlotMachineException
    {
        if (wheels.isEmpty()) {
            throw fail(SlotMachineException.NO_WHEELS);
        }
        int w = clamp(wheel, 1, wheels.size());
        Wheel targetWheel = wheels.get(w - 1);
        try {
            targetWheel.addSymbol(type, pos, color);
        } catch (SlotMachineException e) {
            ok = false;
            throw e;
        }
        ok = true;
        updateFrameColors();
    }

    /**
     * Elimina el simbolo del color dado en todas las ruedas que lo tengan.
     * @throws SlotMachineException SYMBOL_NOT_IN_ANY_WHEEL si ninguna rueda tiene ese color
     */
    public void delSymbol(String symbol) throws SlotMachineException
    {
        ok = false;
        for (Wheel w : wheels) {
            try {
                w.delSymbol(symbol);
                ok = true;
            } catch (SlotMachineException e) {
                // esta rueda no tiene el color: se sigue con las demas
            }
        }
        if (!ok) throw fail(SlotMachineException.SYMBOL_NOT_IN_ANY_WHEEL);
    }

    /**
     * Ubica el simbolo dado como el actual en la rueda indicada.
     * @throws SlotMachineException NO_WHEELS o SYMBOL_NOT_FOUND
     */
    public void placeSymbol(int wheel, String symbol) throws SlotMachineException
    {
        if (wheels.isEmpty()) {
            throw fail(SlotMachineException.NO_WHEELS);
        }
        int idx = clamp(wheel, 1, wheels.size());
        Wheel w = wheels.get(idx - 1);
        try {
            w.placeSymbol(symbol);
        } catch (SlotMachineException e) {
            ok = false;
            throw e;
        }
        ok = true;
    }

    /**
     * Gira la rueda indicada.
     * @throws SlotMachineException NO_WHEELS, WHEEL_LOCKED si la rueda esta fija, o EMPTY_WHEEL
     */
    public void spin(int wheel) throws SlotMachineException
    {
        if (wheels.isEmpty()) {
            throw fail(SlotMachineException.NO_WHEELS);
        }
        int idx = clamp(wheel, 1, wheels.size());
        Wheel w = wheels.get(idx - 1);
        if (w.isLocked()) {
            throw fail(SlotMachineException.WHEEL_LOCKED);
        }
        try {
            w.spin();
        } catch (SlotMachineException e) {
            ok = false;
            throw e;
        }
        ok = true;
        updateFrameColors();
    }

    /**
     * Gira todas las ruedas que no esten fijas.
     * @throws SlotMachineException NO_WHEELS, o EMPTY_WHEEL si alguna rueda girada no tiene simbolos
     *         (las demas ruedas giran de todos modos)
     */
    public void spin() throws SlotMachineException
    {
        if (wheels.isEmpty()) {
            throw fail(SlotMachineException.NO_WHEELS);
        }
        ok = true;
        SlotMachineException pending = null;
        for (Wheel w : wheels) {
            if (!w.isLocked()) {
                try {
                    w.spin();
                } catch (SlotMachineException e) {
                    ok = false;
                    if (pending == null) pending = e;
                }
            }
        }
        updateFrameColors();
        if (pending != null) throw pending;
    }

    /**
     * Gira la rueda indicada un numero de pasos.
     * @throws SlotMachineException NO_WHEELS, WHEEL_LOCKED si la rueda esta fija, o EMPTY_WHEEL
     */
    public void spin(int wheel, int steps) throws SlotMachineException
    {
        if (wheels.isEmpty()) {
            throw fail(SlotMachineException.NO_WHEELS);
        }
        int idx = clamp(wheel, 1, wheels.size());
        Wheel w = wheels.get(idx - 1);
        if (w.isLocked()) {
            throw fail(SlotMachineException.WHEEL_LOCKED);
        }
        try {
            w.spinSteps(steps);
        } catch (SlotMachineException e) {
            ok = false;
            throw e;
        }
        ok = true;
        updateFrameColors();
    }

    /**
     * Deja la maquina en la configuracion dada (un color por rueda, de izquierda a derecha).
     * Es todo o nada: si falla, ninguna rueda cambia.
     * @throws SlotMachineException NO_WHEELS, CONFIGURATION_MISMATCH o SYMBOL_NOT_FOUND
     */
    public void spin(String[] setSymbols) throws SlotMachineException
    {
        if (wheels.isEmpty()) {
            throw fail(SlotMachineException.NO_WHEELS);
        }
        if (setSymbols == null || setSymbols.length != wheels.size()) {
            throw fail(SlotMachineException.CONFIGURATION_MISMATCH);
        }
        for (int i = 0; i < wheels.size(); i++) {
            if (!wheels.get(i).hasColor(setSymbols[i])) {
                throw fail(SlotMachineException.SYMBOL_NOT_FOUND);
            }
        }
        for (int i = 0; i < wheels.size(); i++) {
            wheels.get(i).placeSymbol(setSymbols[i]);
        }
        ok = true;
        updateFrameColors();
    }

    /**
     * Intercambia dos ruedas de posicion.
     * @throws SlotMachineException NO_WHEELS, WHEEL_LOCKED si alguna esta fija,
     *         o WHEEL_CANNOT_SWAP si alguna es rebelde
     */
    public void swap(int wheel1, int wheel2) throws SlotMachineException
    {
        if (wheels.isEmpty()) {
            throw fail(SlotMachineException.NO_WHEELS);
        }
        int i1 = clamp(wheel1, 1, wheels.size()) - 1;
        int i2 = clamp(wheel2, 1, wheels.size()) - 1;
        Wheel w1 = wheels.get(i1);
        Wheel w2 = wheels.get(i2);
        if (w1.isLocked() || w2.isLocked()) {
            throw fail(SlotMachineException.WHEEL_LOCKED);
        }
        if (!w1.canSwap() || !w2.canSwap()) {
            throw fail(SlotMachineException.WHEEL_CANNOT_SWAP);
        }
        wheels.set(i1, w2);
        wheels.set(i2, w1);
        repositionWheels();
        ok = true;
        updateFrameColors();
    }

    /**
     * Fija una rueda para que no gire con spin().
     * @throws SlotMachineException NO_WHEELS o WHEEL_CANNOT_LOCK si la rueda no se deja bloquear
     */
    public void lock(int wheel) throws SlotMachineException
    {
        if (wheels.isEmpty()) {
            throw fail(SlotMachineException.NO_WHEELS);
        }
        int idx = clamp(wheel, 1, wheels.size());
        Wheel w = wheels.get(idx - 1);
        try {
            w.lock();
        } catch (SlotMachineException e) {
            ok = false;
            throw e;
        }
        ok = true;
    }

    /**
     * Suelta una rueda previamente fijada.
     * @throws SlotMachineException NO_WHEELS si no hay ruedas
     */
    public void unlock(int wheel) throws SlotMachineException
    {
        if (wheels.isEmpty()) {
            throw fail(SlotMachineException.NO_WHEELS);
        }
        int idx = clamp(wheel, 1, wheels.size());
        wheels.get(idx - 1).unlock();
        ok = true;
    }

    /** Retorna los colores de los simbolos de la primera rueda. */
    public String[] symbols()
    {
        if (wheels.isEmpty()) return new String[0];
        return wheels.get(0).symbols();
    }

    /** Retorna la cantidad de simbolos distintos en la primera rueda. */
    public int distinctSymbols()
    {
        Set<String> distinct = new HashSet<>();
        for (String color : symbols()) distinct.add(color);
        return distinct.size();
    }

    /** Retorna los colores actualmente visibles en todas las ruedas, de izquierda a derecha. */
    public String[] configuration()
    {
        String[] config = new String[wheels.size()];
        for (int i = 0; i < wheels.size(); i++) {
            config[i] = wheels.get(i).currentSymbol();
        }
        return config;
    }

    /** Indica si la configuracion actual es ganadora. */
   public boolean isJackpot()
    {
        return isJackpotCheck();
    }

    /**
     * Resuelve el problema de la maraton encontrando una configuracion ganadora (invisible).
     * @return true si encontro una solucion
     * @throws SlotMachineException NO_WHEELS si no hay ruedas, NO_SOLUTION si ningun color esta en todas las ruedas
     */
    public boolean solve() throws SlotMachineException
    {
        if (wheels.isEmpty()) {
            throw fail(SlotMachineException.NO_WHEELS);
        }
    
        makeInvisible();
    
        String[] firstWheelSymbols = wheels.get(0).symbols();
        String targetColor = null;
    
        for (String color : firstWheelSymbols) {
            boolean existsInAll = true;
            for (int i = 1; i < wheels.size(); i++) {
                if (!wheels.get(i).hasColor(color)) {
                    existsInAll = false;
                    break;
                }
            }
            if (existsInAll) {
                targetColor = color;
                break;
            }
        }
    
        if (targetColor == null) {
            throw fail(SlotMachineException.NO_SOLUTION);
        }
    
        solution = new String[wheels.size()];
        java.util.Arrays.fill(solution, targetColor);   // guarda el color encontrado, nada mas
    
        ok = true;
        return true;   // encontro una solucion (no significa que ya este ganada)
    }

    /**
     * Simula la solucion encontrada por solve(), mostrando la maquina.
     * @throws SlotMachineException NOT_SOLVED si no se ejecuto solve(); SOLUTION_UNREACHABLE si una
     *         rueda fija tiene otro color, el color objetivo desaparecio de una rueda o no se logra
     *         alcanzar tras varios intentos
     */
    public boolean simulate() throws SlotMachineException
    {
        if (solution == null) {
            throw fail(SlotMachineException.NOT_SOLVED);
        }

        for (int i = 0; i < wheels.size(); i++) {
            Wheel w = wheels.get(i);
            if (!w.hasColor(solution[i])
                || (w.isLocked() && !solution[i].equals(w.currentSymbol()))) {
                throw fail(SlotMachineException.SOLUTION_UNREACHABLE);
            }
        }

        makeVisible();

        for (int i = 0; i < wheels.size(); i++) {
            Wheel w = wheels.get(i);
            int attempts = 0;
            while (!solution[i].equals(w.currentSymbol())) {
                if (attempts++ >= MAX_SPIN_ATTEMPTS || !w.hasColor(solution[i])) {
                    throw fail(SlotMachineException.SOLUTION_UNREACHABLE);
                }
                spin(i+1);
            }
            updateFrameColors();   
        }

        ok = true;
        return isJackpotCheck();
    }


    /** Hace visible el simulador. */
    public void makeVisible()
    {
        visible = true;
        for (Wheel w : wheels) w.makeVisible();
        ok = true;
        updateFrameColors();
    }

    /** Hace invisible el simulador. */
    public void makeInvisible()
    {
        visible = false;
        for (Wheel w : wheels) w.makeInvisible();
        ok = true;
    }

    /** Termina el simulador. */
    public void exit()
    {
        makeInvisible();
        ok = true;
    }

    /** Indica si la ultima operacion se realizo con exito. */
    public boolean ok()
    {
        return ok;
    }

    /** Crea una rueda segun su tipo, o retorna null si el tipo no existe. */
    private Wheel createWheel(String type, int position, int totalWheels)
    {
        if (type == null) return null;
        String t = type.toLowerCase();
        if (t.equals("normal")) return new NormalWheel(position, totalWheels);
        if (t.equals("lefty"))  return new LeftyWheel(position, totalWheels);
        if (t.equals("rebel"))  return new RebelWheel(position, totalWheels);
        return null;
    }

    private void repositionWheels(){   
        int total = wheels.size();
        for (int i = 0; i < total; i++) {
            wheels.get(i).rescale(i + 1, total);
            wheels.get(i).setLeftNeighbor(i > 0 ? wheels.get(i - 1) : null);
        }   
    }   

    private int clamp(int pos, int min, int max)
    {
        if (pos < min) return min;
        if (pos > max) return max;
        return pos;
    }
    private void updateFrameColors()
    {
        boolean isJack = isJackpotCheck();
        for (Wheel w : wheels) {
            w.refreshFrame(isJack);
        }
    }

    private boolean isJackpotCheck()
    {
        if (wheels.isEmpty()) return false;
        Set<String> distinct = new HashSet<>();
        for (String color : configuration()) distinct.add(color);
        return distinct.size() == 1;
    }
}