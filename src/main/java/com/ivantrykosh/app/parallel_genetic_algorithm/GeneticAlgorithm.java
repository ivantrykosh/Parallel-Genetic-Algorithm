package com.ivantrykosh.app.parallel_genetic_algorithm;

import com.ivantrykosh.app.parallel_genetic_algorithm.knapsack.Item;
import com.ivantrykosh.app.parallel_genetic_algorithm.knapsack.Items;
import com.ivantrykosh.app.parallel_genetic_algorithm.knapsack.Knapsack;

import java.util.*;

public class GeneticAlgorithm {
    private final Population population;
    private final long allItemsValue = Items.getInstance().getItems().stream().map(Item::getValue).reduce(0, Integer::sum);
    private final long allItemsWeight = Items.getInstance().getItems().stream().map(Item::getWeight).reduce(0, Integer::sum);

    public GeneticAlgorithm(int populationSize) {
        population = new Population(populationSize);
    }

    public Population getPopulation() {
        return population;
    }

    public Chromosome start(int maxIterations) {
        int iter = 0;
        for (int i = 0; i < maxIterations && !isTerminate(); i++) {
            List<Chromosome> individuals = selectIndividuals(population.getSize());
            List<Chromosome> offspring = performCrossoverForAllParents(individuals);
            List<Chromosome> newOffspring = performMutation(offspring);
            List<Chromosome> evaluatedOffspring = performEvaluation(newOffspring);
            reinsert(evaluatedOffspring);
            iter = i;
        }
        return population.getChromosome(population.getBestChromosomeIndex());
    }

    /**
     * Stochastic Universal Sampling
     */
    public List<Chromosome> selectIndividuals(int numberOfIndividualsToSelect) {
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

    public List<Chromosome> performCrossoverForAllParents(List<Chromosome> parents) {
        List<Chromosome> offspring = new ArrayList<>(parents.size());
        Chromosome bestParent = parents.get(0);
        int parentsSize = parents.size();
        Random random = new Random();
        for (int i = 0; i < parentsSize / 2; i++) {
            int index1 = random.nextInt(0, parents.size());
            Chromosome parent1 = parents.get(index1);
            parents.remove(index1);
            int index2 = random.nextInt(0, parents.size());
            Chromosome parent2 = parents.get(index2);
            parents.remove(index2);

            offspring.addAll(performCrossoverForTwoParents(parent1, parent2));
        }
        if (!parents.isEmpty()) {
            offspring.addAll(performCrossoverForTwoParents(bestParent, parents.get(0)));
        }
        return offspring;
    }

    /**
     * Uniform crossover
     */
    public List<Chromosome> performCrossoverForTwoParents(Chromosome parent1, Chromosome parent2) {
        Chromosome offspring1 = new Knapsack(parent1.getSize(), ((Knapsack) parent1).getMaxWeight());
        Chromosome offspring2 = new Knapsack(parent2.getSize(), ((Knapsack) parent2).getMaxWeight());
        for (int i = 0; i < parent1.getSize(); i++) {
            if (Math.random() < 0.5) {
                offspring1.getGene(i).setGeneValue(parent2.getGene(i).getGeneValue());
                offspring2.getGene(i).setGeneValue(parent1.getGene(i).getGeneValue());
            } else {
                offspring1.getGene(i).setGeneValue(parent1.getGene(i).getGeneValue());
                offspring2.getGene(i).setGeneValue(parent2.getGene(i).getGeneValue());
            }
        }
        return List.of(offspring1, offspring2);
    }

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

    public List<Chromosome> performEvaluation(List<Chromosome> offspring) {
        List<Chromosome> newOffspring = new ArrayList<>(List.copyOf(offspring));
        newOffspring.removeIf(chromosome -> chromosome.calculateFitness() < 0);
        return newOffspring;
    }

    public void reinsert(List<Chromosome> offspring) {
        for (int i = 0; i < offspring.size(); i++) {
            int worstChromosomeIndex = population.getWorstChromosomeIndex();
            population.deleteChromosome(worstChromosomeIndex);
        }
        for (Chromosome chromosome : offspring) {
            population.addChromosome(chromosome);
        }
    }

    private boolean isTerminate() {
        int bestChromosomeIndex = population.getBestChromosomeIndex();
        Chromosome bestChromosome = population.getChromosome(bestChromosomeIndex);
//        double weightRatio = Math.min(((Knapsack) bestChromosome).getMaxWeight() / (double) allItemsWeight, 1L);
        int idealFitness = (int) (Constants.TERMINATION_VALUE * allItemsValue);
        return bestChromosome.calculateFitness() >= idealFitness;
    }
}
