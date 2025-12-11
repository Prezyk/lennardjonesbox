package com.prezyk.md;

import static com.prezyk.util.VectorUtil.copyVector;

public class MoleculeState {
    private final double[] positionVector;
    private final double[] velocityVector;
    private final double[] accelerationVector;

    private MoleculeState(double[] positionVector,
                          double[] velocityVector,
                          double[] accelerationVector) {
        this.positionVector = positionVector;
        this.velocityVector = velocityVector;
        this.accelerationVector = accelerationVector;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final double[] positionVector = new double[2];
        private final double[] velocityVector = new double[2];
        private final double[] accelerationVector = new double[2];


        private Builder() {}

        public Builder positionVector(double[] positionVector) {
            copyVector(this.positionVector, positionVector);
            return this;
        }

        public Builder velocityVector(double[] velocityVector) {
            copyVector(this.velocityVector, velocityVector);
            return this;
        }

        public Builder accelerationVector(double[] accelerationVector) {
            copyVector(this.accelerationVector, accelerationVector);
            return this;
        }

        public MoleculeState build() {
            return new MoleculeState(this.positionVector,
                                     this.velocityVector,
                                     this.accelerationVector);
        }
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
}
