package com.ivantrykosh.app.parallel_genetic_algorithm;

public final class Constants {
    public static final int MAX_ITEM_WEIGHT = 100;
    public static final int MAX_ITEM_VALUE = 100;
    public static final int MAX_KNAPSACK_WEIGHT = 1000;
    public static final int NUMBER_OF_ITEMS = 100;
    public static final double MUTATION_RATE = 0.01;
    public static final double TERMINATION_VALUE = 0.5;

    private Constants() {
        throw new AssertionError();
    }
}
