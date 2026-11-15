package ua.nuk.trspo.lab1;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FunctionTest {

    private final Function f = new Function();

    @Test
    public void zeroGivesLnOne() {
        assertEquals(0.0, f.calculate(0), 1e-12);
    }

    @Test
    public void eMinusOneGivesOne() {
        assertEquals(1.0, f.calculate(Math.E - 1), 1e-12);
    }

    @Test
    public void oneGivesLnTwo() {
        assertEquals(Math.log(2), f.calculate(1), 1e-12);
    }
}
