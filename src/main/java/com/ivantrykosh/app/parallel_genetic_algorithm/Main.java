package com.ivantrykosh.app.parallel_genetic_algorithm;


public class Main {
    public static void main(String[] args) {
        long sumOfExecutionTime = 0;
        int repeats = 20;
        for (int i = 0; i < repeats; i++) {
            GeneticAlgorithm ga = new GeneticAlgorithm(100);
            long time1 = System.currentTimeMillis();
            Chromosome best = ga.start(10000);
            long time2 = System.currentTimeMillis();
            System.out.printf("Max knapsack weight: %d; Best chromosome: fitness - %d, weight - %d \n", Constants.MAX_KNAPSACK_WEIGHT, best.calculateFitness(), best.calculateWeight());
            long executionTime = time2 - time1;
            sumOfExecutionTime += executionTime;
            System.out.printf("Execution time for SGA = %dms \n", executionTime);
        }
        System.out.printf("Average execution time = %.3fms\n", (double) sumOfExecutionTime / repeats);
    }
}
