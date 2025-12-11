package com.prezyk.util;

public class VectorUtil {

    public static double[] copyVector(double[] sourceVector) throws VectorSizeException {
        validateVectorSizeNonZeroOrThrow(sourceVector);
        double[] targetVector = new double[sourceVector.length];
        System.arraycopy(sourceVector, 0, targetVector, 0, targetVector.length);
        return targetVector;
    }

    private static void validateVectorSizeNonZeroOrThrow(double[] vector) {
        if (vector.length == 0) {
            throw VectorSizeException.vectorSizeZero();
        }
    }
}
