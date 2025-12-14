package com.prezyk.md;

import java.util.Random;

public class InitialConditionsGenerator {

    private final SimulationInput simulationInput;

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

    private double[] generateInitialPosition() {
        Random random = new Random();
        return new double[] {
                random.nextDouble() * (getUpperPositionBound() - getLowerPositionBound()) + getLowerPositionBound(),
                random.nextDouble() * (getUpperPositionBound() - getLowerPositionBound()) + getLowerPositionBound(),
        };
    }

    private double getLowerPositionBound() {
        return simulationInput.getMoleculeRadius() * 2;
    }

    private double getUpperPositionBound() {
        return simulationInput.getBoxSize() - simulationInput.getMoleculeRadius() * 2;
    }

    private double[] generateInitialVelocity() {
        Random random = new Random();
        return new double[] {
                random.nextDouble() * 10 - 5,
                random.nextDouble() * 10 - 5,
        };
    }

    private double[] generateInitialAcceleration() {
        return new double[] {0, 0};
    }

}
