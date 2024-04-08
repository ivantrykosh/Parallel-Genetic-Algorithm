package com.ivantrykosh.app.parallel_genetic_algorithm.knapsack;

import com.ivantrykosh.app.parallel_genetic_algorithm.Chromosome;
import com.ivantrykosh.app.parallel_genetic_algorithm.Gene;

import java.util.List;

public class Knapsack extends Chromosome {
    private final int maxWeight;

    public Knapsack(int numberOfItems, int maxKnapsackWeight) {
        super(numberOfItems);
        this.maxWeight = maxKnapsackWeight;
    }

    public int getMaxWeight() {
        return maxWeight;
    }

    @Override
    public int calculateFitness() {
        int fitness = 0;
        int weight = 0;

        List<Gene> genes = getGenes();
        List<Item> items = Items.getInstance().getItems();
        for (int i = 0; i < genes.size(); i++) {
            byte geneValue = genes.get(i).getGeneValue();
            fitness += items.get(i).getValue() * geneValue;
            weight += items.get(i).getWeight() * geneValue;
        }

        if (weight > maxWeight) {
            fitness = -1;
        }
        return fitness;
    }
}
