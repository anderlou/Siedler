package ch.zhaw.utils;

import java.util.Random;

/**
 * Helper class
 *
 * @author StackOverflow
 * @version 1.0
 */
public final class Helper {

    private Helper() {
    }

    /**
     * Generates a random number between 2 and 12.
     *
     * @return An integer between 2 and 12 representing the value of two dice.
     */
    public static int generateDiceThrow() {
        Random random = new Random();
        int dice1 = random.nextInt(1, 7);
        int dice2 = random.nextInt(1, 7);
        return dice1 + dice2;
    }
}