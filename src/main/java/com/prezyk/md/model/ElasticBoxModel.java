package com.prezyk.md.model;

import java.math.BigDecimal;

public class ElasticBoxModel implements MotionModel {

    private static final int X_COORDINATE = 0;
    private static final int Y_COORDINATE = 1;
    private static final BigDecimal ELASTIC_INFLUENCE_THRESHOLD = new BigDecimal("0.5");
    public static final String POTENTIAL_ENERGY_KEY = "Elastic potential energy";

    private final BigDecimal wallStiffness;
    private final BigDecimal boxSize;
    private final BigDecimal moleculeMass;

    public ElasticBoxModel(BigDecimal wallStiffness, BigDecimal boxSize, BigDecimal moleculeMass) {
        this.wallStiffness = wallStiffness;
        this.boxSize = boxSize;
        this.moleculeMass = moleculeMass;
    }

    @Override
    public BigDecimal[][] calculateNextAcceleration(BigDecimal[][] nextPositionsMatrix) {
        BigDecimal[][] accelerationMatrix = new BigDecimal[nextPositionsMatrix.length][nextPositionsMatrix[0].length];
        for(int i = 0; i < accelerationMatrix.length; i++) {
            accelerationMatrix[i][X_COORDINATE] = calculateElasticAcceleration(i, X_COORDINATE, nextPositionsMatrix);
            accelerationMatrix[i][Y_COORDINATE] = calculateElasticAcceleration(i, Y_COORDINATE, nextPositionsMatrix);
        }
        return accelerationMatrix;    }

    @Override
    public BigDecimal calculatePotentialEnergy(BigDecimal[][] currentPositions) {
        BigDecimal elasticEnergy = BigDecimal.ZERO;
        for (int i = 0; i < currentPositions.length; i++) {
            elasticEnergy = elasticEnergy.add(calculateElasticEnergy(i, X_COORDINATE, currentPositions));
            elasticEnergy = elasticEnergy.add(calculateElasticEnergy(i, Y_COORDINATE, currentPositions));
        }

        return elasticEnergy.multiply(moleculeMass);
    }

    @Override
    public String getPotentialEnergyKey() {
        return POTENTIAL_ENERGY_KEY;
    }

    private BigDecimal calculateElasticEnergy(int moleculeIndex, int coordinate, BigDecimal[][] positionMatrix) {
        return calculateLowerBoundEnergyIfAround(moleculeIndex, coordinate, positionMatrix)
                .add(calculateUpperBoundEnergyIfAround(moleculeIndex, coordinate, positionMatrix));
    }

    private BigDecimal calculateLowerBoundEnergyIfAround(int moleculeIndex, int coordinate, BigDecimal[][] positionMatrix) {
        BigDecimal elasticEnergy = BigDecimal.ZERO;
        if (isAroundLowerBound(moleculeIndex, coordinate, positionMatrix)) {
            elasticEnergy = wallStiffness.multiply(BigDecimal.valueOf(0.5))
                                         .multiply(ELASTIC_INFLUENCE_THRESHOLD.subtract(positionMatrix[moleculeIndex][coordinate])
                                                                              .pow(2));
        }
        return elasticEnergy;
    }

    private BigDecimal calculateUpperBoundEnergyIfAround(int moleculeIndex, int coordinate, BigDecimal[][] positionMatrix) {
        BigDecimal elasticEnergy = BigDecimal.ZERO;
        if (isAroundUpperBound(moleculeIndex, coordinate, positionMatrix)) {
            elasticEnergy = wallStiffness.multiply(BigDecimal.valueOf(0.5))
                                         .multiply(boxSize.subtract(ELASTIC_INFLUENCE_THRESHOLD).subtract(positionMatrix[moleculeIndex][coordinate]).pow(2));
        }
        return elasticEnergy;
    }


    private BigDecimal calculateElasticAcceleration(int moleculeIndex, int coordinate, BigDecimal[][] positionMatrix) {
        return calculateLowerBoundInfluenceIfAround(moleculeIndex, coordinate, positionMatrix)
                .add(calculateUpperBoundInfluenceIfAround(moleculeIndex, coordinate, positionMatrix));
    }

    private BigDecimal calculateLowerBoundInfluenceIfAround(int moleculeIndex, int coordinate, BigDecimal[][] positionMatrix) {
        BigDecimal elasticAcceleration = BigDecimal.ZERO;
        if (isAroundLowerBound(moleculeIndex, coordinate, positionMatrix)) {
            elasticAcceleration = wallStiffness.multiply(ELASTIC_INFLUENCE_THRESHOLD.subtract(positionMatrix[moleculeIndex][coordinate]));
        }
        return elasticAcceleration;
    }

    private BigDecimal calculateUpperBoundInfluenceIfAround(int moleculeIndex, int coordinate, BigDecimal[][] positionMatrix) {
        BigDecimal elasticAcceleration = BigDecimal.ZERO;
        if (isAroundUpperBound(moleculeIndex, coordinate, positionMatrix)) {
            elasticAcceleration = wallStiffness.multiply(boxSize.subtract(ELASTIC_INFLUENCE_THRESHOLD)
                                                                .subtract(positionMatrix[moleculeIndex][coordinate]));
        }
        return elasticAcceleration;
    }

    private boolean isAroundLowerBound(int moleculeIndex, int coordinate, BigDecimal[][] positionMatrix) {
        return positionMatrix[moleculeIndex][coordinate].compareTo(ELASTIC_INFLUENCE_THRESHOLD) < 0;
    }

    private boolean isAroundUpperBound(int moleculeIndex, int coordinate, BigDecimal[][] positionMatrix) {
        return positionMatrix[moleculeIndex][coordinate].compareTo(boxSize.subtract(ELASTIC_INFLUENCE_THRESHOLD)) > 0;
    }
}
