import java.io.*;

public class CSVHandler {

    public void save(Molecules molecules, String path) throws FileNotFoundException {

        PrintWriter pw = new PrintWriter(path);
        StringBuilder sb = new StringBuilder();
        sb.append(molecules.getMoleculesQuantity() + "," + molecules.getN() + "," + molecules.getR() + ","  + molecules.getEps() + "," + molecules.getBoxSize() + "\n");

        int n = molecules.getN();
        int quantity = molecules.getMoleculesQuantity();

        sb.append("time,Ekin,Epot,ElastE,");

        for(int i=0; i<quantity; i++) {
            if(i==(quantity-1)) {
                sb.append("Mol" + i + "-rx,Mol" + i + "-ry,Mol" + i + "-vx,Mol" + i + "-vy,Mol" + i + "-ax,Mol" + i + "-ay\n");
            } else {
                sb.append("Mol" + i + "-rx,Mol" + i + "-ry,Mol" + i + "-vx,Mol" + i + "-vy,Mol" + i + "-ax,Mol" + i + "-ay,");
            }
        }

        for(int i=0; i<n; i++) {

            sb.append(molecules.getTime()[i] + "," + molecules.getEkin()[i] + "," + molecules.getEpot()[i] + "," + molecules.getElastE()[i] + ",");

            for(int j=0; j<quantity; j++) {
                sb.append(molecules.getrVectors()[j][i][0] + "," + molecules.getrVectors()[j][i][1] + ",");
                sb.append(molecules.getvVectors()[j][i][0] + "," + molecules.getvVectors()[j][i][1] + ",");
                if(j==(quantity-1)) {
                    sb.append(molecules.getaVectors()[j][i][0] + "," + molecules.getaVectors()[j][i][1] + "\n");
                } else {
                    sb.append(molecules.getaVectors()[j][i][0] + "," + molecules.getaVectors()[j][i][1] + ",");
                }
            }
        }

        pw.write(sb.toString());
        pw.flush();
        pw.close();
    }

    public Molecules load(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line = br.readLine();
        String[] splitLine = line.split(",");
        int quantity = Integer.parseInt(splitLine[0]);
        int n = Integer.parseInt(splitLine[1]);
        double r = Double.parseDouble(splitLine[2]);
        double eps = Double.parseDouble(splitLine[3]);
        double boxSize = Double.parseDouble(splitLine[4]);

        Molecules molecules = new Molecules(quantity, n, r, eps, boxSize);
        int index = 0;
        br.readLine();

        while((line = br.readLine())!=null) {
            splitLine = line.split(",");
            double time = Double.parseDouble(splitLine[0]);
            double[][] rVectors = new double[quantity][];
            double[][] vVectors = new double[quantity][];
            double[][] aVectors = new double[quantity][];
            double Ekin = Double.parseDouble(splitLine[1]);
            double Epot = Double.parseDouble(splitLine[2]);
            double ElastE = Double.parseDouble(splitLine[3]);
            int jIndex;
            for(int j = 0; j<quantity; j++) {
                if(j==0) {
                    jIndex = j + 4;
                } else {
                    jIndex = j*6 + 4;

                }
                rVectors[j] = new double[]{Double.parseDouble(splitLine[jIndex]), Double.parseDouble(splitLine[jIndex+1])};
                vVectors[j] = new double[]{Double.parseDouble(splitLine[jIndex+2]), Double.parseDouble(splitLine[jIndex+3])};
                aVectors[j] = new double[]{Double.parseDouble(splitLine[jIndex+4]), Double.parseDouble(splitLine[jIndex+5])};
            }
            molecules.addRow(index, time, rVectors, vVectors, aVectors, Ekin, Epot, ElastE);
            ++index;
        }

        return molecules;
    }
}
