package com.ivantrykosh.app.parallel_genetic_algorithm;


import com.ivantrykosh.app.parallel_genetic_algorithm.parallel.ParallelGeneticAlgorithm;

public class Main {
    public static void main(String[] args) {
        // todo засікати час без ініціалізації рюкзака
        warmup();
        long sumOfExecutionTimeForSGA = 0;
        long sumOfExecutionTimeForPGA = 0;
        int repeats = 20;
        // todo кількість тасок = кількість потоків
        for (int i = 0; i < repeats; i++) {
            GeneticAlgorithm ga = new GeneticAlgorithm(Constants.POPULATION_SIZE);
            long time1 = System.currentTimeMillis();
            Chromosome best = ga.start(1000); // change to 10000
            long time2 = System.currentTimeMillis();
            System.out.println("SGA:");
            System.out.printf("Max knapsack weight: %d; Best chromosome: fitness - %d, weight - %d \n", Constants.MAX_KNAPSACK_WEIGHT, best.calculateFitness(), best.calculateWeight());
            long executionTime = time2 - time1;
            sumOfExecutionTimeForSGA += executionTime;
            System.out.printf("Execution time for SGA = %dms \n", executionTime);

            ParallelGeneticAlgorithm pga = new ParallelGeneticAlgorithm(Constants.POPULATION_SIZE);
            time1 = System.currentTimeMillis();
            best = pga.start(1000); // change to 10000
            time2 = System.currentTimeMillis();
            System.out.println("PGA:");
            System.out.printf("Max knapsack weight: %d; Best chromosome: fitness - %d, weight - %d \n", Constants.MAX_KNAPSACK_WEIGHT, best.calculateFitness(), best.calculateWeight());
            executionTime = time2 - time1;
            sumOfExecutionTimeForPGA += executionTime;
            System.out.printf("Execution time for PGA = %dms \n", executionTime);

            System.out.println("-".repeat(75));
        }
        double avgSGA = (double) sumOfExecutionTimeForSGA / repeats;
        double avgPGA = (double) sumOfExecutionTimeForPGA / repeats;
        System.out.printf("Average execution time for SGA = %.3fms\n", avgSGA);
        System.out.printf("Average execution time for PGA = %.3fms\n", avgPGA);
        System.out.printf("Speed up = %.3f", avgSGA / avgPGA);
    }

    private static void warmup() {
        // todo
    }
}
