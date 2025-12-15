package com.prezyk.md;

import static com.prezyk.util.VectorUtil.fillMatrixWithValue;

public class NewtonMotionModel implements MotionModel {
    private double mass;

    public NewtonMotionModel(double mass) {
        this.mass = mass;
    }

    @Override
    public double[][] calculateNextAcceleration(double[][] currentPositions) {
        double[][] accelerations = new double[currentPositions.length][currentPositions[0].length];
        accelerations = fillMatrixWithValue(accelerations, 0);
        return accelerations;
    }

    @Override
    public double calculatePotentialEnergy(double[][] currentPositions) {
        return 0;
    }
}
