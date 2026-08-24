public class Symbol extends Circle
{
    private String color;
    private int xPos;
    private int yPos;

    /** Crea un simbolo del color y posicion dados. */
    public Symbol(String color, int x, int y)
    {
        super();
        this.xPos = 20;
        this.yPos = 15;
        this.color = color;
        changeColor(color);
        moveTo(x, y);
    }

    /** Retorna el color de este simbolo. */
    public String getColor()
    {
        return color;
    }

    /** Mueve el simbolo a una posicion absoluta. */
    public void moveTo(int newX, int newY)
    {
        moveHorizontal(newX - xPos);
        moveVertical(newY - yPos);
        xPos = newX;
        yPos = newY;
    }

    /** Desplaza el simbolo horizontalmente sin cambiar su Y. */
    public void shiftX(int delta)
    {
        moveHorizontal(delta);
        xPos += delta;
    }

    /** Cambia el tamano para resaltar el simbolo actual. */
    public void highlight(boolean isCurrent)
    {
        changeSize(isCurrent ? 40 : 26);
    }
}