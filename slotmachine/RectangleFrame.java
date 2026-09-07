public class RectangleFrame extends Rectangle
{
    private int xPos;
    private int yPos;

    /** Crea un rectangulo marco en la posicion dada. */
    public RectangleFrame(int x, int y, int w, int h)
    {
        super();
        xPos = 70;
        yPos = 15;
        moveTo(x, y);
        changeSize(h, w);
    }

    /** Mueve el marco a una posicion absoluta. */
    public void moveTo(int newX, int newY)
    {
        moveHorizontal(newX - xPos);
        moveVertical(newY - yPos);
        xPos = newX;
        yPos = newY;
    }

    /** Desplaza el marco horizontalmente. */
    public void shiftX(int delta)
    {
        moveHorizontal(delta);
        xPos += delta;
    }
    /** Cambia el color del marco y lo redibuja. */
    public void updateColor(String newColor)
    {
        makeInvisible();
        changeColor(newColor);
        makeVisible();
    }
}