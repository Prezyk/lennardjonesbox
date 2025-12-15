package com.prezyk.md;

public interface MotionModel {
    double[][] calculateNextAcceleration(double[][] currentPositions);

    double calculatePotentialEnergy(double[][] currentPositions);
}
