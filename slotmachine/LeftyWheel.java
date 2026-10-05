/**
 * Rueda zurda: si tiene una rueda a su izquierda, al girar copia el simbolo
 * actual de esa rueda (si ella tiene un simbolo de ese color). Si no hay rueda
 * a la izquierda, o no tiene ese color, gira como una rueda normal. Marco naranja.
 */
public class LeftyWheel extends Wheel
{
    /** Color del marco de la rueda zurda. */
    public static final String FRAME_COLOR = "orange";

    private Wheel left;

    /** Crea una rueda zurda en la posicion dada. */
    public LeftyWheel(int position, int totalWheels)
    {
        super(position, totalWheels, FRAME_COLOR);
        left = null;
    }

    @Override
    public String getType()
    {
        return "lefty";
    }

    @Override
    public void setLeftNeighbor(Wheel left)
    {
        this.left = left;
    }

    @Override
    public void spin()
    {
        if (!prepareSpin()) return;
        if (left != null && left.symbolCount() > 0) {
            int idx = indexOf(left.currentSymbol());
            if (idx != -1) {
                landOn(idx);
                return;
            }
        }
        landOn(getCurrentIndex() + random.nextInt(symbolCount()));
    }
}
