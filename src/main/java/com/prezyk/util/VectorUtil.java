package com.prezyk.util;

import java.util.function.BiFunction;

public class VectorUtil {

    public static double[] copyVector(double[] sourceVector) throws VectorSizeException {
        validateVectorSizeNonZeroOrThrow(sourceVector);
        double[] targetVector = new double[sourceVector.length];
        System.arraycopy(sourceVector, 0, targetVector, 0, targetVector.length);
        return targetVector;
    }

    public static double[][] addMatrices(double[][] leftMatrix, double[][] rightMatrix) {
        validateMatricesSizeSameOrThrow(leftMatrix, rightMatrix);
        return operateOnMatrices(leftMatrix, rightMatrix, Double::sum);
    }

    public static double[][] subtractMatrices(double[][] leftMatrix, double[][] rightMatrix) {
        validateMatricesSizeSameOrThrow(leftMatrix, rightMatrix);
        return operateOnMatrices(leftMatrix, rightMatrix, (leftMatrixElement, rightMatrixElement) -> leftMatrixElement - rightMatrixElement);
    }

    public static double[][] multiplyMatrix(double[][] matrix, double scalar) {
        validateMatrixSizeNonZeroOrThrow(matrix);
        return operateOnMatrix(matrix, scalar, (matrixElement, scalarElement) -> matrixElement * scalarElement);
    }

    private static double[][] operateOnMatrices(double[][] leftMatrix, double[][] rightMatrix, BiFunction<Double, Double, Double> operation) {
        double[][] resultMatrix = new double[leftMatrix.length][];
        for (int i = 0; i < resultMatrix.length; i++) {
            resultMatrix[i] = operateOnVectors(leftMatrix[i], rightMatrix[i], operation);
        }
        return resultMatrix;
    }

    private static double[] operateOnVectors(double[] leftVector, double[] rightVector, BiFunction<Double, Double, Double> operation) {
        double[] resultVector = new double[leftVector.length];
        for (int i = 0; i < resultVector.length; i++) {
            resultVector[i] = operation.apply(leftVector[i], rightVector[i]);
        }
        return resultVector;
    }

    private static double[][] operateOnMatrix(double[][] matrix, double scalar, BiFunction<Double, Double, Double> operation) {
        double[][] resultMatrix = new double[matrix.length][];
        for(int i = 0; i < resultMatrix.length; i++) {
            resultMatrix[i] = operateOnVector(matrix[i], scalar, operation);
        }
        return resultMatrix;
    }

    private static double[] operateOnVector(double[] vector, double scalar, BiFunction<Double, Double, Double> operation) {
        double[] resultVector = new double[vector.length];
        for (int i = 0; i < resultVector.length; i++) {
            resultVector[i] = operation.apply(vector[i], scalar);
        }
        return resultVector;
    }

    private static void validateMatricesSizeSameOrThrow(double[][] leftMatrix, double[][] rightMatrix) {
        if (leftMatrix.length != rightMatrix.length) {
            throw VectorSizeException.matrixSizeMismatch(leftMatrix.length, rightMatrix.length);
        }
    }

    private static void validateMatrixSizeNonZeroOrThrow(double[][] matrix) {
        if (matrix.length == 0) {
            throw VectorSizeException.matrixSizeZero();
        }
        for (double[] doubles : matrix) {
            validateVectorSizeNonZeroOrThrow(doubles);
        }
    }

    private static void validateVectorSizeNonZeroOrThrow(double[] vector) {
        if (vector.length == 0) {
            throw VectorSizeException.vectorSizeZero();
        }
    }
}
