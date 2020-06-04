package com.ulstu.pharmacy.generator.random;

public class RandomHelper {

    public static int randomInRange(int min, int max) {
        return (int) (min + (Math.random() * max));
    }
}