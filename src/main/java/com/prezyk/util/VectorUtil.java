package com.prezyk.util;

import java.util.function.BiFunction;
import java.util.function.Function;

public class VectorUtil {

    public static double[] copyVector(double[] sourceVector) throws VectorSizeException {
        validateVectorSizeNonZeroOrThrow(sourceVector);
        double[] targetVector = new double[sourceVector.length];
        System.arraycopy(sourceVector, 0, targetVector, 0, targetVector.length);
        return targetVector;
    }

    public static double sumVectorElements(double[] vector) {
        validateVectorSizeNonZeroOrThrow(vector);
        return vectorToScalarOperation(vector, Double::sum);
    }

    public static double[] divideVector(double[] vector, double scalar) {
        validateVectorSizeNonZeroOrThrow(vector);
        return operateOnVector(vector, scalar, (vectorElement, scalarElement) -> vectorElement / scalarElement);
    }

    public static double[] multiplyVector(double[] vector, double scalar) {
        validateVectorSizeNonZeroOrThrow(vector);
        return operateOnVector(vector, scalar, (vectorElement, scalarElement) -> vectorElement * scalarElement);
    }

    public static double[] subtractVectors(double[] leftVector, double[] rightVector) {
        validateVectorSizeSameOrThrow(leftVector, rightVector);
        return operateOnVectors(leftVector, rightVector, (leftVectorElement, rightVectorElement) -> leftVectorElement - rightVectorElement);
    }

    public static double[] divideScalarByVector(double scalar, double[] vector) {
        validateVectorSizeNonZeroOrThrow(vector);
        return operateOnVector(vector, scalar, (vectorElement, scalarElement) -> scalarElement / vectorElement);
    }

    public static double[] vectorPowerScalar(double[] vector, double scalar) {
        validateVectorSizeNonZeroOrThrow(vector);
        return operateOnVector(vector, scalar, Math::pow);
    }

    public static double vectorLength(double[] vector) {
        return Math.sqrt(Math.pow(vector[0], 2) + Math.pow(vector[1], 2));
    }

    public static double[] matrixVectorLengths(double[][] matrix) {
        validateMatrixLengthNonZeroOrThrow(matrix);
        return matrixToVectorsOperation(matrix, VectorUtil::vectorLength);
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
        validateMatrix(matrix);
        return operateOnMatrix(matrix, scalar, (matrixElement, scalarElement) -> matrixElement * scalarElement);
    }

    public static double[][] matrixPowerScalar(double[][] matrix, double scalar) {
        validateMatrix(matrix);
        return operateOnMatrix(matrix, scalar, Math::pow);
    }

    public static double[][] divideScalarByMatrix(double scalar, double[][] matrix) {
        validateMatrix(matrix);
        return operateOnMatrix(matrix, scalar, (matrixElement, scalarElement) -> scalarElement / matrixElement);
    }

    public static double[] sumMatrixVectors(double[][] matrix) {
        validateMatrix(matrix);
        double[] resultVector = new double[matrix[0].length];
        for (double[] vector: matrix) {
            for (int i = 0; i < vector.length; i++) {
                resultVector[i] += vector[i];
            }
        }
        return resultVector;
    }

    public static double[][] removeVectorFromMatrix(double[][] matrix, int vectorIndex) {
        double[][] matrixWithoutVector = new double[matrix.length-1][];
        if (vectorIndex == 0) {
            System.arraycopy(matrix, vectorIndex + 1, matrixWithoutVector, 0, matrix.length - 1);
        } else if (vectorIndex == matrix.length - 1) {
            System.arraycopy(matrix, 0, matrixWithoutVector, 0, matrix.length - 1);
        } else {
            System.arraycopy(matrix, 0, matrixWithoutVector, 0, vectorIndex);
            System.arraycopy(matrix, vectorIndex + 1, matrixWithoutVector, vectorIndex, matrix.length - vectorIndex - 1);
        }
        return matrixWithoutVector;
    }

    public static double[][] subtractVectorFromMatrix(double[][] matrix, double[] vector) {
        validateMatrix(matrix);
        validateVectorOfSizeOrThrow(vector, matrix[0].length);
        return operateOnMatrixAndVector(matrix, vector, (matrixElement, vectorElement) -> matrixElement - vectorElement);
    }

    public static double[][] fillMatrixWithValue(double[][] matrix, double value) {
        validateMatrix(matrix);
        return operateOnMatrix(matrix, value, (matrixElement, valueElement) -> valueElement);
    }

    private static double[][] operateOnMatrixAndVector(double[][] matrix, double[] vector, BiFunction<Double, Double, Double> operation) {
        double[][] resultMatrix = new double[matrix.length][];
        for (int i = 0; i < resultMatrix.length; i++) {
            resultMatrix[i] = operateOnVectors(matrix[i], vector, operation);
        }
        return resultMatrix;
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

    private static double[] matrixToVectorsOperation(double[][] matrix, Function<double[], Double> operation) {
        double[] resultVector = new double[matrix.length];
        for (int i = 0; i < resultVector.length; i++) {
            resultVector[i] = operation.apply(matrix[i]);
        }
        return resultVector;
    }

    private static double vectorToScalarOperation(double[] vector, BiFunction<Double, Double, Double> operation) {
        double resultScalar = 0.;
        for (double vectorElement : vector) {
            resultScalar = operation.apply(resultScalar, vectorElement);
        }
        return resultScalar;
    }

    private static void validateMatricesSizeSameOrThrow(double[][] leftMatrix, double[][] rightMatrix) {
        if (leftMatrix.length != rightMatrix.length) {
            throw VectorSizeException.matrixSizeMismatch(leftMatrix.length, rightMatrix.length);
        }
    }

    private static void validateMatrix(double[][] matrix) {
        validateMatrixLengthNonZeroOrThrow(matrix);
        int firstVectorSize = matrix[0].length;
        for (double[] vector : matrix) {
            validateVectorSizeNonZeroOrThrow(vector);
            validateVectorOfSizeOrThrow(vector, firstVectorSize);
        }
    }

    private static void validateMatrixLengthNonZeroOrThrow(double[][] matrix) {
        if (matrix.length == 0) {
            throw VectorSizeException.matrixSizeZero();
        }
    }

    private static void validateVectorSizeNonZeroOrThrow(double[] vector) {
        if (vector.length == 0) {
            throw VectorSizeException.vectorSizeZero();
        }
    }

    private static void validateVectorOfSizeOrThrow(double[] vector, int size) {
        if (vector.length != size) {
            throw VectorSizeException.vectorSizeInvalid(vector.length, size);
        }
    }

    private static void validateVectorSizeSameOrThrow(double[] leftVector, double[] rightVector) {
        if (leftVector.length != rightVector.length) {
            throw VectorSizeException.vectorsSizeMismatch(leftVector.length, rightVector.length);
        }
    }
}
