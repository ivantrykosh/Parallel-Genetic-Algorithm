package com.ivantrykosh.app.parallel_genetic_algorithm;


import com.ivantrykosh.app.parallel_genetic_algorithm.parallel.ParallelGeneticAlgorithm;

public class Main {
    public static void main(String[] args) {
//        example();
        warmup();
        int[] numberOfThread = { 2, 4, 8, 10 };
        long sumOfExecutionTimeForSGA = 0;
        long[] sumOfExecutionTimeForPGAs = new long[numberOfThread.length];
        int repeats = 2;
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
        System.out.printf("Average execution time for SGA = %.3fms\n", avgSGA);
        for (int i = 0; i < numberOfThread.length; i++) {
            double avgPGA = (double) sumOfExecutionTimeForPGAs[i] / repeats;
            System.out.printf("Average execution time for PGA (%2d threads) = %9.3fs, speed up = %6.3f\n", numberOfThread[i], avgPGA / 1000, avgSGA / avgPGA);
        }
    }

    private static void example() {
        // example
    }

    private static void warmup() {
        long sum = 0;
        for (int i = 0; i < 10; i++) {
            for (long j = 0; j < 10000000000L; j++) {
                sum += j - (j / 2) * 2;
            }
            sum = 0;
        }
        System.out.println(sum);
    }
}
