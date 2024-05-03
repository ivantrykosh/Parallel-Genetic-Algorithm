package com.ivantrykosh.app.parallel_genetic_algorithm;

public final class Constants {
    // Constants for example
//    public static final int MAX_KNAPSACK_WEIGHT = 3000;
//    public static final int NUMBER_OF_ITEMS = 400;

    public static final int MAX_ITEM_WEIGHT = 100;
    public static final int MAX_ITEM_VALUE = 100;
    public static final int MAX_KNAPSACK_WEIGHT = 2500;
    public static final int NUMBER_OF_ITEMS = 100;
    public static final double MUTATION_RATE = 0.01;
    public static final int POPULATION_SIZE = 400;
    public static final double MIGRATION_PERCENTAGE = 0.1;
    public static final int ITERATION_FOR_MIGRATION = 50;
    public static final int NUMBER_OF_ISLANDS = 20;

    private Constants() {
        throw new AssertionError();
    }
}
