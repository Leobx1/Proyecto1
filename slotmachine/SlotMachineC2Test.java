import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Pruebas de unidad del Ciclo 2 (swap, lock/unlock, spin con pasos,
 * spin con configuracion dada) y de regresion del Ciclo 1.
 *
 * IMPORTANTE: todas las pruebas se ejecutan en modo invisible
 * (nunca se llama a makeVisible()), tal como lo exige el enunciado.
 *
 * Cada prueba documenta si valida "que deberia hacer" (caso valido)
 * o "que NO deberia hacer" (caso invalido / de error).
 */
public class SlotMachineC2Test
{
    private SlotMachine sm;

    /** Crea una maquina de 3 ruedas con simbolos conocidos antes de cada prueba. */
    @Before
    public void setUp()
    {
        sm = new SlotMachine();
        sm.addWheel(1);
        sm.addWheel(2);
        sm.addWheel(3);
        sm.addSymbol(1, 1, "red");
        sm.addSymbol(1, 2, "green");
        sm.addSymbol(2, 1, "blue");
        sm.addSymbol(2, 2, "red");
        sm.addSymbol(3, 1, "yellow");
        sm.addSymbol(3, 2, "red");
    }

    // ---------- Regresion Ciclo 1 ----------

    /** QUE DEBERIA HACER: agregar una rueda incrementa la cantidad de ruedas. */
    @Test
    public void addWheelShouldAddOneWheel()
    {
        int before = sm.configuration().length;
        sm.addWheel(1);
        assertEquals(before + 1, sm.configuration().length);
        assertTrue(sm.ok());
    }

    /** QUE NO DEBERIA HACER: eliminar una rueda de una maquina vacia debe fallar. */
    @Test
    public void delWheelOnEmptyMachineShouldFail()
    {
        SlotMachine empty = new SlotMachine();
        empty.delWheel(1);
        assertFalse(empty.ok());
    }

    /** QUE DEBERIA HACER: placeSymbol ubica el color indicado como el actual. */
    @Test
    public void placeSymbolShouldSetCurrentSymbol()
    {
        sm.placeSymbol(1, "green");
        assertEquals("green", sm.configuration()[0]);
        assertTrue(sm.ok());
    }

    /** QUE NO DEBERIA HACER: placeSymbol con un color inexistente debe fallar. */
    @Test
    public void placeSymbolWithUnknownColorShouldFail()
    {
        sm.placeSymbol(1, "purple");
        assertFalse(sm.ok());
    }

    /** QUE DEBERIA HACER: isJackpot es verdadero cuando las 3 ruedas muestran el mismo color. */
    @Test
    public void isJackpotShouldBeTrueWhenAllWheelsMatch()
    {
        sm.spin(new String[]{"red", "red", "red"});
        assertTrue(sm.isJackpot());
    }

    /** QUE NO DEBERIA HACER: isJackpot debe ser falso cuando los colores difieren. */
    @Test
    public void isJackpotShouldBeFalseWhenWheelsDiffer()
    {
        sm.spin(new String[]{"red", "blue", "yellow"});
        assertFalse(sm.isJackpot());
    }

    // ---------- Ciclo 2: lock / unlock ----------

    /** QUE DEBERIA HACER: una rueda fijada queda marcada como no disponible para girar. */
    @Test
    public void lockShouldPreventSingleWheelSpin()
    {
        sm.lock(2);
        String before = sm.configuration()[1];
        sm.spin(2);
        assertFalse(sm.ok());
        assertEquals(before, sm.configuration()[1]);
    }

    /** QUE DEBERIA HACER: al soltar una rueda, spin(wheel) vuelve a funcionar sobre ella. */
    @Test
    public void unlockShouldAllowSpinAgain()
    {
        sm.lock(2);
        sm.unlock(2);
        sm.spin(2);
        assertTrue(sm.ok());
    }

    /** QUE DEBERIA HACER: spin() general debe saltar las ruedas fijas y girar las demas. */
    @Test
    public void generalSpinShouldSkipLockedWheels()
    {
        sm.lock(2);
        String lockedBefore = sm.configuration()[1];
        sm.spin();
        assertTrue(sm.ok());
        assertEquals(lockedBefore, sm.configuration()[1]);
    }

    /** QUE NO DEBERIA HACER: lock sobre una maquina sin ruedas debe fallar. */
    @Test
    public void lockOnEmptyMachineShouldFail()
    {
        SlotMachine empty = new SlotMachine();
        empty.lock(1);
        assertFalse(empty.ok());
    }

    // ---------- Ciclo 2: swap ----------

    /** QUE DEBERIA HACER: swap intercambia el contenido visible de dos ruedas. */
    @Test
    public void swapShouldExchangeWheelConfigurations()
    {
        sm.placeSymbol(1, "red");
        sm.placeSymbol(2, "blue");
        String[] before = sm.configuration().clone();
        sm.swap(1, 2);
        assertTrue(sm.ok());
        assertEquals(before[1], sm.configuration()[0]);
        assertEquals(before[0], sm.configuration()[1]);
    }

    /** QUE NO DEBERIA HACER: swap con una rueda fija no debe realizar el intercambio. */
    @Test
    public void swapShouldFailWhenAWheelIsLocked()
    {
        sm.lock(1);
        String[] before = sm.configuration().clone();
        sm.swap(1, 2);
        assertFalse(sm.ok());
        assertArrayEquals(before, sm.configuration());
    }

    // ---------- Ciclo 2: spin(wheel, steps) ----------

    /** QUE DEBERIA HACER: spin(wheel, steps) avanza exactamente el numero de pasos dado. */
    @Test
    public void spinWithStepsShouldAdvanceExactAmount()
    {
        sm.placeSymbol(1, "red"); // wheel1 = [red, green], indice 0
        sm.spin(1, 1);
        assertEquals("green", sm.configuration()[0]);
        assertTrue(sm.ok());
    }

    /** QUE DEBERIA HACER: spin(wheel, steps) debe ciclar correctamente al pasar el limite. */
    @Test
    public void spinWithStepsShouldWrapAround()
    {
        sm.placeSymbol(1, "red"); // wheel1 tiene 2 simbolos
        sm.spin(1, 2); // dos pasos en una rueda de 2 -> vuelve al mismo simbolo
        assertEquals("red", sm.configuration()[0]);
    }

    /** QUE NO DEBERIA HACER: spin(wheel, steps) sobre una rueda fija no debe moverla. */
    @Test
    public void spinWithStepsShouldFailWhenWheelIsLocked()
    {
        sm.lock(3);
        String before = sm.configuration()[2];
        sm.spin(3, 1);
        assertFalse(sm.ok());
        assertEquals(before, sm.configuration()[2]);
    }

    // ---------- Ciclo 2: spin(setSymbols) ----------

    /** QUE DEBERIA HACER: spin(setSymbols) deja la maquina exactamente en la configuracion dada. */
    @Test
    public void spinWithConfigurationShouldApplyGivenColors()
    {
        sm.spin(new String[]{"red", "blue", "yellow"});
        assertTrue(sm.ok());
        assertArrayEquals(new String[]{"red", "blue", "yellow"}, sm.configuration());
    }

    /** QUE NO DEBERIA HACER: spin(setSymbols) con un tamano distinto al numero de ruedas debe fallar. */
    @Test
    public void spinWithConfigurationShouldFailWithWrongSize()
    {
        String[] before = sm.configuration().clone();
        sm.spin(new String[]{"red", "blue"});
        assertFalse(sm.ok());
        assertArrayEquals(before, sm.configuration());
    }

    /** QUE NO DEBERIA HACER: spin(setSymbols) con un color inexistente en alguna rueda debe fallar
     *  sin alterar ninguna rueda (todo o nada). */
    @Test
    public void spinWithConfigurationShouldFailWithUnknownColorAndNotChangeAnything()
    {
        String[] before = sm.configuration().clone();
        sm.spin(new String[]{"red", "purple", "yellow"});
        assertFalse(sm.ok());
        assertArrayEquals(before, sm.configuration());
    }
}
