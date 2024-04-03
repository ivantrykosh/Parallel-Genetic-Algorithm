package com.ivantrykosh.app.parallel_genetic_algorithm;

public class Gene {
    private byte geneValue;

    public Gene(byte geneValue) {
        this.geneValue = geneValue;
    }

    public void setGeneValue(byte geneValue) {
        this.geneValue = geneValue;
    }

    public void changeGeneValue() {
        this.geneValue = (byte) (this.geneValue == 0 ? 1 : 0);
    }

    public byte getGeneValue() {
        return geneValue;
    }

    @Override
    public String toString() {
        return String.valueOf(geneValue);
    }
}
