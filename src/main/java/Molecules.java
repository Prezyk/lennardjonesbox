public class Molecules {

    private int moleculesQuantity;
    private double[] time;
    private int n;
    private double[][][] rVectors;
    private double[][][] vVectors;
    private double[][][] aVectors;
    private double[] Ekin;
    private double[] Epot;
    private double[] ElastE;
    private double r;
    private double eps;
    private double boxSize;



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
        this.Ekin = new double[n];
        this.Epot = new double[n];
        this.ElastE  = new double[n];


        for(int i=0; i<moleculesQuantity; i++) {
            this.rVectors[i] = new double[n][2];
            this.vVectors[i] = new double[n][2];
            this.aVectors[i] = new double[n][2];
        }


    }

    public void addRow(int index, double time, double[][] rVector, double[][] vVector, double[][] aVector, double Ekin, double Epot, double ElastE) {
        this.time[index] = time;
        this.Ekin[index] = Ekin;
        this.Epot[index] = Epot;
        this.ElastE[index] = ElastE;
        for(int i=0; i<this.moleculesQuantity; i++) {
            this.rVectors[i][index] = rVector[i];
            this.aVectors[i][index] = aVector[i];
            this.vVectors[i][index] = vVector[i];
        }
    }

    public int getMoleculesQuantity() {
        return moleculesQuantity;
    }

    public void setMoleculesQuantity(int moleculesQuantity) {
        this.moleculesQuantity = moleculesQuantity;
    }

    public double[] getTime() {
        return time;
    }

    public void setTime(double[] time) {
        this.time = time;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public double[][][] getrVectors() {
        return rVectors;
    }

    public void setrVectors(double[][][] rVectors) {
        this.rVectors = rVectors;
    }

    public double[][][] getvVectors() {
        return vVectors;
    }

    public void setvVectors(double[][][] vVectors) {
        this.vVectors = vVectors;
    }

    public double[][][] getaVectors() {
        return aVectors;
    }

    public void setaVectors(double[][][] aVectors) {
        this.aVectors = aVectors;
    }

    public double[] getEkin() {
        return Ekin;
    }

    public void setEkin(double[] ekin) {
        Ekin = ekin;
    }

    public double[] getEpot() {
        return Epot;
    }

    public void setEpot(double[] epot) {
        Epot = epot;
    }


    public double[] getElastE() {
        return ElastE;
    }

    public void setElastE(double[] elastE) {
        ElastE = elastE;
    }

    public double getR() {
        return r;
    }

    public void setR(double r) {
        this.r = r;
    }

    public double getEps() {
        return eps;
    }

    public void setEps(double eps) {
        this.eps = eps;
    }

    public double getBoxSize() {
        return boxSize;
    }

    public void setBoxSize(double boxSize) {
        this.boxSize = boxSize;
    }
}
