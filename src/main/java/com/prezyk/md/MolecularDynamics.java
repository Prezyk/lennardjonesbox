package com.prezyk.md;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import static com.prezyk.md.VerletIntegration.calculateNextBoxState;
import static com.prezyk.md.VerletIntegration.calculateNextMoleculeStates;

public class MolecularDynamics {

    private static final int X_COORDINATE = 0;
    private static final int Y_COORDINATE = 1;
    private static final double ELASTIC_INFLUENCE_THRESHOLD = 0.5;

    private final SimulationInput simulationInput;
    private final Simulation simulation;
    private BoxState currentBoxState;
    private MoleculeState[] currentMoleculesStates;

    public MolecularDynamics(SimulationInput simulationInput) {
        this.simulationInput = simulationInput;
        simulation = new Simulation(simulationInput);
        InitialConditionsGenerator initialConditionsGenerator = new InitialConditionsGenerator(simulationInput);
        currentMoleculesStates = initialConditionsGenerator.generateInitialConditions();
        currentBoxState = calculateNextBoxState(currentMoleculesStates, simulationInput.getEpsilon(), simulationInput.getMass(), 1, this::calculateElasticEnergyNew);
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
            currentMoleculesStates = calculateNextMoleculeStates(currentMoleculesStates, simulationInput.getTimeStep(), simulationInput.getEpsilon(), simulationInput.getMass(), 1,
                                                                 this::calculateElasticInteraction);
    }

    private void calculateBoxState() {
        currentBoxState = calculateNextBoxState(currentMoleculesStates, simulationInput.getEpsilon(), simulationInput.getMass(), 1,
                                                this::calculateElasticEnergyNew);
    }

    private double[][] calculateElasticInteraction(double[][] accelerationMatrix) {
        double elasticEnergyFactor = 0;
        for(int i = 0; i < simulationInput.getMoleculesQuantity(); i++) {
            double d = calculateElasticInteraction(i, X_COORDINATE, accelerationMatrix);
            d += calculateElasticInteraction(i, Y_COORDINATE, accelerationMatrix);
            elasticEnergyFactor += Math.pow(d, 2);
        }
        return accelerationMatrix;
    }

    private double calculateElasticEnergyNew(double[][] accelerationMatrix) {
        double elasticEnergyFactor = 0.;
        for (int i = 0; i < accelerationMatrix.length; i++) {
            elasticEnergyFactor += calculateElasticInteraction(i,  X_COORDINATE, accelerationMatrix);
            elasticEnergyFactor += calculateElasticInteraction(i, Y_COORDINATE, accelerationMatrix);
        }

        return calculateElasticEnergy(elasticEnergyFactor);
    }


    private double calculateElasticInteraction(int moleculeIndex, int coordinate, double[][] accelerationMatrix) {
        return calculateLowerBoundInfluenceIfAround(moleculeIndex, coordinate, accelerationMatrix)
                + calculateUpperBoundInfluenceIfAround(moleculeIndex, coordinate, accelerationMatrix);
    }
    
    private double calculateLowerBoundInfluenceIfAround(int moleculeIndex, int coordinate, double[][] accelerationMatrix) {
        double d = 0;
        if (isAroundLowerBound(moleculeIndex, coordinate)) {
            d = ELASTIC_INFLUENCE_THRESHOLD - currentMoleculesStates[moleculeIndex].getPositionVector()[coordinate];
            accelerationMatrix[moleculeIndex][coordinate] += simulationInput.getWallStiffness() * d;
        }
        return d;
    }
    
    private double calculateUpperBoundInfluenceIfAround(int moleculeIndex, int coordinate, double[][] accelerationMatrix) {
        double d = 0;
        if (isAroundUpperBound(moleculeIndex, coordinate)) {
            d = simulationInput.getBoxSize() - ELASTIC_INFLUENCE_THRESHOLD - currentMoleculesStates[moleculeIndex].getPositionVector()[coordinate];
            accelerationMatrix[moleculeIndex][coordinate] += simulationInput.getWallStiffness() * d;
        }
        return d;
    }

    private boolean isAroundLowerBound(int moleculeIndex, int coordinate) {
        return currentMoleculesStates[moleculeIndex].getPositionVector()[coordinate] < ELASTIC_INFLUENCE_THRESHOLD;
    }

    private boolean isAroundUpperBound(int moleculeIndex, int coordinate) {
        return currentMoleculesStates[moleculeIndex].getPositionVector()[coordinate] > (simulationInput.getBoxSize() - ELASTIC_INFLUENCE_THRESHOLD);
    }

    private void verletStep() {
        calculateMoleculesAcceleration();
        calculateBoxState();
    }

    private double calculateElasticEnergy(double elasticEnergyFactor) {
        return elasticEnergyFactor * 0.5 * simulationInput.getWallStiffness()  * simulationInput.getMass();
    }
}
