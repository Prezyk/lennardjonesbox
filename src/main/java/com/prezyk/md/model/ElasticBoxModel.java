package com.prezyk.md.model;

public class ElasticBoxModel implements MotionModel {

    private static final int X_COORDINATE = 0;
    private static final int Y_COORDINATE = 1;
    private static final double ELASTIC_INFLUENCE_THRESHOLD = 0.5;
    public static final String POTENTIAL_ENERGY_KEY = "Elastic potential energy";

    private final double wallStiffness;
    private final double boxSize;
    private final double moleculeMass;

    public ElasticBoxModel(double wallStiffness, double boxSize, double moleculeMass) {
        this.wallStiffness = wallStiffness;
        this.boxSize = boxSize;
        this.moleculeMass = moleculeMass;
    }

    @Override
    public double[][] calculateNextAcceleration(double[][] nextPositionsMatrix) {
        double[][] accelerationMatrix = new double[nextPositionsMatrix.length][nextPositionsMatrix[0].length];
        for(int i = 0; i < accelerationMatrix.length; i++) {
            accelerationMatrix[i][X_COORDINATE] = calculateElasticAcceleration(i, X_COORDINATE, nextPositionsMatrix);
            accelerationMatrix[i][Y_COORDINATE] = calculateElasticAcceleration(i, Y_COORDINATE, nextPositionsMatrix);
        }
        return accelerationMatrix;    }

    @Override
    public double calculatePotentialEnergy(double[][] currentPositions) {
        double elasticEnergy = 0.;
        for (int i = 0; i < currentPositions.length; i++) {
            elasticEnergy += calculateElasticEnergy(i, X_COORDINATE, currentPositions);
            elasticEnergy += calculateElasticEnergy(i, Y_COORDINATE, currentPositions);
        }

        return elasticEnergy * moleculeMass;
    }

    @Override
    public String getPotentialEnergyKey() {
        return POTENTIAL_ENERGY_KEY;
    }

    private double calculateElasticEnergy(int moleculeIndex, int coordinate, double[][] positionMatrix) {
        return calculateLowerBoundEnergyIfAround(moleculeIndex, coordinate, positionMatrix)
                + calculateUpperBoundEnergyIfAround(moleculeIndex, coordinate, positionMatrix);
    }

    private double calculateLowerBoundEnergyIfAround(int moleculeIndex, int coordinate, double[][] positionMatrix) {
        double elasticEnergy = 0;
        if (isAroundLowerBound(moleculeIndex, coordinate, positionMatrix)) {
            elasticEnergy = wallStiffness * 0.5 * Math.pow((ELASTIC_INFLUENCE_THRESHOLD - positionMatrix[moleculeIndex][coordinate]), 2);
        }
        return elasticEnergy;
    }

    private double calculateUpperBoundEnergyIfAround(int moleculeIndex, int coordinate, double[][] positionMatrix) {
        double elasticEnergy = 0;
        if (isAroundUpperBound(moleculeIndex, coordinate, positionMatrix)) {
            elasticEnergy = wallStiffness  * 0.5 * Math.pow((boxSize - ELASTIC_INFLUENCE_THRESHOLD - positionMatrix[moleculeIndex][coordinate]), 2);
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
            elasticAcceleration = wallStiffness * (ELASTIC_INFLUENCE_THRESHOLD - positionMatrix[moleculeIndex][coordinate]);
        }
        return elasticAcceleration;
    }

    private double calculateUpperBoundInfluenceIfAround(int moleculeIndex, int coordinate, double[][] positionMatrix) {
        double elasticAcceleration = 0;
        if (isAroundUpperBound(moleculeIndex, coordinate, positionMatrix)) {
            elasticAcceleration = wallStiffness * (boxSize - ELASTIC_INFLUENCE_THRESHOLD - positionMatrix[moleculeIndex][coordinate]);
        }
        return elasticAcceleration;
    }

    private boolean isAroundLowerBound(int moleculeIndex, int coordinate, double[][] positionMatrix) {
        return positionMatrix[moleculeIndex][coordinate] < ELASTIC_INFLUENCE_THRESHOLD;
    }

    private boolean isAroundUpperBound(int moleculeIndex, int coordinate, double[][] positionMatrix) {
        return positionMatrix[moleculeIndex][coordinate] > (boxSize - ELASTIC_INFLUENCE_THRESHOLD);
    }
}
