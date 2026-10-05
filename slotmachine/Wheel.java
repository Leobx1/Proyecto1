import java.util.ArrayList;
import java.util.Random;

/**
 * Rueda abstracta de la maquina tragamonedas.
 *
 * Contiene lo comun a todas las ruedas (simbolos, marco, bloqueo, dibujo) y
 * deja a las subclases la forma de girar ({@link #spin()}) y las reglas sobre
 * que operaciones admiten (canLock, canSwap, canDelete).
 *
 * Subclases: {@link NormalWheel}, {@link LeftyWheel} y {@link RebelWheel}.
 */
public abstract class Wheel
{
    private static final int BASE_X = 40;
    private static final int SPACING = 80;
    private static final int BASE_Y = 50;
    private static final int VISIBLE_SPACING = 50;
    private static final String JACKPOT_COLOR = "yellow";

    private ArrayList<Symbol> symbols;
    private int currentIndex;
    private int xPosition;
    private boolean visible;
    private boolean ok;
    private boolean locked;
    private RectangleFrame frame;
    /** Generador aleatorio compartido por las ruedas. */
    protected static final Random random = new Random();
    private int diameter;
    private int spacing;
    private String baseFrameColor;

    /**
     * Crea una rueda vacia en la posicion dada.
     * @param frameColor color del marco que identifica visualmente el tipo de rueda
     */
    protected Wheel(int position, int totalWheels, String frameColor)
    {
        baseFrameColor = frameColor;
        spacing = Math.max(25, 260 / totalWheels);
        diameter = Math.max(8, spacing - 22);
        xPosition = BASE_X + (position - 1) * spacing;
        symbols = new ArrayList<>();
        currentIndex = 0;
        visible = false;
        ok = true;
        locked = false;
        frame = new RectangleFrame(xPosition - 5, BASE_Y - 10, spacing - 10, diameter + 30);
        frame.changeColor(baseFrameColor);
    }

    /** Agrega un simbolo normal del color dado (se crea invisible). */
    public void addSymbol(int pos, String color)
    {
        addSymbol("normal", pos, color);
    }

    /** Agrega un simbolo del tipo y color dados (se crea invisible). Falla si el tipo no existe. */
    public void addSymbol(String type, int pos, String color)
    {
        int p = clamp(pos, 1, symbols.size() + 1);
        int centeredX = xPosition + 45;
        Symbol s = createSymbol(type, color, centeredX, -100);
        if (s == null) {
            ok = false;
            return;
        }
        s.changeColor(color);
        symbols.add(p - 1, s);
        ok = true;
        currentIndex = 0;  
        if (visible) updateVisibleSymbols();  
    }

    /** Crea un simbolo segun su tipo, o retorna null si el tipo no existe. */
    private Symbol createSymbol(String type, String color, int x, int y)
    {
        if (type == null) return null;
        String t = type.toLowerCase();
        if (t.equals("normal"))     return new NormalSymbol(color, x, y);
        if (t.equals("ephemeral"))  return new EphemeralSymbol(color, x, y);
        if (t.equals("shy"))        return new ShySymbol(color, x, y);
        if (t.equals("contagious")) return new ContagiousSymbol(color, x, y);
        return null;
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

    /** Nombre del tipo de rueda (por ejemplo "normal"). */
    public abstract String getType();

    /** Gira la rueda segun las reglas de su tipo. */
    public abstract void spin();

    /** Indica si esta rueda se puede fijar. Por defecto si. */
    public boolean canLock()
    {
        return true;
    }

    /** Indica si esta rueda se puede intercambiar de posicion. Por defecto si. */
    public boolean canSwap()
    {
        return true;
    }

    /** Indica si esta rueda se puede eliminar. Por defecto si. */
    public boolean canDelete()
    {
        return true;
    }

    /** Informa cual es la rueda inmediatamente a la izquierda (null si no hay). Por defecto se ignora. */
    public void setLeftNeighbor(Wheel left)
    {
    }

    /** Coloca la rueda en un indice aleatorio sin contar como giro (para el estado inicial). */
    public void randomize()
    {
        if (symbols.isEmpty()) {
            ok = false;
            return;
        }
        currentIndex = random.nextInt(symbols.size());
        updateVisibleSymbols();
        ok = true;
    }

    /** Prepara un giro: retorna false (y marca ok=false) si no hay simbolos. */
    protected boolean prepareSpin()
    {
        if (symbols.isEmpty()) {
            ok = false;
            return false;
        }
        return true;
    }

    /** Cantidad de simbolos de la rueda. */
    protected int symbolCount()
    {
        return symbols.size();
    }

    /** Indice del simbolo actual. */
    protected int getCurrentIndex()
    {
        return currentIndex;
    }

    /** Termina un giro en el indice dado: notifica al simbolo seleccionado y actualiza el dibujo. */
    protected void landOn(int index)
    {
        currentIndex = ((index % symbols.size()) + symbols.size()) % symbols.size();
        symbols.get(currentIndex).onSpinSelected(symbols);
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
            if (i == totalSteps - 1) {
                // El simbolo donde cae la rueda se notifica ANTES de dibujarlo,
                // para no mostrarlo un instante en su estado anterior.
                symbols.get(currentIndex).onSpinSelected(symbols);
            }
            updateVisibleSymbols();
        }
        ok = true;
    }

    /** Fija la rueda para que no gire con spin(). */
    public void lock()
    {
        if (!canLock()) {
            ok = false;
            return;
        }
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
    private void updateVisibleSymbols(){
    if (symbols.isEmpty()) return;

    
    int idxCurr = currentIndex;
    
    for (int i = 0; i < symbols.size(); i++) {
        Symbol s = symbols.get(i);
        int size = s.getDisplaySize();
        int frameLeft = xPosition - 5;
        int frameTop = BASE_Y - 10;
        int symbolX = frameLeft + (spacing - 10 - size) / 2;      // centrado horizontal
        int symbolY = frameTop + (diameter + 30 - size) / 2;      // centrado vertical
        
        if (i == idxCurr) {
            s.updateDisplay(symbolX, symbolY, visible);
        }  else {
            s.makeInvisible();
        }
    }
    }

    /** Indice del primer simbolo del color dado, o -1 si no existe. */
    protected int indexOf(String color)
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
  
    /** Color base del marco, propio del tipo de rueda. */
    public String getFrameColor()
    {
        return baseFrameColor;
    }

    /** Pinta el marco de amarillo si hay jackpot, o con el color propio de su tipo si no. */
    public void refreshFrame(boolean jackpot)
    {
        setFrameColor(jackpot ? JACKPOT_COLOR : baseFrameColor);
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
        public void rescale(int newPosition, int totalWheels)
    {
        spacing = Math.max(25, 260 / totalWheels);
        diameter = Math.max(10, spacing - 15);
        int newX = BASE_X + (newPosition - 1) * spacing;
        int delta = newX - xPosition;          
    
        frame.changeSize(diameter + 30, spacing - 10);
        frame.moveTo(newX - 5, BASE_Y - 10);    
    
        for (Symbol s : symbols) {
            s.changeSize(diameter);
            s.shiftX(delta);                   
        }
    
        xPosition = newX;
    } 
}