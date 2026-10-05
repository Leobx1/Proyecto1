/**
 * Simbolo timido: alterna entre visible e invisible cada vez que es
 * seleccionado al girar. Sigue contando como simbolo actual de la rueda
 * (para configuration() y isJackpot()) aunque no se dibuje.
 */
public class ShySymbol extends Symbol
{
    private boolean shown;

    /** Crea un simbolo timido del color y posicion dados; empieza visible. */
    public ShySymbol(String color, int x, int y)
    {
        super(color, x, y);
        shown = true;
    }

    @Override
    public String getType()
    {
        return "shy";
    }

    @Override
    public void onSpinSelected()
    {
        shown = !shown;
    }

    @Override
    protected boolean isDisplayable()
    {
        return shown;
    }

    /** Indica si el simbolo esta en estado visible. */
    public boolean isShown()
    {
        return shown;
    }
}
