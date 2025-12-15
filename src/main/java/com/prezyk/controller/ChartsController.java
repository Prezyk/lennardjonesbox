package com.prezyk.controller;

import com.prezyk.event.EventDispatcher;
import com.prezyk.event.SimulationCalculationsFinishedEvent;
import com.prezyk.event.SimulationFunctionRunEvent;
import com.prezyk.md.model.LennardJonesModel;
import com.prezyk.md.Simulation;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Pane;

import java.util.List;
import java.util.stream.Collectors;

public class ChartsController {

    @FXML
    Pane chartPane;

    @FXML
    ScatterChart<Number, Number> chart;

    @FXML
    NumberAxis yAxis;

    public void initialize() {
        EventDispatcher eventDispatcher = EventDispatcher.getInstance();
        eventDispatcher.registerEventHandler(SimulationCalculationsFinishedEvent.class, this::simulationCalculationsFinishedEventHandler);
        eventDispatcher.registerEventHandler(SimulationFunctionRunEvent.class, this::simulationFunctionRunEventHandler);
    }

    private void reloadChartData(Simulation molecules) {
        ChartMapper chartMapper = new ChartMapper(molecules);
        resetChartsIfNeeded(chartMapper);
        loadAllCharts(chartMapper);
    }

    private void resetChartsIfNeeded(ChartMapper chartMapper) {

        List<String> currentChartNames = chart.getData()
                                              .stream()
                                              .map(XYChart.Series::getName)
                                              .collect(Collectors.toList());
        chart.setLegendVisible(true);

        if (chartMapper.getNames()
                       .size() != currentChartNames.size() || !chartMapper.getNames()
                                                                          .containsAll(currentChartNames)) {
            chart.getData()
                 .clear();
            initCharts(chartMapper);
        }
    }

    private void initCharts(ChartMapper chartMapper) {
        chartMapper.getNames()
                   .forEach(chartName -> {
                       XYChart.Series<Number, Number> series = new XYChart.Series<>();
                       series.setName(chartName);
                       chart.getData()
                            .add(series);
                   });
    }

    private void loadAllCharts(ChartMapper chartMapper) {
        chart.getData()
             .forEach(series -> loadChart(series, chartMapper.getTimeSeries(series.getName()), chartMapper.getTimePoints()));
    }

    private void loadChart(XYChart.Series<Number, Number> series, Double[] timeSeries, double[] timePoints) {
        series.getData()
              .clear();

        if (timeSeries.length != timePoints.length)
            throw new IllegalArgumentException("Time series should have the same amount of points as time points.");

        for (int i = 0; i < timePoints.length; i++) {
            series.getData()
                  .add(new XYChart.Data<>(timePoints[i], timeSeries[i]));
        }
    }

    private void loadChart(XYChart.Series<Number, Number> series, double[] timeSeries, double[] timePoints) {
        series.getData()
              .clear();

        if (timeSeries.length != timePoints.length)
            throw new IllegalArgumentException("Time series should have the same amount of points as time points.");

        for (int i = 0; i < timePoints.length; i++) {
            series.getData()
                  .add(new XYChart.Data<>(timePoints[i], timeSeries[i]));
        }
    }

    private void simulationCalculationsFinishedEventHandler(SimulationCalculationsFinishedEvent event) {
        Simulation molecules = event.getMolecules();
        Platform.runLater(() -> reloadChartData(molecules));
    }

    private void simulationFunctionRunEventHandler(SimulationFunctionRunEvent event) {
        LennardJonesModel lj = new LennardJonesModel(event.getSimulationInput().getEpsilon(), event.getSimulationInput().getMass(), event.getSimulationInput().getSigma());
        double[] functionValues = lj.calculateEnergyInFunctionOfDistance(event.getDistances());
        chart.getData()
             .clear();
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Function");
        chart.getData()
             .add(series);
        for (double t: event.getDistances()) {
            System.out.println(t);
        }
        loadChart(series, functionValues, event.getDistances());
    }

}
