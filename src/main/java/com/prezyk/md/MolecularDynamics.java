package com.prezyk.md;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class MolecularDynamics {

    private static final int X_COORDINATE = 0;
    private static final int Y_COORDINATE = 1;
    private static final double ELASTIC_INFLUENCE_THRESHOLD = 0.5;

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
//        integrator = new VerletIntegration(new LennardJonesModel(simulationInput.getEpsilon(), simulationInput.getMass(), 1));
        integrator = new VerletIntegration(new NewtonMotionModel(simulationInput.getMass()));
        currentBoxState = integrator.calculateNextBoxState(currentMoleculesStates, simulationInput.getMass(), (positionsMatrix) -> this.calculateElasticEnergyNew(positionsMatrix, simulationInput.getMass()));
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
            currentMoleculesStates = integrator.calculateNextMoleculeStates(currentMoleculesStates, simulationInput.getTimeStep(),
                                                                            this::calculateElasticAcceleration);
    }

    private void calculateBoxState() {
        currentBoxState = integrator.calculateNextBoxState(currentMoleculesStates, simulationInput.getMass(),
                                                           (positionsMatrix) -> this.calculateElasticEnergyNew(positionsMatrix, simulationInput.getMass()));
    }

    private double[][] calculateElasticAcceleration(double[][] accelerationMatrix, double[][] positionsMatrix) {
        for(int i = 0; i < accelerationMatrix.length; i++) {
            accelerationMatrix[i][X_COORDINATE] = accelerationMatrix[i][X_COORDINATE] + calculateElasticAcceleration(i, X_COORDINATE, positionsMatrix);
            accelerationMatrix[i][Y_COORDINATE] = accelerationMatrix[i][Y_COORDINATE] + calculateElasticAcceleration(i, Y_COORDINATE, positionsMatrix);
        }
        return accelerationMatrix;
    }

    private double calculateElasticEnergyNew(double[][] positionMatrix, double mass) {
        double elasticEnergy = 0.;
        for (int i = 0; i < positionMatrix.length; i++) {
            elasticEnergy += calculateElasticEnergyNew(i, X_COORDINATE, positionMatrix);
            elasticEnergy += calculateElasticEnergyNew(i, Y_COORDINATE, positionMatrix);
        }

        return elasticEnergy * mass;
    }

    private double calculateElasticEnergyNew(int moleculeIndex, int coordinate, double[][] positionMatrix) {
        return calculateLowerBoundEnergyIfAround(moleculeIndex, coordinate, positionMatrix)
                + calculateUpperBoundEnergyIfAround(moleculeIndex, coordinate, positionMatrix);
    }

    private double calculateLowerBoundEnergyIfAround(int moleculeIndex, int coordinate, double[][] positionMatrix) {
        double elasticEnergy = 0;
        if (isAroundLowerBound(moleculeIndex, coordinate, positionMatrix)) {
            elasticEnergy = simulationInput.getWallStiffness() * 0.5 * Math.pow((ELASTIC_INFLUENCE_THRESHOLD - positionMatrix[moleculeIndex][coordinate]), 2);
        }
        return elasticEnergy;
    }

    private double calculateUpperBoundEnergyIfAround(int moleculeIndex, int coordinate, double[][] positionMatrix) {
        double elasticEnergy = 0;
        if (isAroundUpperBound(moleculeIndex, coordinate, positionMatrix)) {
            elasticEnergy = simulationInput.getWallStiffness()  * 0.5 * Math.pow((simulationInput.getBoxSize() - ELASTIC_INFLUENCE_THRESHOLD - positionMatrix[moleculeIndex][coordinate]), 2);
        }
        return elasticEnergy;
    }


    private double calculateElasticAcceleration(int moleculeIndex, int coordinate, double[][] positionMatrix) {
        return (calculateLowerBoundInfluenceIfAround(moleculeIndex, coordinate, positionMatrix)
                + calculateUpperBoundInfluenceIfAround(moleculeIndex, coordinate, positionMatrix));
    }
    
    private double calculateLowerBoundInfluenceIfAround(int moleculeIndex, int coordinate, double[][] positionMatrix) {
        double elasticAcceleration = 0;
        if (isAroundLowerBound(moleculeIndex, coordinate, positionMatrix)) {
            elasticAcceleration = simulationInput.getWallStiffness() * (ELASTIC_INFLUENCE_THRESHOLD - positionMatrix[moleculeIndex][coordinate]);
        }
        return elasticAcceleration;
    }
    
    private double calculateUpperBoundInfluenceIfAround(int moleculeIndex, int coordinate, double[][] positionMatrix) {
        double elasticAcceleration = 0;
        if (isAroundUpperBound(moleculeIndex, coordinate, positionMatrix)) {
            elasticAcceleration = simulationInput.getWallStiffness() * (simulationInput.getBoxSize() - ELASTIC_INFLUENCE_THRESHOLD - positionMatrix[moleculeIndex][coordinate]);
        }
        return elasticAcceleration;
    }

    private boolean isAroundLowerBound(int moleculeIndex, int coordinate, double[][] positionMatrix) {
        return positionMatrix[moleculeIndex][coordinate] < ELASTIC_INFLUENCE_THRESHOLD;
    }

    private boolean isAroundUpperBound(int moleculeIndex, int coordinate, double[][] positionMatrix) {
        return positionMatrix[moleculeIndex][coordinate] > (simulationInput.getBoxSize() - ELASTIC_INFLUENCE_THRESHOLD);
    }

    private void verletStep() {
        calculateMoleculesAcceleration();
        calculateBoxState();
    }
}
