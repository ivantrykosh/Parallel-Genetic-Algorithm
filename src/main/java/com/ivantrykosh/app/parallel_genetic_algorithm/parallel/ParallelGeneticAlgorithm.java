package com.ivantrykosh.app.parallel_genetic_algorithm.parallel;

import com.ivantrykosh.app.parallel_genetic_algorithm.*;

import java.util.*;
import java.util.concurrent.*;

public class ParallelGeneticAlgorithm extends GeneticAlgorithm {
    private final int numberOfThreads;

    public ParallelGeneticAlgorithm(int populationSize, int numberOfThreads) {
        super(populationSize);
        this.numberOfThreads = numberOfThreads;
    }

    @Override
    public Result start(int maxIterations) {
        long time1 = System.currentTimeMillis();

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        List<Chromosome> populationList = this.population.getChromosomes();
        Collections.shuffle(populationList);

        List<Future<Chromosome>> futureOffspring = new ArrayList<>(Constants.NUMBER_OF_ISLANDS);
        int sizeOfPart = population.getSize() / Constants.NUMBER_OF_ISLANDS;
        Migration migration = null;
        for (int j = 0; j < Constants.NUMBER_OF_ISLANDS; j++) {
            if (j % 2 == 0) {
                if (j == Constants.NUMBER_OF_ISLANDS - 1 && Constants.NUMBER_OF_ISLANDS % 2 == 1) {
                    migration = new Migration(1);
                } else {
                    migration = new Migration(2);
                }
            }

            List<Chromosome> subPopulation = new ArrayList<>(populationList.subList(j * sizeOfPart, (j + 1) * sizeOfPart));
            Island island = new Island(maxIterations, new Population(subPopulation), migration);
            futureOffspring.add(executorService.submit(island));
        }


        List<Chromosome> bestOffspring = new ArrayList<>(Constants.NUMBER_OF_ISLANDS);
        try {
            for (Future<Chromosome> future : futureOffspring) {
                bestOffspring.add(future.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        Population bestPopulation = new Population(bestOffspring);
        Chromosome bestChromosome = bestPopulation.getChromosome(bestPopulation.getBestChromosomeIndex());

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("Queueing systems did not terminate");
                }
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }

        long executionTime = System.currentTimeMillis() - time1;
        return new Result(bestChromosome, executionTime, numberOfThreads);
    }
}
