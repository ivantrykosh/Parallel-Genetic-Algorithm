package com.ivantrykosh.app.parallel_genetic_algorithm;


import com.ivantrykosh.app.parallel_genetic_algorithm.parallel.ParallelGeneticAlgorithm;

public class Main {
    public static void main(String[] args) {
        // todo засікати час без ініціалізації рюкзака
        warmup();
        long sumOfExecutionTimeForSGA = 0;
        long sumOfExecutionTimeForPGA5 = 0;
        long sumOfExecutionTimeForPGA10 = 0;
        int repeats = 20;
        // todo кількість тасок = кількість потоків
        for (int i = 0; i < repeats; i++) {
            GeneticAlgorithm ga = new GeneticAlgorithm(Constants.POPULATION_SIZE);
            long time1 = System.currentTimeMillis();
            Chromosome best;
            best = ga.start(1000); // change to 10000
            long time2 = System.currentTimeMillis();
            System.out.println("SGA:");
            System.out.printf("Max knapsack weight: %d; Best chromosome: fitness - %d, weight - %d \n", Constants.MAX_KNAPSACK_WEIGHT, best.calculateFitness(), best.calculateWeight());
            long executionTime = time2 - time1;
            sumOfExecutionTimeForSGA += executionTime;
            System.out.printf("Execution time for SGA = %dms \n", executionTime);

            ParallelGeneticAlgorithm pga = new ParallelGeneticAlgorithm(Constants.POPULATION_SIZE, 4);
            time1 = System.currentTimeMillis();
            best = pga.start(1000); // change to 10000
            time2 = System.currentTimeMillis();
            System.out.println("PGA for 4:");
            System.out.printf("Max knapsack weight: %d; Best chromosome: fitness - %d, weight - %d \n", Constants.MAX_KNAPSACK_WEIGHT, best.calculateFitness(), best.calculateWeight());
            executionTime = time2 - time1;
            sumOfExecutionTimeForPGA5 += executionTime;
            System.out.printf("Execution time for PGA = %dms \n", executionTime);

            pga = new ParallelGeneticAlgorithm(Constants.POPULATION_SIZE, 8);
            time1 = System.currentTimeMillis();
            best = pga.start(1000); // change to 10000
            time2 = System.currentTimeMillis();
            System.out.println("PGA for 8:");
            System.out.printf("Max knapsack weight: %d; Best chromosome: fitness - %d, weight - %d \n", Constants.MAX_KNAPSACK_WEIGHT, best.calculateFitness(), best.calculateWeight());
            executionTime = time2 - time1;
            sumOfExecutionTimeForPGA10 += executionTime;
            System.out.printf("Execution time for PGA = %dms \n", executionTime);

            System.out.println("-".repeat(75));
        }
        double avgSGA = (double) sumOfExecutionTimeForSGA / repeats;
        double avgPGA5 = (double) sumOfExecutionTimeForPGA5 / repeats;
        double avgPGA10 = (double) sumOfExecutionTimeForPGA10 / repeats;
        System.out.printf("Average execution time for SGA = %.3fms\n", avgSGA);
        System.out.printf("Average execution time for PGA5 = %.3fms\n", avgPGA5);
        System.out.printf("Average execution time for PGA8 = %.3fms\n", avgPGA10);
        System.out.printf("Speed up for 4 = %.3f\n", avgSGA / avgPGA5);
        System.out.printf("Speed up for 8 = %.3f\n", avgSGA / avgPGA10);
    }

    private static void warmup() {
        // todo
    }
}
