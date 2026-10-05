/** Rueda normal: gira a un simbolo aleatorio. Marco negro. */
public class NormalWheel extends Wheel
{
    /** Color del marco de la rueda normal. */
    public static final String FRAME_COLOR = "black";

    /** Crea una rueda normal en la posicion dada. */
    public NormalWheel(int position, int totalWheels)
    {
        super(position, totalWheels, FRAME_COLOR);
    }

    @Override
    public String getType()
    {
        return "normal";
    }

    @Override
    public void spin()
    {
        if (!prepareSpin()) return;
        landOn(getCurrentIndex() + random.nextInt(symbolCount()));
    }
}
