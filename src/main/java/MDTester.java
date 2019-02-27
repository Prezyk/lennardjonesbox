import java.io.FileNotFoundException;
import java.io.IOException;

public class MDTester {
	public static void main(String[] args) throws IOException {

//		MD md = new MD(4, 3.4, 1.65, 40, 10, 100, 50);
//		long start = System.nanoTime();
//		double dt = 0.01;
//		double time = 0;
//		int n = (int)Math.floor(100/dt);
//
//		Molecules m = new Molecules(4, n);
//		for(int i=0; i<500; i++) {
//			md.verletStep(dt);
//
//			double[][] rVec = md.getrAtoms();
//			double[][] vVec = md.getvAtoms();
//			double[][] aVec = md.getaAtoms();
//
//
//			m.addRow(i, time, rVec, vVec, aVec);
//
//
//
////			double E =  md.getKinE()+md.getPotE();
////			double[][] R = md.getrAtoms();
////			double distance = Math.sqrt(Math.pow(R[0][0]-R[1][0],2) + Math.pow(R[0][1]-R[1][1],2));
////			System.out.println(md.getrAtom(0)[0] + "    " + md.getrAtom(0)[1] + "       " + E +  "       " + distance );
//
//
//		}
//		CSVHandler csv = new CSVHandler();
//
//		csv.save(m, "testSave.csv");


		CSVHandler csv = new CSVHandler();
		Molecules m = csv.load("testSave.csv");
		for(int i=0; i<m.getN(); i++) {
			System.out.println(m.getvVectors()[0][i][0]  + "\t:\t" + m.getrVectors()[0][i][1]);
		}


	}
}
