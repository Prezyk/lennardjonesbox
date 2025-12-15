package com.prezyk.md;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.prezyk.util.VectorUtil.*;

public class VerletIntegration {

    private final MotionModel motionModel;

    public VerletIntegration(MotionModel motionModel) {
        this.motionModel = motionModel;
    }

    public MoleculeState[] calculateNextMoleculeStates(MoleculeState[] currentMoleculesStates, double timeStep,
                                                              BiFunction<double[][], double[][], double[][]> additionalAccelerationImpact) {
        double[][] positionsMatrix = extractPositionsMatrix(currentMoleculesStates);
        double[][] velocitiesMatrix = extractVelocitiesMatrix(currentMoleculesStates);
        double[][] accelerationsMatrix = extractAccelerationsMatrix(currentMoleculesStates);

        double[][] nextPositionsMatrix = addMatrices(
                addMatrices(positionsMatrix, multiplyMatrix(velocitiesMatrix, timeStep)),
                multiplyMatrix(accelerationsMatrix, Math.pow(timeStep, 2) / 2.)
        );
        double[][] nextAccelerationsMatrix = motionModel.calculateNextAcceleration(nextPositionsMatrix);
        nextAccelerationsMatrix = additionalAccelerationImpact.apply(nextAccelerationsMatrix, nextPositionsMatrix);
        double[][] nextVelocitiesMatrix = addMatrices(velocitiesMatrix, multiplyMatrix(addMatrices(accelerationsMatrix, nextAccelerationsMatrix), timeStep / 2.));
        return convertToMoleculeStates(nextPositionsMatrix, nextVelocitiesMatrix, nextAccelerationsMatrix);
    }

    public BoxState calculateNextBoxState(MoleculeState[] nextMoleculesStates,
                                                 double mass,
                                                 Function<double[][], Double> additionalEnergy) {
        double kineticEnergy = calculateKineticEnergy(nextMoleculesStates, mass);
        double elasticEnergy = additionalEnergy.apply(extractPositionsMatrix(nextMoleculesStates));
        double potentialEnergy = motionModel.calculatePotentialEnergy(extractPositionsMatrix(nextMoleculesStates));
        return new BoxState(kineticEnergy, elasticEnergy, potentialEnergy);
    }

    private Double calculateKineticEnergy(MoleculeState[] moleculesStates, double mass) {
        double kineticEnergy = 0.;
        for (MoleculeState moleculeState: moleculesStates) {
            kineticEnergy += mass * Math.pow(vectorLength(moleculeState.getVelocityVector()), 2) / 2;
        }
        return kineticEnergy;
    }

    private double[][] extractPositionsMatrix(MoleculeState[] moleculeStates) {
        return Arrays.stream(moleculeStates).map(com.prezyk.md.MoleculeState::getPositionVector).toArray(double[][]::new);
    }

    private double[][] extractVelocitiesMatrix(MoleculeState[] moleculeStates) {
        return Arrays.stream(moleculeStates).map(com.prezyk.md.MoleculeState::getVelocityVector).toArray(double[][]::new);
    }

    private double[][] extractAccelerationsMatrix(MoleculeState[] moleculeStates) {
        return Arrays.stream(moleculeStates).map(com.prezyk.md.MoleculeState::getAccelerationVector).toArray(double[][]::new);
    }

    private MoleculeState[] convertToMoleculeStates(double[][] positions, double[][] velocities, double[][] accelerations) {
        MoleculeState[] moleculeStates = new MoleculeState[positions.length];
        for (int i = 0; i < positions.length; i++) {
            moleculeStates[i] = new MoleculeState(positions[i], velocities[i], accelerations[i]);
        }
        return moleculeStates;
    }
}
