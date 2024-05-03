package com.ivantrykosh.app.parallel_genetic_algorithm;

import java.util.*;

public class GeneticAlgorithm {
    protected final Population population;

    public GeneticAlgorithm(int populationSize) {
        population = new Population(populationSize);
    }

    public Result start(int maxIterations) {
        long time1 = System.currentTimeMillis();

        List<Chromosome> populationList = this.population.getChromosomes();
        Collections.shuffle(populationList);

        List<Population> islands = new ArrayList<>(Constants.NUMBER_OF_ISLANDS);
        int sizeOfPart = population.getSize() / Constants.NUMBER_OF_ISLANDS;
        for (int j = 0; j < Constants.NUMBER_OF_ISLANDS; j++) {
            List<Chromosome> subPopulation = new ArrayList<>(populationList.subList(j * sizeOfPart, (j + 1) * sizeOfPart));
            islands.add(new Population(subPopulation));
        }

        for (int i = 0; i < maxIterations; i++) {
            for (Population island : islands) {
                List<Chromosome> individuals = GeneticOperations.selectIndividuals(island.getSize(), island);
                List<Chromosome> offspring = GeneticOperations.performCrossoverForAllParents(individuals);
                List<Chromosome> newOffspring = GeneticOperations.performMutation(offspring);
                List<Chromosome> evaluatedOffspring = GeneticOperations.performEvaluation(newOffspring);
                GeneticOperations.reinsert(evaluatedOffspring, island);
            }
            if (i % Constants.ITERATION_FOR_MIGRATION == 0) {
                List<List<Chromosome>> chromosomesToMigrate = new ArrayList<>(Constants.NUMBER_OF_ISLANDS);
                for (Population island : islands) {
                    List<Chromosome> toMigrate = island.getSortedChromosomes().subList(0, (int) (island.getSize() * Constants.MIGRATION_PERCENTAGE));
                    chromosomesToMigrate.add(toMigrate);
                    island.deleteAllChromosomes(toMigrate);
                }
                Collections.shuffle(chromosomesToMigrate);
                for (int j = 0; j < islands.size(); j++) {
                    islands.get(j).addAllChromosomes(chromosomesToMigrate.get(j));
                }
            }
        }

        List<Chromosome> bestOffspring = new ArrayList<>(Constants.NUMBER_OF_ISLANDS);
        for (Population island : islands) {
            bestOffspring.add(island.getChromosome(island.getBestChromosomeIndex()));
        }
        Population bestPopulation = new Population(bestOffspring);
        Chromosome bestChromosome = bestPopulation.getChromosome(bestPopulation.getBestChromosomeIndex());

        long executionTime = System.currentTimeMillis() - time1;
        return new Result(bestChromosome, executionTime, 1);
    }
}
