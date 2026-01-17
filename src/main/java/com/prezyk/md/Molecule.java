package com.prezyk.md;

import com.prezyk.md.state.MoleculeState;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class Molecule {

    private final String id;
    private final BigDecimal radius;
    private final BigDecimal mass;
    private final MoleculeState[] moleculeTimeStates;

    public Molecule(BigDecimal radius, BigDecimal mass, int timePoints) {
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
        return radius.compareTo(molecule.radius) == 0 && mass.compareTo(molecule.mass) == 0 && Objects.equals(id, molecule.id) && Objects.deepEquals(moleculeTimeStates, molecule.moleculeTimeStates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, radius, mass, Arrays.hashCode(moleculeTimeStates));
    }

    public BigDecimal[][] getPositionVectorSeries() {
        return Arrays.stream(moleculeTimeStates)
                     .map(MoleculeState::getPositionVector)
                     .toArray(BigDecimal[][]::new);
    }

    public BigDecimal[][] getVelocityVectorSeries() {
        return Arrays.stream(moleculeTimeStates)
                     .map(MoleculeState::getVelocityVector)
                     .toArray(BigDecimal[][]::new);
    }

    public BigDecimal[][] getAccelerationVectorSeries() {
        return Arrays.stream(moleculeTimeStates)
                     .map(MoleculeState::getAccelerationVector)
                     .toArray(BigDecimal[][]::new);
    }

    public void setState(int timePoint, MoleculeState moleculeTimeState) {
        this.moleculeTimeStates[timePoint] = moleculeTimeState;
    }

}
