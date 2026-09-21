/** Interfaz que define el contrato para todas las figuras geometricas. */
public interface Figura
{
    /** Hace visible la figura en el canvas. */
    void makeVisible();

    /** Hace invisible la figura en el canvas. */
    void makeInvisible();

    /** Cambia el color de la figura. */
    void changeColor(String newColor);

    /** Desplaza la figura horizontalmente. */
    void moveHorizontal(int distance);

    /** Desplaza la figura verticalmente. */
    void moveVertical(int distance);
}
