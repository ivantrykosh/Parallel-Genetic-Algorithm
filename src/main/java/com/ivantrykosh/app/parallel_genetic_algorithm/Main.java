package com.ivantrykosh.app.parallel_genetic_algorithm;


import com.ivantrykosh.app.parallel_genetic_algorithm.knapsack.Items;
import com.ivantrykosh.app.parallel_genetic_algorithm.parallel.ParallelGeneticAlgorithm;

public class Main {
    public static void main(String[] args) {
        Items.getInstance();
//        example();
        warmup();
        test();
    }

    private static void test() {
        int[] numberOfThread = { 2, 4, 8, 10 };
        long sumOfExecutionTimeForSGA = 0;
        long[] sumOfExecutionTimeForPGAs = new long[numberOfThread.length];
        int repeats = 20;
        for (int i = 0; i < repeats; i++) {
            GeneticAlgorithm ga = new GeneticAlgorithm(Constants.POPULATION_SIZE);
            Result sgaResult = ga.start(1000);
            System.out.println(sgaResult);
            sumOfExecutionTimeForSGA += sgaResult.getExecutionTimeInMillis();

            for (int j = 0; j < numberOfThread.length; j++) {
                ParallelGeneticAlgorithm pga = new ParallelGeneticAlgorithm(Constants.POPULATION_SIZE, numberOfThread[j]);
                Result pgaResult = pga.start(1000);
                System.out.println(pgaResult);
                sumOfExecutionTimeForPGAs[j] += pgaResult.getExecutionTimeInMillis();
            }

            System.out.println("-".repeat(100));
        }
        double avgSGA = (double) sumOfExecutionTimeForSGA / repeats;
        System.out.printf("Average execution time for SGA = %.3fs\n", avgSGA / 1000);
        for (int i = 0; i < numberOfThread.length; i++) {
            double avgPGA = (double) sumOfExecutionTimeForPGAs[i] / repeats;
            System.out.printf("Average execution time for PGA (%2d threads) = %9.3fs, speed up = %6.3f\n", numberOfThread[i], avgPGA / 1000, avgSGA / avgPGA);
        }
    }

    private static void example() {
        // You need to change to example parameters in Constants and change to example instance in Items
        System.out.println("Example:");
        System.out.println(Items.getInstance());
        int[] numberOfThread = { 2, 4, 8, 10 };
        GeneticAlgorithm ga = new GeneticAlgorithm(Constants.POPULATION_SIZE);
        Result sgaResult = ga.start(1000);
        System.out.println(sgaResult);
        System.out.println("Best chromosome:" + sgaResult.getBestChromosome());

        for (int j = 0; j < numberOfThread.length; j++) {
            ParallelGeneticAlgorithm pga = new ParallelGeneticAlgorithm(Constants.POPULATION_SIZE, numberOfThread[j]);
            Result pgaResult = pga.start(1000);
            System.out.println(pgaResult);
            System.out.println("Best chromosome:" + pgaResult.getBestChromosome());
        }

        System.out.println("-".repeat(100));
    }

    private static void warmup() {
        long sum = 0;
        for (int i = 0; i < 10; i++) {
            for (long j = 0; j < 10000000000L; j++) {
                sum += j - (j / 2) * 2;
            }
            sum -= sum;
        }
    }
}
