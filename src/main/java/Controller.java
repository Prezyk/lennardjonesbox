import javafx.animation.PathTransition;
import javafx.fxml.FXML;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Controller {

    @FXML
    Pane root;

    @FXML
    GridPane mainLayout;

    @FXML
    Button tbtnPlay;

    @FXML
    Button tbtnStop;

    @FXML
    Button tbtnPause;

    @FXML
    Button tbtnAnim;

    @FXML
    Button tbtnChart;

    @FXML
    Slider sliderMolecules;

    @FXML
    TextField txtTime;

    @FXML
    TextField txtStep;

    @FXML
    TextField txtR0;

    @FXML
    TextField txtEps;

    @FXML
    TextField txtMass;

    @FXML
    TextField txtMolecules;

    @FXML
    TextField txtBoxSize;

    @FXML
    TextField txtWallStiffness;

    @FXML
    Label labelInvalidTime;

    @FXML
    Label labelInvalidStep;

    @FXML
    Label labelInvalidR0;

    @FXML
    Label labelInvalidEps;

    @FXML
    Label labelInvalidMass;

    @FXML
    Label labelInvalidMolecules;

    @FXML
    Label labelInvalidBoxSize;

    @FXML
    Label labelInvalidWallStiffness;

    List<Path> pathList;

    MD md;

    ScatterChart<Number, Number> figure;

    NumberAxis x;
    NumberAxis y;

    Label calculationStatusLabel = new Label();

    XYChart.Series<Number, Number> eKin;
    XYChart.Series<Number, Number> ePot;
    XYChart.Series<Number, Number> eElat;
    XYChart.Series<Number, Number> eTotal;

    List<PathTransition> ptr;
    ArrayList<Circle> atoms;
    boolean stop = true;


    @FXML
    FileChooser chooser;
    Molecules molec;


    public void initialize() {
        sliderMolecules.valueProperty()
                       .addListener(l -> txtMolecules.setText(String.valueOf(sliderMolecules.getValue())));

        eKin = new XYChart.Series<>();
        ePot = new XYChart.Series<>();
        eElat = new XYChart.Series<>();
        eTotal = new XYChart.Series<>();


    }

    @FXML
    public void tbtnPlayAction() {
        if (stop) {
            stop = false;
            for (PathTransition P : ptr) {
                P.playFromStart();
                System.out.println("Play next");
            }
        } else {
            for (PathTransition P : ptr) {
                P.play();
            }
        }
    }

    @FXML
    public void tbtnPauseAction() {
        for (PathTransition P : ptr) {
            P.pause();
        }

    }

    @FXML
    public void tbtnStopActon() {
        for (PathTransition P : ptr) {
            P.stop();
        }
        stop = true;

    }

    @FXML
    public void tbtnAnimAction() {
        root.getChildren()
            .clear();
        for (Circle C : atoms) {
            root.getChildren()
                .add(C);
        }
        tbtnPlay.setDisable(false);
        tbtnPause.setDisable(false);
        tbtnStop.setDisable(false);
    }


    @FXML
    public void tbtnChartAction() {
        root.getChildren()
            .clear();
        root.getChildren()
            .add(figure);


        tbtnPause.setDisable(true);
        tbtnPlay.setDisable(true);
        tbtnStop.setDisable(true);
    }

    @FXML
    public void btnLoadAction() throws IOException {
        root.getChildren()
            .clear();
        calculationStatusLabel.setText("Load in progress");
        CSVHandler csv = new CSVHandler();
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(new Stage());
        System.out.println(file.getAbsolutePath());

        molec = csv.load(file.getAbsolutePath());
        pathList = new ArrayList<>();
        ptr = new ArrayList<>();
        x = new NumberAxis();
        y = new NumberAxis();
        figure = new ScatterChart<>(x, y);
        figure.setLegendVisible(true);
        figure.setMinSize(root.getWidth(), root.getHeight());


        for (int i = 0; i < molec.getMoleculesQuantity(); i++) {
            pathList.add(new Path());
        }

        for (int i = 0; i < molec.getN(); i++) {
            for (int a = 0; a < molec.getMoleculesQuantity(); a++) {

                double xCoord = molec.getrVectors()[a][i][0] * root.getWidth() / molec.getBoxSize();
                double yCoord = root.getWidth() - molec.getrVectors()[a][i][1] * root.getWidth() / molec.getBoxSize();

                if (i == 0) {
                    pathList.get(a)
                            .getElements()
                            .add(new MoveTo(xCoord, yCoord));
                } else {
                    pathList.get(a)
                            .getElements()
                            .add(new LineTo(xCoord, yCoord));
                }

            }

            eKin.getData()
                .add(new XYChart.Data<>(molec.getTime()[i], molec.getEkin()[i]));
            ePot.getData()
                .add(new XYChart.Data<>(molec.getTime()[i], molec.getEpot()[i]));
            eElat.getData()
                 .add(new XYChart.Data<>(molec.getTime()[i], molec.getElastE()[i]));
            eTotal.getData()
                  .add(new XYChart.Data<>(molec.getTime()[i], molec.getEpot()[i] + molec.getEkin()[i] + molec.getElastE()[i]));


        }
        eKin.setName("Kinetic E");
        ePot.setName("Potential E");
        eElat.setName("Elastic E");
        eTotal.setName("Total E");
        ptr = new ArrayList<>();
        atoms = new ArrayList<>();
        double rScaled = molec.getR() / molec.getBoxSize() * root.getWidth();


        for (int i = 0; i < molec.getMoleculesQuantity(); i++) {
            atoms.add(new Circle(rScaled));
        }


        if (!figure.getData()
                   .isEmpty())
            figure.getData()
                  .clear();
        figure.getData()
              .add(eKin);
        figure.getData()
              .add(ePot);
        figure.getData()
              .add(eElat);
        figure.getData()
              .add(eTotal);
        for (int i = 0; i < molec.getMoleculesQuantity(); i++) {
            ptr.add(new PathTransition());
            ptr.get(i)
               .setDuration(Duration.seconds(molec.getTime()[molec.getTime().length - 1]));
            ptr.get(i)
               .setPath(pathList.get(i));
            ptr.get(i)
               .setNode(atoms.get(i));
        }
        calculationStatusLabel.setText("Calculation done");
        tbtnAnim.setDisable(false);
        tbtnChart.setDisable(false);


    }


    @FXML
    public void btnSaveAction() throws FileNotFoundException {


        CSVHandler csv = new CSVHandler();
        csv.save(molec, "GUISaveTest.csv");

    }

    @FXML
    public void btnOkAction() {

        if (!root.getChildren()
                 .isEmpty())
            root.getChildren()
                .clear();


        tbtnPause.setDisable(true);
        tbtnPlay.setDisable(true);
        tbtnStop.setDisable(true);

        tbtnPlay.setDisable(true);
        tbtnAnim.setDisable(true);


        double time;
        try {
            labelInvalidTime.setVisible(false);
            time = Double.parseDouble(txtTime.getText());
            if (time <= 0)
                throw new NumberFormatException();

        } catch (NumberFormatException n) {
            labelInvalidTime.setVisible(true);
            return;
        }

        double step;
        try {
            labelInvalidStep.setVisible(false);
            step = Double.parseDouble(txtStep.getText());
            if (step <= 0 || step > 0.05)
                throw new NumberFormatException();

        } catch (NumberFormatException n) {
            labelInvalidStep.setVisible(true);
            return;
        }


        double r0;
        try {
            labelInvalidR0.setVisible(false);
            r0 = Double.parseDouble(txtR0.getText());
        } catch (NumberFormatException n) {
            labelInvalidR0.setVisible(true);
            return;
        }


        double eps;
        try {
            labelInvalidEps.setVisible(false);

            eps = Double.parseDouble(txtEps.getText());
        } catch (NumberFormatException n) {
            labelInvalidEps.setVisible(true);
            return;
        }


        double mass;
        try {
            labelInvalidMass.setVisible(false);

            mass = Double.parseDouble(txtMass.getText());
            if (mass <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException n) {
            labelInvalidMass.setVisible(true);
            return;
        }


        double moleculesReaded;
        try {
            labelInvalidMolecules.setVisible(false);

            moleculesReaded = Double.parseDouble(txtMolecules.getText());
            if (moleculesReaded <= 0 || (moleculesReaded - (int) moleculesReaded) > 0)
                throw new NumberFormatException();
        } catch (NumberFormatException n) {
            labelInvalidMolecules.setVisible(true);
            return;
        }

        int molecules = (int) moleculesReaded;

        double boxSize;
        try {
            labelInvalidMass.setVisible(false);

            boxSize = Double.parseDouble(txtBoxSize.getText());
            if (boxSize <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException n) {
            labelInvalidMass.setVisible(true);
            return;
        }

        double wallStiffness;
        try {
            labelInvalidWallStiffness.setVisible(false);

            wallStiffness = Double.parseDouble(txtWallStiffness.getText());
            if (wallStiffness <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException n) {
            labelInvalidMass.setVisible(true);
            return;
        }


        atoms = new ArrayList<>();
        pathList = new ArrayList<>();


        double rScaled = r0 / boxSize * root.getWidth();
        for (int i = 0; i < molecules; i++) {
            atoms.add(new Circle(rScaled));
            pathList.add(new Path());
        }

        md = new MD(molecules, r0, eps, mass, time, boxSize, wallStiffness);


        int n = (int) Math.floor(time / step);

        molec = new Molecules(molecules, n, r0, eps, boxSize);

        x = new NumberAxis();
        y = new NumberAxis();

        figure = new ScatterChart<>(x, y);

        figure.setLegendVisible(true);
        figure.setMinSize(root.getWidth(), root.getHeight());


        eKin = new XYChart.Series<>();
        ePot = new XYChart.Series<>();
        eElat = new XYChart.Series<>();
        eTotal = new XYChart.Series<>();

        eKin.setName("Kinetic E");
        ePot.setName("Potential E");
        eElat.setName("Elastic E");
        eTotal.setName("Total E");


        final double threadBoxSize = boxSize;
        final double threadStep = step;
        final double threadTime = time;
        root.getChildren()
            .add(calculationStatusLabel);
        calculationStatusLabel.setText("Calculation in progress");


        new Thread() {
            @Override
            public synchronized void start() {
                double currentTime = 0;
                super.start();
                for (int i = 0; i < n; i++) {

                    molec.addRow(i, currentTime, md.getrAtoms(), md.getvAtoms(), md.getaAtoms(), md.getKinE(), md.getPotE(), md.getElastE());
                    double[][] rAtoms = md.getrAtoms();

                    for (int a = 0; a < molecules; a++) {

                        double xCoord = rAtoms[a][0] * root.getWidth() / threadBoxSize;
                        double yCoord = root.getWidth() - rAtoms[a][1] * root.getWidth() / threadBoxSize;
                        if (i == 0) {
                            pathList.get(a)
                                    .getElements()
                                    .add(new MoveTo(xCoord, yCoord));
                        } else {
                            pathList.get(a)
                                    .getElements()
                                    .add(new LineTo(xCoord, yCoord));
                        }
                    }

                    eKin.getData()
                        .add(new XYChart.Data<>(currentTime, md.getKinE()));
                    ePot.getData()
                        .add(new XYChart.Data<>(currentTime, md.getPotE()));
                    eElat.getData()
                         .add(new XYChart.Data<>(currentTime, md.getElastE()));
                    eTotal.getData()
                          .add(new XYChart.Data<>(currentTime, md.getPotE() + md.getKinE() + md.getElastE()));
                    md.verletStep(threadStep);
                    currentTime += threadStep;
                    tbtnAnim.setDisable(false);
                    tbtnChart.setDisable(false);


                }
                ptr = new ArrayList<>();

                if (!figure.getData()
                           .isEmpty())
                    figure.getData()
                          .clear();
                figure.getData()
                      .add(eKin);
                figure.getData()
                      .add(ePot);
                figure.getData()
                      .add(eElat);
                figure.getData()
                      .add(eTotal);
                for (int i = 0; i < molecules; i++) {
                    ptr.add(new PathTransition());
                    ptr.get(ptr.size() - 1)
                       .setDuration(Duration.seconds(threadTime));
                    ptr.get(ptr.size() - 1)
                       .setPath(pathList.get(i));
                    ptr.get(ptr.size() - 1)
                       .setNode(atoms.get(i));
                }
                calculationStatusLabel.setText("Calculation done");
                tbtnAnim.setDisable(false);
                tbtnChart.setDisable(false);

            }
        }.start();
    }

    @FXML
    public void txtMoleculesAction() {
        try {
            labelInvalidMolecules.setVisible(false);
            sliderMolecules.setValue(Double.parseDouble(txtMolecules.getText()));
        } catch (Exception e) {
            labelInvalidMolecules.setVisible(true);
        }

    }
}




