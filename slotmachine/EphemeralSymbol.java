/**
 * Simbolo efimero: cada vez que es seleccionado al girar decrece su tamano
 * hasta quedar como un punto (tamano minimo), donde se mantiene.
 */
public class EphemeralSymbol extends Symbol
{
    /** Tamano minimo: el simbolo queda como un punto. */
    public static final int MIN_SIZE = 2;
    /** Cuanto decrece el tamano en cada seleccion. */
    public static final int STEP = 6;

    private int size;

    /** Crea un simbolo efimero del color y posicion dados, con tamano completo. */
    public EphemeralSymbol(String color, int x, int y)
    {
        super(color, x, y);
        size = SELECTED_SIZE;
    }

    @Override
    public String getType()
    {
        return "ephemeral";
    }

    @Override
    public void onSpinSelected()
    {
        size = Math.max(MIN_SIZE, size - STEP);
    }

    @Override
    protected int getDisplaySize()
    {
        return size;
    }

    @Override
    public String getMark()
    {
        return "ring";
    }

    @Override
    protected java.awt.Shape buildMark(int x, int y, int d)
    {
        if (d < 12) return null;
        double outer = d * 0.76;
        double inner = d * 0.52;
        java.awt.geom.Area ring = new java.awt.geom.Area(new java.awt.geom.Ellipse2D.Double(
            x + (d - outer) / 2, y + (d - outer) / 2, outer, outer));
        ring.subtract(new java.awt.geom.Area(new java.awt.geom.Ellipse2D.Double(
            x + (d - inner) / 2, y + (d - inner) / 2, inner, inner)));
        return ring;
    }

    /** Retorna el tamano actual del simbolo. */
    public int getSize()
    {
        return size;
    }
}
