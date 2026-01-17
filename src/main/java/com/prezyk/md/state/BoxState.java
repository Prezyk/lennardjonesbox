package com.prezyk.md.state;

import java.math.BigDecimal;
import java.util.HashMap;

public class BoxState {
    private final BigDecimal kineticEnergy;
    private final HashMap<String, BigDecimal> potentialEnergies;

    public BoxState(BigDecimal kineticEnergy) {
        this.kineticEnergy = kineticEnergy;
        this.potentialEnergies = new HashMap<>();
    }

    private BoxState(BigDecimal kineticEnergy, HashMap<String, BigDecimal> potentialEnergies) {
        this.kineticEnergy = kineticEnergy;
        this.potentialEnergies = potentialEnergies;
    }

    public BigDecimal getKineticEnergy() {
        return kineticEnergy;
    }


    public BigDecimal getTotalEnergy() {
        return kineticEnergy.add(getTotalPotentialEnergy());
    }

    private BigDecimal getTotalPotentialEnergy() {
        return potentialEnergies.values().stream().reduce(BigDecimal::add).get();
    }

    public BoxState clone() {
        return new BoxState(kineticEnergy, (HashMap<String, BigDecimal>) potentialEnergies.clone());
    }

    public BigDecimal getPotentialEnergy(String key) {
        return potentialEnergies.get(key);
    }

    public void putPotentialEnergy(String key, BigDecimal energy) {
        this.potentialEnergies.put(key, energy);
    }

}
