package com.ivantrykosh.app.parallel_genetic_algorithm.parallel;

import com.ivantrykosh.app.parallel_genetic_algorithm.Chromosome;
import com.ivantrykosh.app.parallel_genetic_algorithm.Constants;
import com.ivantrykosh.app.parallel_genetic_algorithm.Gene;
import com.ivantrykosh.app.parallel_genetic_algorithm.GeneticAlgorithm;

import java.util.*;
import java.util.concurrent.*;

public class ParallelGeneticAlgorithm extends GeneticAlgorithm {
    private ExecutorService executorService;

    public ParallelGeneticAlgorithm(int populationSize) {
        super(populationSize);
    }

    @Override
    public Chromosome start(int maxIterations) {
        executorService = Executors.newFixedThreadPool(8);
        for (int i = 0; i < maxIterations && !isTerminate(); i++) {
//            long time1 = System.currentTimeMillis();
            List<Chromosome> parents = selectIndividuals(population.getSize());
//            System.out.println(System.currentTimeMillis() - time1);
//            time1 = System.currentTimeMillis();

            Collections.shuffle(parents);

            List<Future<List<Chromosome>>> futureOffspring = new ArrayList<>();
            int sizeOfPart = population.getSize() / 5;
            for (int j = 0; j < 5; j++) {
//                long time1 = System.currentTimeMillis();
                List<Chromosome> subParents = new ArrayList<>(parents.subList(j * sizeOfPart, (j + 1) * sizeOfPart));
                Worker worker = new Worker(this, subParents);
                    var f = executorService.submit(worker);
//                    f.get();
                    futureOffspring.add(f);
//                System.out.println(System.currentTimeMillis() - time1);
            }

            List<Chromosome> evaluatedOffspring = new ArrayList<>(population.getSize());
            try {
                for (Future<List<Chromosome>> future : futureOffspring) {
                    evaluatedOffspring.addAll(future.get());

                }
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            if (population.getSize() % 2 != 0) {
                evaluatedOffspring.remove(population.getSize() + 1);
            }
//            System.out.println(System.currentTimeMillis() - time1);
//            time1 = System.currentTimeMillis();

            reinsert(evaluatedOffspring);
//            System.out.println(System.currentTimeMillis() - time1);
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("Queueing systems did not terminate");
                }
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        return population.getChromosome(population.getBestChromosomeIndex());
    }

//    @Override
//    public List<Chromosome> performCrossoverForAllParents(List<Chromosome> parents) {
//        List<Chromosome> offspring = new ArrayList<>(parents.size());
//        Chromosome bestParent = parents.get(0);
//        int parentsSize = parents.size();
//        Random random = new Random();
//        List<Future<List<Chromosome>>> futureOffspring = new ArrayList<>(parentsSize);
//        for (int i = 0; i < parentsSize / 2; i++) {
//            int index1 = random.nextInt(0, parents.size());
//            Chromosome parent1 = parents.get(index1);
//            parents.remove(index1);
//            int index2 = random.nextInt(0, parents.size());
//            Chromosome parent2 = parents.get(index2);
//            parents.remove(index2);
//
//            futureOffspring.add(executorService.submit(() -> performCrossoverForTwoParents(parent1, parent2)));
////            offspring.addAll(performCrossoverForTwoParents(parent1, parent2));
//        }
//        if (!parents.isEmpty()) {
//            futureOffspring.add(executorService.submit(() -> performCrossoverForTwoParents(bestParent, parents.get(0)).subList(0, 0)));
////            offspring.add(performCrossoverForTwoParents(bestParent, parents.get(0)).get(0));
//        }
//        try {
//            for (Future<List<Chromosome>> partOfNewOffspring : futureOffspring) {
//                offspring.addAll(partOfNewOffspring.get());
//            }
//        } catch (InterruptedException | ExecutionException e) {
//            throw new RuntimeException(e);
//        }
//        return offspring;
//    }

//    @Override
//    public List<Chromosome> performMutation(List<Chromosome> offspring) {
//        List<Chromosome> newOffspring = List.copyOf(offspring);
//        Random random = new Random();
//        List<Future<?>> futureOffspring = new ArrayList<>(offspring.size());
//        for (Chromosome chromosome : newOffspring) {
//            futureOffspring.add(executorService.submit(() -> {
//                for (Gene gene : chromosome) {
//                    if (random.nextDouble() <= Constants.MUTATION_RATE) {
//                        gene.changeGeneValue();
//                    }
//                }
//            }));
//        }
//
//        try {
//            for (Future<?> mutation : futureOffspring) {
//                mutation.get();
//            }
//        } catch (InterruptedException | ExecutionException e) {
//            throw new RuntimeException(e);
//        }
//        return newOffspring;
//    }

//    @Override
//    public List<Chromosome> performEvaluation(List<Chromosome> offspring) {
//        ConcurrentLinkedQueue<Chromosome> newOffspring = new ConcurrentLinkedQueue<>(offspring);
//        List<Future<?>> futureOffspring = new ArrayList<>(offspring.size());
//        for (Chromosome chromosome : newOffspring) {
//            futureOffspring.add(executorService.submit(() -> {
//                if (chromosome.calculateFitness() < 0) {
//                    newOffspring.remove(chromosome);
//                }
//            }));
//        }
//
//        try {
//            for (Future<?> partOfNewOffspring : futureOffspring) {
//                partOfNewOffspring.get();
//            }
//        } catch (InterruptedException | ExecutionException e) {
//            throw new RuntimeException(e);
//        }
//        return newOffspring.stream().toList();
//    }
}
