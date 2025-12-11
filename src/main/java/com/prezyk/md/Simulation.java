package com.prezyk.md;

import java.util.Arrays;

public class Simulation {

    private final double[] time;
    private final Molecule[] molecules;
    private final Box box;
    private final double epsilon;

    public Simulation(int moleculesQuantity, int timePoints, double moleculeRadius, double epsilon, double boxSize, double mass, double wallStiffness) {
        this.epsilon = epsilon;
        this.time = new double[timePoints];
        this.box = new Box(boxSize, wallStiffness, timePoints);
        this.molecules = new Molecule[moleculesQuantity];
        for (int i = 0; i < this.molecules.length; i++) {
            this.molecules[i] = new Molecule(moleculeRadius, mass, timePoints);
        }
    }

    public int getMoleculesQuantity() {
        return molecules.length;
    }

    public void addRow(int timePoint, double time, double[][] rVector, double[][] vVector, double[][] aVector, double kineticEnergy, double potentialEnergy, double boxElasticEnergy) {
        this.time[timePoint] = time;
        this.box.setState(timePoint, new BoxState(kineticEnergy, boxElasticEnergy, potentialEnergy));
        for(int i=0; i<this.getMoleculesQuantity(); i++) {
            this.molecules[i].setState(timePoint, new MoleculeState(rVector[i], vVector[i], aVector[i]));
        }
    }

    public double[] getTime() {
        return time;
    }

    public double getDuration() {
        return time[time.length - 1] - time[0];
    }

    public int getTimePoints() {
        return time.length;
    }

    public double[][][] getPositionVectors() {
        return Arrays.stream(this.molecules)
                                 .map(Molecule::getPositionVectorSeries)
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

    public double getBoxSize() {
        return this.box.getSize();
    }

    public Double[] getTotalEnergySeries() {
        return this.box.getTotalEnergySeries();
    }
}
