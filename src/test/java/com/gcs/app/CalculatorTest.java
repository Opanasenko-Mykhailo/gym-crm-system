package com.gcs.app;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CalculatorTest {

    private final Calculator calculator = new Calculator();

    @Test
    void testAdd() {
        assertEquals(5.0, calculator.add(2, 3));
        assertEquals(-1.0, calculator.add(-2, 1));
        assertEquals(0.0, calculator.add(0, 0));
    }

    @Test
    void testSubtract() {
        assertEquals(1.0, calculator.subtract(3, 2));
        assertEquals(-3.0, calculator.subtract(-2, 1));
        assertEquals(0.0, calculator.subtract(0, 0));
    }

    @Test
    void testMultiply() {
        assertEquals(6.0, calculator.multiply(2, 3));
        assertEquals(-2.0, calculator.multiply(-2, 1));
        assertEquals(0.0, calculator.multiply(0, 5));
    }

    @Test
    void testDivide() {
        assertEquals(2.0, calculator.divide(6, 3));
        assertEquals(-2.0, calculator.divide(-6, 3));
        assertEquals(0.0, calculator.divide(0, 5));
    }

    @Test
    void testDivideByZero() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> calculator.divide(5, 0));
        assertEquals("Division by zero is impossible!", exception.getMessage());
    }
}
