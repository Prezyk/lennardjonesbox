package com.prezyk.util;

import org.junit.jupiter.api.Test;

import static com.prezyk.util.VectorUtil.*;
import static org.junit.jupiter.api.Assertions.*;

class VectorUtilTest {

    @Test
    void testCopyVectorEmpty() {
        assertThrows(VectorSizeException.class, () -> copyVector(new double[]{}));
    }

    @Test
    void testCopyVector() {
        double[] vector = new double[] {1, 3, 5, 7};
        double[] copiedVector = VectorUtil.copyVector(vector);

        assertNotEquals(vector, copiedVector);
        assertVectorsEquals(vector, copiedVector);
    }

    @Test
    void testSumVectorElementsEmpty() {
        assertThrows(VectorSizeException.class, () -> sumVectorElements(new double[]{}));
    }

    @Test
    void testSumVectorElements() {
        double[] vector = new double[] { 1, 2, 3, 4, 5 };
        double expectedSum = 15;

        double actualSum = sumVectorElements(vector);
        assertEquals(expectedSum, actualSum);
    }

    @Test
    void testDivideVectorEmpty() {
        assertThrows(VectorSizeException.class, () -> divideVector(new double[]{}, 2));
    }

    @Test
    void testDivideVector() {
        double[] vector = new double[] {3, 5, 7, 9};
        double[] actualDividedVector = divideVector(vector, 2.);

        double[] expectedDividedVector = new double[] {1.5, 2.5, 3.5, 4.5};

        assertVectorsEquals(expectedDividedVector, actualDividedVector);
    }

    @Test
    void testMultiplyVectorEmpty() {
        assertThrows(VectorSizeException.class, () -> multiplyVector(new double[]{}, 5));
    }

    @Test
    void testMultiplyVector() {
        double[] vector = new double[] {4, 3, 6};
        double[] actualMultipliedVector = multiplyVector(vector, 4.);

        double[] expectedMultipliedVector = new double[] {16, 12, 24};

        assertEquals(expectedMultipliedVector.length, actualMultipliedVector.length);
        assertVectorsEquals(expectedMultipliedVector, actualMultipliedVector);
    }

    @Test
    void testSubtractVectorsSizeMismatch() {
        assertThrows(VectorSizeException.class, () -> subtractVectors(new double[2], new double[3]));
    }

    @Test
    void testSubtractVectors() {
        double[] leftVector = new double[] {3, 5, -1};
        double[] rightVector = new double[] {5, 2, 9};

        double[] actualResultVector = subtractVectors(leftVector, rightVector);
        double[] expectedResultVector = new double[] {-2, 3, -10};

        assertVectorsEquals(expectedResultVector, actualResultVector);
    }

    @Test
    void testDivideScalarByVectorEmpty() {
        assertThrows(VectorSizeException.class, () -> divideScalarByVector(2, new double[]{}));
    }

    @Test
    void testDivideScalarByVector() {
        double[] vector = new double[] {10, 5, 2};
        double scalar = 1;

        double[] actualResultVector = divideScalarByVector(scalar, vector);
        double[] expectedResultVector = new double[] {0.1, 0.2, 0.5};
        assertVectorsEquals(expectedResultVector, actualResultVector);
    }

    @Test
    void testRemoveVectorFromMatrix() {
        double[][] matrix = new double[][] {
                new double[] {1, 2, 3},
                new double[] {4, 5, 6},
                new double[] {7, 8, 9}
        };

        double[][] actualResultMatrix = removeVectorFromMatrix(matrix, 1);
        double[][] expectedResultMatrix = new double[][] {
                new double[] {1, 2, 3},
                new double[] {7, 8, 9}
        };

        assertMatrixEquals(expectedResultMatrix, actualResultMatrix);
    }

    @Test
    void testRemoveFirstVectorFromMatrix() {
        double[][] matrix = new double[][] {
                new double[] {1, 2, 3},
                new double[] {4, 5, 6},
                new double[] {7, 8, 9}
        };

        double[][] actualResultMatrix = removeVectorFromMatrix(matrix, 0);
        double[][] expectedResultMatrix = new double[][] {
                new double[] {4, 5, 6},
                new double[] {7, 8, 9}
        };

        assertMatrixEquals(expectedResultMatrix, actualResultMatrix);
    }

    @Test
    void testRemoveLastVectorFromMatrix() {
        double[][] matrix = new double[][] {
                new double[] {1, 2, 3},
                new double[] {4, 5, 6},
                new double[] {7, 8, 9}
        };

        double[][] actualResultMatrix = removeVectorFromMatrix(matrix, 2);
        double[][] expectedResultMatrix = new double[][] {
                new double[] {1, 2, 3},
                new double[] {4, 5, 6}
        };

        assertMatrixEquals(expectedResultMatrix, actualResultMatrix);
    }

    private void assertMatrixEquals(double[][] expectedMatrix, double[][] actualMatrix) {
        assertEquals(expectedMatrix.length, actualMatrix.length);
        for (int i = 0; i < expectedMatrix.length; i++) {
            assertVectorsEquals(expectedMatrix[i], actualMatrix[i]);
        }
    }

    private void assertVectorsEquals(double[] expectedVector, double[] actualVector) {
        assertEquals(expectedVector.length, actualVector.length);
        for (int i = 0; i < expectedVector.length; i++) {
            assertEquals(expectedVector[i], actualVector[i]);
        }
    }
}