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
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        long time1 = System.currentTimeMillis();

        List<Chromosome> populationList = this.population.getChromosomes();
        Collections.shuffle(populationList);

        int numberOfParts = numberOfThreads;
        List<Future<Chromosome>> futureOffspring = new ArrayList<>(numberOfParts);
        int sizeOfPart = population.getSize() / numberOfParts;
        for (int j = 0; j < numberOfParts; j++) {
            List<Chromosome> subPopulation = new ArrayList<>(populationList.subList(j * sizeOfPart, (j + 1) * sizeOfPart)); // todo винести sublist у Worker
            Worker worker = new Worker(this, maxIterations, new Population(subPopulation));
            futureOffspring.add(executorService.submit(worker));
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

        long executionTime = System.currentTimeMillis() - time1;

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

        return new Result(bestChromosome, executionTime, numberOfThreads);
    }

    /**
     * Migration: first thread wait for second and they exchange their chromosomes
     */
    private int index = 0;
    private final Object migrationObject = new Object();
    private List<Chromosome> migrateChromosomes = new ArrayList<>();
    public List<Chromosome> migrate(List<Chromosome> toMigrate) {
        List<Chromosome> migratedChromosomes = null;
        synchronized (migrationObject) {
            index++;
            while (index % 2 == 1) {
                try {
                    migrateChromosomes = new ArrayList<>(toMigrate);
                    if (index == numberOfThreads) {
                        index = 0;
                        migratedChromosomes = new ArrayList<>(migrateChromosomes);
                        break;
                    } else {
                        migrationObject.wait();
                        migratedChromosomes = new ArrayList<>(migrateChromosomes);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            if (migratedChromosomes == null) {
                migratedChromosomes = new ArrayList<>(migrateChromosomes);
                migrateChromosomes = toMigrate;
            }
            migrationObject.notifyAll();
        }
        return migratedChromosomes;
    }
}
