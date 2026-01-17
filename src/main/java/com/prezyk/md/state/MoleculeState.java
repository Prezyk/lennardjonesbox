package com.prezyk.md.state;

import java.math.BigDecimal;

import static com.prezyk.util.VectorUtil.copyVector;

public class MoleculeState {
    private final BigDecimal[] positionVector;
    private final BigDecimal[] velocityVector;
    private final BigDecimal[] accelerationVector;

    public MoleculeState(BigDecimal[] positionVector,
                         BigDecimal[] velocityVector,
                         BigDecimal[] accelerationVector) {
        this.positionVector = copyVector(positionVector);
        this.velocityVector = copyVector(velocityVector);
        this.accelerationVector = copyVector(accelerationVector);
    }

    public BigDecimal[] getPositionVector() {
        return positionVector;
    }

    public BigDecimal[] getVelocityVector() {
        return velocityVector;
    }

    public BigDecimal[] getAccelerationVector() {
        return accelerationVector;
    }

    public MoleculeState clone() {
        return new MoleculeState(positionVector, velocityVector, accelerationVector);
    }
}
