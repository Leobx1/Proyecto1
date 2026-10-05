/**
 * Simbolo abstracto de la maquina tragamonedas.
 *
 * Define lo comun a todos los tipos de simbolo (color, posicion, dibujo) y deja
 * a las subclases el comportamiento que ocurre cuando el simbolo es
 * seleccionado por una rueda al girar ({@link #onSpinSelected()}).
 *
 * Subclases: {@link NormalSymbol}, {@link EphemeralSymbol}, {@link ShySymbol}
 * y {@link ContagiousSymbol}.
 */
public abstract class Symbol extends Circle
{
    /** Tamano del simbolo cuando es el actual de la rueda. */
    protected static final int SELECTED_SIZE = 40;
    /** Tamano del simbolo cuando no es el actual. */
    protected static final int UNSELECTED_SIZE = 26;

    private String color;

    /** Crea un simbolo del color y posicion dados. */
    protected Symbol(String color, int x, int y)
    {
        super();
        this.color = color;
        moveTo(x, y);
    }

    /** Retorna el nombre del tipo de simbolo (por ejemplo "normal"). */
    public abstract String getType();

    /** Se ejecuta cada vez que la rueda gira y selecciona este simbolo como el actual. */
    public abstract void onSpinSelected();

    /**
     * Se ejecuta cuando la rueda selecciona este simbolo y le informa cuales son
     * todos los simbolos de la rueda. Por defecto ignora la lista; los tipos que
     * afectan a otros simbolos (como el contagioso) la sobrescriben.
     */
    public void onSpinSelected(java.util.List<Symbol> companions)
    {
        onSpinSelected();
    }

    /** Indica si el simbolo se debe dibujar cuando es el actual. Por defecto si. */
    protected boolean isDisplayable()
    {
        return true;
    }

    /** Tamano con el que se dibuja cuando es el actual. Por defecto SELECTED_SIZE. */
    protected int getDisplaySize()
    {
        return SELECTED_SIZE;
    }

    /**
     * Actualiza la visualizacion del simbolo actual de una rueda.
     * @param x posicion horizontal
     * @param y posicion vertical
     * @param wheelVisible true si la rueda a la que pertenece esta visible
     */
    public void updateDisplay(int x, int y, boolean wheelVisible)
    {
        moveTo(x, y);
        highlight(true);
        if (wheelVisible) {
            if (isDisplayable()) {
                makeVisible();
                applyColor();
            } else {
                makeInvisible();
            }
        }
    }

    /** Dibuja el simbolo con su color actual. */
    public void applyColor()
    {
        changeColor(color);
    }

    /** Retorna el color de este simbolo. */
    public String getColor()
    {
        return color;
    }

    /** Cambia el color logico del simbolo (solo para subclases). */
    protected void setColor(String newColor)
    {
        color = newColor;
    }

    /** Mueve el simbolo a una posicion absoluta. */
    public void moveTo(int newX, int newY)
    {
        moveHorizontal(newX - getXPosition());
        moveVertical(newY - getYPosition());
    }

    /** Desplaza el simbolo horizontalmente sin cambiar su Y. */
    public void shiftX(int delta)
    {
        moveHorizontal(delta);
    }

    /** Cambia el tamano para resaltar el simbolo actual. */
    public void highlight(boolean isCurrent)
    {
        changeSize(isCurrent ? getDisplaySize() : UNSELECTED_SIZE);
    }
}
