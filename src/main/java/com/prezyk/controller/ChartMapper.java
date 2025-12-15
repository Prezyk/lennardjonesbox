package com.prezyk.controller;

import com.prezyk.md.Simulation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChartMapper {

    private static final String KINETIC_ENERGY = "Kinetic E";
    private static final String POTENTIAL_ENERGY = "Potential E";
    private static final String BOX_ELASTIC_ENERGY = "Elastic E";
    private static final String TOTAL_ENERGY = "Total E";

    private final Simulation simulation;
    private final Map<String, Double[]> namedTimeSeries;

    public ChartMapper(Simulation simulation) {
        this.simulation = simulation;
        this.namedTimeSeries = new HashMap<>();
//        this.namedTimeSeries.put(KINETIC_ENERGY, simulation.getKineticEnergySeries());
        this.namedTimeSeries.put(POTENTIAL_ENERGY, simulation.getPotentialEnergySeries());
//        this.namedTimeSeries.put(BOX_ELASTIC_ENERGY, simulation.getBoxElasticEnergySeries());
//        this.namedTimeSeries.put(TOTAL_ENERGY, simulation.getTotalEnergySeries());
    }

    public double[] getTimePoints() {
        return simulation.getTime();
    }

    public Double[] getTimeSeries(String timeSeriesName) {
        return namedTimeSeries.get(timeSeriesName);
    }

    public List<String> getNames() {
        return namedTimeSeries.keySet()
                              .stream()
                              .sorted()
                              .collect(Collectors.toList());
    }

}
