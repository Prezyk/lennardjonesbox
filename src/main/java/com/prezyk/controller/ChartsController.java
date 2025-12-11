package com.prezyk.controller;

import com.prezyk.event.EventDispatcher;
import com.prezyk.md.Simulation;
import com.prezyk.event.SimulationCalculationsFinishedEvent;
import javafx.application.Platform;
import javafx.fxml.FXML;
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

    public void initialize() {
        EventDispatcher eventDispatcher = EventDispatcher.getInstance();
        eventDispatcher.registerEventHandler(SimulationCalculationsFinishedEvent.class, this::simulationCalculationsFinishedEventHandler);
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

}
