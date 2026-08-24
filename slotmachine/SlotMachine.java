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

    /** Agrega un simbolo del color dado en la posicion pos, en todas las ruedas. */
    public void addSymbol(int pos, String color)
    {
        ok = !wheels.isEmpty();
        for (Wheel w : wheels) {
            w.addSymbol(pos, color);
            ok = ok && w.ok();
        }
        if (!ok) alert("No se pudo agregar el simbolo.");
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

    /** Gira la rueda indicada. */
    public void spin(int wheel)
    {
        if (wheels.isEmpty()) {
            ok = false;
            alert("No hay ruedas disponibles.");
            return;
        }
        int idx = clamp(wheel, 1, wheels.size());
        wheels.get(idx - 1).spin();
        ok = wheels.get(idx - 1).ok();
    }

    /** Gira todas las ruedas. */
    public void spin()
    {
        ok = !wheels.isEmpty();
        for (Wheel w : wheels) {
            w.spin();
            ok = ok && w.ok();
        }
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
        if (wheels.isEmpty()) return false;
        Set<String> distinct = new HashSet<>();
        for (String color : configuration()) distinct.add(color);
        return distinct.size() == 1;
    }

    /** Hace visible el simulador. */
    public void makeVisible()
    {
        visible = true;
        for (Wheel w : wheels) w.makeVisible();
        ok = true;
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
}