package com.prezyk.md;

import com.prezyk.md.model.ElasticBoxModel;
import com.prezyk.md.model.LennardJonesModel;
import com.prezyk.md.state.BoxState;
import com.prezyk.md.state.MoleculeState;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class MolecularDynamics {

    private final SimulationInput simulationInput;
    private final Simulation simulation;
    private BoxState currentBoxState;
    private MoleculeState[] currentMoleculesStates;
    private final VerletIntegration integrator;

    public MolecularDynamics(SimulationInput simulationInput) {
        this.simulationInput = simulationInput;
        simulation = new Simulation(simulationInput);
        InitialConditionsGenerator initialConditionsGenerator = new InitialConditionsGenerator(simulationInput);
        currentMoleculesStates = initialConditionsGenerator.generateInitialConditions();
        integrator = new VerletIntegration();
        integrator.registerModel(new LennardJonesModel(simulationInput.getEpsilon(), simulationInput.getMass(), simulationInput.getSigma()));
        integrator.registerModel(new ElasticBoxModel(simulationInput.getWallStiffness(), simulationInput.getBoxSize(), simulationInput.getMass()));
        currentBoxState = integrator.calculateNextBoxState(currentMoleculesStates, simulationInput.getMass());
        simulation.setState(0, 0, currentBoxState, Arrays.stream(currentMoleculesStates)
                                                                                                           .map(MoleculeState::clone)
                                                                                                           .toArray(MoleculeState[]::new));
    }

    public CompletableFuture<Simulation> calculateSimulationConcurrent() {
        CompletableFuture<Simulation> futureMolecules = new CompletableFuture<>();
        Runnable calculate = () -> futureMolecules.complete(calculateSimulation());
        new Thread(calculate).start();
        return futureMolecules;
    }

    public Simulation calculateSimulation() {
        double currentTime = 0;
        for (int i = 0; i < simulationInput.getTimeStepsAmount(); i++) {
            simulation.setState(i, currentTime, currentBoxState, Arrays.stream(currentMoleculesStates)
                                                                                                      .map(MoleculeState::clone)
                                                                                                      .toArray(MoleculeState[]::new));
            verletStep();
            currentTime += simulationInput.getTimeStep();
        }
        return simulation;
    }

    private void calculateMoleculesAcceleration() {
            currentMoleculesStates = integrator.calculateNextMoleculeStates(currentMoleculesStates, simulationInput.getTimeStep());
    }

    private void calculateBoxState() {
        currentBoxState = integrator.calculateNextBoxState(currentMoleculesStates, simulationInput.getMass());
    }

    private void verletStep() {
        calculateMoleculesAcceleration();
        calculateBoxState();
    }
}
