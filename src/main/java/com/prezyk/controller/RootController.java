package com.prezyk.controller;

import com.prezyk.event.EventDispatcher;
import com.prezyk.event.SimulationCalculationsFinishedEvent;
import com.prezyk.event.SimulationConditionsConfirmedEvent;
import com.prezyk.md.MolecularDynamics;
import com.prezyk.md.Simulation;
import com.prezyk.md.SimulationInput;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;

import java.util.concurrent.CompletableFuture;

public class RootController {

    @FXML
    GridPane mainLayout;

    public void initialize() {
        EventDispatcher eventDispatcher = EventDispatcher.getInstance();
        eventDispatcher.registerEventHandler(SimulationConditionsConfirmedEvent.class, this::simulationConditionsConfirmedEventHandler);
    }

    private void simulationConditionsConfirmedEventHandler(SimulationConditionsConfirmedEvent event) {

        SimulationInput simulationConditions = event.getSimulationConditions();

        MolecularDynamics molecularDynamics = new MolecularDynamics(simulationConditions);

        CompletableFuture<Simulation> futureSimulationResult = molecularDynamics.calculateSimulationConcurrent();

        futureSimulationResult.thenAccept(molecules -> EventDispatcher.getInstance()
                                                                      .dispatchEvent(new SimulationCalculationsFinishedEvent(molecules)));
    }

}
