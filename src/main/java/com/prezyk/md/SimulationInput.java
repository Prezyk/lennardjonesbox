package com.prezyk.md;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class SimulationInput {
    private final int moleculesQuantity;
    private final BigDecimal moleculeRadius;
    private final BigDecimal epsilon;
    private final BigDecimal mass;
    private final BigDecimal time;
    private final BigDecimal timeStep;
    private final BigDecimal boxSize;
    private final BigDecimal wallStiffness;
    private final BigDecimal sigma;

    private SimulationInput(int moleculesQuantity, BigDecimal moleculeRadius, BigDecimal epsilon, BigDecimal mass, BigDecimal time, BigDecimal timeStep, BigDecimal boxSize, BigDecimal wallStiffness, BigDecimal sigma) {
        this.moleculesQuantity = moleculesQuantity;
        this.moleculeRadius = moleculeRadius;
        this.epsilon = epsilon;
        this.mass = mass;
        this.time = time;
        this.timeStep = timeStep;
        this.boxSize = boxSize;
        this.wallStiffness = wallStiffness;
        this.sigma = sigma;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        int moleculesQuantity;
        BigDecimal moleculeRadius;
        BigDecimal epsilon;
        BigDecimal mass;
        BigDecimal time;
        BigDecimal timeStep;
        BigDecimal boxSize;
        BigDecimal wallStiffness;
        BigDecimal sigma;

        private Builder() {}

        public Builder moleculesQuantity(int moleculesQuantity) {
            this.moleculesQuantity = moleculesQuantity;
            return this;
        }

        public Builder moleculeRadius(BigDecimal moleculeRadius) {
            this.moleculeRadius = moleculeRadius;
            return this;
        }

        public Builder epsilon(BigDecimal epsilon) {
            this.epsilon = epsilon;
            return this;
        }

        public Builder mass(BigDecimal mass) {
            this.mass = mass;
            return this;
        }

        public Builder time(BigDecimal time) {
            this.time = time;
            return this;
        }

        public Builder timeStep(BigDecimal timeStep) {
            this.timeStep = timeStep;
            return this;
        }

        public Builder boxSize(BigDecimal boxSize) {
            this.boxSize = boxSize;
            return this;
        }

        public Builder wallStiffness(BigDecimal wallStiffness) {
            this.wallStiffness = wallStiffness;
            return this;
        }

        public Builder sigma(BigDecimal sigma) {
            this.sigma = sigma;
            return this;
        }

        public SimulationInput build() {
            return new SimulationInput(moleculesQuantity,
                                       moleculeRadius,
                                       epsilon,
                                       mass,
                                       time,
                                       timeStep,
                                       boxSize,
                                       wallStiffness,
                                       sigma);
        }
    }

    public int getMoleculesQuantity() {
        return moleculesQuantity;
    }

    public BigDecimal getMoleculeRadius() {
        return moleculeRadius;
    }

    public BigDecimal getEpsilon() {
        return epsilon;
    }

    public BigDecimal getMass() {
        return mass;
    }

    public BigDecimal getTime() {
        return time;
    }

    public BigDecimal getTimeStep() {
        return timeStep;
    }

    public BigDecimal getBoxSize() {
        return boxSize;
    }

    public BigDecimal getWallStiffness() {
        return wallStiffness;
    }

    public int getTimeStepsAmount() {
        return time.divide(timeStep, RoundingMode.HALF_UP).intValue();
    }

    public BigDecimal getSigma() {
        return sigma;
    }
}
