package com.prezyk.md;

public class Main {
    public static void main(String[] args) {
        SimulationInput simulationInput = SimulationInput.builder()
                .time(10)
                .boxSize(200)
                .moleculeRadius(10)
                .moleculesQuantity(50)
                .timeStep(0.01)
                .mass(0.01)
                .epsilon(50)
                .wallStiffness(100)
                                                         .build();

        MolecularDynamics molecularDynamics = new MolecularDynamics(simulationInput);
        Simulation simulation = molecularDynamics.calculateSimulation();
        System.out.println("");
    }
}
