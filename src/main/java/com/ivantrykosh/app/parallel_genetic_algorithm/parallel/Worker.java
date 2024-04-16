package com.ivantrykosh.app.parallel_genetic_algorithm.parallel;

import com.ivantrykosh.app.parallel_genetic_algorithm.Chromosome;

import java.util.List;
import java.util.concurrent.Callable;

public class Worker implements Callable<List<Chromosome>> {
    private final ParallelGeneticAlgorithm pga;
    private final List<Chromosome> parents;


    public Worker(ParallelGeneticAlgorithm pga, List<Chromosome> parents) {
        this.pga = pga;
        this.parents = parents;
    }

    @Override
    public List<Chromosome> call() {
        List<Chromosome> res = pga.performCrossoverForAllParents(parents);
        List<Chromosome> res2 = pga.performMutation(res);
        List<Chromosome> res3 = pga.performEvaluation(res2);
        return res3;
    }
}
