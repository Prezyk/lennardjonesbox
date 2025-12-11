package com.prezyk.md;

public class Main {
    public static void main(String[] args) {
        SimulationInput simulationInput = SimulationInput.builder()
                .time(10)
                .boxSize(200)
                .moleculeRadius(10)
                .moleculesQuantity(4)
                .timeStep(0.01)
                .mass(10)
                .epsilon(10)
                .wallStiffness(100)
                                                         .build();

        MolecularDynamics molecularDynamics = new MolecularDynamics(simulationInput);
        molecularDynamics.calculateSimulation();
    }
}
