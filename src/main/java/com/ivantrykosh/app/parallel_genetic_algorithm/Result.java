package com.ivantrykosh.app.parallel_genetic_algorithm;

public class Result {
    private final Chromosome bestChromosome;
    private final long executionTimeInMillis;
    private final int threadNumber;

    public Result(Chromosome bestChromosome, long executionTimeInMillis, int threadNumber) {
        this.bestChromosome = bestChromosome;
        this.executionTimeInMillis = executionTimeInMillis;
        this.threadNumber = threadNumber;
    }

    public Chromosome getBestChromosome() {
        return bestChromosome;
    }

    public long getExecutionTimeInMillis() {
        return executionTimeInMillis;
    }

    private double getExecutionTimeIsSeconds() {
        return (double) executionTimeInMillis / 1000;
    }

    @Override
    public String toString() {
        return String.format("Genetic Algorithm - threads: %2d - fitness: %7d - weight: %7d - execution time: %9.3fs", threadNumber, bestChromosome.calculateFitness(), bestChromosome.calculateWeight(), getExecutionTimeIsSeconds());
    }
}
