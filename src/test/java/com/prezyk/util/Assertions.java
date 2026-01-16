package com.prezyk.util;

import org.opentest4j.AssertionFailedError;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Assertions {

    public static void assertDoubleMatrixEquals(double[][] expectedMatrix, double[][] actualMatrix) {
        try {
            assertEquals(expectedMatrix.length, actualMatrix.length, String.format("Expected matrix column count was %d, but actual was %d", expectedMatrix.length, actualMatrix.length));

            for (int i = 0; i < expectedMatrix.length; i++) {
                assertEquals(expectedMatrix[i].length, actualMatrix[i].length, String.format("Expected %d row size was %d, but actual was %d", i, expectedMatrix[i].length, actualMatrix[i].length));
                for (int j = 0; j < expectedMatrix[i].length; j++) {
                    assertEquals(expectedMatrix[i][j], actualMatrix[i][j], String.format("Expected matrix element [%d, %d] value was %f, but actual was %f", i, j, expectedMatrix[i][j], actualMatrix[i][j]));
                }
            }
        } catch (AssertionFailedError e) {
            String message = "Expected matrix: \n" +
                    formatMatrix(expectedMatrix) +
                    "\nActual matrix: \n" +
                    formatMatrix(actualMatrix);
            throw new AssertionFailedError(message, e);
        }
    }

    private static String formatMatrix(double[][] matrix) {
        return Arrays.stream(matrix)
                     .map(row -> Arrays.stream(row).mapToObj(String::valueOf).collect(Collectors.joining(",\t")))
                     .map(row -> "[" + row + "]")
                     .collect(Collectors.joining("\n"));
    }
}
