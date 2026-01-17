package com.prezyk.event;

import com.prezyk.md.SimulationInput;

import java.math.BigDecimal;

public class SimulationFunctionRunEvent implements Event {
    private final BigDecimal[] distances;
    private final SimulationInput simulationInput;

    public SimulationFunctionRunEvent(BigDecimal[] distances, SimulationInput simulationInput) {
        this.distances = distances;
        this.simulationInput = simulationInput;
    }

    public BigDecimal[] getDistances() {
        return distances;
    }

    public SimulationInput getSimulationInput() {
        return simulationInput;
    }
}
