package com.prezyk.md;

import com.prezyk.md.state.MoleculeState;

import java.math.BigDecimal;
import java.util.Random;

public class InitialConditionsGenerator {

    protected final SimulationInput simulationInput;

    public InitialConditionsGenerator(SimulationInput simulationInput) {
        this.simulationInput = simulationInput;
    }

    public MoleculeState[] generateInitialConditions() {
        MoleculeState[] initialStates = new MoleculeState[simulationInput.getMoleculesQuantity()];
        for (int i = 0; i < initialStates.length; i++) {
            initialStates[i] = new MoleculeState(generateInitialPosition(),
                                                 generateInitialVelocity(),
                                                 generateInitialAcceleration());
        }
        return initialStates;
    }

    private BigDecimal[] generateInitialPosition() {
        Random random = new Random();
        return new BigDecimal[] {
                BigDecimal.valueOf(random.nextDouble())
                          .multiply(getUpperPositionBound().subtract(getLowerPositionBound()))
                          .add(getLowerPositionBound()),
                BigDecimal.valueOf(random.nextDouble())
                          .multiply(getUpperPositionBound().subtract(getLowerPositionBound()))
                          .add(getLowerPositionBound()),
        };
    }

    private BigDecimal getLowerPositionBound() {
        return simulationInput.getMoleculeRadius()
                              .multiply(BigDecimal.valueOf(2));
    }

    private BigDecimal getUpperPositionBound() {
        return simulationInput.getBoxSize()
                              .subtract(simulationInput.getMoleculeRadius())
                              .multiply(BigDecimal.valueOf(2));
    }

    private BigDecimal[] generateInitialVelocity() {
        Random random = new Random();
        return new BigDecimal[] {
                BigDecimal.valueOf(random.nextDouble())
                          .multiply(BigDecimal.valueOf(4))
                          .subtract(BigDecimal.valueOf(2)),
                BigDecimal.valueOf(random.nextDouble())
                          .multiply(BigDecimal.valueOf(4))
                          .subtract(BigDecimal.valueOf(2)),
        };
    }

    private BigDecimal[] generateInitialAcceleration() {
        return new BigDecimal[] {BigDecimal.ZERO, BigDecimal.ZERO};
    }
}
