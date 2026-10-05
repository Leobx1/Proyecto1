import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JOptionPane;

public class SlotMachine
{
    private ArrayList<Wheel> wheels;
    private boolean visible;
    private boolean ok;
    private String[] solution;

    /** Crea una maquina tragamonedas vacia. */
    public SlotMachine()
    {
        wheels = new ArrayList<>();
        visible = false;
        ok = true;
        solution = null;
    }

    /** Crea una maquina con n ruedas normales y n simbolos normales de colores variados. */
    public SlotMachine(int n)
    {
        this(n, "normal", "normal");
    }

    /**
     * Crea una maquina con n ruedas del tipo dado y n simbolos del tipo dado por rueda,
     * de colores variados. Tipos de rueda: normal, lefty, rebel.
     * Tipos de simbolo: normal, ephemeral, shy, contagious.
     */
    public SlotMachine(int n, String wheelType, String symbolType)
    {
        wheels = new ArrayList<>();
        visible = false;
        ok = true;
        solution = null;

        String[] availableColors = {"red", "blue", "green", "magenta"};

        for (int i = 1; i <= n; i++) {
            addWheel(wheelType, i);
            if (!ok) return;
            for (int j = 0; j < n; j++) {
                String color = availableColors[(int)(Math.random() * availableColors.length)];
                addSymbol(symbolType, i, j + 1, color);
            }
            wheels.get(i - 1).randomize();
        }
    }

    /** Agrega una rueda en la posicion pos. */
    public void addWheel(int pos)
    {
        addWheel("normal", pos);
    }

    /** Agrega una rueda del tipo dado (normal, lefty, rebel) en la posicion pos. */
    public void addWheel(String type, int pos)
    {
        int p = clamp(pos, 1, wheels.size() + 1);
        Wheel w = createWheel(type, p, wheels.size() + 1);
        if (w == null) {
            ok = false;
            alert("Tipo de rueda desconocido: " + type);
            return;
        }
        wheels.add(p - 1, w);
        repositionWheels();
        if (visible) w.makeVisible();
        ok = true;
    }

    /** Elimina la rueda en la posicion pos. */
    public void delWheel(int pos)
    {
        if (wheels.isEmpty()) {
            ok = false;
            alert("No hay ruedas para eliminar.");
            return;
        }
        int p = clamp(pos, 1, wheels.size());
        Wheel w = wheels.get(p - 1);
        if (!w.canDelete()) {
            ok = false;
            alert("La rueda " + p + " (" + w.getType() + ") no se puede eliminar.");
            return;
        }
        wheels.remove(p - 1);
        if (visible) w.makeInvisible();
        repositionWheels();
        ok = true;
    }

    /** Agrega un simbolo del color dado en la posicion pos de la rueda especificada. */
    public void addSymbol(int wheel, int pos, String color)
    {
        addSymbol("normal", wheel, pos, color);
    }

    /** Agrega un simbolo del tipo dado (normal, ephemeral, shy, contagious) en la posicion pos de la rueda. */
    public void addSymbol(String type, int wheel, int pos, String color)
    {
        if (wheels.isEmpty()) {
            ok = false;
            alert("No hay ruedas disponibles.");
            return;
        }
        int w = clamp(wheel, 1, wheels.size());
        Wheel targetWheel = wheels.get(w - 1);
        targetWheel.addSymbol(type, pos, color);
        ok = targetWheel.ok();
        if (!ok) alert("No se pudo agregar el simbolo (tipo \"" + type + "\") a la rueda " + w);
        updateFrameColors();
    }

    /** Elimina el simbolo del color dado en todas las ruedas que lo tengan. */
    public void delSymbol(String symbol)
    {
        ok = false;
        for (Wheel w : wheels) {
            w.delSymbol(symbol);
            ok = ok || w.ok();
        }
        if (!ok) alert("El simbolo no existe en ninguna rueda.");
    }

    /** Ubica el simbolo dado como el actual en la rueda indicada. */
    public void placeSymbol(int wheel, String symbol)
    {
        if (wheels.isEmpty()) {
            ok = false;
            alert("No hay ruedas disponibles.");
            return;
        }
        int idx = clamp(wheel, 1, wheels.size());
        Wheel w = wheels.get(idx - 1);
        w.placeSymbol(symbol);
        ok = w.ok();
        if (!ok) alert("El simbolo no existe en esa rueda.");
    }

    /** Gira la rueda indicada. Falla si la rueda esta fija (locked). */
    public void spin(int wheel)
    {
        if (wheels.isEmpty()) {
            ok = false;
            alert("No hay ruedas disponibles.");
            return;
        }
        int idx = clamp(wheel, 1, wheels.size());
        Wheel w = wheels.get(idx - 1);
        if (w.isLocked()) {
            ok = false;
            alert("La rueda " + idx + " esta fija y no puede girar.");
            return;
        }
        w.spin();
        ok = w.ok();
        updateFrameColors();
    }

    /** Gira todas las ruedas que no esten fijas. */
    public void spin()
    {
        ok = !wheels.isEmpty();
        for (Wheel w : wheels) {
            if (!w.isLocked()) {
                w.spin();
                ok = ok && w.ok();
            }
        }
        updateFrameColors();
    }

    /** Gira la rueda indicada un numero de pasos. Falla si la rueda esta fija. */
    public void spin(int wheel, int steps)
    {
        if (wheels.isEmpty()) {
            ok = false;
            alert("No hay ruedas disponibles.");
            return;
        }
        int idx = clamp(wheel, 1, wheels.size());
        Wheel w = wheels.get(idx - 1);
        if (w.isLocked()) {
            ok = false;
            alert("La rueda " + idx + " esta fija y no puede girar.");
            return;
        }
        w.spinSteps(steps);
        ok = w.ok();
        updateFrameColors();
    }

    /** Deja la maquina en la configuracion dada (un color por rueda, de izquierda a derecha). */
    public void spin(String[] setSymbols)
    {
        if (wheels.isEmpty() || setSymbols == null || setSymbols.length != wheels.size()) {
            ok = false;
            alert("La configuracion dada no coincide con el numero de ruedas.");
            return;
        }
        for (int i = 0; i < wheels.size(); i++) {
            if (!wheels.get(i).hasColor(setSymbols[i])) {
                ok = false;
                alert("El color " + setSymbols[i] + " no existe en la rueda " + (i + 1));
                return;
            }
        }
        for (int i = 0; i < wheels.size(); i++) {
            wheels.get(i).placeSymbol(setSymbols[i]);
        }
        ok = true;
        updateFrameColors();
    }

    /** Intercambia dos ruedas de posicion. Falla si alguna esta fija. */
    public void swap(int wheel1, int wheel2)
    {
        if (wheels.isEmpty()) {
            ok = false;
            alert("No hay ruedas disponibles.");
            return;
        }
        int i1 = clamp(wheel1, 1, wheels.size()) - 1;
        int i2 = clamp(wheel2, 1, wheels.size()) - 1;
        Wheel w1 = wheels.get(i1);
        Wheel w2 = wheels.get(i2);
        if (w1.isLocked() || w2.isLocked()) {
            ok = false;
            alert("No se puede intercambiar: una de las ruedas esta fija.");
            return;
        }
        if (!w1.canSwap() || !w2.canSwap()) {
            ok = false;
            alert("No se puede intercambiar: una de las ruedas es rebelde.");
            return;
        }
        wheels.set(i1, w2);
        wheels.set(i2, w1);
        repositionWheels();
        ok = true;
        updateFrameColors();
    }

    /** Fija una rueda para que no gire con spin(). */
    public void lock(int wheel)
    {
        if (wheels.isEmpty()) {
            ok = false;
            alert("No hay ruedas disponibles.");
            return;
        }
        int idx = clamp(wheel, 1, wheels.size());
        Wheel w = wheels.get(idx - 1);
        if (!w.canLock()) {
            ok = false;
            alert("La rueda " + idx + " (" + w.getType() + ") no se deja bloquear.");
            return;
        }
        w.lock();
        ok = true;
    }

    /** Suelta una rueda previamente fijada. */
    public void unlock(int wheel)
    {
        if (wheels.isEmpty()) {
            ok = false;
            alert("No hay ruedas disponibles.");
            return;
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

    /** Resuelve el problema de la maratón encontrando una configuracion ganadora (invisible). */
    public boolean solve(){
        if (wheels.isEmpty()) {
            ok = false;
            return false;
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
            ok = false;
            return false;
        }
    
        solution = new String[wheels.size()];
        java.util.Arrays.fill(solution, targetColor);   // guarda el color encontrado, nada mas
    
        ok = true;
        return true;   // encontro una solucion (no significa que ya este ganada)
    }

    /** Simula la solucion encontrada por solve(), mostrando la maquina. */
    public boolean simulate()
    {
        if (solution == null) {
            ok = false;
            alert("Ejecute solve() primero.");
            return false;
        }

        makeVisible();

        for (int i = 0; i < wheels.size(); i++) {
            Wheel w = wheels.get(i);
            if (w.isLocked() && !w.currentSymbol().equals(solution[i])) {
                ok = false;   // rueda fija con otro color: no se puede llegar a la solucion
                return false;
            }
            while (!w.currentSymbol().equals(solution[i])) {
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

    private void alert(String message)
    {
        if (visible) {
            JOptionPane.showMessageDialog(null, message, "Aviso", JOptionPane.WARNING_MESSAGE);
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