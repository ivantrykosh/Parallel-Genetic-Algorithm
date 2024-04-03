package com.ivantrykosh.app.parallel_genetic_algorithm.knapsack;

import com.ivantrykosh.app.parallel_genetic_algorithm.Constants;

import java.util.ArrayList;
import java.util.List;

public final class Items {
    private static volatile Items instance;

    private final List<Item> items = new ArrayList<>();

    private Items(int numberOfItems, int maxItemWeight, int maxItemValue) {
        for (int i = 0; i < numberOfItems; i++) {
            Item newItem = new Item(maxItemWeight, maxItemValue);
            items.add(newItem);
        }
    }

    public List<Item> getItems() {
        return items;
    }

    public static Items getInstance() {
        if (instance != null) {
            return instance;
        }
        synchronized (Items.class) {
            if (instance == null) {
                instance = new Items(Constants.NUMBER_OF_ITEMS, Constants.MAX_ITEM_WEIGHT, Constants.MAX_ITEM_VALUE);
            }
            return instance;
        }
    }

    @Override
    public String toString() {
        return items.toString();
    }
}
