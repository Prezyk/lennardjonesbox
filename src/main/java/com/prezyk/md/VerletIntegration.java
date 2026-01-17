package com.prezyk.md;

import com.prezyk.md.model.MotionModel;
import com.prezyk.md.state.BoxState;
import com.prezyk.md.state.MoleculeState;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    public MoleculeState[] calculateNextMoleculeStates(MoleculeState[] currentMoleculesStates, BigDecimal timeStep) {
        BigDecimal[][] positionsMatrix = extractPositionsMatrix(currentMoleculesStates);
        BigDecimal[][] velocitiesMatrix = extractVelocitiesMatrix(currentMoleculesStates);
        BigDecimal[][] accelerationsMatrix = extractAccelerationsMatrix(currentMoleculesStates);

        BigDecimal[][] nextPositionsMatrix = addMatrices(
                addMatrices(positionsMatrix, multiplyMatrix(velocitiesMatrix, timeStep)),
                multiplyMatrix(accelerationsMatrix, timeStep.pow(2).divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP))
        );
        BigDecimal[][] nextAccelerationsMatrix = calculateNextAcceleration(nextPositionsMatrix);
        BigDecimal[][] nextVelocitiesMatrix = addMatrices(velocitiesMatrix, multiplyMatrix(addMatrices(accelerationsMatrix, nextAccelerationsMatrix), timeStep.divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP)));
        return convertToMoleculeStates(nextPositionsMatrix, nextVelocitiesMatrix, nextAccelerationsMatrix);
    }

    private BigDecimal[][] calculateNextAcceleration(BigDecimal[][] nextPositionsMatrix) {
        BigDecimal[][] accelerationMatrix = new BigDecimal[nextPositionsMatrix.length][nextPositionsMatrix[0].length];
        for (MotionModel motionModel: motionModels) {
            accelerationMatrix = addMatrices(accelerationMatrix, motionModel.calculateNextAcceleration(nextPositionsMatrix));
        }
        return accelerationMatrix;
    }

    public BoxState calculateNextBoxState(MoleculeState[] nextMoleculesStates, BigDecimal mass) {
        BigDecimal kineticEnergy = calculateKineticEnergy(nextMoleculesStates, mass);
        BoxState boxState = new BoxState(kineticEnergy);
        for (MotionModel motionModel: motionModels) {
            boxState.putPotentialEnergy(motionModel.getPotentialEnergyKey(), motionModel.calculatePotentialEnergy(extractPositionsMatrix(nextMoleculesStates)));
        }
        return boxState;
    }

    private BigDecimal calculateKineticEnergy(MoleculeState[] moleculesStates, BigDecimal mass) {
        BigDecimal kineticEnergy = BigDecimal.ZERO;
        for (MoleculeState moleculeState: moleculesStates) {
            kineticEnergy = kineticEnergy.add(vectorLength(moleculeState.getVelocityVector()).pow(2)
                                                                                             .divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP)
                                                                                             .multiply(mass));
        }
        return kineticEnergy;
    }

    private BigDecimal[][] extractPositionsMatrix(MoleculeState[] moleculeStates) {
        return Arrays.stream(moleculeStates).map(MoleculeState::getPositionVector).toArray(BigDecimal[][]::new);
    }

    private BigDecimal[][] extractVelocitiesMatrix(MoleculeState[] moleculeStates) {
        return Arrays.stream(moleculeStates).map(MoleculeState::getVelocityVector).toArray(BigDecimal[][]::new);
    }

    private BigDecimal[][] extractAccelerationsMatrix(MoleculeState[] moleculeStates) {
        return Arrays.stream(moleculeStates).map(MoleculeState::getAccelerationVector).toArray(BigDecimal[][]::new);
    }

    private MoleculeState[] convertToMoleculeStates(BigDecimal[][] positions, BigDecimal[][] velocities, BigDecimal[][] accelerations) {
        MoleculeState[] moleculeStates = new MoleculeState[positions.length];
        for (int i = 0; i < positions.length; i++) {
            moleculeStates[i] = new MoleculeState(positions[i], velocities[i], accelerations[i]);
        }
        return moleculeStates;
    }
}
