package com.prezyk.md;

public class BoxState {
    private final double kineticEnergy;
    private final double elasticEnergy;
    private final double potentialEnergy;

    public BoxState(double kineticEnergy, double elasticEnergy, double potentialEnergy) {
        this.kineticEnergy = kineticEnergy;
        this.elasticEnergy = elasticEnergy;
        this.potentialEnergy = potentialEnergy;
    }

    public double getKineticEnergy() {
        return kineticEnergy;
    }

    public double getElasticEnergy() {
        return elasticEnergy;
    }

    public double getPotentialEnergy() {
        return potentialEnergy;
    }

    public double getTotalEnergy() {
        return kineticEnergy + elasticEnergy + potentialEnergy;
    }

    public BoxState clone() {
        return new BoxState(kineticEnergy, elasticEnergy, potentialEnergy);
    }
}
