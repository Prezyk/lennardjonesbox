package com.prezyk.util;

public class VectorUtil {

    private static final String TARGET_VECTOR_NAME = "target";
    private static final String SOURCE_VECTOR_NAME = "source";

    public static void copyVector(double[] targetVector, double[] sourceVector) throws VectorSizeException {
        validateVectorsOrThrow(targetVector, sourceVector);
        System.arraycopy(sourceVector, 0, targetVector, 0, targetVector.length);
    }

    private static void validateVectorsOrThrow(double[] targetVector, double[] sourceVector) throws VectorSizeException {
        validateVectorsSizeMatchOrThrow(targetVector, sourceVector);
        validateVectorSizeNonZeroOrThrow(targetVector, TARGET_VECTOR_NAME);
        validateVectorSizeNonZeroOrThrow(sourceVector, SOURCE_VECTOR_NAME);
    }

    private static void validateVectorsSizeMatchOrThrow(double[] targetVector, double[] sourceVector) throws VectorSizeException {
        if (targetVector.length != sourceVector.length) {
            throw VectorSizeException.vectorSizeMismatch(targetVector.length, sourceVector.length);
        }
    }

    private static void validateVectorSizeNonZeroOrThrow(double[] vector, String vectorName) {
        if (vector.length == 0) {
            throw VectorSizeException.vectorSizeZero(vectorName);
        }
    }
}
