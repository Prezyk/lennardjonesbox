package com.prezyk.md;

import com.prezyk.md.state.BoxState;
import com.prezyk.md.state.MoleculeState;

import java.util.Arrays;

public class Simulation {

    private final double[] time;
    private final Molecule[] molecules;
    private final Box box;
    private final double epsilon;

    public Simulation(SimulationInput simulationInput) {
        this.epsilon = simulationInput.getEpsilon();
        this.time = new double[simulationInput.getTimeStepsAmount()];
        this.box = new Box(simulationInput.getBoxSize(), simulationInput.getWallStiffness(), simulationInput.getTimeStepsAmount());
        this.molecules = new Molecule[simulationInput.getMoleculesQuantity()];
        for (int i = 0; i < this.molecules.length; i++) {
            this.molecules[i] = new Molecule(simulationInput.getMoleculeRadius(), simulationInput.getMass(), simulationInput.getTimeStepsAmount());
        }
    }

    public int getMoleculesQuantity() {
        return molecules.length;
    }

    public void setState(int timePoint, double time, BoxState boxState, MoleculeState[] moleculesStates) {
        this.time[timePoint] = time;
        this.box.setState(timePoint, boxState);
        this.setMoleculesStates(timePoint, moleculesStates);
    }

    private void setMoleculesStates(int timePoint, MoleculeState[] moleculesStates) {
        for (int i = 0; i < this.molecules.length; i++) {
            this.molecules[i].setState(timePoint, moleculesStates[i]);
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
