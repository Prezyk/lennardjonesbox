package com.prezyk.md;

import com.prezyk.md.model.ElasticBoxModel;
import com.prezyk.md.model.LennardJonesModel;
import com.prezyk.md.state.BoxState;

import java.math.BigDecimal;
import java.util.Arrays;

public class Box {
    private final BigDecimal size;
    private final BigDecimal wallStiffness;
    private final BoxState[] boxStates;

    public Box(BigDecimal size, BigDecimal wallStiffness, int timePoints) {
        this.size = size;
        this.wallStiffness = wallStiffness;
        this.boxStates = new BoxState[timePoints];
    }

    public void setState(int timePoint, BoxState boxState) {
        this.boxStates[timePoint] = boxState;
    }

    public BigDecimal getSize() {
        return size;
    }

    public BigDecimal[] getKineticEnergySeries() {
        return Arrays.stream(boxStates)
                     .map(BoxState::getKineticEnergy)
                     .toArray(BigDecimal[]::new);
    }

    public BigDecimal[] getPotentialEnergySeries() {
        return Arrays.stream(boxStates)
                     .map(boxState -> boxState.getPotentialEnergy(LennardJonesModel.POTENTIAL_ENERGY_KEY))
                     .toArray(BigDecimal[]::new);
    }

    public BigDecimal[] getElasticEnergySeries() {
        return Arrays.stream(boxStates)
                     .map(boxState -> boxState.getPotentialEnergy(ElasticBoxModel.POTENTIAL_ENERGY_KEY))
                     .toArray(BigDecimal[]::new);
    }

    public BigDecimal[] getTotalEnergySeries() {
        return Arrays.stream(boxStates)
                     .map(BoxState::getTotalEnergy)
                     .toArray(BigDecimal[]::new);
    }
}
