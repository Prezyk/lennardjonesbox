package com.prezyk.util;

import javax.swing.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;

public class VectorUtil {

    public static BigDecimal[][] initializeZeroMatrix(int rows, int columns) {
        BigDecimal[][] matrix = new BigDecimal[rows][];
        for (int i = 0; i < rows; i++) {
            matrix[i] = initializeZeroVector(columns);
        }
        return matrix;
    }

    public static BigDecimal[] initializeZeroVector(int length) {
        BigDecimal[] vector = new BigDecimal[length];
        Arrays.fill(vector, BigDecimal.ZERO);
        return vector;
    }

    public static BigDecimal[] copyVector(BigDecimal[] sourceVector) throws VectorSizeException {
        validateVectorSizeNonZeroOrThrow(sourceVector);
        BigDecimal[] targetVector = new BigDecimal[sourceVector.length];
        System.arraycopy(sourceVector, 0, targetVector, 0, targetVector.length);
        return targetVector;
    }

    public static BigDecimal sumVectorElements(BigDecimal[] vector) {
        validateVectorSizeNonZeroOrThrow(vector);
        return vectorToScalarOperation(vector, BigDecimal::add);
    }

    public static BigDecimal[] divideVector(BigDecimal[] vector, BigDecimal scalar) {
        validateVectorSizeNonZeroOrThrow(vector);
        return operateOnVector(vector, scalar, (vectorElement, scalarElement) -> vectorElement.divide(scalarElement, RoundingMode.HALF_UP));
    }

    public static BigDecimal[] multiplyVector(BigDecimal[] vector, BigDecimal scalar) {
        validateVectorSizeNonZeroOrThrow(vector);
        return operateOnVector(vector, scalar, BigDecimal::multiply);
    }

    public static BigDecimal[] subtractVectors(BigDecimal[] leftVector, BigDecimal[] rightVector) {
        validateVectorSizeSameOrThrow(leftVector, rightVector);
        return operateOnVectors(leftVector, rightVector, BigDecimal::subtract);
    }

    public static BigDecimal[] divideScalarByVector(BigDecimal scalar, BigDecimal[] vector) {
        validateVectorSizeNonZeroOrThrow(vector);
        return operateOnVector(vector, scalar, (vectorElement, scalarElement) -> scalarElement.divide(vectorElement, RoundingMode.HALF_UP));
    }

    public static BigDecimal[] vectorPowerScalar(BigDecimal[] vector, BigDecimal scalar) {
        validateVectorSizeNonZeroOrThrow(vector);
        return operateOnVector(vector, scalar, (element, powerValue) -> element.pow(powerValue.intValue()));
    }

    public static BigDecimal vectorLength(BigDecimal[] vector) {
        return (vector[0].pow(2).add(vector[1].pow(2))).sqrt(MathContext.DECIMAL128);
    }

    public static BigDecimal[] matrixVectorLengths(BigDecimal[][] matrix) {
        validateMatrixLengthNonZeroOrThrow(matrix);
        return matrixToVectorsOperation(matrix, VectorUtil::vectorLength);
    }

    public static BigDecimal[][] addMatrices(BigDecimal[][] leftMatrix, BigDecimal[][] rightMatrix) {
        validateMatricesSizeSameOrThrow(leftMatrix, rightMatrix);
        return operateOnMatrices(leftMatrix, rightMatrix, BigDecimal::add);
    }

    public static BigDecimal[][] subtractMatrices(BigDecimal[][] leftMatrix, BigDecimal[][] rightMatrix) {
        validateMatricesSizeSameOrThrow(leftMatrix, rightMatrix);
        return operateOnMatrices(leftMatrix, rightMatrix, BigDecimal::subtract);
    }

    public static BigDecimal[][] multiplyMatrix(BigDecimal[][] matrix, BigDecimal scalar) {
        validateMatrix(matrix);
        return operateOnMatrix(matrix, scalar, BigDecimal::multiply);
    }

    public static BigDecimal[][] matrixPowerScalar(BigDecimal[][] matrix, BigDecimal scalar) {
        validateMatrix(matrix);
        return operateOnMatrix(matrix, scalar, (element, powerValue) -> element.pow(powerValue.intValue()));
    }

    public static BigDecimal[][] divideScalarByMatrix(BigDecimal scalar, BigDecimal[][] matrix) {
        validateMatrix(matrix);
        return operateOnMatrix(matrix, scalar, (matrixElement, scalarElement) -> scalarElement.divide(matrixElement, RoundingMode.HALF_UP));
    }

    public static BigDecimal[] sumMatrixVectors(BigDecimal[][] matrix) {
        validateMatrix(matrix);
        BigDecimal[] resultVector = initializeZeroVector(matrix[0].length);
        for (BigDecimal[] vector: matrix) {
            for (int i = 0; i < vector.length; i++) {
                resultVector[i] = resultVector[i].add(vector[i]);
            }
        }
        return resultVector;
    }

    public static BigDecimal[][] removeVectorFromMatrix(BigDecimal[][] matrix, int vectorIndex) {
        BigDecimal[][] matrixWithoutVector = new BigDecimal[matrix.length-1][];
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

    public static BigDecimal[][] subtractVectorFromMatrix(BigDecimal[][] matrix, BigDecimal[] vector) {
        validateMatrix(matrix);
        validateVectorOfSizeOrThrow(vector, matrix[0].length);
        return operateOnMatrixAndVector(matrix, vector, BigDecimal::subtract);
    }

    public static BigDecimal[][] fillMatrixWithValue(BigDecimal[][] matrix, BigDecimal value) {
        validateMatrix(matrix);
        return operateOnMatrix(matrix, value, (matrixElement, valueElement) -> valueElement);
    }

    private static BigDecimal[][] operateOnMatrixAndVector(BigDecimal[][] matrix, BigDecimal[] vector, BiFunction<BigDecimal, BigDecimal, BigDecimal> operation) {
        BigDecimal[][] resultMatrix = new BigDecimal[matrix.length][];
        for (int i = 0; i < resultMatrix.length; i++) {
            resultMatrix[i] = operateOnVectors(matrix[i], vector, operation);
        }
        return resultMatrix;
    }

    private static BigDecimal[][] operateOnMatrices(BigDecimal[][] leftMatrix, BigDecimal[][] rightMatrix, BiFunction<BigDecimal, BigDecimal, BigDecimal> operation) {
        BigDecimal[][] resultMatrix = new BigDecimal[leftMatrix.length][];
        for (int i = 0; i < resultMatrix.length; i++) {
            resultMatrix[i] = operateOnVectors(leftMatrix[i], rightMatrix[i], operation);
        }
        return resultMatrix;
    }

    private static BigDecimal[] operateOnVectors(BigDecimal[] leftVector, BigDecimal[] rightVector, BiFunction<BigDecimal, BigDecimal, BigDecimal> operation) {
        BigDecimal[] resultVector = new BigDecimal[leftVector.length];
        for (int i = 0; i < resultVector.length; i++) {
            resultVector[i] = operation.apply(leftVector[i], rightVector[i]);
        }
        return resultVector;
    }

    private static BigDecimal[][] operateOnMatrix(BigDecimal[][] matrix, BigDecimal scalar, BiFunction<BigDecimal, BigDecimal, BigDecimal> operation) {
        BigDecimal[][] resultMatrix = new BigDecimal[matrix.length][];
        for(int i = 0; i < resultMatrix.length; i++) {
            resultMatrix[i] = operateOnVector(matrix[i], scalar, operation);
        }
        return resultMatrix;
    }

    private static BigDecimal[] operateOnVector(BigDecimal[] vector, BigDecimal scalar, BiFunction<BigDecimal, BigDecimal, BigDecimal> operation) {
        BigDecimal[] resultVector = new BigDecimal[vector.length];
        for (int i = 0; i < resultVector.length; i++) {
            resultVector[i] = operation.apply(vector[i], scalar);
        }
        return resultVector;
    }

    private static BigDecimal[] matrixToVectorsOperation(BigDecimal[][] matrix, Function<BigDecimal[], BigDecimal> operation) {
        BigDecimal[] resultVector = new BigDecimal[matrix.length];
        for (int i = 0; i < resultVector.length; i++) {
            resultVector[i] = operation.apply(matrix[i]);
        }
        return resultVector;
    }

    private static BigDecimal vectorToScalarOperation(BigDecimal[] vector, BiFunction<BigDecimal, BigDecimal, BigDecimal> operation) {
        BigDecimal resultScalar = BigDecimal.ZERO;
        for (BigDecimal vectorElement : vector) {
            resultScalar = operation.apply(resultScalar, vectorElement);
        }
        return resultScalar;
    }

    private static void validateMatricesSizeSameOrThrow(BigDecimal[][] leftMatrix, BigDecimal[][] rightMatrix) {
        if (leftMatrix.length != rightMatrix.length) {
            throw VectorSizeException.matrixSizeMismatch(leftMatrix.length, rightMatrix.length);
        }
    }

    private static void validateMatrix(BigDecimal[][] matrix) {
        validateMatrixLengthNonZeroOrThrow(matrix);
        int firstVectorSize = matrix[0].length;
        for (BigDecimal[] vector : matrix) {
            validateVectorSizeNonZeroOrThrow(vector);
            validateVectorOfSizeOrThrow(vector, firstVectorSize);
        }
    }

    private static void validateMatrixLengthNonZeroOrThrow(BigDecimal[][] matrix) {
        if (matrix.length == 0) {
            throw VectorSizeException.matrixSizeZero();
        }
    }

    private static void validateVectorSizeNonZeroOrThrow(BigDecimal[] vector) {
        if (vector.length == 0) {
            throw VectorSizeException.vectorSizeZero();
        }
    }

    private static void validateVectorOfSizeOrThrow(BigDecimal[] vector, int size) {
        if (vector.length != size) {
            throw VectorSizeException.vectorSizeInvalid(vector.length, size);
        }
    }

    private static void validateVectorSizeSameOrThrow(BigDecimal[] leftVector, BigDecimal[] rightVector) {
        if (leftVector.length != rightVector.length) {
            throw VectorSizeException.vectorsSizeMismatch(leftVector.length, rightVector.length);
        }
    }
}
