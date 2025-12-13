package com.prezyk.md;

import static com.prezyk.util.VectorUtil.*;

public class VerletIntegration {

    public double[][] calculateNextPosition(double[][] previousPosition, double[][] currentPosition, double[][] acceleration, double timeStep) {
        return addMatrices(
                subtractMatrices(
                        multiplyMatrix(currentPosition, 2),
                        previousPosition
                ),
                multiplyMatrix(
                        acceleration,
                        Math.pow(timeStep, 2)/2.
                )
        );
    }
}
