package com.prezyk.event;

import com.prezyk.md.SimulationInput;

public class SimulationFunctionRunEvent implements Event {
    private double[] distances;
    private SimulationInput simulationInput;

    public SimulationFunctionRunEvent(double[] distances, SimulationInput simulationInput) {
        this.distances = distances;
        this.simulationInput = simulationInput;
    }

    public double[] getDistances() {
        return distances;
    }

    public SimulationInput getSimulationInput() {
        return simulationInput;
    }
}
