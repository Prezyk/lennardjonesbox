package com.prezyk.md;

import static com.prezyk.util.VectorUtil.copyVector;

public class MoleculeState {
    private final double[] positionVector;
    private final double[] velocityVector;
    private final double[] accelerationVector;

    public MoleculeState(double[] positionVector,
                         double[] velocityVector,
                         double[] accelerationVector) {
        this.positionVector = copyVector(positionVector);
        this.velocityVector = copyVector(velocityVector);
        this.accelerationVector = copyVector(accelerationVector);
    }

    public double[] getPositionVector() {
        return positionVector;
    }

    public double[] getVelocityVector() {
        return velocityVector;
    }

    public double[] getAccelerationVector() {
        return accelerationVector;
    }

    public MoleculeState clone() {
        return new MoleculeState(positionVector, velocityVector, accelerationVector);
    }
}
