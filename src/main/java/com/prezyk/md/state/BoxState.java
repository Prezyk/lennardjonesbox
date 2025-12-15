package com.prezyk.md.state;

import java.util.HashMap;

public class BoxState {
    private final double kineticEnergy;
    private final HashMap<String, Double> potentialEnergies;

    public BoxState(double kineticEnergy) {
        this.kineticEnergy = kineticEnergy;
        this.potentialEnergies = new HashMap<>();
    }

    private BoxState(double kineticEnergy, HashMap<String, Double> potentialEnergies) {
        this.kineticEnergy = kineticEnergy;
        this.potentialEnergies = potentialEnergies;
    }

    public double getKineticEnergy() {
        return kineticEnergy;
    }


    public double getTotalEnergy() {
        return kineticEnergy + getTotalPotentialEnergy();
    }

    private double getTotalPotentialEnergy() {
        return potentialEnergies.values().stream().mapToDouble(Double::valueOf).sum();
    }

    public BoxState clone() {
        return new BoxState(kineticEnergy, (HashMap<String, Double>) potentialEnergies.clone());
    }

    public double getPotentialEnergy(String key) {
        return potentialEnergies.get(key);
    }

    public void putPotentialEnergy(String key, Double energy) {
        this.potentialEnergies.put(key, energy);
    }

}
