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

        int numberOfParts = numberOfThreads;
        List<Future<Chromosome>> futureOffspring = new ArrayList<>(numberOfParts);
        int sizeOfPart = population.getSize() / numberOfParts;
        for (int j = 0; j < numberOfParts; j++) {
            List<Chromosome> subPopulation = new ArrayList<>(populationList.subList(j * sizeOfPart, (j + 1) * sizeOfPart));
            Island island = new Island(this, maxIterations, new Population(subPopulation));
            futureOffspring.add(executorService.submit(island));
        }

        List<Chromosome> bestOffspring = new ArrayList<>(numberOfParts);
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

    /**
     * Migration: threads wait for each other and then exchange their migrants
     */
    private int index = 0;
    private final Object migrationObject = new Object();
    private final List<List<Chromosome>> chromosomesToMigrate = new ArrayList<>();
    public List<Chromosome> migrate(List<Chromosome> toMigrate) {
        List<Chromosome> migratedChromosomes;
        synchronized (migrationObject) {
            index++;
            chromosomesToMigrate.add(toMigrate);
            while (index != numberOfThreads && index != 0) {
                try {
                    migrationObject.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            migratedChromosomes = chromosomesToMigrate.remove(new Random().nextInt(chromosomesToMigrate.size()));
            index = 0;
            migrationObject.notifyAll();
        }
        return migratedChromosomes;
    }
}
