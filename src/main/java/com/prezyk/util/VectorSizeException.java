package com.prezyk.util;

public class VectorSizeException extends RuntimeException {
    public VectorSizeException(String message) {
        super(message);
    }

    public static VectorSizeException vectorSizeMismatch(int vector1Size, int vector2Size) {
        return new VectorSizeException(String.format("Vector size mismatch, provided vectors of size %d and %d", vector1Size, vector2Size));
    }

    public static VectorSizeException vectorSizeZero(String vectorName) {
        return new VectorSizeException(String.format("Vector %s size is 0.", vectorName));
    }
}
