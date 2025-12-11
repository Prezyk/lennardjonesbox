package com.prezyk.md;

import java.util.Arrays;

public class Simulation {

    private final int moleculesQuantity;
    private final double[] time;
    private final int n;
    private final Molecule[] molecules;
    private final Box box;
    private final double r;
    private final double eps;



    public Simulation(int moleculesQuantity, int n, double r, double eps, double boxSize, double mass, double wallStiffness) {
        this.n = n;
        this.moleculesQuantity = moleculesQuantity;
        this.r = r;
        this.eps = eps;
        this.time = new double[n];
        this.box = new Box(boxSize, wallStiffness, n);
        this.molecules = new Molecule[moleculesQuantity];
        for (int i = 0; i < this.molecules.length; i++) {
            this.molecules[i] = new Molecule(r, mass, n);
        }
    }

    public int getMoleculesQuantity() {
        return moleculesQuantity;
    }

    public void addRow(int timePoint, double time, double[][] rVector, double[][] vVector, double[][] aVector, double kineticEnergy, double potentialEnergy, double boxElasticEnergy) {
        this.time[timePoint] = time;
        this.box.setState(timePoint, new BoxState(kineticEnergy, boxElasticEnergy, potentialEnergy));
        for(int i=0; i<this.moleculesQuantity; i++) {
            this.molecules[i].setState(timePoint, new MoleculeState(rVector[i], vVector[i], aVector[i]));
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
                                 .map(Molecule::getPositionVectorSeries)
                                 .toArray(double[][][]::new);
    }

    public double[][][] getvVectors() {
        return Arrays.stream(this.molecules)
                                 .map(Molecule::getVelocityVectorSeries)
                                 .toArray(double[][][]::new);
    }

    public double[][][] getaVectors() {
        return Arrays.stream(this.molecules)
                                 .map(Molecule::getAccelerationVectorSeries)
                                 .toArray(double[][][]::new);
    }

    public Double[] getKineticEnergySeries() {
        return this.box.getKineticEnergySeries();
    }

    public Double[] getPotentialEnergySeries() {
        return this.box.getPotentialEnergySeries();
    }

    public Double[] getBoxElasticEnergySeries() {
        return this.box.getElasticEnergySeries();
    }

    public double getR() {
        return r;
    }

    public double getEps() {
        return eps;
    }

    public double getBoxSize() {
        return this.box.getSize();
    }

    public Double[] getTotalEnergySeries() {
        return this.box.getTotalEnergySeries();
    }
}
