import java.util.ArrayList;
import java.util.Random;

public class Wheel
{
    private static final int BASE_X = 40;
    private static final int SPACING = 80;
    private static final int BASE_Y = 50;
    private static final int VISIBLE_SPACING = 40;

    private ArrayList<Symbol> symbols;
    private int currentIndex;
    private int xPosition;
    private boolean visible;
    private boolean ok;
    private boolean locked;
    private RectangleFrame frame;
    private static Random random = new Random();

    /** Crea una rueda vacia en la posicion dada. */
    public Wheel(int position)
    {
        xPosition = BASE_X + (position - 1) * SPACING;
        symbols = new ArrayList<>();
        currentIndex = 0;
        visible = false;
        ok = true;
        locked = false;
        frame = new RectangleFrame(xPosition - 5, BASE_Y - 10, 70, 180);
        frame.changeColor("black");
    }

    /** Agrega un simbolo del color dado (se crea invisible). */
    public void addSymbol(int pos, String color)
    {
        int p = clamp(pos, 1, symbols.size() + 1);
        int centeredX = xPosition + 30;
        Symbol s = new Symbol(color, centeredX, -100);
        symbols.add(p - 1, s);
        ok = true;
        currentIndex = 0;  
        if (visible) updateVisibleSymbols();  
    }

    /** Elimina el primer simbolo que tenga el color dado. */
    public void delSymbol(String color)
    {
        int idx = indexOf(color);
        if (idx == -1) {
            ok = false;
            return;
        }
        Symbol s = symbols.remove(idx);
        if (visible) s.makeInvisible();
        if (currentIndex >= symbols.size()) currentIndex = 0;
        if (visible) updateVisibleSymbols();
        ok = true;
    }

    /** Ubica como simbolo actual el que tenga el color dado. */
    public void placeSymbol(String color)
    {
        int idx = indexOf(color);
        if (idx == -1) {
            ok = false;
            return;
        }
        currentIndex = idx;
        if (visible) updateVisibleSymbols();
        ok = true;
    }

    /** Gira la rueda a un simbolo aleatorio. */
    public void spin()
    {
        if (symbols.isEmpty()) {
            ok = false;
            return;
        }
        int jumps = random.nextInt(symbols.size());
        currentIndex = (currentIndex + jumps) % symbols.size();
        updateVisibleSymbols();
        ok = true;
    }

    /** Gira la rueda un numero de pasos, mostrando cada paso si es visible. */
    public void spinSteps(int steps)
    {
        if (symbols.isEmpty()) {
            ok = false;
            return;
        }
        int direction = steps < 0 ? -1 : 1;
        int totalSteps = Math.abs(steps);
        for (int i = 0; i < totalSteps; i++) {
            currentIndex = ((currentIndex + direction) % symbols.size() + symbols.size()) % symbols.size();
            updateVisibleSymbols();
        }
        ok = true;
    }

    /** Fija la rueda para que no gire con spin(). */
    public void lock()
    {
        locked = true;
        ok = true;
    }

    /** Suelta la rueda para que vuelva a girar con spin(). */
    public void unlock()
    {
        locked = false;
        ok = true;
    }

    /** Indica si la rueda esta fija. */
    public boolean isLocked()
    {
        return locked;
    }

    /** Indica si la rueda tiene un simbolo del color dado. */
    public boolean hasColor(String color)
    {
        return indexOf(color) != -1;
    }

    /** Retorna el color del simbolo actualmente visible en el medio. */
    public String currentSymbol()
    {
        if (symbols.isEmpty()) {
            ok = false;
            return null;
        }
        ok = true;
        return symbols.get(currentIndex).getColor();
    }

    /** Retorna los colores de todos los simbolos en orden. */
    public String[] symbols()
    {
        String[] result = new String[symbols.size()];
        for (int i = 0; i < symbols.size(); i++) {
            result[i] = symbols.get(i).getColor();
        }
        return result;
    }

    /** Reubica esta rueda en una nueva posicion horizontal. */
    public void reposition(int newPosition)
    {
        int newX = BASE_X + (newPosition - 1) * SPACING;
        int delta = newX - xPosition;
        xPosition = newX;
        if (delta != 0) {
            frame.shiftX(delta);
            for (Symbol s : symbols) s.shiftX(delta);
        }
    }

    /** Hace visible la rueda y actualiza los 3 simbolos visibles. */
    public void makeVisible()
    {
        visible = true;
        frame.makeVisible();
        updateVisibleSymbols();
        ok = true;
    }

    /** Hace invisible la rueda y todos sus simbolos. */
    public void makeInvisible()
    {
        visible = false;
        frame.makeInvisible();
        for (Symbol s : symbols) s.makeInvisible();
        ok = true;
    }

    /** Indica si la ultima operacion se realizo con exito. */
    public boolean ok()
    {
        return ok;
    }

    /** Actualiza cuales 3 simbolos son visibles (anterior, actual, siguiente). */
    private void updateVisibleSymbols()
    {
        if (symbols.isEmpty()) return;

        int idxPrev = (currentIndex - 1 + symbols.size()) % symbols.size();
        int idxCurr = currentIndex;
        int idxNext = (currentIndex + 1) % symbols.size();

        for (int i = 0; i < symbols.size(); i++) {
            Symbol s = symbols.get(i);
            
            if (i == idxPrev) {
                s.moveTo(xPosition + 30, BASE_Y);
                s.highlight(false);
                if (visible) s.makeVisible();
            } else if (i == idxCurr) {
                s.moveTo(xPosition + 30, BASE_Y + VISIBLE_SPACING);
                s.highlight(true);
                if (visible) s.makeVisible();
            } else if (i == idxNext) {
                s.moveTo(xPosition + 30, BASE_Y + 2 * VISIBLE_SPACING);
                s.highlight(false);
                if (visible) s.makeVisible();
            } else {
                s.makeInvisible();
            }
        }
    }

    private int indexOf(String color)
    {
        for (int i = 0; i < symbols.size(); i++) {
            if (symbols.get(i).getColor().equals(color)) return i;
        }
        return -1;
    }

    private int clamp(int pos, int min, int max)
    {
        if (pos < min) return min;
        if (pos > max) return max;
        return pos;
    }
  
    /** Cambia el color del marco de la rueda (sin forzar visibilidad si esta invisible). */
    public void setFrameColor(String color)
    {
        if (visible) {
            frame.updateColor(color);
            updateVisibleSymbols();
        } else {
            frame.changeColor(color);
        }
    }
}