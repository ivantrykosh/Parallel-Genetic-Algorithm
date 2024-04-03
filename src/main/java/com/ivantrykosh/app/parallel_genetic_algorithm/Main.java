package com.ivantrykosh.app.parallel_genetic_algorithm;

import com.ivantrykosh.app.parallel_genetic_algorithm.knapsack.Items;

public class Main {
    public static void main(String[] args) {
        Population population = new Population(10);
        System.out.println(population);
        System.out.println(population.getBestChromosomeIndex());
        System.out.println(Items.getInstance().getItems());
    }
}
