package com.ivantrykosh.app.parallel_genetic_algorithm;

import com.ivantrykosh.app.parallel_genetic_algorithm.knapsack.Knapsack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Population implements Iterable<Chromosome> {
    private final List<Chromosome> chromosomes;

    public Population(int populationSize) {
        chromosomes = new ArrayList<>(populationSize);
        initializePopulation(populationSize);
    }

    private void initializePopulation(int populationSize) {
        for (int i = 0; i < populationSize; i++) {
            Chromosome chromosome = new Knapsack(Constants.NUMBER_OF_ITEMS, Constants.MAX_KNAPSACK_WEIGHT);
            int index = i % Constants.NUMBER_OF_ITEMS;
            chromosome.getGene(index).setGeneValue((byte) 1);
            if (chromosome.calculateFitness() < 0) {
                chromosome.getGene(index).setGeneValue((byte) 0);
            }
            chromosomes.add(chromosome);
        }
    }

    public int getBestChromosomeIndex() {
        int bestChromosomeIndex = 0;
        for (int i = 0; i < chromosomes.size(); i++) {
            if (chromosomes.get(i).calculateFitness() > chromosomes.get(bestChromosomeIndex).calculateFitness()) {
                bestChromosomeIndex = i;
            }
        }
        return bestChromosomeIndex;
    }

    public int getWorstChromosomeIndex() {
        int worstChromosomeIndex = 0;
        for (int i = 0; i < chromosomes.size(); i++) {
            if (chromosomes.get(i).calculateFitness() < chromosomes.get(worstChromosomeIndex).calculateFitness()) {
                worstChromosomeIndex = i;
            }
        }
        return worstChromosomeIndex;
    }

    public boolean addChromosome(Chromosome chromosome) {
        if (chromosome.calculateFitness() < 0) {
            return false;
        }
        return chromosomes.add(chromosome);
    }

    public boolean deleteChromosome(int index) {
        return chromosomes.remove(index) != null;
    }

    public List<Chromosome> getChromosomes() {
        return chromosomes;
    }

    public Chromosome getChromosome(int index) {
        return chromosomes.get(index);
    }

    public long calculateFitnessForPopulation() {
        long fitnessForPopulation = 0;
        for (Chromosome chromosome : chromosomes) {
            int chromosomeFitness = chromosome.calculateFitness();
            if (chromosomeFitness >= 0) {
                fitnessForPopulation += chromosomeFitness;
            }
        }
        return fitnessForPopulation;
    }

    @Override
    public Iterator<Chromosome> iterator() {
        return chromosomes.iterator();
    }

    @Override
    public String toString() {
        return "Population{" + chromosomes + "}";
    }
}
