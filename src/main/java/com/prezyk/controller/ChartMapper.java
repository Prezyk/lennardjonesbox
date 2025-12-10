package com.prezyk.controller;

import com.prezyk.md.Molecules;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChartMapper {

    private static final String KINETIC_ENERGY = "Kinetic E";
    private static final String POTENTIAL_ENERGY = "Potential E";
    private static final String BOX_ELASTIC_ENERGY = "Elastic E";
    private static final String TOTAL_ENERGY = "Total E";

    private final Molecules molecules;
    private final Map<String, double[]> namedTimeSeries;

    public ChartMapper(Molecules molecules) {
        this.molecules = molecules;
        this.namedTimeSeries = new HashMap<>();
        this.namedTimeSeries.put(KINETIC_ENERGY, molecules.getKineticEnergy());
        this.namedTimeSeries.put(POTENTIAL_ENERGY, molecules.getPotentialEnergy());
        this.namedTimeSeries.put(BOX_ELASTIC_ENERGY, molecules.getBoxElasticEnergy());
        this.namedTimeSeries.put(TOTAL_ENERGY, molecules.getTotalEnergy());
    }

    public double[] getTimePoints() {
        return molecules.getTime();
    }

    public double[] getTimeSeries(String timeSeriesName) {
        return namedTimeSeries.get(timeSeriesName);
    }

    public List<String> getNames() {
        return namedTimeSeries.keySet()
                              .stream()
                              .sorted()
                              .collect(Collectors.toList());
    }

}
