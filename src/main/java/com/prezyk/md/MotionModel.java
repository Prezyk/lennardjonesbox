package com.prezyk.md;

public interface MotionModel {
    double[][] calculateNextAcceleration(double[][] nextPositionsMatrix);

    double calculatePotentialEnergy(double[][] positionsMatrix);

    String getPotentialEnergyKey();
}
