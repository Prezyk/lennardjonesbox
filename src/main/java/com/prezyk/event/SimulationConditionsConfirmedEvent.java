package com.prezyk.event;

import com.prezyk.md.SimulationInput;

public class SimulationConditionsConfirmedEvent implements Event {
    private final SimulationInput simulationConditions;

    public SimulationConditionsConfirmedEvent(SimulationInput simulationConditions) {
        this.simulationConditions = simulationConditions;
    }

    public SimulationInput getSimulationConditions() {
        return simulationConditions;
    }
}
