import java.util.List;

/**
 * Simbolo contagioso (tipo nuevo propuesto): cuando es seleccionado al girar,
 * contagia su color a todos los demas simbolos de su rueda, de modo que
 * quedan todos iguales.
 */
public class ContagiousSymbol extends Symbol
{
    /** Crea un simbolo contagioso del color y posicion dados. */
    public ContagiousSymbol(String color, int x, int y)
    {
        super(color, x, y);
    }

    @Override
    public String getType()
    {
        return "contagious";
    }

    @Override
    public String getMark()
    {
        return "cross";
    }

    @Override
    protected java.awt.Shape buildMark(int x, int y, int d)
    {
        java.awt.BasicStroke stroke = new java.awt.BasicStroke(Math.max(2f, d * 0.16f));
        double a = d * 0.24;
        double b = d * 0.76;
        java.awt.geom.Area cross = new java.awt.geom.Area(stroke.createStrokedShape(
            new java.awt.geom.Line2D.Double(x + a, y + a, x + b, y + b)));
        cross.add(new java.awt.geom.Area(stroke.createStrokedShape(
            new java.awt.geom.Line2D.Double(x + b, y + a, x + a, y + b))));
        return cross;
    }

    @Override
    public void onSpinSelected()
    {
        // Sin companeros no hay a quien contagiar.
    }

    @Override
    public void onSpinSelected(List<Symbol> companions)
    {
        for (Symbol other : companions) {
            if (other != this) other.setColor(getColor());
        }
    }
}
