package com.prezyk.md;

import java.util.Arrays;

public class Box {
    private final double size;
    private final double wallStiffness;
    private final BoxState[] boxStates;

    public Box(double size, double wallStiffness, int timePoints) {
        this.size = size;
        this.wallStiffness = wallStiffness;
        this.boxStates = new BoxState[timePoints];
    }

    public void setState(int timePoint, BoxState boxState) {
        this.boxStates[timePoint] = boxState;
    }

    public double getSize() {
        return size;
    }

    public Double[] getKineticEnergySeries() {
        return Arrays.stream(boxStates)
                     .map(BoxState::getKineticEnergy)
                     .toArray(Double[]::new);
    }

    public Double[] getPotentialEnergySeries() {
        return Arrays.stream(boxStates)
                     .map(boxState -> boxState.getPotentialEnergy(LennardJonesModel.POTENTIAL_ENERGY_KEY))
                     .toArray(Double[]::new);
    }

    public Double[] getElasticEnergySeries() {
        return Arrays.stream(boxStates)
                     .map(boxState -> boxState.getPotentialEnergy(ElasticBoxModel.POTENTIAL_ENERGY_KEY))
                     .toArray(Double[]::new);
    }

    public Double[] getTotalEnergySeries() {
        return Arrays.stream(boxStates)
                     .map(BoxState::getTotalEnergy)
                     .toArray(Double[]::new);
    }
}
