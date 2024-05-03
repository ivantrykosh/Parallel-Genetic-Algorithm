package com.ivantrykosh.app.parallel_genetic_algorithm.parallel;

import com.ivantrykosh.app.parallel_genetic_algorithm.Chromosome;

import java.util.ArrayList;
import java.util.List;

public class Migration {
    private final int numberOfIslandsInMigration;
    private int index = 0;

    public Migration(int numberOfIslandInMigration) {
        this.numberOfIslandsInMigration = numberOfIslandInMigration;
    }

    /**
     * Migration: thread waits for second, and then they exchange their migrants.
     * If numberOfIslandsInMigration equals to 1, then return passed toMigrate
     */
    private List<Chromosome> chromosomesToMigrate = null;
    public synchronized List<Chromosome> migrate(List<Chromosome> toMigrate) {
        int localIndex = ++index;
        while (index != numberOfIslandsInMigration) {
            try {
                chromosomesToMigrate = toMigrate;
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        List<Chromosome> migratedChromosomes = new ArrayList<>(index == 1 ? toMigrate : chromosomesToMigrate);
        chromosomesToMigrate = toMigrate;
        if (localIndex == 1) {
            index = 0;
        }
        notifyAll();
        return migratedChromosomes;
    }
}
