package com.prezyk.md.model;

import java.math.BigDecimal;

import static com.prezyk.util.VectorUtil.*;

public class LennardJonesModel implements MotionModel {
    public static final String POTENTIAL_ENERGY_KEY = "LJ potential energy";

    private final BigDecimal epsilon;
    private final BigDecimal mass;
    private final BigDecimal sigma;

    public LennardJonesModel(BigDecimal epsilon, BigDecimal mass, BigDecimal sigma) {
        this.epsilon = epsilon;
        this.mass = mass;
        this.sigma = sigma;
    }

    @Override
    public BigDecimal[][] calculateNextAcceleration(BigDecimal[][] nextPositionsMatrix) {
        BigDecimal[][] accelerationMatrix = new BigDecimal[nextPositionsMatrix.length][];
        for (int i = 0; i < nextPositionsMatrix.length; i++) {
            BigDecimal[][] relativeMoleculeDistances = calculateMoleculeDistances(i, nextPositionsMatrix);
            BigDecimal[][] moleculeForces = calculateForcesForMolecule(relativeMoleculeDistances);
            BigDecimal[] resultantForce = calculateResultantForceForMolecule(moleculeForces);
            accelerationMatrix[i] = divideVector(resultantForce, mass);
        }
        return accelerationMatrix;
    }

    @Override
    public BigDecimal calculatePotentialEnergy(BigDecimal[][] currentPositions) {
        BigDecimal potentialEnergy = BigDecimal.ZERO;
        for (int i = 0; i < currentPositions.length; i++) {
            BigDecimal[][] relativeMoleculeDistances = calculateMoleculeDistances(i, currentPositions);
            potentialEnergy = potentialEnergy.add(calculateEnergyForMolecule(relativeMoleculeDistances));
        }
        return potentialEnergy;
    }

    public BigDecimal[] calculateEnergyInFunctionOfDistance(BigDecimal[] distances) {
        return calculateEnergyForDistances(distances);
    }

    @Override
    public String getPotentialEnergyKey() {
        return POTENTIAL_ENERGY_KEY;
    }

    private BigDecimal[][] calculateMoleculeDistances(int moleculeIndex, BigDecimal[][] moleculesPositions) {
        BigDecimal[][] otherMoleculesPositions = removeVectorFromMatrix(moleculesPositions, moleculeIndex);
        return subtractVectorFromMatrix(otherMoleculesPositions, moleculesPositions[moleculeIndex]);
    }

    private BigDecimal[] calculateResultantForceForMolecule(BigDecimal[][] moleculeForceMatrix) {
        return sumMatrixVectors(moleculeForceMatrix);
    }

    private BigDecimal[][] calculateForcesForMolecule(BigDecimal[][] moleculeDistanceMatrixFromOtherMolecules) {
        return multiplyMatrix(
                subtractMatrices(
                        multiplyMatrix(
                                divideScalarByMatrix(
                                        BigDecimal.ONE,
                                        matrixPowerScalar(moleculeDistanceMatrixFromOtherMolecules, BigDecimal.valueOf(13))
                                ),
                                sigma.pow(12).multiply(BigDecimal.valueOf(2))
                        ),
                        multiplyMatrix(
                                divideScalarByMatrix(
                                        BigDecimal.ONE,
                                        matrixPowerScalar(moleculeDistanceMatrixFromOtherMolecules, BigDecimal.valueOf(7))
                                ),
                                sigma.pow(6)
                        )
                ),
                epsilon.multiply(BigDecimal.valueOf(-24))
        );
    }

    private BigDecimal calculateEnergyForMolecule(BigDecimal[][] moleculeDistanceMatrixFromOtherMolecules) {
        BigDecimal[] scalarDistanceVector = matrixVectorLengths(moleculeDistanceMatrixFromOtherMolecules);
        return sumVectorElements(
            calculateEnergyForDistances(scalarDistanceVector)
        );
    }

    private BigDecimal[] calculateEnergyForDistances(BigDecimal[] scalarDistanceVector) {
        return multiplyVector(
                subtractVectors(
                        multiplyVector(
                                divideScalarByVector(
                                        BigDecimal.ONE,
                                        vectorPowerScalar(scalarDistanceVector, BigDecimal.valueOf(12))
                                ),
                                sigma.pow(12).multiply(BigDecimal.valueOf(2))
                        ),
                        multiplyVector(
                                divideScalarByVector(
                                        BigDecimal.ONE,
                                        vectorPowerScalar(scalarDistanceVector, BigDecimal.valueOf(6))
                                ),
                                sigma.pow(6)
                        )
                ),
                epsilon.multiply(BigDecimal.valueOf(4))
        );
    }
}
