package com.prezyk.md;

import java.util.Arrays;

public class Simulation {

    private final int moleculesQuantity;
    private final double[] time;
    private final int n;
    private final Molecule[] molecules;
    private final double[] kineticEnergy;
    private final double[] potentialEnergy;
    private final double[] boxElasticEnergy;
    private final double[] totalEnergy;
    private final double r;
    private final double eps;
    private final double boxSize;



    public Simulation(int moleculesQuantity, int n, double r, double eps, double boxSize, double mass) {
        this.n = n;
        this.moleculesQuantity = moleculesQuantity;
        this.r = r;
        this.eps = eps;
        this.boxSize = boxSize;
        this.time = new double[n];
        this.molecules = new Molecule[moleculesQuantity];
        for (int i = 0; i < this.molecules.length; i++) {
            this.molecules[i] = new Molecule(r, mass, n);
        }
        this.kineticEnergy = new double[n];
        this.potentialEnergy = new double[n];
        this.boxElasticEnergy = new double[n];
        this.totalEnergy = new double[n];
    }

    public int getMoleculesQuantity() {
        return moleculesQuantity;
    }

    public void addRow(int timePoint, double time, double[][] rVector, double[][] vVector, double[][] aVector, double kineticEnergy, double potentialEnergy, double boxElasticEnergy) {
        this.time[timePoint] = time;
        this.kineticEnergy[timePoint] = kineticEnergy;
        this.potentialEnergy[timePoint] = potentialEnergy;
        this.boxElasticEnergy[timePoint] = boxElasticEnergy;
        this.totalEnergy[timePoint] = kineticEnergy + potentialEnergy + boxElasticEnergy;
        for(int i=0; i<this.moleculesQuantity; i++) {
            MoleculeState moleculeState = MoleculeState.builder()
                                                       .positionVector(rVector[i])
                                                       .velocityVector(vVector[i])
                                                       .accelerationVector(aVector[i])
                                                       .build();
            this.molecules[i].setState(timePoint, moleculeState);
        }
    }

    public double[] getTime() {
        return time;
    }

    public double getDuration() {
        return time[time.length - 1];
    }

    public int getN() {
        return n;
    }

    public double[][][] getrVectors() {
        return Arrays.stream(this.molecules)
                                 .map(Molecule::getPositionVectors)
                                 .toArray(double[][][]::new);
    }

    public double[][][] getvVectors() {
        return Arrays.stream(this.molecules)
                                 .map(Molecule::getVelocityVectors)
                                 .toArray(double[][][]::new);
    }

    public double[][][] getaVectors() {
        return Arrays.stream(this.molecules)
                                 .map(Molecule::getAccelerationVectors)
                                 .toArray(double[][][]::new);
    }

    public double[] getKineticEnergy() {
        return kineticEnergy;
    }

    public double[] getPotentialEnergy() {
        return potentialEnergy;
    }

    public double[] getBoxElasticEnergy() {
        return boxElasticEnergy;
    }

    public double getR() {
        return r;
    }

    public double getEps() {
        return eps;
    }

    public double getBoxSize() {
        return boxSize;
    }

    public double[] getTotalEnergy() {
        return totalEnergy;
    }
}
