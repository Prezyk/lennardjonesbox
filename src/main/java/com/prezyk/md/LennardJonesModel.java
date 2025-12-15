package com.prezyk.md;

import static com.prezyk.util.VectorUtil.*;

public class LennardJonesModel implements MotionModel {
    public static final String POTENTIAL_ENERGY_KEY = "LJ potential energy";

    private final double epsilon;
    private final double mass;
    private final double sigma;

    public LennardJonesModel(double epsilon, double mass, double sigma) {
        this.epsilon = epsilon;
        this.mass = mass;
        this.sigma = sigma;
    }

    @Override
    public double[][] calculateNextAcceleration(double[][] nextPositionsMatrix) {
        double[][] accelerationMatrix = new double[nextPositionsMatrix.length][];
        for (int i = 0; i < nextPositionsMatrix.length; i++) {
            double[][] relativeMoleculeDistances = calculateMoleculeDistances(i, nextPositionsMatrix);
            double[][] moleculeForces = calculateForcesForMolecule(relativeMoleculeDistances,
                                                                   epsilon,
                                                                   sigma);
            double[] resultantForce = calculateResultantForceForMolecule(moleculeForces);
            accelerationMatrix[i] = divideVector(resultantForce, mass);
        }
        return accelerationMatrix;
    }

    @Override
    public double calculatePotentialEnergy(double[][] currentPositions) {
        double potentialEnergy = 0;
        for (int i = 0; i < currentPositions.length; i++) {
            double[][] relativeMoleculeDistances = calculateMoleculeDistances(i, currentPositions);
            potentialEnergy += calculateEnergyForMolecule(relativeMoleculeDistances,
                                                                   epsilon,
                                                                   sigma);
        }
        return potentialEnergy;
    }

    @Override
    public String getPotentialEnergyKey() {
        return POTENTIAL_ENERGY_KEY;
    }

    private double[][] calculateMoleculeDistances(int moleculeIndex, double[][] moleculesPositions) {
        double[][] otherMoleculesPositions = removeVectorFromMatrix(moleculesPositions, moleculeIndex);
        return subtractVectorFromMatrix(otherMoleculesPositions, moleculesPositions[moleculeIndex]);
    }

    private double[] calculateResultantForceForMolecule(double[][] moleculeForceMatrix) {
        return sumMatrixVectors(moleculeForceMatrix);
    }

    private double[][] calculateForcesForMolecule(double[][] moleculeDistanceMatrixFromOtherMolecules,
                                            double epsilon,
                                            double sigma) {
        return multiplyMatrix(
                subtractMatrices(
                        multiplyMatrix(
                                divideScalarByMatrix(
                                        1,
                                        matrixPowerScalar(moleculeDistanceMatrixFromOtherMolecules, 13)
                                ),
                                2 * Math.pow(sigma, 12)
                        ),
                        multiplyMatrix(
                                divideScalarByMatrix(
                                        1,
                                        matrixPowerScalar(moleculeDistanceMatrixFromOtherMolecules, 7)
                                ),
                                Math.pow(sigma, 6)
                        )
                ),
                (24) * epsilon
        );
    }

    private double calculateEnergyForMolecule(double[][] moleculeDistanceMatrixFromOtherMolecules,
                                                     double epsilon,
                                                     double sigma) {
        double[] scalarDistanceVector = matrixVectorLengths(moleculeDistanceMatrixFromOtherMolecules);
        return sumVectorElements(
                multiplyVector(
                        subtractVectors(
                                multiplyVector(
                                        divideScalarByVector(
                                                1,
                                                vectorPowerScalar(scalarDistanceVector, 12)
                                        ),
                                        2 * Math.pow(sigma, 12)
                                ),
                                multiplyVector(
                                        divideScalarByVector(
                                                1,
                                                vectorPowerScalar(scalarDistanceVector, 6)
                                        ),
                                        Math.pow(sigma, 6)
                                )
                        ),
                        4 * epsilon
                )
        );
    }
}
