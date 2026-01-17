package com.prezyk.algebra;

import java.math.BigDecimal;

public interface EuclideanRepresentation<T, R> {
    R getValue();
    int getLength();
    T add(T euclideanRepresentation);
    T subtract(T euclideanRepresentation);
    T divideElementWise(T euclideanRepresentation);
    T multiplyElementWise(T euclideanRepresentation);
    T multiplyByScalar(BigDecimal scalar);
    T divideByScalar(BigDecimal scalar);
    T powerByScalar(BigDecimal scalar);
    T divideScalar(BigDecimal scalar);
    T removeElement(int index);
    T copy();

}
