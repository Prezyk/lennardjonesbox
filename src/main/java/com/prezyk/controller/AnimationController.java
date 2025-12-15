package com.prezyk.controller;

import com.prezyk.event.EventDispatcher;
import com.prezyk.event.SimulationCalculationsFinishedEvent;
import com.prezyk.event.SimulationConditionsConfirmedEvent;
import com.prezyk.md.Simulation;
import com.prezyk.md.SimulationInput;
import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

public class AnimationController {
    @FXML
    Pane animationPane;

    @FXML
    Button tbtnPlay;

    @FXML
    Button tbtnStop;

    @FXML
    Button tbtnPause;

    List<Path> pathList;
    List<PathTransition> ptr;
    ArrayList<Circle> atoms;
    boolean stop = true;

    Label calculationStatusLabel = new Label();

    public void initialize() {
        EventDispatcher eventDispatcher = EventDispatcher.getInstance();
        eventDispatcher.registerEventHandler(SimulationConditionsConfirmedEvent.class, this::simulationConditionsConfirmedEventHandler);
        eventDispatcher.registerEventHandler(SimulationCalculationsFinishedEvent.class, this::simulationCalculationsFinishedEventHandler);
    }


    @FXML
    public void tbtnPlayAction() {
        if (stop) {
            stop = false;
            for (PathTransition P : ptr) {
                P.playFromStart();
            }
        } else {
            for (PathTransition P : ptr) {
                P.play();
            }
        }
    }

    @FXML
    public void tbtnPauseAction() {
        for (PathTransition P : ptr) {
            P.pause();
        }
    }

    @FXML
    public void tbtnStopActon() {
        for (PathTransition P : ptr) {
            P.stop();
        }
        stop = true;
    }

    private CompletableFuture<Void> prepareAnimationData(Simulation simulation) throws InterruptedException {
        CompletableFuture<Void> future = new CompletableFuture<>();
        ExecutorService executorService = Executors.newFixedThreadPool(simulation.getMoleculesQuantity());
        List<Callable<Void>> callables = new ArrayList<>();
        HashMap<Integer, Void> finishedMolecules = new HashMap<>();

        System.out.println("Starting preparation with " + simulation);
        for (int a = 0; a < simulation.getMoleculesQuantity(); a++) {
            int b = a;
            callables.add(() -> {
                prepareMoleculeAnimation(simulation, b, future, finishedMolecules);
                return null;
            });
        }
        executorService.invokeAll(callables);
        return future;
    }

    private void prepareMoleculeAnimation(Simulation simulation, int moleculeIndex, CompletableFuture<Void> future, HashMap<Integer, Void> hashMap) {
        System.out.println("Starting preparation for " + moleculeIndex);
        for (int i = 0; i < simulation.getTimePoints(); i++) {
            double xCoord = simulation.getPositionVectors()[moleculeIndex][i][0] * animationPane.getWidth() / simulation.getBoxSize();
            double yCoord = animationPane.getHeight() - simulation.getPositionVectors()[moleculeIndex][i][1] * animationPane.getHeight() / simulation.getBoxSize();
            pathList.get(moleculeIndex)
                    .getElements()
                    .add(i == 0 ? new MoveTo(xCoord, yCoord) : new LineTo(xCoord, yCoord));
        }
        System.out.println("Completed loop for " + moleculeIndex);
        completeFutureIfAllDone(simulation, moleculeIndex, future, hashMap);
    }

    private synchronized void completeFutureIfAllDone(Simulation simulation, int moleculeIndex, CompletableFuture<Void> future, HashMap<Integer, Void> hashMap) {
        hashMap.put(moleculeIndex, null);
        System.out.println("Finished calculating transitions for " + moleculeIndex);
        if (hashMap.keySet().size() == simulation.getMoleculesQuantity()) {
            System.out.println("Completing preparation...");
            future.complete(null);
        }
    }

    private CompletableFuture<Void> animateMolecules(Simulation molecules) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        new Thread(() -> {
            System.out.println("Starting animation with " + molecules);
            ptr = new ArrayList<>();
            for (int i = 0; i < molecules.getMoleculesQuantity(); i++) {
                ptr.add(createPathTransition(atoms.get(i), pathList.get(i), molecules.getDuration()));
            }
            future.complete(null);
        }).start();
        return future;
    }

    private PathTransition createPathTransition(Circle node, Path path, double time) {
        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.seconds(time));
        pathTransition.setPath(path);
        pathTransition.setNode(node);
        return pathTransition;
    }

    private void simulationConditionsConfirmedEventHandler(SimulationConditionsConfirmedEvent event) {
        if (!animationPane.getChildren().isEmpty())
            animationPane.getChildren().clear();

        tbtnPause.setDisable(true);
        tbtnPlay.setDisable(true);
        tbtnStop.setDisable(true);

        atoms = new ArrayList<>();
        pathList = new ArrayList<>();

        calculationStatusLabel.setText("Calculation in progress");

        SimulationInput simulationConditions = event.getSimulationConditions();

        double rScaled = simulationConditions.getMoleculeRadius() / simulationConditions.getBoxSize() * animationPane.getWidth();
        for (int i = 0; i < simulationConditions.getMoleculesQuantity(); i++) {
            atoms.add(new Circle(rScaled));
            pathList.add(new Path());
        }

        animationPane.getChildren().add(calculationStatusLabel);
    }

    private void simulationCalculationsFinishedEventHandler(SimulationCalculationsFinishedEvent event) {
        Simulation molecules = event.getMolecules();
        System.out.println("Simulation finished event");
        try {
            prepareAnimationData(molecules).thenCompose((x) -> animateMolecules(molecules)).thenApply((x) -> {
                Platform.runLater(() -> {
                    calculationStatusLabel.setText("Calculation done");
                    tbtnPause.setDisable(false);
                    tbtnPlay.setDisable(false);
                    tbtnStop.setDisable(false);
                    for (Circle C : atoms) {
                        animationPane.getChildren().add(C);
                    }
                });
                return null;
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
