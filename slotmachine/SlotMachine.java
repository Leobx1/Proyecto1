import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JOptionPane;

public class SlotMachine
{
    private ArrayList<Wheel> wheels;
    private boolean visible;
    private boolean ok;

    /** Crea una maquina tragamonedas vacia. */
    public SlotMachine()
    {
        wheels = new ArrayList<>();
        visible = false;
        ok = true;
    }

    /** Agrega una rueda en la posicion pos. */
    public void addWheel(int pos)
    {
        int p = clamp(pos, 1, wheels.size() + 1);
        Wheel w = new Wheel(p);
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
        Wheel w = wheels.remove(p - 1);
        if (visible) w.makeInvisible();
        repositionWheels();
        ok = true;
    }

    /** Agrega un simbolo del color dado en la posicion pos de la rueda especificada. */
    public void addSymbol(int wheel, int pos, String color)
    {
        if (wheels.isEmpty()) {
            ok = false;
            alert("No hay ruedas disponibles.");
            return;
        }
        int w = clamp(wheel, 1, wheels.size());
        Wheel targetWheel = wheels.get(w - 1);
        targetWheel.addSymbol(pos, color);
        ok = targetWheel.ok();
        if (!ok) alert("No se pudo agregar el simbolo a la rueda " + w);
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
        wheels.get(idx - 1).lock();
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

    private void repositionWheels()
    {
        for (int i = 0; i < wheels.size(); i++) {
            wheels.get(i).reposition(i + 1);
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
        String frameColor = isJack ? "yellow" : "black";
        for (Wheel w : wheels) {
            w.setFrameColor(frameColor);
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