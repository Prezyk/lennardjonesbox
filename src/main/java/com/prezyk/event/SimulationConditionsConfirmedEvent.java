package com.prezyk.event;

import com.prezyk.md.SimulationConditions;

public class SimulationConditionsConfirmedEvent implements Event {
    private final SimulationConditions simulationConditions;

    public SimulationConditionsConfirmedEvent(SimulationConditions simulationConditions) {
        this.simulationConditions = simulationConditions;
    }

    public SimulationConditions getSimulationConditions() {
        return simulationConditions;
    }
}
