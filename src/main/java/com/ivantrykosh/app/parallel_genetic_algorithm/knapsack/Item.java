package com.ivantrykosh.app.parallel_genetic_algorithm.knapsack;

import java.util.Random;

public class Item {
    private final int weight;
    private final int value;

    public Item(int maxItemWeight, int maxItemValue) {
        this(1, maxItemWeight, 1, maxItemValue);
    }

    public Item(int minItemWeight, int maxItemWeight, int minItemValue, int maxItemValue) {
        Random random = new Random();

        weight = random.nextInt(minItemWeight, maxItemWeight);
        value = random.nextInt(minItemValue, maxItemValue);
    }

    public int getWeight() {
        return weight;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return '{' +
                "w=" + weight +
                ", v=" + value +
                '}';
    }
}
