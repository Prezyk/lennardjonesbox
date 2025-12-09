public class Molecules {

    private final int moleculesQuantity;
    private final double[] time;
    private final int n;
    private final double[][][] rVectors;
    private final double[][][] vVectors;
    private final double[][][] aVectors;
    private final double[] kineticEnergy;
    private final double[] potentialEnergy;
    private final double[] boxElasticEnergy;
    private final double[] totalEnergy;
    private final double r;
    private final double eps;
    private final double boxSize;



    Molecules(int moleculesQuantity, int n, double r, double eps, double boxSize) {
        this.n = n;
        this.moleculesQuantity = moleculesQuantity;
        this.r = r;
        this.eps = eps;
        this.boxSize = boxSize;
        this.time = new double[n];
        this.rVectors = new double[moleculesQuantity][][];
        this.vVectors = new double[moleculesQuantity][][];
        this.aVectors = new double[moleculesQuantity][][];
        this.kineticEnergy = new double[n];
        this.potentialEnergy = new double[n];
        this.boxElasticEnergy = new double[n];
        this.totalEnergy = new double[n];



        for(int i=0; i<moleculesQuantity; i++) {
            this.rVectors[i] = new double[n][2];
            this.vVectors[i] = new double[n][2];
            this.aVectors[i] = new double[n][2];
        }


    }

    public int getMoleculesQuantity() {
        return moleculesQuantity;
    }

    public void addRow(int index, double time, double[][] rVector, double[][] vVector, double[][] aVector, double kineticEnergy, double potentialEnergy, double boxElasticEnergy) {
        this.time[index] = time;
        this.kineticEnergy[index] = kineticEnergy;
        this.potentialEnergy[index] = potentialEnergy;
        this.boxElasticEnergy[index] = boxElasticEnergy;
        this.totalEnergy[index] = kineticEnergy + potentialEnergy + boxElasticEnergy;
        for(int i=0; i<this.moleculesQuantity; i++) {
            this.rVectors[i][index] = new double[2];
            this.rVectors[i][index][0] = rVector[i][0];
            this.rVectors[i][index][1] = rVector[i][1];

            this.aVectors[i][index] = new double[2];
            this.aVectors[i][index][0] = aVector[i][0];
            this.aVectors[i][index][1] = aVector[i][1];

            this.vVectors[i][index] = new double[2];
            this.vVectors[i][index][0] = vVector[i][0];
            this.vVectors[i][index][1] = vVector[i][1];
        }
    }

    public double[] getTime() {
        return time;
    }

    public int getN() {
        return n;
    }

    public double[][][] getrVectors() {
        return rVectors;
    }

    public double[][][] getvVectors() {
        return vVectors;
    }

    public double[][][] getaVectors() {
        return aVectors;
    }

    public double[] getKineticEnergy() {
        return kineticEnergy;
    }

    public double[] getPotentialEnergy() {
        return potentialEnergy;
    }

    public double[] getBoxElasticEnergy() {
        return boxElasticEnergy;
    }

    public double getR() {
        return r;
    }

    public double getEps() {
        return eps;
    }

    public double getBoxSize() {
        return boxSize;
    }

    public double[] getTotalEnergy() {
        return totalEnergy;
    }
}
