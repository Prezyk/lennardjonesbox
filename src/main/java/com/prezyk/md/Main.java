package com.prezyk.md;

import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {
        SimulationInput simulationInput = SimulationInput.builder()
                .time(BigDecimal.valueOf(10))
                .boxSize(BigDecimal.valueOf(200))
                .moleculeRadius(BigDecimal.valueOf(10))
                .moleculesQuantity(50)
                .timeStep(BigDecimal.valueOf(0.01))
                .mass(BigDecimal.valueOf(0.01))
                .epsilon(BigDecimal.valueOf(50))
                .wallStiffness(BigDecimal.valueOf(100))
                .build();

        MolecularDynamics molecularDynamics = new MolecularDynamics(simulationInput);
        Simulation simulation = molecularDynamics.calculateSimulation();
        System.out.println("");
    }
}
