package com.prezyk.md;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.prezyk.util.VectorUtil.*;

public class VerletIntegration {

    private final List<MotionModel> motionModels;

    public VerletIntegration() {
        this.motionModels = new ArrayList<>();
    }

    public void registerModel(MotionModel motionModel) {
        this.motionModels.add(motionModel);
    }

    public MoleculeState[] calculateNextMoleculeStates(MoleculeState[] currentMoleculesStates, double timeStep) {
        double[][] positionsMatrix = extractPositionsMatrix(currentMoleculesStates);
        double[][] velocitiesMatrix = extractVelocitiesMatrix(currentMoleculesStates);
        double[][] accelerationsMatrix = extractAccelerationsMatrix(currentMoleculesStates);

        double[][] nextPositionsMatrix = addMatrices(
                addMatrices(positionsMatrix, multiplyMatrix(velocitiesMatrix, timeStep)),
                multiplyMatrix(accelerationsMatrix, Math.pow(timeStep, 2) / 2.)
        );
        double[][] nextAccelerationsMatrix = calculateNextAcceleration(nextPositionsMatrix);
        double[][] nextVelocitiesMatrix = addMatrices(velocitiesMatrix, multiplyMatrix(addMatrices(accelerationsMatrix, nextAccelerationsMatrix), timeStep / 2.));
        return convertToMoleculeStates(nextPositionsMatrix, nextVelocitiesMatrix, nextAccelerationsMatrix);
    }

    private double[][] calculateNextAcceleration(double[][] nextPositionsMatrix) {
        double[][] accelerationMatrix = new double[nextPositionsMatrix.length][nextPositionsMatrix[0].length];
        for (MotionModel motionModel: motionModels) {
            accelerationMatrix = addMatrices(accelerationMatrix, motionModel.calculateNextAcceleration(nextPositionsMatrix));
        }
        return accelerationMatrix;
    }

    public BoxState calculateNextBoxState(MoleculeState[] nextMoleculesStates, double mass) {
        double kineticEnergy = calculateKineticEnergy(nextMoleculesStates, mass);
        BoxState boxState = new BoxState(kineticEnergy);
        for (MotionModel motionModel: motionModels) {
            boxState.putPotentialEnergy(motionModel.getPotentialEnergyKey(), motionModel.calculatePotentialEnergy(extractPositionsMatrix(nextMoleculesStates)));
        }
        return boxState;
    }

    private double calculatePotentialEnergy(double[][] positionsMatrix) {
        double potentialEnergy = 0.;

        return potentialEnergy;
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
