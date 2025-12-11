package com.prezyk.md;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class Molecule {

    private final String id;
    private final double radius;
    private final double mass;
    private final MoleculeState[] moleculeTimeStates;

    public Molecule(double radius, double mass, int timePoints) {
        this.id = UUID.randomUUID()
                      .toString();
        this.radius = radius;
        this.mass = mass;
        this.moleculeTimeStates = new MoleculeState[timePoints];
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Molecule molecule = (Molecule) o;
        return Double.compare(radius, molecule.radius) == 0 && Double.compare(mass, molecule.mass) == 0 && Objects.equals(id, molecule.id) && Objects.deepEquals(moleculeTimeStates, molecule.moleculeTimeStates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, radius, mass, Arrays.hashCode(moleculeTimeStates));
    }

    public double[][] getPositionVectorSeries() {
        return Arrays.stream(moleculeTimeStates)
                     .map(MoleculeState::getPositionVector)
                     .toArray(double[][]::new);
    }

    public double[][] getVelocityVectorSeries() {
        return Arrays.stream(moleculeTimeStates)
                     .map(MoleculeState::getVelocityVector)
                     .toArray(double[][]::new);
    }

    public double[][] getAccelerationVectorSeries() {
        return Arrays.stream(moleculeTimeStates)
                     .map(MoleculeState::getAccelerationVector)
                     .toArray(double[][]::new);
    }

    public void setState(int timePoint, MoleculeState moleculeTimeState) {
        this.moleculeTimeStates[timePoint] = moleculeTimeState;
    }

}
