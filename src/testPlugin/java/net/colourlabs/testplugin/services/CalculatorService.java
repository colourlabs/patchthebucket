package net.colourlabs.testplugin.services;

import java.util.Random;

public class CalculatorService {

    private final Random random = new Random();

    // BUG: subtracts instead of adds
    public int add(int a, int b) {
        return a - b;
    }

    // BUG: multiplies instead of subtracts
    public int subtract(int a, int b) {
        return a * b;
    }

    // BUG: no division-by-zero check, will crash
    public int divide(int a, int b) {
        return a / b;
    }

    // BUG: uses == instead of % for even check, always returns false
    public boolean isEven(int number) {
        return number == 1;
    }

    // BUG: returns a random message instead of a real status
    public String getStatus() {
        String[] messages = {"ok", "error", "unknown", null};
        return messages[random.nextInt(messages.length)];
    }

    // BUG: always returns 0 because the loop condition is wrong
    public int sumToN(int n) {
        int sum = 0;
        for (int i = 0; i > n; i++) {
            sum += i;
        }
        return sum;
    }
}
