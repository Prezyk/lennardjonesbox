package com.prezyk.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.prezyk.util.CalculationPrecision.PRECISION;
import static com.prezyk.util.VectorUtil.*;
import static org.junit.jupiter.api.Assertions.*;

class VectorUtilTest {

    @Test
    void testCopyVectorEmpty() {
        assertThrows(VectorSizeException.class, () -> copyVector(new BigDecimal[]{}));
    }

    @Test
    void testCopyVector() {
        BigDecimal[] vector = new BigDecimal[] {
                BigDecimal.valueOf(1).setScale(PRECISION, RoundingMode.HALF_UP),
                BigDecimal.valueOf(3).setScale(PRECISION, RoundingMode.HALF_UP),
                BigDecimal.valueOf(5).setScale(PRECISION, RoundingMode.HALF_UP),
                BigDecimal.valueOf(7).setScale(PRECISION, RoundingMode.HALF_UP)
        };
        BigDecimal[] copiedVector = VectorUtil.copyVector(vector);

        assertNotEquals(vector, copiedVector);
        assertVectorsEquals(vector, copiedVector);
    }

    @Test
    void testSumVectorElementsEmpty() {
        assertThrows(VectorSizeException.class, () -> sumVectorElements(new BigDecimal[]{}));
    }

    @Test
    void testSumVectorElements() {
        BigDecimal[] vector = new BigDecimal[] {
                BigDecimal.valueOf(1).setScale(PRECISION, RoundingMode.HALF_UP),
                BigDecimal.valueOf(2).setScale(PRECISION, RoundingMode.HALF_UP),
                BigDecimal.valueOf(3).setScale(PRECISION, RoundingMode.HALF_UP),
                BigDecimal.valueOf(4).setScale(PRECISION, RoundingMode.HALF_UP),
                BigDecimal.valueOf(5).setScale(PRECISION, RoundingMode.HALF_UP)
        };
        BigDecimal expectedSum = BigDecimal.valueOf(15).setScale(PRECISION, RoundingMode.HALF_UP);

        BigDecimal actualSum = sumVectorElements(vector);
        assertEquals(expectedSum, actualSum);
    }

    @Test
    void testDivideVectorEmpty() {
        assertThrows(VectorSizeException.class, () -> divideVector(new BigDecimal[]{}, BigDecimal.valueOf(2)
                                                                                                 .setScale(PRECISION, RoundingMode.HALF_UP)));
    }

    @Test
    void testDivideVector() {
        BigDecimal[] vector = new BigDecimal[] {
                BigDecimal.valueOf(3).setScale(PRECISION, RoundingMode.HALF_UP),
                BigDecimal.valueOf(5).setScale(PRECISION, RoundingMode.HALF_UP),
                BigDecimal.valueOf(7).setScale(PRECISION, RoundingMode.HALF_UP),
                BigDecimal.valueOf(9).setScale(PRECISION, RoundingMode.HALF_UP)
        };
        BigDecimal[] actualDividedVector = divideVector(vector, BigDecimal.valueOf(2)
                                                                          .setScale(PRECISION, RoundingMode.HALF_UP));

        BigDecimal[] expectedDividedVector = new BigDecimal[] {
                BigDecimal.valueOf(1.5).setScale(PRECISION, RoundingMode.HALF_UP),
                BigDecimal.valueOf(2.5).setScale(PRECISION, RoundingMode.HALF_UP),
                BigDecimal.valueOf(3.5).setScale(PRECISION, RoundingMode.HALF_UP),
                BigDecimal.valueOf(4.5).setScale(PRECISION, RoundingMode.HALF_UP)
        };

        assertVectorsEquals(expectedDividedVector, actualDividedVector);
    }

    @Test
    void testMultiplyVectorEmpty() {
        assertThrows(VectorSizeException.class, () -> multiplyVector(new BigDecimal[]{}, BigDecimal.valueOf(5)));
    }

    @Test
    void testMultiplyVector() {
        BigDecimal[] vector = new BigDecimal[] {
                BigDecimal.valueOf(4).setScale(PRECISION, RoundingMode.HALF_UP),
                BigDecimal.valueOf(3).setScale(PRECISION, RoundingMode.HALF_UP),
                BigDecimal.valueOf(6).setScale(PRECISION, RoundingMode.HALF_UP)
        };
        BigDecimal[] actualMultipliedVector = multiplyVector(vector, BigDecimal.valueOf(4.).setScale(PRECISION, RoundingMode.HALF_UP));

        BigDecimal[] expectedMultipliedVector = new BigDecimal[] {
                BigDecimal.valueOf(16).setScale(PRECISION * 2, RoundingMode.HALF_UP),
                BigDecimal.valueOf(12).setScale(PRECISION * 2, RoundingMode.HALF_UP),
                BigDecimal.valueOf(24).setScale(PRECISION * 2, RoundingMode.HALF_UP)
        };

        assertEquals(expectedMultipliedVector.length, actualMultipliedVector.length);
        assertVectorsEquals(expectedMultipliedVector, actualMultipliedVector);
    }

    @Test
    void testSubtractVectorsSizeMismatch() {
        assertThrows(VectorSizeException.class, () -> subtractVectors(new BigDecimal[2], new BigDecimal[3]));
    }

    @Test
    void testSubtractVectors() {
        BigDecimal[] leftVector = new BigDecimal[] {
                BigDecimal.valueOf(3).setScale(PRECISION, RoundingMode.HALF_UP),
                BigDecimal.valueOf(5).setScale(PRECISION, RoundingMode.HALF_UP),
                BigDecimal.valueOf(-1).setScale(PRECISION, RoundingMode.HALF_UP)
        };
        BigDecimal[] rightVector = new BigDecimal[] {
                BigDecimal.valueOf(5).setScale(PRECISION, RoundingMode.HALF_UP),
                BigDecimal.valueOf(2).setScale(PRECISION, RoundingMode.HALF_UP),
                BigDecimal.valueOf(9).setScale(PRECISION, RoundingMode.HALF_UP)
        };

        BigDecimal[] actualResultVector = subtractVectors(leftVector, rightVector);
        BigDecimal[] expectedResultVector = new BigDecimal[] {
                BigDecimal.valueOf(-2).setScale(PRECISION, RoundingMode.HALF_UP),
                BigDecimal.valueOf(3).setScale(PRECISION, RoundingMode.HALF_UP),
                BigDecimal.valueOf(-10).setScale(PRECISION, RoundingMode.HALF_UP)
        };

        assertVectorsEquals(expectedResultVector, actualResultVector);
    }

    @Test
    void testDivideScalarByVectorEmpty() {
        assertThrows(VectorSizeException.class, () -> divideScalarByVector(BigDecimal.valueOf(2), new BigDecimal[]{}));
    }

    @Test
    void testDivideScalarByVector() {
        BigDecimal[] vector = new BigDecimal[] {
                BigDecimal.valueOf(10).setScale(PRECISION, RoundingMode.HALF_UP),
                BigDecimal.valueOf(5).setScale(PRECISION, RoundingMode.HALF_UP),
                BigDecimal.valueOf(2).setScale(PRECISION, RoundingMode.HALF_UP)
        };
        BigDecimal scalar = BigDecimal.ONE.setScale(PRECISION, RoundingMode.HALF_UP);

        BigDecimal[] actualResultVector = divideScalarByVector(scalar, vector);
        BigDecimal[] expectedResultVector = new BigDecimal[] {
                BigDecimal.valueOf(0.1).setScale(PRECISION, RoundingMode.HALF_UP),
                BigDecimal.valueOf(0.2).setScale(PRECISION, RoundingMode.HALF_UP),
                BigDecimal.valueOf(0.5).setScale(PRECISION, RoundingMode.HALF_UP)
        };
        assertVectorsEquals(expectedResultVector, actualResultVector);
    }

    @Test
    void testRemoveVectorFromMatrix() {
        BigDecimal[][] matrix = new BigDecimal[][] {
                new BigDecimal[] {
                        BigDecimal.valueOf(1).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(2).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(3).setScale(PRECISION, RoundingMode.HALF_UP)
                },
                new BigDecimal[] {
                        BigDecimal.valueOf(4).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(5).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(6).setScale(PRECISION, RoundingMode.HALF_UP)
                },
                new BigDecimal[] {
                        BigDecimal.valueOf(7).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(8).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(9).setScale(PRECISION, RoundingMode.HALF_UP)
                }
        };

        BigDecimal[][] actualResultMatrix = removeVectorFromMatrix(matrix, 1);
        BigDecimal[][] expectedResultMatrix = new BigDecimal[][] {
                new BigDecimal[] {
                        BigDecimal.valueOf(1).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(2).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(3).setScale(PRECISION, RoundingMode.HALF_UP)
                },
                new BigDecimal[] {
                        BigDecimal.valueOf(7).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(8).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(9).setScale(PRECISION, RoundingMode.HALF_UP)
                }
        };

        assertMatrixEquals(expectedResultMatrix, actualResultMatrix);
    }

    @Test
    void testRemoveFirstVectorFromMatrix() {
        BigDecimal[][] matrix = new BigDecimal[][] {
                new BigDecimal[] {
                        BigDecimal.valueOf(1).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(2).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(3).setScale(PRECISION, RoundingMode.HALF_UP)
                },
                new BigDecimal[] {
                        BigDecimal.valueOf(4).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(5).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(6).setScale(PRECISION, RoundingMode.HALF_UP)
                },
                new BigDecimal[] {
                        BigDecimal.valueOf(7).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(8).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(9).setScale(PRECISION, RoundingMode.HALF_UP)
                }
        };

        BigDecimal[][] actualResultMatrix = removeVectorFromMatrix(matrix, 0);
        BigDecimal[][] expectedResultMatrix = new BigDecimal[][] {
                new BigDecimal[] {
                        BigDecimal.valueOf(4).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(5).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(6).setScale(PRECISION, RoundingMode.HALF_UP)
                },
                new BigDecimal[] {
                        BigDecimal.valueOf(7).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(8).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(9).setScale(PRECISION, RoundingMode.HALF_UP)
                }
        };

        assertMatrixEquals(expectedResultMatrix, actualResultMatrix);
    }

    @Test
    void testRemoveLastVectorFromMatrix() {
        BigDecimal[][] matrix = new BigDecimal[][] {
                new BigDecimal[] {
                        BigDecimal.valueOf(1).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(2).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(3).setScale(PRECISION, RoundingMode.HALF_UP)
                },
                new BigDecimal[] {
                        BigDecimal.valueOf(4).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(5).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(6).setScale(PRECISION, RoundingMode.HALF_UP)
                },
                new BigDecimal[] {
                        BigDecimal.valueOf(7).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(8).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(9).setScale(PRECISION, RoundingMode.HALF_UP)
                }
        };

        BigDecimal[][] actualResultMatrix = removeVectorFromMatrix(matrix, 2);
        BigDecimal[][] expectedResultMatrix = new BigDecimal[][] {
                new BigDecimal[] {
                        BigDecimal.valueOf(1).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(2).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(3).setScale(PRECISION, RoundingMode.HALF_UP)
                },
                new BigDecimal[] {
                        BigDecimal.valueOf(4).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(5).setScale(PRECISION, RoundingMode.HALF_UP),
                        BigDecimal.valueOf(6).setScale(PRECISION, RoundingMode.HALF_UP)
                }
        };

        assertMatrixEquals(expectedResultMatrix, actualResultMatrix);
    }

    private void assertMatrixEquals(BigDecimal[][] expectedMatrix, BigDecimal[][] actualMatrix) {
        assertEquals(expectedMatrix.length, actualMatrix.length);
        for (int i = 0; i < expectedMatrix.length; i++) {
            assertVectorsEquals(expectedMatrix[i], actualMatrix[i]);
        }
    }

    private void assertVectorsEquals(BigDecimal[] expectedVector, BigDecimal[] actualVector) {
        assertEquals(expectedVector.length, actualVector.length);
        for (int i = 0; i < expectedVector.length; i++) {
            assertEquals(expectedVector[i], actualVector[i]);
        }
    }
}