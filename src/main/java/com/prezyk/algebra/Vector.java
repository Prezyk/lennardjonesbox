package com.prezyk.algebra;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.function.BiFunction;

import static com.prezyk.algebra.Util.removeValueFromArray;
import static com.prezyk.util.VectorUtil.validateVectorSizeNonZeroOrThrow;
import static com.prezyk.util.VectorUtil.validateVectorSizeSameOrThrow;

public class Vector implements EuclideanRepresentation<Vector, BigDecimal[]> {

    private final BigDecimal[] value;

    public Vector(BigDecimal[] value) {
        this.value = value;
    }

    @Override
    public BigDecimal[] getValue() {
        return value;
    }

    @Override
    public int getLength() {
        return getValue().length;
    }

    @Override
    public Vector add(Vector vector) {
        validateVectorSizeSameOrThrow(getValue(), vector.getValue());
        BigDecimal[] resultValue =  operateByVector(vector.getValue(), BigDecimal::add);
        return new Vector(resultValue);
    }

    @Override
    public Vector subtract(Vector vector) {
        validateVectorSizeSameOrThrow(getValue(), vector.getValue());
        BigDecimal[] resultValue = operateByVector(vector.getValue(), BigDecimal::subtract);
        return new Vector(resultValue);
    }

    @Override
    public Vector divideElementWise(Vector vector) {
        validateVectorSizeSameOrThrow(getValue(), vector.getValue());
        BigDecimal[] resultValue = operateByVector(vector.getValue(),
                                                   (leftVectorElement, rightVectorElement) -> leftVectorElement.divide(rightVectorElement, RoundingMode.HALF_UP));
        return new Vector(resultValue);
    }

    @Override
    public Vector multiplyElementWise(Vector vector) {
        validateVectorSizeSameOrThrow(getValue(), vector.getValue());
        BigDecimal[] resultValue = operateByVector(vector.getValue(), BigDecimal::multiply);
        return new Vector(resultValue);
    }

    @Override
    public Vector multiplyByScalar(BigDecimal scalar) {
        BigDecimal[] resultValue =  operateByScalar(scalar, BigDecimal::multiply);
        return new Vector(resultValue);
    }

    @Override
    public Vector divideByScalar(BigDecimal scalar) {
        BigDecimal[] resultValue =  operateByScalar(scalar,
                                                    (vectorElement, scalarElement) -> vectorElement.divide(scalarElement, RoundingMode.HALF_UP));
        return new Vector(resultValue);
    }

    @Override
    public Vector powerByScalar(BigDecimal scalar) {
        BigDecimal[] resultValue =  operateByScalar(scalar,
                                                    (element, powerValue) -> element.pow(powerValue.intValue()));
        return new Vector(resultValue);
    }

    @Override
    public Vector divideScalar(BigDecimal scalar) {
        BigDecimal[] resultValue =  operateByScalar(scalar,
                                                    (vectorElement, scalarElement) -> scalarElement.divide(vectorElement, RoundingMode.HALF_UP));
        return new Vector(resultValue);
    }

    @Override
    public Vector removeElement(int index) {
        BigDecimal[] resultValue = removeValueFromArray(getValue(), index, BigDecimal.class);
        return new Vector(resultValue);
    }

    @Override
    public Vector copy() {
        BigDecimal[] value = new BigDecimal[getValue().length];
        System.arraycopy(getValue(), 0, value, 0, value.length);
        return new Vector(value);
    }

    public BigDecimal sumVectorElements() {
        validateVectorSizeNonZeroOrThrow(getValue());
        return reduceToScalar(BigDecimal::add);
    }
    public static Vector initializeZeroVector(int length) {
        BigDecimal[] value = new BigDecimal[length];
        Arrays.fill(value, BigDecimal.ZERO);
        return new Vector(value);
    }

    private BigDecimal reduceToScalar(BiFunction<BigDecimal, BigDecimal, BigDecimal> operation) {
        BigDecimal resultScalar = BigDecimal.ZERO;
        for (BigDecimal vectorElement : getValue()) {
            resultScalar = operation.apply(resultScalar, vectorElement);
        }
        return resultScalar;
    }

    private BigDecimal[] operateByVector(BigDecimal[] vectorValue, BiFunction<BigDecimal, BigDecimal, BigDecimal> operation) {
        BigDecimal[] resultVector = new BigDecimal[getLength()];
        for (int i = 0; i < resultVector.length; i++) {
            resultVector[i] = operation.apply(getValue()[i], vectorValue[i]);
        }
        return resultVector;
    }

    private BigDecimal[] operateByScalar(BigDecimal scalar, BiFunction<BigDecimal, BigDecimal, BigDecimal> operation) {
        BigDecimal[] resultVector = new BigDecimal[getLength()];
        for (int i = 0; i < resultVector.length; i++) {
            resultVector[i] = operation.apply(getValue()[i], scalar);
        }
        return resultVector;
    }


}
