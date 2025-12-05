import java.util.Random;

public class MD {

    private int nAtoms;
    private double[][] rAtoms;
    private double[][] vAtoms;
    private double[][] aAtoms;

    private double potE;
    private double kinE;
    private double elastE;
    private double time;

    private double mass;
    private double eps;
    private double r0;
    private double rCut2;

    private double boxSize;
    private double wallStiffness;



    public MD(int molecules, double r0, double eps, double mass, double time, double boxSize, double wallStiffness) {
        super();
        Random gen = new Random();
        this.setnAtoms(molecules);
        this.setrAtoms(new double[this.getnAtoms()][2]);
        this.setvAtoms(new double[this.getnAtoms()][2]);
        this.setaAtoms(new double[this.getnAtoms()][2]);
        this.setBoxSize(boxSize);
        this.setR0(r0);
        this.setrCut2(Math.pow(2*this.getR0(),2));
        this.setEps(eps);
        this.setMass(mass);
        this.setPotE(0);
        this.setKinE(0);
        this.setTime(time);
        this.setWallStiffness(wallStiffness);

        for (int i = 0; i< this.getnAtoms(); i++) {

            this.getrAtoms()[i] = new double[]{Math.abs(gen.nextDouble()* this.getBoxSize() - this.getR0()), Math.abs(gen.nextDouble()* this.getBoxSize() - this.getR0())};

            for(int j=0; j<(i+1); j++) {
                if ((i!=j)&& ((Math.pow(getrAtoms()[i][0]- getrAtoms()[j][0],2) + Math.pow(getrAtoms()[i][1]- getrAtoms()[j][1],2)) <= (this.getR0() * this.getR0()))) {
                    this.getrAtoms()[i] = new double[]{Math.abs(gen.nextDouble()* this.getBoxSize() - this.getR0()), Math.abs(gen.nextDouble()* this.getBoxSize() - this.getR0())};
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

    public void calculateAcc() {

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
                fr6 = Math.pow(getR0() /rij, 6);

                if(rij2< getrCut2()) {
                    fr = -(48* getEps() /rij2) * fr6*(fr6* - 0.5) / getMass();
                    fx = fr*dx;
                    fy = fr*dy;

                    getaAtoms()[i][0] += fx;
                    getaAtoms()[j][0] -= fx;
                    getaAtoms()[i][1] += fy;
                    getaAtoms()[j][1] -= fy;

                    setPotE(getPotE() + 2*getEps()*fr6*(fr6-1.0));
                }
            }
        }

        setElastE(0);
        for(int i=0; i<nAtoms; i++) {
            double d;
            if(rAtoms[i][0] < 0.5) {
                d = 0.5 - rAtoms[i][0];
                aAtoms[i][0] += wallStiffness*d;
                setElastE(getElastE() + 0.5*wallStiffness*d*d*mass);
            }

            if(rAtoms[i][0] > (boxSize-0.5)) {
                d = boxSize - 0.5 - rAtoms[i][0];
                aAtoms[i][0] += wallStiffness*d;
                setElastE(getElastE() + 0.5*wallStiffness*d*d*mass);
            }

            if(rAtoms[i][1] < 0.5) {
                d = 0.5 - rAtoms[i][1];
                aAtoms[i][1] += wallStiffness*d;
                setElastE(getElastE() + 0.5*wallStiffness*d*d*mass);
            }

            if(rAtoms[i][1] > (boxSize-0.5)) {
                d = boxSize - 0.5 - rAtoms[i][1];
                aAtoms[i][1] += wallStiffness*d;
                setElastE(getElastE() + 0.5*wallStiffness*d*d*mass);
            }
        }
    }


    private void kinECacl() {
        setKinE(0);
        for(int i = 0; i< getnAtoms(); i++) {
            setKinE(getKinE() + getMass() *(Math.pow(getvAtoms()[i][0],2) + Math.pow(getvAtoms()[i][1],2))/2);
        }
    }

    public void verletStep(double dt) {
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

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public double getEps() {
        return eps;
    }

    public void setEps(double eps) {
        this.eps = eps;
    }

    public double getR0() {
        return r0;
    }

    public void setR0(double r0) {
        this.r0 = r0;
    }

    public double getrCut2() {
        return rCut2;
    }

    public void setrCut2(double rCut2) {
        this.rCut2 = rCut2;
    }

    public double getBoxSize() {
        return boxSize;
    }

    public void setBoxSize(double boxSize) {
        this.boxSize = boxSize;
    }

    public void setWallStiffness(double wallStiffness) {
        this.wallStiffness = wallStiffness;
    }

    public double getElastE() {
        return elastE;
    }

    public void setElastE(double elastE) {
        this.elastE = elastE;
    }
}
