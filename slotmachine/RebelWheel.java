/** Rueda rebelde: gira normalmente, pero no se deja bloquear, intercambiar ni eliminar. Marco gris. */
public class RebelWheel extends Wheel
{
    /** Color del marco de la rueda rebelde. */
    public static final String FRAME_COLOR = "gray";

    /** Crea una rueda rebelde en la posicion dada. */
    public RebelWheel(int position, int totalWheels)
    {
        super(position, totalWheels, FRAME_COLOR);
    }

    @Override
    public String getType()
    {
        return "rebel";
    }

    @Override
    public void spin()
    {
        if (!prepareSpin()) return;
        landOn(getCurrentIndex() + random.nextInt(symbolCount()));
    }

    @Override
    public boolean canLock()
    {
        return false;
    }

    @Override
    public boolean canSwap()
    {
        return false;
    }

    @Override
    public boolean canDelete()
    {
        return false;
    }
}
