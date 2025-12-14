package com.prezyk.util;

public class VectorSizeException extends RuntimeException {
    public VectorSizeException(String message) {
        super(message);
    }

    public static VectorSizeException matrixSizeZero() {
        return new VectorSizeException("Matrix size cannot be 0");
    }

    public static VectorSizeException vectorSizeZero() {
        return new VectorSizeException("Vector size cannot be 0");
    }

    public static VectorSizeException vectorsSizeMismatch(int leftVectorSize, int rightVectorSize) {
        return new VectorSizeException(String.format("Vectors size mismatch, left vector of length %d and right vector of size %d",
                                                     leftVectorSize,
                                                     rightVectorSize));
    }

    public static VectorSizeException matrixSizeMismatch(int leftMatrixLength, int rightMatrixLength) {
        return new VectorSizeException(String.format("Matrix length mismatch, left matrix of length %d and right matrix of size %d",
                                                     leftMatrixLength,
                                                     rightMatrixLength));
    }

    public static VectorSizeException vectorSizeInvalid(int actualVectorSize, int requiredVectorSize) {
        return new VectorSizeException(String.format("Vector should be of size %d, but was %d",
                                                     requiredVectorSize,
                                                     actualVectorSize));
    }
}
