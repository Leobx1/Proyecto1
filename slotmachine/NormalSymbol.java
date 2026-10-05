/** Simbolo normal: no tiene ningun comportamiento especial al ser seleccionado. */
public class NormalSymbol extends Symbol
{
    /** Crea un simbolo normal del color y posicion dados. */
    public NormalSymbol(String color, int x, int y)
    {
        super(color, x, y);
    }

    @Override
    public String getType()
    {
        return "normal";
    }

    @Override
    public void onSpinSelected()
    {
        // Sin cambios: es el simbolo base.
    }
}
