package com.prezyk.md;

public class TwoMoleculesCollision extends InitialConditionsGenerator {

    public TwoMoleculesCollision(SimulationInput simulationInput) {
        super(SimulationInput.builder()
                            .time(simulationInput.getTime())
                            .timeStep(simulationInput.getTimeStep())
                            .moleculeRadius(simulationInput.getMoleculeRadius())
                            .epsilon(simulationInput.getEpsilon())
                            .mass(simulationInput.getMass())
                            .moleculesQuantity(2)
                            .boxSize(simulationInput.getBoxSize())
                            .wallStiffness(simulationInput.getWallStiffness())
                            .build());
    }

    @Override
    public MoleculeState[] generateInitialConditions() {
        MoleculeState[] initialStates = new MoleculeState[simulationInput.getMoleculesQuantity()];
        for (int i = 0; i < initialStates.length; i++) {
            initialStates[i] = new MoleculeState(generateInitialPosition()[i],
                                                 generateInitialVelocity()[i],
                                                 generateInitialAcceleration());
        }
        return initialStates;
    }

    private double[][] generateInitialPosition() {
        return new double[][] {
                new double[] {simulationInput.getBoxSize() / 3., simulationInput.getBoxSize() / 2.},
                new double[] {simulationInput.getBoxSize() / 3. * 2., simulationInput.getBoxSize() / 2.}
        };
    }

    private double[][] generateInitialVelocity() {
        return new double[][] {
                new double[] { 10, 0 },
                new double[] { -10, 0}
        };
    }

    private double[] generateInitialAcceleration() {
        return new double[] {0, 0};
    }

    public SimulationInput getSimulationInput() {
        return this.simulationInput;
    }
}
