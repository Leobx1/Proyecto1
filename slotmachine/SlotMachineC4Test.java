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

    /** Accion que puede lanzar una SlotMachineException. */
    private interface Action
    {
        void run() throws SlotMachineException;
    }

    /** Verifica que la accion lance una SlotMachineException con el mensaje dado. */
    private void expect(String message, Action action)
    {
        try {
            action.run();
            fail("Debio lanzar: " + message);
        } catch (SlotMachineException e) {
            assertEquals(message, e.getMessage());
        }
    }

    /** Rueda 1 normal, rueda 2 lefty y rueda 3 rebel, todas con rojo y verde. */
    @Before
    public void setUp() throws SlotMachineException
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
    public void leftyShouldCopyLeftWheel() throws SlotMachineException
    {
        sm.placeSymbol(1, "green");
        sm.spin(2);
        assertEquals("green", sm.configuration()[1]);
    }

    /** NO DEBERIA: una rueda lefty sin rueda a la izquierda no falla al girar. */
    @Test
    public void leftyWithoutLeftWheelShouldStillSpin() throws SlotMachineException
    {
        SlotMachine one = new SlotMachine();
        one.addWheel("lefty", 1);
        one.addSymbol(1, 1, "red");
        one.spin(1);
        assertTrue(one.ok());
    }

    /** NO DEBERIA: la rueda rebel no se deja bloquear. */
    @Test
    public void rebelShouldNotLock() throws SlotMachineException
    {
        expect(SlotMachineException.WHEEL_CANNOT_LOCK, () -> sm.lock(3));
        assertFalse(sm.ok());
    }

    /** NO DEBERIA: la rueda rebel no se deja intercambiar. */
    @Test
    public void rebelShouldNotSwap() throws SlotMachineException
    {
        expect(SlotMachineException.WHEEL_CANNOT_SWAP, () -> sm.swap(1, 3));
        assertFalse(sm.ok());
    }

    /** NO DEBERIA: la rueda rebel no se deja eliminar. */
    @Test
    public void rebelShouldNotBeDeleted() throws SlotMachineException
    {
        expect(SlotMachineException.WHEEL_CANNOT_DELETE, () -> sm.delWheel(3));
        assertFalse(sm.ok());
        assertEquals(3, sm.configuration().length);
    }

    /** DEBERIA: la rueda normal si se puede bloquear. */
    @Test
    public void normalShouldLock() throws SlotMachineException
    {
        sm.lock(1);
        assertTrue(sm.ok());
    }

    /** DEBERIA: el simbolo ephemeral decrece en cada seleccion hasta un punto. */
    @Test
    public void ephemeralShouldShrinkUntilPoint() throws SlotMachineException
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
    public void shyShouldToggleVisibility() throws SlotMachineException
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
    public void hiddenShyShouldStillCount() throws SlotMachineException
    {
        SlotMachine one = new SlotMachine();
        one.addWheel(1);
        one.addSymbol("shy", 1, 1, "blue");
        one.spin(1, 1);
        assertEquals("blue", one.configuration()[0]);
    }

    /** DEBERIA: el simbolo contagioso pasa su color a los demas simbolos de la rueda. */
    @Test
    public void contagiousShouldSpreadColor() throws SlotMachineException
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
    public void unknownTypesShouldFail() throws SlotMachineException
    {
        expect(SlotMachineException.UNKNOWN_WHEEL_TYPE, () -> sm.addWheel("inexistente", 1));
        assertFalse(sm.ok());
        expect(SlotMachineException.UNKNOWN_SYMBOL_TYPE, () -> sm.addSymbol("inexistente", 1, 1, "red"));
        assertFalse(sm.ok());
    }

    /** NO DEBERIA: una maquina con numero de ruedas no positivo se acepta. */
    @Test
    public void nonPositiveWheelCountShouldFail() throws SlotMachineException
    {
        expect(SlotMachineException.INVALID_WHEEL_COUNT, () -> new SlotMachine(0));
        expect(SlotMachineException.INVALID_WHEEL_COUNT, () -> new SlotMachine(-2));
    }

    /** NO DEBERIA: un color nulo o no soportado se acepta al agregar un simbolo. */
    @Test
    public void invalidColorShouldFail() throws SlotMachineException
    {
        expect(SlotMachineException.INVALID_COLOR, () -> sm.addSymbol(1, 1, "purple"));
        expect(SlotMachineException.INVALID_COLOR, () -> sm.addSymbol(1, 1, null));
        assertEquals(2, sm.symbols().length);
    }

    /** NO DEBERIA: operar sobre una maquina sin ruedas. */
    @Test
    public void emptyMachineShouldFail() throws SlotMachineException
    {
        SlotMachine empty = new SlotMachine();
        expect(SlotMachineException.NO_WHEELS, () -> empty.spin());
        expect(SlotMachineException.NO_WHEELS, () -> empty.spin(1));
        expect(SlotMachineException.NO_WHEELS, () -> empty.swap(1, 2));
        expect(SlotMachineException.NO_WHEELS, () -> empty.unlock(1));
        expect(SlotMachineException.NO_WHEELS, () -> empty.addSymbol(1, 1, "red"));
        expect(SlotMachineException.NO_WHEELS, () -> empty.placeSymbol(1, "red"));
        expect(SlotMachineException.NO_WHEELS, () -> empty.solve());
    }

    /** NO DEBERIA: girar una rueda sin simbolos. */
    @Test
    public void spinEmptyWheelShouldFail() throws SlotMachineException
    {
        SlotMachine one = new SlotMachine();
        one.addWheel(1);
        expect(SlotMachineException.EMPTY_WHEEL, () -> one.spin(1));
        expect(SlotMachineException.EMPTY_WHEEL, () -> one.spin(1, 2));
        expect(SlotMachineException.EMPTY_WHEEL, () -> one.spin());
    }

    /** NO DEBERIA: eliminar un color que ninguna rueda tiene. */
    @Test
    public void delSymbolNotInAnyWheelShouldFail() throws SlotMachineException
    {
        expect(SlotMachineException.SYMBOL_NOT_IN_ANY_WHEEL, () -> sm.delSymbol("purple"));
        sm.delSymbol("red");
        assertTrue(sm.ok());
    }

    /** NO DEBERIA: simular sin resolver, o resolver una maquina sin color comun. */
    @Test
    public void solveAndSimulateShouldFailWhenImpossible() throws SlotMachineException
    {
        expect(SlotMachineException.NOT_SOLVED, () -> sm.simulate());
        SlotMachine none = new SlotMachine();
        none.addWheel(1);
        none.addWheel(2);
        none.addSymbol(1, 1, "red");
        none.addSymbol(2, 1, "blue");
        expect(SlotMachineException.NO_SOLUTION, () -> none.solve());
    }

    /** NO DEBERIA: simular cuando un simbolo contagioso hizo desaparecer el color objetivo (antes era un bucle infinito). */
    @Test
    public void simulateShouldFailWhenTargetColorDisappears() throws SlotMachineException
    {
        SlotMachine two = new SlotMachine();
        two.addWheel(1);
        two.addWheel(2);
        two.addSymbol(1, 1, "blue");
        two.addSymbol("contagious", 1, 2, "red");
        two.addSymbol(2, 1, "blue");
        two.addSymbol(2, 2, "red");
        assertTrue(two.solve());
        two.placeSymbol(1, "red");
        two.spin(1, 1);
        two.spin(1, 1);
        expect(SlotMachineException.SOLUTION_UNREACHABLE, () -> two.simulate());
    }

    /** NO DEBERIA: simular con una rueda fija que muestra otro color. */
    @Test
    public void simulateShouldFailWithLockedWheelOfOtherColor() throws SlotMachineException
    {
        SlotMachine two = new SlotMachine();
        two.addWheel(1);
        two.addWheel(2);
        two.addSymbol(1, 1, "red");
        two.addSymbol(1, 2, "blue");
        two.addSymbol(2, 1, "red");
        two.addSymbol(2, 2, "blue");
        assertTrue(two.solve());
        two.placeSymbol(2, "blue");
        two.lock(2);
        expect(SlotMachineException.SOLUTION_UNREACHABLE, () -> two.simulate());
    }

    /** DEBERIA: cada tipo de simbolo tiene una marca visual propia y distinta. */
    @Test
    public void everySymbolTypeShouldHaveDistinctMark() throws SlotMachineException
    {
        String[] marks = {
            new NormalSymbol("red", 0, 0).getMark(),
            new EphemeralSymbol("red", 0, 0).getMark(),
            new ShySymbol("red", 0, 0).getMark(),
            new ContagiousSymbol("red", 0, 0).getMark()
        };
        for (int i = 0; i < marks.length; i++)
            for (int j = i + 1; j < marks.length; j++)
                assertNotEquals(marks[i], marks[j]);
    }
}
