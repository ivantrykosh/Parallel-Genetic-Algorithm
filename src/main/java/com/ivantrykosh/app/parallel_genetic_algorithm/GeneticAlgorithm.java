package com.ivantrykosh.app.parallel_genetic_algorithm;

import com.ivantrykosh.app.parallel_genetic_algorithm.knapsack.Knapsack;
import com.ivantrykosh.app.parallel_genetic_algorithm.parallel.Island;

import java.util.*;
import java.util.concurrent.Future;

public class GeneticAlgorithm {
    protected final Population population;

    public GeneticAlgorithm(int populationSize) {
        population = new Population(populationSize);
    }

    public Result start(int maxIterations) {
        long time1 = System.currentTimeMillis();

        List<Chromosome> populationList = this.population.getChromosomes();
        Collections.shuffle(populationList);

        int numberOfParts = 5;
        List<Population> islands = new ArrayList<>(numberOfParts);
        int sizeOfPart = population.getSize() / numberOfParts;
        for (int j = 0; j < numberOfParts; j++) {
            List<Chromosome> subPopulation = new ArrayList<>(populationList.subList(j * sizeOfPart, (j + 1) * sizeOfPart));
            islands.add(new Population(subPopulation));
        }

        for (int i = 0; i < maxIterations; i++) {
            for (Population island : islands) {
                List<Chromosome> individuals = selectIndividuals(island.getSize(), island);
                List<Chromosome> offspring = performCrossoverForAllParents(individuals);
                List<Chromosome> newOffspring = performMutation(offspring);
                List<Chromosome> evaluatedOffspring = performEvaluation(newOffspring);
                reinsert(evaluatedOffspring, island);
            }
            if (i % Constants.ITERATION_FOR_MIGRATION == 0) {
                List<List<Chromosome>> chromosomesToMigrate = new ArrayList<>(numberOfParts);
                for (Population island : islands) {
                    List<Chromosome> toMigrate = island.getSortedChromosomes().subList(0, (int) (island.getSize() * Constants.MIGRATION_PERCENTAGE));
                    chromosomesToMigrate.add(toMigrate);
                    island.deleteAllChromosomes(toMigrate);
                }
                Random random = new Random();
                for (Population island : islands) {
                    List<Chromosome> migratedChromosomes = chromosomesToMigrate.remove(random.nextInt(chromosomesToMigrate.size()));
                    island.addAllChromosomes(migratedChromosomes);
                }
            }
        }

        List<Chromosome> bestOffspring = new ArrayList<>(numberOfParts);
        for (Population island : islands) {
            bestOffspring.add(island.getChromosome(island.getBestChromosomeIndex()));
        }
        Population bestPopulation = new Population(bestOffspring);
        Chromosome bestChromosome = bestPopulation.getChromosome(bestPopulation.getBestChromosomeIndex());

        long executionTime = System.currentTimeMillis() - time1;
        return new Result(bestChromosome, executionTime, 1);
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

    /**
     * Choose two random parents and perform crossover
     */
    public List<Chromosome> performCrossoverForAllParents(List<Chromosome> parents) {
        List<Chromosome> offspring = new ArrayList<>(parents.size());
        Chromosome bestParent = parents.get(0);
        int parentsSize = parents.size();
        Random random = new Random();
        for (int i = 0; i < parentsSize / 2; i++) {
            int index1 = random.nextInt(parents.size());
            Chromosome parent1 = parents.remove(index1);
            int index2 = random.nextInt(parents.size());
            Chromosome parent2 = parents.remove(index2);

            offspring.addAll(performCrossoverForTwoParents(parent1, parent2));
        }
        if (!parents.isEmpty()) {
            offspring.add(performCrossoverForTwoParents(bestParent, parents.get(0)).get(0));
        }
        return offspring;
    }

    /**
     * Uniform crossover
     */
    public List<Chromosome> performCrossoverForTwoParents(Chromosome parent1, Chromosome parent2) {
        Chromosome offspring1 = new Knapsack(parent1.getSize(), ((Knapsack) parent1).getMaxWeight());
        Chromosome offspring2 = new Knapsack(parent2.getSize(), ((Knapsack) parent2).getMaxWeight());
        Random random = new Random();
        for (int i = 0; i < parent1.getSize(); i++) {
            if (random.nextDouble() < 0.5) {
                offspring1.getGene(i).setGeneValue(parent2.getGene(i).getGeneValue());
                offspring2.getGene(i).setGeneValue(parent1.getGene(i).getGeneValue());
            } else {
                offspring1.getGene(i).setGeneValue(parent1.getGene(i).getGeneValue());
                offspring2.getGene(i).setGeneValue(parent2.getGene(i).getGeneValue());
            }
        }
        return List.of(offspring1, offspring2);
    }

    /**
     * Gene mutation (change value to the opposite) with some rate
     */
    public List<Chromosome> performMutation(List<Chromosome> offspring) {
        List<Chromosome> newOffspring = List.copyOf(offspring);
        Random random = new Random();
        for (Chromosome chromosome : newOffspring) {
            for (Gene gene : chromosome) {
                if (random.nextDouble() <= Constants.MUTATION_RATE) {
                    gene.changeGeneValue();
                }
            }
        }
        return newOffspring;
    }

    /**
     * Evaluation: If chromosome is not fit, remove one from offspring
     */
    public List<Chromosome> performEvaluation(List<Chromosome> offspring) {
        List<Chromosome> newOffspring = new ArrayList<>(offspring);
        newOffspring.removeIf(chromosome -> chromosome.calculateFitness() < 0);
        return newOffspring;
    }

    /**
     * Remove the worst fittest chromosomes and add new chromosomes to population
     */
    public void reinsert(List<Chromosome> offspring, Population population) {
        List<Chromosome> oldOffspring = population.getSortedChromosomes();
        int populationSize = population.getSize();
        population.deleteAllChromosomes(oldOffspring.subList(populationSize - offspring.size(), populationSize));
        for (Chromosome chromosome : offspring) {
            population.addChromosome(chromosome);
        }
    }
}
