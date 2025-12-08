public class SimulationConditions {
    private final int moleculesQuantity;
    private final double moleculeRadius;
    private final double epsilon;
    private final double mass;
    private final double time;
    private final double timeStep;
    private final double boxSize;
    private final double wallStiffness;

    private SimulationConditions(int moleculesQuantity, double moleculeRadius, double epsilon, double mass, double time, double timeStep, double boxSize, double wallStiffness) {
        this.moleculesQuantity = moleculesQuantity;
        this.moleculeRadius = moleculeRadius;
        this.epsilon = epsilon;
        this.mass = mass;
        this.time = time;
        this.timeStep = timeStep;
        this.boxSize = boxSize;
        this.wallStiffness = wallStiffness;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        int moleculesQuantity;
        double moleculeRadius;
        double epsilon;
        double mass;
        double time;
        double timeStep;
        double boxSize;
        double wallStiffness;

        private Builder() {}

        public Builder moleculesQuantity(int moleculesQuantity) {
            this.moleculesQuantity = moleculesQuantity;
            return this;
        }

        public Builder moleculeRadius(double moleculeRadius) {
            this.moleculeRadius = moleculeRadius;
            return this;
        }

        public Builder epsilon(double epsilon) {
            this.epsilon = epsilon;
            return this;
        }

        public Builder mass(double mass) {
            this.mass = mass;
            return this;
        }

        public Builder time(double time) {
            this.time = time;
            return this;
        }

        public Builder timeStep(double timeStep) {
            this.timeStep = timeStep;
            return this;
        }

        public Builder boxSize(double boxSize) {
            this.boxSize = boxSize;
            return this;
        }

        public Builder wallStiffness(double wallStiffness) {
            this.wallStiffness = wallStiffness;
            return this;
        }

        public SimulationConditions build() {
            return new SimulationConditions(moleculesQuantity,
                                            moleculeRadius,
                                            epsilon,
                                            mass,
                                            time,
                                            timeStep,
                                            boxSize,
                                            wallStiffness);
        }
    }

    public int getMoleculesQuantity() {
        return moleculesQuantity;
    }

    public double getMoleculeRadius() {
        return moleculeRadius;
    }

    public double getEpsilon() {
        return epsilon;
    }

    public double getMass() {
        return mass;
    }

    public double getTime() {
        return time;
    }

    public double getTimeStep() {
        return timeStep;
    }

    public double getBoxSize() {
        return boxSize;
    }

    public double getWallStiffness() {
        return wallStiffness;
    }

    public int getTimeStepsAmount() {
        return (int) Math.floor(time / timeStep);
    }
}
