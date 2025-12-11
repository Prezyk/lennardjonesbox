package com.prezyk.md;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class Molecule {
    private static final int X_AXIS = 0;
    private static final int Y_AXIS = 1;

    private final String id;
    private final double radius;
    private final double mass;
    private final double[][] positionVectors;
    private final double[][] velocityVectors;
    private final double[][] accelerationVectors;


    public Molecule(double radius, double mass, int timePoints) {
        this.id = UUID.randomUUID()
                      .toString();
        this.radius = radius;
        this.mass = mass;
        positionVectors = new double[timePoints][2];
        velocityVectors = new double[timePoints][2];
        accelerationVectors = new double[timePoints][2];
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Molecule molecule = (Molecule) o;
        return Double.compare(radius, molecule.radius) == 0 && Objects.equals(id, molecule.id) && Objects.deepEquals(positionVectors, molecule.positionVectors) && Objects.deepEquals(velocityVectors, molecule.velocityVectors) && Objects.deepEquals(accelerationVectors, molecule.accelerationVectors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, radius, Arrays.deepHashCode(positionVectors), Arrays.deepHashCode(velocityVectors), Arrays.deepHashCode(accelerationVectors));
    }

    public double[][] getPositionVectors() {
        return positionVectors;
    }

    public double[][] getVelocityVectors() {
        return velocityVectors;
    }

    public double[][] getAccelerationVectors() {
        return accelerationVectors;
    }

    public void setPositionVector(double[] positionVector, int timePoint) {
        copyVector(this.positionVectors[timePoint], positionVector);
    }

    public void setVelocityVector(double[] velocityVector, int timePoint) {
        copyVector(this.velocityVectors[timePoint], velocityVector);
    }

    public void setAccelerationVector(double[] accelerationVector, int timePoint) {
        copyVector(this.accelerationVectors[timePoint], accelerationVector);
    }

    private void copyVector(double[] targetVector, double[] sourceVector) {
        targetVector[X_AXIS] = sourceVector[X_AXIS];
        targetVector[Y_AXIS] = sourceVector[Y_AXIS];
    }
}
