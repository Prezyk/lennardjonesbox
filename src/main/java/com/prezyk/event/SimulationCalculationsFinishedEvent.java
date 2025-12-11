package com.prezyk.event;

import com.prezyk.md.Simulation;

public class SimulationCalculationsFinishedEvent implements Event {
    private final Simulation molecules;

    public SimulationCalculationsFinishedEvent(Simulation molecules) {
        this.molecules = molecules;
    }

    public Simulation getMolecules() {
        return molecules;
    }
}
