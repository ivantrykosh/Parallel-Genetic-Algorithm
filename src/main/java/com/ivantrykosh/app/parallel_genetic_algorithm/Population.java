package com.ivantrykosh.app.parallel_genetic_algorithm;

import com.ivantrykosh.app.parallel_genetic_algorithm.knapsack.Knapsack;

import java.util.*;

public class Population implements Iterable<Chromosome> {
    private final List<Chromosome> chromosomes;

    public Population(int populationSize) {
        chromosomes = new ArrayList<>(populationSize);
        initializePopulation(populationSize);
    }

    public Population(List<Chromosome> chromosomes) {
        this.chromosomes = chromosomes;
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

    public boolean addAllChromosomes(List<Chromosome> chromosomes) {
        return this.chromosomes.addAll(chromosomes);
    }

    public boolean deleteChromosome(int index) {
        return chromosomes.remove(index) != null;
    }

    public boolean deleteAllChromosomes(List<Chromosome> chromosomes) {
        return this.chromosomes.removeAll(chromosomes);
    }

    public List<Chromosome> getChromosomes() {
        return new ArrayList<>(chromosomes);
    }

    public Chromosome getChromosome(int index) {
        return chromosomes.get(index);
    }

    public int getSize() {
        return chromosomes.size();
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

    /**
     * Return sorted chromosomes
     */
    public Map<Chromosome, Integer> calculateFitnessForEveryChromosome() {
        Map<Chromosome, Integer> allChromosomes = new HashMap<>();
        for (Chromosome chromosome : chromosomes) {
            int chromosomeFitness = chromosome.calculateFitness();
            allChromosomes.put(chromosome, chromosomeFitness);
        }

        List<Map.Entry<Chromosome, Integer>> entries = new ArrayList<>(allChromosomes.entrySet());
        entries.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        Map<Chromosome, Integer> sortedChromosomes = new LinkedHashMap<>();
        for (Map.Entry<Chromosome, Integer> entry : entries) {
            sortedChromosomes.put(entry.getKey(), entry.getValue());
        }
        return sortedChromosomes;
    }

    public List<Chromosome> getSortedChromosomes() {
        List<Chromosome> sortedChromosomes = new ArrayList<>(chromosomes);
        sortedChromosomes.sort((o1, o2) -> Integer.compare(o2.calculateFitness(), o1.calculateFitness()));
        return sortedChromosomes;
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
