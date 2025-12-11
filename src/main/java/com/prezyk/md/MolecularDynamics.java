package com.prezyk.md;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class MolecularDynamics {

    private double potE;
    private double kinE;
    private double elastE;

    private double rCut2;
    private final SimulationInput simulationInput;
    private final Simulation simulation;
    private final MoleculeState[] currentMoleculesStates;

    public MolecularDynamics(SimulationInput simulationInput) {
        super();
        this.simulationInput = simulationInput;

        this.setrCut2(Math.pow(2 * simulationInput.getMoleculeRadius(), 2));
        this.setPotE(0);
        this.setKinE(0);
        simulation = new Simulation(simulationInput);
        InitialConditionsGenerator initialConditionsGenerator = new InitialConditionsGenerator(simulationInput);
        currentMoleculesStates = initialConditionsGenerator.generateInitialConditions();

        calculateAcc();
        kinECacl();
        simulation.setState(0, 0, new BoxState(getKinE(), getElastE(), getPotE()), Arrays.stream(currentMoleculesStates)
                                                                                                           .map(MoleculeState::clone)
                                                                                                           .toArray(MoleculeState[]::new));
    }

    public CompletableFuture<Simulation> calculateSimulationConcurrent() {
        CompletableFuture<Simulation> futureMolecules = new CompletableFuture<>();
        Runnable calculate = () -> futureMolecules.complete(calculateSimulation());
        new Thread(calculate).start();
        return futureMolecules;
    }

    public Simulation calculateSimulation() {
        Simulation molecules = new Simulation(simulationInput);
        double currentTime = 0;
        for (int i = 0; i < simulationInput.getTimeStepsAmount(); i++) {
            molecules.setState(i, currentTime, new BoxState(getKinE(), getElastE(), getPotE()), Arrays.stream(currentMoleculesStates)
                                                                                                      .map(MoleculeState::clone)
                                                                                                      .toArray(MoleculeState[]::new));
            verletStep();
            currentTime += simulationInput.getTimeStep();
        }
        return molecules;
    }

    private void calculateAcc() {

        for(int i=0; i< currentMoleculesStates.length; i++) {
            currentMoleculesStates[i].getAccelerationVector()[0] = 0;
            currentMoleculesStates[i].getAccelerationVector()[1] = 0;
        }

        double rij2;
        double rij;
        double dx;
        double dy;
        double fr6;
        double fr;
        double fx;
        double fy;
        setPotE(0);
        for(int i = 0; i < currentMoleculesStates.length; i++) {
            for(int j = i + 1; j < currentMoleculesStates.length; j++) {

                dx = currentMoleculesStates[i].getPositionVector()[0] - currentMoleculesStates[j].getPositionVector()[0];
                dy = currentMoleculesStates[i].getPositionVector()[1] - currentMoleculesStates[j].getPositionVector()[1];
                rij2 = dx*dx + dy*dy;
                rij = Math.sqrt(rij2);
                fr6 = Math.pow(simulationInput.getMoleculeRadius() /rij, 6);

                if(rij2 < calcRcut2(simulationInput)) {
                    fr = -(48 * simulationInput.getEpsilon() /rij2) * fr6 * (fr6 * - 0.5) / simulationInput.getMass();
                    fx = fr*dx;
                    fy = fr*dy;

                    currentMoleculesStates[i].getAccelerationVector()[0] += fx;
                    currentMoleculesStates[j].getAccelerationVector()[0] -= fx;
                    currentMoleculesStates[i].getAccelerationVector()[1] += fy;
                    currentMoleculesStates[j].getAccelerationVector()[1] -= fy;

                    setPotE(getPotE() + 2 * simulationInput.getEpsilon() * fr6 * (fr6 - 1.0));
                }
            }
        }

        setElastE(0);
        for(int i=0; i < simulationInput.getMoleculesQuantity(); i++) {
            double d;
            if(currentMoleculesStates[i].getPositionVector()[0] < 0.5) {
                d = 0.5 - currentMoleculesStates[i].getPositionVector()[0];
                currentMoleculesStates[i].getAccelerationVector()[0] += simulationInput.getWallStiffness() * d;
                setElastE(getElastE() + 0.5 * simulationInput.getWallStiffness() * d * d * simulationInput.getMass());
            }

            if(currentMoleculesStates[i].getPositionVector()[0] > (simulationInput.getBoxSize()-0.5)) {
                d = simulationInput.getBoxSize() - 0.5 - currentMoleculesStates[i].getPositionVector()[0];
                currentMoleculesStates[i].getAccelerationVector()[0] += simulationInput.getWallStiffness() * d;
                setElastE(getElastE() + 0.5 * simulationInput.getWallStiffness() * d * d * simulationInput.getMass());
            }

            if(currentMoleculesStates[i].getPositionVector()[1] < 0.5) {
                d = 0.5 - currentMoleculesStates[i].getPositionVector()[1];
                currentMoleculesStates[i].getAccelerationVector()[1] += simulationInput.getWallStiffness() * d;
                setElastE(getElastE() + 0.5 * simulationInput.getWallStiffness() * d * d * simulationInput.getMass());
            }

            if(currentMoleculesStates[i].getPositionVector()[1] > (simulationInput.getBoxSize() - 0.5)) {
                d = simulationInput.getBoxSize() - 0.5 - currentMoleculesStates[i].getPositionVector()[1];
                currentMoleculesStates[i].getAccelerationVector()[1] += simulationInput.getWallStiffness() * d;
                setElastE(getElastE() + 0.5 * simulationInput.getWallStiffness() * d * d * simulationInput.getMass());
            }
        }
    }

    private double calcRcut2(SimulationInput simulationInput) {
        return Math.pow(2 * simulationInput.getMoleculeRadius(), 2);
    }


    private void kinECacl() {
        setKinE(0);
        for(int i = 0; i< currentMoleculesStates.length; i++) {
            setKinE(getKinE() + simulationInput.getMass() *(Math.pow(currentMoleculesStates[i].getVelocityVector()[0], 2) + Math.pow(currentMoleculesStates[i].getVelocityVector()[1], 2))/2);
        }
    }

    private void verletStep() {
        double[][] tempVAtoms = new double[currentMoleculesStates.length][2];
        for(int i = 0; i < currentMoleculesStates.length; i++) {
            tempVAtoms[i][0] = currentMoleculesStates[i].getVelocityVector()[0] + simulationInput.getTimeStep() * currentMoleculesStates[i].getAccelerationVector()[0]/2;
            tempVAtoms[i][1] = currentMoleculesStates[i].getVelocityVector()[1] + simulationInput.getTimeStep() * currentMoleculesStates[i].getAccelerationVector()[1]/2;

            currentMoleculesStates[i].getPositionVector()[0] += simulationInput.getTimeStep() * tempVAtoms[i][0];
            currentMoleculesStates[i].getPositionVector()[1] += simulationInput.getTimeStep() * tempVAtoms[i][1];
        }
        calculateAcc();
        for(int i = 0; i < currentMoleculesStates.length; i++) {
            currentMoleculesStates[i].getVelocityVector()[0] = tempVAtoms[i][0] + simulationInput.getTimeStep() * currentMoleculesStates[i].getAccelerationVector()[0]/2;
            currentMoleculesStates[i].getVelocityVector()[1] = tempVAtoms[i][1] + simulationInput.getTimeStep() * currentMoleculesStates[i].getAccelerationVector()[1]/2;
        }
        kinECacl();
    }

    public double getPotE() {
        return potE;
    }

    public void setPotE(double potE) {
        this.potE = potE;
    }

    public double getKinE() {
        return kinE;
    }

    public void setKinE(double kinE) {
        this.kinE = kinE;
    }

    public double getrCut2() {
        return rCut2;
    }

    public void setrCut2(double rCut2) {
        this.rCut2 = rCut2;
    }

    public double getElastE() {
        return elastE;
    }

    public void setElastE(double elastE) {
        this.elastE = elastE;
    }
}
