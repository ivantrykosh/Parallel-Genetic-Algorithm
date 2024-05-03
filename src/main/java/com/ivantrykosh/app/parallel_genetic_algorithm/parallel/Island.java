package com.ivantrykosh.app.parallel_genetic_algorithm.parallel;

import com.ivantrykosh.app.parallel_genetic_algorithm.Chromosome;
import com.ivantrykosh.app.parallel_genetic_algorithm.Constants;
import com.ivantrykosh.app.parallel_genetic_algorithm.GeneticOperations;
import com.ivantrykosh.app.parallel_genetic_algorithm.Population;

import java.util.List;
import java.util.concurrent.Callable;

public class Island implements Callable<Chromosome> {
    private final Population population;
    private final int maxIterations;
    private final Migration migration;


    public Island(int maxIterations, Population population, Migration migration) {
        this.maxIterations = maxIterations;
        this.population = population;
        this.migration = migration;
    }

    @Override
    public Chromosome call() {
        for (int i = 0; i < maxIterations; i++) {
            List<Chromosome> individuals = GeneticOperations.selectIndividuals(population.getSize(), population);
            List<Chromosome> offspring = GeneticOperations.performCrossoverForAllParents(individuals);
            List<Chromosome> newOffspring = GeneticOperations.performMutation(offspring);
            List<Chromosome> evaluatedOffspring = GeneticOperations.performEvaluation(newOffspring);
            GeneticOperations.reinsert(evaluatedOffspring, population);
            if (i % Constants.ITERATION_FOR_MIGRATION == 0) {
                List<Chromosome> toMigrate = population.getSortedChromosomes().subList(0, (int) (population.getSize() * Constants.MIGRATION_PERCENTAGE));
                population.deleteAllChromosomes(toMigrate);
                population.addAllChromosomes(migration.migrate(toMigrate));
            }
        }
        return population.getChromosome(population.getBestChromosomeIndex());
    }
}
