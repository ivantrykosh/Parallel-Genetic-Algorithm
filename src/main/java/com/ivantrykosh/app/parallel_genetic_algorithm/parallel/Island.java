package com.ivantrykosh.app.parallel_genetic_algorithm.parallel;

import com.ivantrykosh.app.parallel_genetic_algorithm.Chromosome;
import com.ivantrykosh.app.parallel_genetic_algorithm.Constants;
import com.ivantrykosh.app.parallel_genetic_algorithm.Population;

import java.util.List;
import java.util.concurrent.Callable;

public class Island implements Callable<Chromosome> {
    private final ParallelGeneticAlgorithm pga;
    private final Population population;
    private final int maxIterations;


    public Island(ParallelGeneticAlgorithm pga, int maxIterations, Population population) {
        this.pga = pga;
        this.maxIterations = maxIterations;
        this.population = population;
    }

    @Override
    public Chromosome call() {
        for (int i = 0; i < maxIterations; i++) {
            List<Chromosome> individuals = pga.selectIndividuals(population.getSize(), population);
            List<Chromosome> offspring = pga.performCrossoverForAllParents(individuals);
            List<Chromosome> newOffspring = pga.performMutation(offspring);
            List<Chromosome> evaluatedOffspring = pga.performEvaluation(newOffspring);
            pga.reinsert(evaluatedOffspring, population);
            if (i % Constants.ITERATION_FOR_MIGRATION == 0) {
                List<Chromosome> toMigrate = population.getSortedChromosomes().subList(0, (int) (population.getSize() * Constants.MIGRATION_PERCENTAGE));
                population.deleteAllChromosomes(toMigrate);
                population.addAllChromosomes(pga.migrate(toMigrate));
            }
        }
        return population.getChromosome(population.getBestChromosomeIndex());
    }
}
