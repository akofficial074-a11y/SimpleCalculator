package test.com.example.calculator;

import org.junit.*;

import com.example.calculator.Calculator;

import static org.junit.Assert.*;

public class CalculatorTest {
    private Calculator c;

    @Before
    public void setUp() { c = new Calculator(); }

    @Test
    public void testAdd() { assertEquals(5.0, c.add(2,3), 0.0001); }

    @Test
    public void testSub() { assertEquals(-1.0, c.subtract(2,3), 0.0001); }

    @Test
    public void testMul() { assertEquals(6.0, c.multiply(2,3), 0.0001); }

    @Test
    public void testDiv() { assertEquals(2.0, c.divide(6,3), 0.0001); }

    @Test(expected = IllegalArgumentException.class)
    public void testDivideByZero() { c.divide(1, 0); }
}

