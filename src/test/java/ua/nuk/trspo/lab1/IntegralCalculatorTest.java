package ua.nuk.trspo.lab1;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IntegralCalculatorTest {

    private static final double EXACT = 2 * Math.log(2) - 1;

    private static Function constant(double c) {
        return new Function() {
            @Override
            public double calculate(double x) {
                return c;
            }
        };
    }

    private static Function identity() {
        return new Function() {
            @Override
            public double calculate(double x) {
                return x;
            }
        };
    }

    @Test
    public void constantFunction() {
        assertEquals(6.0, new IntegralCalculator(3).calculate(0, 3, 1000, constant(2)), 1e-9);
    }

    @Test
    public void rightRectanglesFormulaOnSmallExample() {
        assertEquals(0.625, new IntegralCalculator(1).calculate(0, 1, 4, identity()), 1e-12);
    }

    @Test
    public void variantIntegralIsCloseToExactValue() {
        double r = new IntegralCalculator(4).calculate(0, 1, 2_000_000, new Function());
        assertEquals(EXACT, r, 1e-6);
    }

    @Test
    public void resultDoesNotDependOnThreadCount() {
        double single = new IntegralCalculator(1).calculate(0, 1, 100_000, new Function());
        for (int t = 2; t <= 20; t++) {
            double r = new IntegralCalculator(t).calculate(0, 1, 100_000, new Function());
            assertEquals("потоків: " + t, single, r, 1e-9);
        }
    }

    @Test
    public void moreThreadsThanSteps() {
        assertEquals(6.0, new IntegralCalculator(10).calculate(0, 3, 3, identity()), 1e-12);
    }

    @Test
    public void singleStep() {
        assertEquals(Math.log(2), new IntegralCalculator(5).calculate(0, 1, 1, new Function()), 1e-12);
    }

    @Test(expected = IllegalArgumentException.class)
    public void zeroThreadsRejected() {
        new IntegralCalculator(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void zeroStepsRejected() {
        new IntegralCalculator(2).calculate(0, 1, 0, new Function());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullFunctionRejected() {
        new IntegralCalculator(2).calculate(0, 1, 10, null);
    }
}
