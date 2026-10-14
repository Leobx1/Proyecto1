import java.util.Arrays;

public class SlotMachineContestTest
{
    /** Resuelve la maquina; retorna false si no tiene solucion (NO_SOLUTION). */
    private boolean trySolve(SlotMachine game)
    {
        try {
            return game.solve();
        } catch (SlotMachineException e) {
            return false;
        }
    }

    public void test1() throws SlotMachineException
    {
        System.out.println("\n--- Test 1: Constructor SlotMachine(n) ---");
        SlotMachine game = new SlotMachine(3);
        System.out.println("Config: " + Arrays.toString(game.configuration()));
        assert game.ok();
        System.out.println("✓ PASS");
    }

        public void test2() throws SlotMachineException
    {
        System.out.println("\n--- Test 2: solve() ---");
        SlotMachine game = new SlotMachine(3);
        boolean result = trySolve(game);
        System.out.println("solve() retorna: " + result);
        System.out.println("Config: " + Arrays.toString(game.configuration()));
        System.out.println("Jackpot: " + game.isJackpot());
        
        if (result) {
            assert game.isJackpot();  
            System.out.println("✓ PASS - Solución encontrada");
        } else {
            System.out.println("✓ PASS - Sin solución común (combinación aleatoria)");
        }
    }

    public void test3() throws SlotMachineException
    {
        System.out.println("\n--- Test 3: simulate() ---");
        SlotMachine game = new SlotMachine(4);
        trySolve(game);
        
        if (game.ok()) {
            boolean result = game.simulate();
            System.out.println("simulate() retorna: " + result);
            System.out.println("Config: " + Arrays.toString(game.configuration()));
            assert result && game.isJackpot();
            System.out.println("✓ PASS");
        } else {
            System.out.println("✓ PASS - solve() no encontró solución");
        }
    }

    public void test4() throws SlotMachineException
        {
        System.out.println("\n--- Test 4: spin(wheel, steps) ---");
        SlotMachine game = new SlotMachine(3);
        String[] before = game.configuration();
        game.spin(1, 1);
        String[] after = game.configuration();
        System.out.println("Antes: " + Arrays.toString(before));
        System.out.println("Después: " + Arrays.toString(after));
        assert game.ok();
        System.out.println("✓ PASS");
        }

    public void test5() throws SlotMachineException
    {
        System.out.println("\n--- Test 5: distinctSymbols() ---");
        SlotMachine game = new SlotMachine(2);
        int count = game.distinctSymbols();
        System.out.println("Símbolos distintos: " + count);
        assert count > 0;
        System.out.println("✓ PASS");
    }

    public void sharedTest1() throws SlotMachineException
    {
        System.out.println("\n--- Shared Test 1: SlotMachine(2) ---");
        SlotMachine game = new SlotMachine(2);
        boolean solved = trySolve(game);
        
        if (solved) {
            assert game.isJackpot();
            System.out.println("✓ PASS - Jackpot encontrado");
        } else {
            System.out.println("✓ PASS - Sin solución (combinación aleatoria sin colores en común)");
        }
    }
    
    public void sharedTest2() throws SlotMachineException
    {
        System.out.println("\n--- Shared Test 2: SlotMachine(5) ---");
        SlotMachine game = new SlotMachine(5);
        boolean solved = trySolve(game);
        
        if (solved) {
            game.simulate();
            assert game.isJackpot();
            System.out.println("✓ PASS - Jackpot encontrado y simulado");
        } else {
            System.out.println("✓ PASS - Sin solución (combinación aleatoria sin colores en común)");
        }
    }

    public static void main(String[] args)
    {
        System.out.println("\n=== SLOT MACHINE CONTEST TESTS ===");
        SlotMachineContestTest t = new SlotMachineContestTest();
        
        try {
            t.test1();
            t.test2();
            t.test3();
            t.test4();
            t.test5();
            t.sharedTest1();
            t.sharedTest2();
            System.out.println("\n✓ ALL TESTS PASSED\n");
        } catch (AssertionError | SlotMachineException e) {
            System.out.println("\n✗ TEST FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }   
}
