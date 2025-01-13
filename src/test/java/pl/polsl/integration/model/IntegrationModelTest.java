package pl.polsl.integration.model;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import pl.polsl.integration.controller.IntegrationController;

/**
 *
 * @version 4.0 final
 */
public class IntegrationModelTest {

    public IntegrationModelTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    /*
     * ARGUMENTS FORMATTING
     * -w : width, specifies the width of the trapezoid for calculating the
     * integration (either this or -d)
     * -d : divisions, specifies the number of trapezoids the function should be
     * divided into (either this or -w)
     * -min : starting point (inclusive)
     * -max : end point (inclusive)
     * -f : polynomial function in format a*x^n + b*x^m + ... (no spaces)
     */

    /**
     * Test for the correct handling of valid arguments in the {@link IntegrationModel#readArgs(String[])} method.
     * This test checks if the arguments passed to the method are parsed correctly and if the model properties 
     * such as mode, width, divisions, bounds, and function are set as expected.
     *
     * @param argsString        The input arguments as a single string (e.g., "-w 0.1 -min 0 -max 10 -f 2*x^2+3*x+1").
     * @param expectedMode      The expected mode (e.g., "w" for width or "d" for divisions).
     * @param expectedWidth     The expected width of the trapezoid (null if not applicable).
     * @param expectedDivisions The expected number of divisions (null if not applicable).
     * @param expectedMin       The expected minimum bound of the range.
     * @param expectedMax       The expected maximum bound of the range.
     * @param expectedFunction  The expected polynomial function as a string.
     */
    @ParameterizedTest
    @CsvSource({
            "-w 0.1 -min 0 -max 10 -f 2*x^2+3*x^1, w, 0.1, 0, 0, 10, 2*x^2+3*x^1",
            "-d 100 -min -5 -max 5 -f x^3-2*x^1, d, 0, 100, -5, 5, x^3-2*x^1",
            "-w 0.05 -min 1 -max 3 -f x^2, w, 0.05, 0, 1, 3, x^2",
            "-d 50 -min 0 -max 100 -f 5*x^4+3*x^2, d, 0, 50, 0, 100, 5*x^4+3*x^2",
            "-w 0.2 -min -10 -max -5 -f 4*x^3, w, 0.2, 0, -10, -5, 4*x^3"
    })
    public void testReadArgsCorrect(
        String argsString,
        String expectedMode,
        Double expectedWidth,
        Integer expectedDivisions,
        double expectedMin,
        double expectedMax,
        String expectedFunction) 
    {

        IntegrationController controller = new IntegrationController();
        String[] args = argsString.split(" ");

        try {
            controller.readArgs(args);

            assertEquals(expectedMin, controller.getRange().x(), "Min value mismatch!");
            assertEquals(expectedMax, controller.getRange().y(), "Max value mismatch!");
            assertEquals(expectedFunction, controller.getFunction(), "Function string mismatch!");

            switch (expectedMode) {
                case "d" -> {
                    assertEquals(IntegrationStrategyEnum.DivisionsCount, controller.getIntegrationStrategy(),
                            "Mode value mismatch.");
                    assertEquals(expectedDivisions, controller.getD(), "Divisions value mismatch.");
                }
                case "w" -> {
                    assertEquals(IntegrationStrategyEnum.TrapesoidWidth, controller.getIntegrationStrategy(),
                            "Mode value mismatch.");
                    assertEquals(expectedWidth, controller.getW(), 0.0001, "Width value mismatch.");
                }
                default -> fail("Unexpected mode value.");
            }
        } catch (IntegrationException e) {
            fail("No exception expected for valid arguments, but got: " + e.getMessage());
        }
    }

    /**
     * Test for invalid arguments in the {@link IntegrationModel#readArgs(String[])} method.
     * This test checks if the {@link IntegrationException} is thrown when invalid arguments are passed, such as missing parameters, 
     * invalid function format, or unsupported mode.
     * 
     * @param argsString The input arguments as a single string (e.g., "-w 0.1", "-d 1").
     */
    @ParameterizedTest
    @CsvSource({
            "-w 0.1", // Missing multiple parameters
            "-d 1", // Missing multiple parameters
            "-min 0", // Missing multiple parameters
            "-max 10", // Missing multiple parameters
            "-f 3*x^2", // Missing multiple parameters
            "-w 0.1 -min 0 -max 10", // Missing function
            "-d 100 -min -5 -max 5", // Missing function
            "-w 1 -max 10 -f 2*x^2", // Missing min
            "-w 1 -min 0 -f 3*x^2",// Missing max
            "-z 0.2 -min -10 -max -5 -f 4*x^3+2", // Invalid mode (-z)
            "-d 50 -min 0 -max 100 -f", // Missing parameter value
            "-w 0.2 -min -10 -max -5 -f 4*sqrt(x)", // Invalid function
            "-w 0 -min 0 -max 10 -f 3*x^2", // Invalid width ( Zero )
            "-d 0 -min -5 -max 5 -f x^3-2*x^1", // Invalid divisions ( Zero )
            "-w -1 -min 1 -max 3 -f x^2", // Invalid width ( Negative )
            "-d -50 -min 0 -max 100 -f 5*x^4+3*x^2", // Invalid divisions ( Negative )
            "-d 1.5 -min -10 -max -5 -f 4*x^3", // Invalid divisions ( Non-integer )
    })
    public void testReadArgsInvalid(String argsString) {
        IntegrationController controller = new IntegrationController();
        String[] args = argsString.split(" ");

        try {
            controller.readArgs(args);
            fail("Expected exception but no exception was thrown.");
        } catch (IntegrationException e) {
        }
    }

    /**
     * Test for setting valid modes in the {@link IntegrationModel#setIntegrationStrategy(IntegrationStrategyEnum)} method.
     * This test ensures that valid modes ('d' for divisions and 'w' for width) are correctly set and the corresponding strategy is applied.
     * 
     * @param m The mode character ('d' or 'w') to test.
     */
    @ParameterizedTest
    @ValueSource(chars = {'d', 'w'})
    public void testSetModeCorrect(char m) {
        IntegrationModel instance = new IntegrationModel();
        try {
            switch (m) {
                case 'd' -> {
                    instance.setIntegrationStrategy(IntegrationStrategyEnum.DivisionsCount);
                    assertEquals(IntegrationStrategyEnum.DivisionsCount, instance.getIntegrationStrategy(),
                            "Mode value mismatch.");
                }
                case 'w' -> {
                    instance.setIntegrationStrategy(IntegrationStrategyEnum.TrapesoidWidth);
                    assertEquals(IntegrationStrategyEnum.TrapesoidWidth, instance.getIntegrationStrategy(),
                            "Mode value mismatch.");
                }
                default -> fail("Unexpected mode value.");
            }
        } catch (IllegalArgumentException e) {
            fail("No exception expected for valid mode, but got: " + e.getMessage());
        }
    }
    

    /**
     * Test for the readiness of the model in the {@link IntegrationModel#isReady()} method.
     * This test verifies that the model is not considered ready until all necessary parameters are set,
     * such as bounds, function, and strategy. The method also checks that the model correctly flags missing parameters
     * before it is validated and considered ready.
     */
    @Test
    public void testIsReady()
    {
        IntegrationModel instance = new IntegrationModel();
        assertFalse(instance.isReady(), "Model should not be ready before setting arguments.");

        try {
            instance.validate();
            assertFalse(instance.isReady(), "Model should not be ready before setting all arguments.");
            fail("Expected exception but no exception was thrown.");
        } catch (IntegrationException e) {
        }

        instance.setDivisions(2);
        try {
            instance.validate();
            assertFalse(instance.isReady(), "Model should not be ready before setting all arguments.");
            fail("Expected exception but no exception was thrown.");
        } catch (IntegrationException e) {
        }

        instance.setIntegrationStrategy(IntegrationStrategyEnum.DivisionsCount);      
        
        instance.setLowerBound(0);
        instance.setUpperBound(10);
        instance.setFunction("3*x^2");
        try {
            instance.validate();
            assertTrue(instance.isReady(), "Model should be ready after setting arguments.");
        } catch (IntegrationException e) {
            fail("No exception expected for valid arguments, but got: " + e.getMessage());
        }
    }

    @ParameterizedTest
    @CsvSource({
        "0, 10",  // Przypadek standardowy
        "-5, 5",  // Przypadek z ujemnymi liczbami
        "1, 1"     // Graniczny przypadek (granice równe)
    })
    void testFlipBounds(double min, double max) {
        IntegrationModel instance = new IntegrationModel();

        instance.setLowerBound(min);
        instance.setUpperBound(max);
        
        instance.flipBounds();
        
        assertEquals(min, instance.getUpperBound());
        assertEquals(max, instance.getLowerBound());
    }

    /**
     * Test for the calculation of the integral in the {@link IntegrationModel#calculate()} method.
     * This test verifies that the integration is correctly computed using both width-based and division-based strategies,
     * and checks that the result matches the expected value within an acceptable error margin.
     */
    @Test
    void testCalcualte() {
        IntegrationModel instance = new IntegrationModel();
        instance.setLowerBound(0);
        instance.setUpperBound(10);
        instance.setFunction("2*x^1");
        instance.setIntegrationStrategy(IntegrationStrategyEnum.TrapesoidWidth);
        instance.setWidth(0.125);

        try {
            double result = instance.calculate();
            assertEquals(100, result, 0.1, "Result should be 100.");
        } catch (IntegrationException e) {
            fail("No exception expected for valid arguments, but got: " + e.getMessage());
        }

        instance.setIntegrationStrategy(IntegrationStrategyEnum.DivisionsCount);
        instance.setDivisions(100);
        try {
            double result = instance.calculate();
            assertEquals(100, result, 0.1, "Result should be 100.");
        } catch (IntegrationException e) {
            fail("No exception expected for valid arguments, but got: " + e.getMessage());
        }
    }
}
