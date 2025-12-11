package com.prezyk.util;

public class VectorSizeException extends RuntimeException {
    public VectorSizeException(String message) {
        super(message);
    }

    public static VectorSizeException vectorSizeZero() {
        return new VectorSizeException("Vector size cannot be 0");
    }
}
