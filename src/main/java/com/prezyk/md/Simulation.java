package com.prezyk.md;

import com.prezyk.md.state.BoxState;
import com.prezyk.md.state.MoleculeState;

import java.math.BigDecimal;
import java.util.Arrays;

public class Simulation {

    private final BigDecimal[] time;
    private final Molecule[] molecules;
    private final Box box;
    private final BigDecimal epsilon;

    public Simulation(SimulationInput simulationInput) {
        this.epsilon = simulationInput.getEpsilon();
        this.time = new BigDecimal[simulationInput.getTimeStepsAmount()];
        this.box = new Box(simulationInput.getBoxSize(), simulationInput.getWallStiffness(), simulationInput.getTimeStepsAmount());
        this.molecules = new Molecule[simulationInput.getMoleculesQuantity()];
        for (int i = 0; i < this.molecules.length; i++) {
            this.molecules[i] = new Molecule(simulationInput.getMoleculeRadius(), simulationInput.getMass(), simulationInput.getTimeStepsAmount());
        }
    }

    public int getMoleculesQuantity() {
        return molecules.length;
    }

    public void setState(int timePoint, BigDecimal time, BoxState boxState, MoleculeState[] moleculesStates) {
        this.time[timePoint] = time;
        this.box.setState(timePoint, boxState);
        this.setMoleculesStates(timePoint, moleculesStates);
    }

    private void setMoleculesStates(int timePoint, MoleculeState[] moleculesStates) {
        for (int i = 0; i < this.molecules.length; i++) {
            this.molecules[i].setState(timePoint, moleculesStates[i]);
        }
    }

    public BigDecimal[] getTime() {
        return time;
    }

    public BigDecimal getDuration() {
        return time[time.length - 1].subtract(time[0]);
    }

    public int getTimePoints() {
        return time.length;
    }

    public BigDecimal[][][] getPositionVectors() {
        return Arrays.stream(this.molecules)
                                 .map(Molecule::getPositionVectorSeries)
                                 .toArray(BigDecimal[][][]::new);
    }

    public BigDecimal[] getKineticEnergySeries() {
        return this.box.getKineticEnergySeries();
    }

    public BigDecimal[] getPotentialEnergySeries() {
        return this.box.getPotentialEnergySeries();
    }

    public BigDecimal[] getBoxElasticEnergySeries() {
        return this.box.getElasticEnergySeries();
    }

    public BigDecimal getBoxSize() {
        return this.box.getSize();
    }

    public BigDecimal[] getTotalEnergySeries() {
        return this.box.getTotalEnergySeries();
    }
}
