package com.prezyk.md;

import java.util.Arrays;
import java.util.function.Function;

import static com.prezyk.md.LennardJonesModel.calculateCurrentAcceleration;
import static com.prezyk.md.LennardJonesModel.calculatePotentialEnergy;
import static com.prezyk.util.VectorUtil.*;

public class VerletIntegration {

    public static MoleculeState[] calculateNextMoleculeStates(MoleculeState[] currentMoleculesStates, double timeStep, double epsilon,
                                                              double mass,
                                                              double sigma,
                                                              Function<double[][], double[][]> additionalAccelerationImpact) {
        double[][] positionsMatrix = extractPositionsMatrix(currentMoleculesStates);
        double[][] velocitiesMatrix = extractVelocitiesMatrix(currentMoleculesStates);
        double[][] accelerationsMatrix = extractAccelerationsMatrix(currentMoleculesStates);

        double[][] halfWayVelocitiesMatrix = addMatrices(velocitiesMatrix, multiplyMatrix(accelerationsMatrix, timeStep / 2.));
        double[][] nextPositionsMatrix = addMatrices(positionsMatrix, multiplyMatrix(halfWayVelocitiesMatrix, timeStep));
        double[][] nextAccelerationsMatrix = calculateCurrentAcceleration(nextPositionsMatrix, epsilon, mass, sigma);
        nextAccelerationsMatrix = additionalAccelerationImpact.apply(nextAccelerationsMatrix);
        double[][] nextVelocitiesMatrix = addMatrices(halfWayVelocitiesMatrix, multiplyMatrix(nextAccelerationsMatrix, timeStep / 2.));
        return convertToMoleculeStates(nextPositionsMatrix, nextVelocitiesMatrix, nextAccelerationsMatrix);
    }

    public static BoxState calculateNextBoxState(MoleculeState[] nextMoleculesStates, double epsilon,
                                                 double mass,
                                                 double sigma,
                                                 Function<double[][], Double> additionalEnergy) {
        double kineticEnergy = calculateKineticEnergy(nextMoleculesStates, mass);
        double elasticEnergy = additionalEnergy.apply(extractAccelerationsMatrix(nextMoleculesStates));
        double potentialEnergy = calculatePotentialEnergy(extractPositionsMatrix(nextMoleculesStates), epsilon, sigma);
        return new BoxState(kineticEnergy, elasticEnergy, potentialEnergy);
    }

    private static Double calculateKineticEnergy(MoleculeState[] moleculesStates, double mass) {
        double kineticEnergy = 0.;
        for (MoleculeState moleculeState: moleculesStates) {
            kineticEnergy += mass * Math.pow(vectorLength(moleculeState.getVelocityVector()), 2) / 2;
        }
        return kineticEnergy;
    }

    private static double[][] extractPositionsMatrix(MoleculeState[] moleculeStates) {
        return Arrays.stream(moleculeStates).map(com.prezyk.md.MoleculeState::getPositionVector).toArray(double[][]::new);
    }

    private static double[][] extractVelocitiesMatrix(MoleculeState[] moleculeStates) {
        return Arrays.stream(moleculeStates).map(com.prezyk.md.MoleculeState::getVelocityVector).toArray(double[][]::new);
    }

    private static double[][] extractAccelerationsMatrix(MoleculeState[] moleculeStates) {
        return Arrays.stream(moleculeStates).map(com.prezyk.md.MoleculeState::getAccelerationVector).toArray(double[][]::new);
    }

    private static MoleculeState[] convertToMoleculeStates(double[][] positions, double[][] velocities, double[][] accelerations) {
        MoleculeState[] moleculeStates = new MoleculeState[positions.length];
        for (int i = 0; i < positions.length; i++) {
            moleculeStates[i] = new MoleculeState(positions[i], velocities[i], accelerations[i]);
        }
        return moleculeStates;
    }
}
