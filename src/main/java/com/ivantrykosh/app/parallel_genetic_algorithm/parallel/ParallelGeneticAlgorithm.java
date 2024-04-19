package com.ivantrykosh.app.parallel_genetic_algorithm.parallel;

import com.ivantrykosh.app.parallel_genetic_algorithm.*;
import com.ivantrykosh.app.parallel_genetic_algorithm.knapsack.Knapsack;

import java.util.*;
import java.util.concurrent.*;

public class ParallelGeneticAlgorithm extends GeneticAlgorithm {
    private final Object migrationObject = new Object();
    private ExecutorService executorService;
    private final int numberOfThread;
    int index = 0;

    public ParallelGeneticAlgorithm(int populationSize, int numberOfThread) {
        super(populationSize);
        this.numberOfThread = numberOfThread;
    }

    private ParallelGeneticAlgorithm(Population population, int numberOfThread) {
        super(population);
        this.numberOfThread = numberOfThread;
    }

    @Override
    public Chromosome start(int maxIterations) {
        executorService = Executors.newFixedThreadPool(numberOfThread);
        List<Chromosome> populationList = this.population.getChromosomes();
        Collections.shuffle(populationList);

        int numberOfParts = numberOfThread;
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
        Population newPopulation = new Population(bestOffspring);
        return newPopulation.getChromosome(newPopulation.getBestChromosomeIndex());
    }

    /**
     * Stochastic Universal Sampling
     */
    public List<Chromosome> selectIndividuals(int numberOfIndividualsToSelect, Population population) {
        List<Chromosome> sortedChromosomes = population.getSortedChromosomes();

        long populationFitness = population.calculateFitnessForPopulation();
        int interval = (int) (populationFitness / numberOfIndividualsToSelect);
        int point = new Random().nextInt(interval);

        int index = 0;
        long cumulativeFitness = 0;
        List<Chromosome> individuals = new ArrayList<>(numberOfIndividualsToSelect);
        for (int i = 0; i < numberOfIndividualsToSelect; i++) {
            while (sortedChromosomes.get(index).calculateFitness() + cumulativeFitness < point) {
                cumulativeFitness += sortedChromosomes.get(index).calculateFitness();
                index = (index + 1) % sortedChromosomes.size();
            }
            individuals.add(sortedChromosomes.get(index));
            point += interval;
        }
        return individuals;
    }

    public void reinsert(List<Chromosome> offspring, Population population) {
        List<Chromosome> oldOffspring = population.getSortedChromosomes();
        int populationSize = population.getSize();
        population.deleteAllChromosomes(oldOffspring.subList(populationSize - offspring.size(), populationSize));
        for (Chromosome chromosome : offspring) {
            population.addChromosome(chromosome);
        }
    }

    List<Chromosome> migrateChromosomes = new ArrayList<>();
    public List<Chromosome> migrate(List<Chromosome> toMigrate) {
        List<Chromosome> migratedChromosomes = null;
        synchronized (migrationObject) {
            index++;
            while (index % 2 == 1) {
                try {
                    migrateChromosomes = new ArrayList<>(toMigrate);
                    if (index == numberOfThread) {
                        index = 0;
                        migratedChromosomes = new ArrayList<>(migrateChromosomes);
                        break;
                    } else {
                        migrationObject.wait();
                        migratedChromosomes = new ArrayList<>(migrateChromosomes);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
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
