package com.ivantrykosh.app.parallel_genetic_algorithm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class Chromosome implements Iterable<Gene> {
    private final List<Gene> genes;

    protected Chromosome(int numberOfGenes) {
        this.genes = new ArrayList<>(numberOfGenes);
        initializeChromosome(numberOfGenes);
    }

    private void initializeChromosome(int numberOfGenes) {
        for (int i = 0; i < numberOfGenes; i++) {
            genes.add(new Gene((byte) 0));
        }
    }

    public Gene getGene(int index) {
        return genes.get(index);
    }

    public List<Gene> getGenes() {
        return new ArrayList<>(genes);
    }

    public int getSize() {
        return genes.size();
    }

    @Override
    public Iterator<Gene> iterator() {
        return genes.iterator();
    }

    @Override
    public String toString() {
        return genes.toString();
    }

    public abstract int calculateFitness();
}
