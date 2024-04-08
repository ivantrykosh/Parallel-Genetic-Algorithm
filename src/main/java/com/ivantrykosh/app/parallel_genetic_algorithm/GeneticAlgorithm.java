package com.ivantrykosh.app.parallel_genetic_algorithm;

import com.ivantrykosh.app.parallel_genetic_algorithm.knapsack.Items;
import com.ivantrykosh.app.parallel_genetic_algorithm.knapsack.Knapsack;

import java.util.*;

public class GeneticAlgorithm {
    private final Population population;

    public GeneticAlgorithm(int populationSize) {
        population = new Population(populationSize);
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

    /**
     * Uniform crossover
     */
    public Chromosome[] performCrossover(Chromosome parent1, Chromosome parent2) {
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
        return new Chromosome[]{offspring1, offspring2};
    }
}
