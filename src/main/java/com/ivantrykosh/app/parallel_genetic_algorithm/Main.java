package com.ivantrykosh.app.parallel_genetic_algorithm;

import com.ivantrykosh.app.parallel_genetic_algorithm.knapsack.Items;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        GeneticAlgorithm ga = new GeneticAlgorithm(10);
        List<Chromosome> individuals = ga.selectIndividuals(10);
        System.out.println(individuals);
        System.out.println(Arrays.toString(ga.performCrossover(individuals.get(0), individuals.get(9))));
    }
}
