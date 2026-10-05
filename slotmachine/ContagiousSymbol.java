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
