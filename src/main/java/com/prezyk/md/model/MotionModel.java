package com.prezyk.md.model;

import java.math.BigDecimal;

public interface MotionModel {
    BigDecimal[][] calculateNextAcceleration(BigDecimal[][] nextPositionsMatrix);

    BigDecimal calculatePotentialEnergy(BigDecimal[][] positionsMatrix);

    String getPotentialEnergyKey();
}
