import java.util.ArrayList;

public class Wheel
{
    private static final int BASE_X = 40;
    private static final int SPACING = 60;
    private static final int BASE_Y = 50;
    private static final int SYMBOL_SPACING = 40;

    private ArrayList<Symbol> symbols;
    private int currentIndex;
    private int xPosition;
    private boolean visible;
    private boolean ok;

    /** Crea una rueda vacia en la posicion dada. */
    public Wheel(int position)
    {
        xPosition = BASE_X + (position - 1) * SPACING;
        symbols = new ArrayList<>();
        currentIndex = 0;
        visible = false;
        ok = true;
    }

    /** Agrega un simbolo del color dado en la posicion pos. */
    public void addSymbol(int pos, String color)
    {
        int p = clamp(pos, 1, symbols.size() + 1);
        int y = BASE_Y + (p - 1) * SYMBOL_SPACING;
        Symbol s = new Symbol(color, xPosition, y);
        symbols.add(p - 1, s);
        repositionSymbols();
        if (visible) s.makeVisible();
        currentIndex = 0;
        updateHighlight();
        ok = true;
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
        repositionSymbols();
        if (currentIndex >= symbols.size()) currentIndex = 0;
        updateHighlight();
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
        updateHighlight();
        ok = true;
    }

    /** Gira la rueda al siguiente simbolo. */
    public void spin()
    {
        if (symbols.isEmpty()) {
            ok = false;
            return;
        }
        currentIndex = (currentIndex + 1) % symbols.size();
        updateHighlight();
        ok = true;
    }

    /** Retorna el color del simbolo actualmente visible. */
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
            for (Symbol s : symbols) s.shiftX(delta);
        }
    }

    /** Hace visible la rueda y todos sus simbolos. */
    public void makeVisible()
    {
        visible = true;
        for (Symbol s : symbols) s.makeVisible();
        ok = true;
    }

    /** Hace invisible la rueda y todos sus simbolos. */
    public void makeInvisible()
    {
        visible = false;
        for (Symbol s : symbols) s.makeInvisible();
        ok = true;
    }

    /** Indica si la ultima operacion se realizo con exito. */
    public boolean ok()
    {
        return ok;
    }

    private void repositionSymbols()
    {
        for (int i = 0; i < symbols.size(); i++) {
            symbols.get(i).moveTo(xPosition, BASE_Y + i * SYMBOL_SPACING);
        }
    }

    private void updateHighlight()
    {
        for (int i = 0; i < symbols.size(); i++) {
            symbols.get(i).highlight(i == currentIndex);
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
}