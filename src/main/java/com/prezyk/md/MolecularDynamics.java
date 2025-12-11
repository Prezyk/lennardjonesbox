package com.prezyk.md;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class MolecularDynamics {

    private int nAtoms;
    private double[][] rAtoms;
    private double[][] vAtoms;
    private double[][] aAtoms;

    private double potE;
    private double kinE;
    private double elastE;

    private double rCut2;
    private final SimulationInput simulationConditions;

    public MolecularDynamics(SimulationInput simulationConditions) {
        super();
        Random gen = new Random();
        this.simulationConditions = simulationConditions;
        this.setnAtoms(simulationConditions.getMoleculesQuantity());
        this.setrAtoms(new double[this.getnAtoms()][2]);
        this.setvAtoms(new double[this.getnAtoms()][2]);
        this.setaAtoms(new double[this.getnAtoms()][2]);
        this.setrCut2(Math.pow(2 * simulationConditions.getMoleculeRadius(), 2));
        this.setPotE(0);
        this.setKinE(0);
        for (int i = 0; i< this.getnAtoms(); i++) {

            this.getrAtoms()[i] = new double[]{Math.abs(gen.nextDouble()* simulationConditions.getBoxSize() - simulationConditions.getMoleculeRadius()), Math.abs(gen.nextDouble()* simulationConditions.getBoxSize() - simulationConditions.getMoleculeRadius())};

            for(int j=0; j<(i+1); j++) {
                if ((i!=j)&& ((Math.pow(getrAtoms()[i][0]- getrAtoms()[j][0],2) + Math.pow(getrAtoms()[i][1]- getrAtoms()[j][1],2)) <= (simulationConditions.getMoleculeRadius() * simulationConditions.getMoleculeRadius()))) {
                    this.getrAtoms()[i] = new double[]{Math.abs(gen.nextDouble()* simulationConditions.getBoxSize() - simulationConditions.getMoleculeRadius()), Math.abs(gen.nextDouble()* simulationConditions.getBoxSize() - simulationConditions.getMoleculeRadius())};
                    j = 0;
                }
            }

            getvAtoms()[i] = new double[]{gen.nextGaussian()*10, gen.nextGaussian()*10};
            calculateAcc();
            kinECacl();
        }

        this.getrAtoms()[0] = new double[]{50, 350};
        this.getrAtoms()[1] = new double[]{300, 350};
        this.getvAtoms()[0] = new double[]{100, 0};
        this.getvAtoms()[1] = new double[]{-100, 0};
    }

    public CompletableFuture<Simulation> calculateSimulationConcurrent() {
        CompletableFuture<Simulation> futureMolecules = new CompletableFuture<>();
        Runnable calculate = () -> futureMolecules.complete(calculateSimulation());
        new Thread(calculate).start();
        return futureMolecules;
    }

    public Simulation calculateSimulation() {
        Simulation molecules = new Simulation(simulationConditions.getMoleculesQuantity(),
                                              simulationConditions.getTimeStepsAmount(),
                                              simulationConditions.getMoleculeRadius(),
                                              simulationConditions.getEpsilon(),
                                              simulationConditions.getBoxSize(),
                                              simulationConditions.getMass(),
                                              simulationConditions.getWallStiffness());
        double currentTime = 0;
        for (int i = 0; i < simulationConditions.getTimeStepsAmount(); i++) {
            molecules.addRow(i, currentTime, rAtoms, vAtoms, aAtoms, kinE, potE, elastE);
            verletStep(simulationConditions.getTimeStep());
            currentTime += simulationConditions.getTimeStep();
        }
        return molecules;
    }

    private void calculateAcc() {

        for(int i=0; i<getnAtoms(); i++) {
            setaAtom(i, new double[]{0, 0});
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
        for(int i = 0; i< getnAtoms(); i++) {
            for(int j = i+1; j< getnAtoms(); j++) {

                dx = getrAtoms()[i][0]- getrAtoms()[j][0];
                dy = getrAtoms()[i][1]- getrAtoms()[j][1];
                rij2 = dx*dx + dy*dy;
                rij = Math.sqrt(rij2);
                fr6 = Math.pow(simulationConditions.getMoleculeRadius() /rij, 6);

                if(rij2< getrCut2()) {
                    fr = -(48 * simulationConditions.getEpsilon() /rij2) * fr6 * (fr6 * - 0.5) / simulationConditions.getMass();
                    fx = fr*dx;
                    fy = fr*dy;

                    getaAtoms()[i][0] += fx;
                    getaAtoms()[j][0] -= fx;
                    getaAtoms()[i][1] += fy;
                    getaAtoms()[j][1] -= fy;

                    setPotE(getPotE() + 2 * simulationConditions.getEpsilon() * fr6 * (fr6 - 1.0));
                }
            }
        }

        setElastE(0);
        for(int i=0; i<nAtoms; i++) {
            double d;
            if(rAtoms[i][0] < 0.5) {
                d = 0.5 - rAtoms[i][0];
                aAtoms[i][0] += simulationConditions.getWallStiffness() * d;
                setElastE(getElastE() + 0.5 * simulationConditions.getWallStiffness() * d * d * simulationConditions.getMass());
            }

            if(rAtoms[i][0] > (simulationConditions.getBoxSize()-0.5)) {
                d = simulationConditions.getBoxSize() - 0.5 - rAtoms[i][0];
                aAtoms[i][0] += simulationConditions.getWallStiffness() * d;
                setElastE(getElastE() + 0.5 * simulationConditions.getWallStiffness() * d * d * simulationConditions.getMass());
            }

            if(rAtoms[i][1] < 0.5) {
                d = 0.5 - rAtoms[i][1];
                aAtoms[i][1] += simulationConditions.getWallStiffness() * d;
                setElastE(getElastE() + 0.5 * simulationConditions.getWallStiffness() * d * d * simulationConditions.getMass());
            }

            if(rAtoms[i][1] > (simulationConditions.getBoxSize() - 0.5)) {
                d = simulationConditions.getBoxSize() - 0.5 - rAtoms[i][1];
                aAtoms[i][1] += simulationConditions.getWallStiffness() * d;
                setElastE(getElastE() + 0.5 * simulationConditions.getWallStiffness() * d * d * simulationConditions.getMass());
            }
        }
    }


    private void kinECacl() {
        setKinE(0);
        for(int i = 0; i< getnAtoms(); i++) {
            setKinE(getKinE() + simulationConditions.getMass() *(Math.pow(getvAtoms()[i][0],2) + Math.pow(getvAtoms()[i][1],2))/2);
        }
    }

    private void verletStep(double dt) {
        double[][] tempVAtoms = new double[getnAtoms()][2];
        for(int i = 0; i< getnAtoms(); i++) {
            tempVAtoms[i][0] = getvAtoms()[i][0] + dt* getaAtoms()[i][0]/2;
            tempVAtoms[i][1] = getvAtoms()[i][1] + dt* getaAtoms()[i][1]/2;

            getrAtoms()[i][0] += dt*tempVAtoms[i][0];
            getrAtoms()[i][1] += dt*tempVAtoms[i][1];
        }
        calculateAcc();
        for(int i = 0; i< getnAtoms(); i++) {
            getvAtoms()[i][0] = tempVAtoms[i][0] + dt* getaAtoms()[i][0]/2;
            getvAtoms()[i][1] = tempVAtoms[i][1] + dt* getaAtoms()[i][1]/2;
        }
        kinECacl();
    }


    public void setaAtom(int i, double[] acc) {
        this.aAtoms[i] = acc;
    }

    public int getnAtoms() {
        return nAtoms;
    }

    public void setnAtoms(int nAtoms) {
        this.nAtoms = nAtoms;
    }

    public double[][] getrAtoms() {
        return rAtoms;
    }

    public void setrAtoms(double[][] rAtoms) {
        this.rAtoms = rAtoms;
    }

    public double[][] getvAtoms() {
        return vAtoms;
    }

    public void setvAtoms(double[][] vAtoms) {
        this.vAtoms = vAtoms;
    }

    public double[][] getaAtoms() {
        return aAtoms;
    }

    public void setaAtoms(double[][] aAtoms) {
        this.aAtoms = aAtoms;
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
