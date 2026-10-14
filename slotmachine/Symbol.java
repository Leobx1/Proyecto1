/**
 * Simbolo abstracto de la maquina tragamonedas.
 *
 * Define lo comun a todos los tipos de simbolo (color, posicion, dibujo) y deja
 * a las subclases el comportamiento que ocurre cuando el simbolo es
 * seleccionado por una rueda al girar ({@link #onSpinSelected()}).
 *
 * Cada tipo se distingue visualmente por una marca dibujada sobre el simbolo
 * (el color del simbolo no cambia, porque es su identidad en el juego):
 * normal sin marca, efimero con anillo, timido con punto central y
 * contagioso con una cruz.
 *
 * Subclases: {@link NormalSymbol}, {@link EphemeralSymbol}, {@link ShySymbol}
 * y {@link ContagiousSymbol}.
 */
public abstract class Symbol extends Circle
{
    /** Colores que el Canvas sabe dibujar y que se aceptan para un simbolo. */
    private static final java.util.Set<String> VALID_COLORS = new java.util.HashSet<>(
        java.util.Arrays.asList("red", "black", "blue", "yellow", "green", "magenta",
                                "white", "orange", "cyan", "gray", "pink"));

    /** Tamano del simbolo cuando es el actual de la rueda. */
    protected static final int SELECTED_SIZE = 40;
    /** Tamano del simbolo cuando no es el actual. */
    protected static final int UNSELECTED_SIZE = 26;

    private String color;
    private int diameter = 30;
    private boolean drawn = false;
    private final Object markKey = new Object();

    /** Indica si el color dado es soportado por el Canvas. */
    public static boolean isValidColor(String color)
    {
        return color != null && VALID_COLORS.contains(color);
    }

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

    /** Nombre de la marca visual del tipo ("none", "ring", "dot", "cross"). */
    public String getMark()
    {
        return "none";
    }

    /** Forma de la marca visual dentro del simbolo (x, y, d: esquina y diametro), o null si no tiene. */
    protected java.awt.Shape buildMark(int x, int y, int d)
    {
        return null;
    }

    /** Color de la marca: contrasta con el color del simbolo. */
    protected String markColor()
    {
        switch (color) {
            case "yellow": case "green": case "white": case "cyan": case "orange": case "pink":
                return "black";
            default:
                return "white";
        }
    }

    private void drawMark()
    {
        if (!drawn) return;
        java.awt.Shape mark = buildMark(getXPosition(), getYPosition(), diameter);
        if (mark == null) return;
        Canvas.getCanvas().draw(markKey, markColor(), mark);
    }

    private void eraseMark()
    {
        if (drawn && buildMark(0, 0, diameter) != null) Canvas.getCanvas().erase(markKey);
    }

    @Override
    public void makeVisible()
    {
        drawn = true;
        super.makeVisible();
        drawMark();
    }

    @Override
    public void makeInvisible()
    {
        eraseMark();
        drawn = false;
        super.makeInvisible();
    }

    @Override
    public void moveHorizontal(int distance)
    {
        super.moveHorizontal(distance);
        drawMark();
    }

    @Override
    public void moveVertical(int distance)
    {
        super.moveVertical(distance);
        drawMark();
    }

    @Override
    public void changeSize(int newDiameter)
    {
        diameter = newDiameter;
        super.changeSize(newDiameter);
        drawMark();
    }

    @Override
    public void changeColor(String newColor)
    {
        super.changeColor(newColor);
        drawMark();
    }
}
