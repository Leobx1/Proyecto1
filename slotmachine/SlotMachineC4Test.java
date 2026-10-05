import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
  tipos de ruedas y de simbolos.
 * se ejecutan en modo invisible (sin makeVisible()).
 */
public class SlotMachineC4Test
{
    private SlotMachine sm;

    /** Rueda 1 normal, rueda 2 lefty y rueda 3 rebel, todas con rojo y verde. */
    @Before
    public void setUp()
    {
        sm = new SlotMachine();
        sm.addWheel("normal", 1);
        sm.addWheel("lefty", 2);
        sm.addWheel("rebel", 3);
        for (int w = 1; w <= 3; w++) {
            sm.addSymbol(w, 1, "red");
            sm.addSymbol(w, 2, "green");
        }
    }

    /** DEBERIA: la rueda lefty copia el simbolo de la rueda a su izquierda. */
    @Test
    public void leftyShouldCopyLeftWheel()
    {
        sm.placeSymbol(1, "green");
        sm.spin(2);
        assertEquals("green", sm.configuration()[1]);
    }

    /** NO DEBERIA: una rueda lefty sin rueda a la izquierda no falla al girar. */
    @Test
    public void leftyWithoutLeftWheelShouldStillSpin()
    {
        SlotMachine one = new SlotMachine();
        one.addWheel("lefty", 1);
        one.addSymbol(1, 1, "red");
        one.spin(1);
        assertTrue(one.ok());
    }

    /** NO DEBERIA: la rueda rebel no se deja bloquear. */
    @Test
    public void rebelShouldNotLock()
    {
        sm.lock(3);
        assertFalse(sm.ok());
    }

    /** NO DEBERIA: la rueda rebel no se deja intercambiar. */
    @Test
    public void rebelShouldNotSwap()
    {
        sm.swap(1, 3);
        assertFalse(sm.ok());
    }

    /** NO DEBERIA: la rueda rebel no se deja eliminar. */
    @Test
    public void rebelShouldNotBeDeleted()
    {
        sm.delWheel(3);
        assertFalse(sm.ok());
        assertEquals(3, sm.configuration().length);
    }

    /** DEBERIA: la rueda normal si se puede bloquear. */
    @Test
    public void normalShouldLock()
    {
        sm.lock(1);
        assertTrue(sm.ok());
    }

    /** DEBERIA: el simbolo ephemeral decrece en cada seleccion hasta un punto. */
    @Test
    public void ephemeralShouldShrinkUntilPoint()
    {
        EphemeralSymbol e = new EphemeralSymbol("red", 0, 0);
        int before = e.getSize();
        e.onSpinSelected();
        assertTrue(e.getSize() < before);
        for (int i = 0; i < 20; i++) e.onSpinSelected();
        assertEquals(EphemeralSymbol.MIN_SIZE, e.getSize());
    }

    /** DEBERIA: el simbolo shy alterna entre visible e invisible. */
    @Test
    public void shyShouldToggleVisibility()
    {
        ShySymbol s = new ShySymbol("blue", 0, 0);
        assertTrue(s.isShown());
        s.onSpinSelected();
        assertFalse(s.isShown());
        s.onSpinSelected();
        assertTrue(s.isShown());
    }

    /** DEBERIA: un simbolo shy oculto sigue siendo el simbolo actual de la rueda. */
    @Test
    public void hiddenShyShouldStillCount()
    {
        SlotMachine one = new SlotMachine();
        one.addWheel(1);
        one.addSymbol("shy", 1, 1, "blue");
        one.spin(1, 1);
        assertEquals("blue", one.configuration()[0]);
    }

    /** DEBERIA: el simbolo contagioso pasa su color a los demas simbolos de la rueda. */
    @Test
    public void contagiousShouldSpreadColor()
    {
        SlotMachine one = new SlotMachine();
        one.addWheel(1);
        one.addSymbol("contagious", 1, 1, "red");
        one.addSymbol("normal", 1, 2, "blue");
        one.addSymbol("normal", 1, 3, "green");
        one.spin(1, 3);
        for (String c : one.symbols()) assertEquals("red", c);
    }

    /** NO DEBERIA: un tipo de rueda o de simbolo desconocido se acepta. */
    @Test
    public void unknownTypesShouldFail()
    {
        sm.addWheel("inexistente", 1);
        assertFalse(sm.ok());
        sm.addSymbol("inexistente", 1, 1, "red");
        assertFalse(sm.ok());
    }
}