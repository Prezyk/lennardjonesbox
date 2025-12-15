package com.prezyk.md.model;

public interface MotionModel {
    double[][] calculateNextAcceleration(double[][] nextPositionsMatrix);

    double calculatePotentialEnergy(double[][] positionsMatrix);

    String getPotentialEnergyKey();
}
