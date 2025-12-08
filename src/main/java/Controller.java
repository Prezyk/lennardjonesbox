import javafx.animation.PathTransition;
import javafx.concurrent.Task;
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
import java.util.function.Function;

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

        initCharts();
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
    //TODO this does not work
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
        initCharts();


        for (int i = 0; i < molec.getMoleculesQuantity(); i++) {
            pathList.add(new Path());
        }

        animateMoleculesAndPrepareChart();
        setChartNames();
        atoms = new ArrayList<>();
        ptr = new ArrayList<>();

        double rScaled = molec.getR() / molec.getBoxSize() * root.getWidth();
        for (int i = 0; i < molec.getMoleculesQuantity(); i++) {
            atoms.add(new Circle(rScaled));
            ptr.add(new PathTransition());
        }


        reloadChartData();
        for (int i = 0; i < molec.getMoleculesQuantity(); i++) {
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

        tbtnChart.setDisable(true);
        tbtnAnim.setDisable(true);


        double time = getValidValue(txtTime, labelInvalidTime, (timeToValidate) -> timeToValidate <= 0);

        double step = getValidValue(txtStep, labelInvalidStep, (stepToValidate) -> stepToValidate <= 0 || stepToValidate > 0.05);

        double r0 = getValidValue(txtR0, labelInvalidR0);

        double eps = getValidValue(txtEps, labelInvalidEps);

        double mass = getValidValue(txtMass, labelInvalidMass, massToValidate -> massToValidate <= 0);

        double moleculesReaded = getValidValue(txtMolecules, labelInvalidMolecules, moleculesToValidate -> moleculesToValidate <= 0 || (moleculesToValidate - moleculesToValidate.intValue()) > 0);

        int molecules = (int) moleculesReaded;

        double boxSize = getValidValue(txtBoxSize, labelInvalidMass, massToValidate -> massToValidate <= 0);

        double wallStiffness = getValidValue(txtWallStiffness, labelInvalidWallStiffness, wallStiffnessToValidate -> wallStiffnessToValidate <= 0);


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

        initCharts();

        final double threadStep = step;
        root.getChildren()
            .add(calculationStatusLabel);
        calculationStatusLabel.setText("Calculation in progress");

        new Task<Void> () {
            @Override
            public Void call() {
                double currentTime = 0;
                for (int i = 0; i < n; i++) {
                    molec.addRow(i, currentTime, md.getrAtoms(), md.getvAtoms(), md.getaAtoms(), md.getKinE(), md.getPotE(), md.getElastE());
                    md.verletStep(threadStep);
                    currentTime += threadStep;
                }
                animateMoleculesAndPrepareChart();
                ptr = new ArrayList<>();

                reloadChartData();
                for (int i = 0; i < molecules; i++) {
                    ptr.add(new PathTransition());
                    ptr.get(ptr.size() - 1)
                       .setDuration(Duration.seconds(molec.getTime()[molec.getTime().length - 1]));
                    ptr.get(ptr.size() - 1)
                       .setPath(pathList.get(i));
                    ptr.get(ptr.size() - 1)
                       .setNode(atoms.get(i));
                }
                calculationStatusLabel.setText("Calculation done");
                tbtnAnim.setDisable(false);
                tbtnChart.setDisable(false);
                return null;
            }
        }.call();
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

    private Double getValidValue(TextField valueField, Label errorLabel) {
        return getValidValue(valueField, errorLabel, dummy -> Boolean.FALSE);
    }

    private Double getValidValue(TextField valueField, Label errorLabel, Function<Double, Boolean> invalidityChecker) {
        double value = -1.;
        try {
            errorLabel.setVisible(false);
            value = Double.parseDouble(valueField.getText());
            if (invalidityChecker.apply(value))
                throw new IllegalArgumentException();

        } catch (IllegalArgumentException e) {
            errorLabel.setVisible(true);
        }
        return value;
    }

    private void initCharts() {
        initChartsStructure();
        resetChartData();
        setChartNames();
    }

    private void initChartsStructure() {
        x = new NumberAxis();
        y = new NumberAxis();
        figure = new ScatterChart<>(x, y);
        figure.setLegendVisible(true);
        figure.setMinSize(root.getWidth(), root.getHeight());
    }

    private void resetChartData() {
        eKin = new XYChart.Series<>();
        ePot = new XYChart.Series<>();
        eElat = new XYChart.Series<>();
        eTotal = new XYChart.Series<>();
    }

    private void setChartNames() {
        eKin.setName("Kinetic E");
        ePot.setName("Potential E");
        eElat.setName("Elastic E");
        eTotal.setName("Total E");
    }

    private void reloadChartData() {
        if (!figure.getData().isEmpty())
            figure.getData().clear();
        figure.getData().add(eKin);
        figure.getData().add(ePot);
        figure.getData().add(eElat);
        figure.getData().add(eTotal);
    }

    private void animateMoleculesAndPrepareChart() {
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
            tbtnAnim.setDisable(false);
            tbtnChart.setDisable(false);
        }
    }
}




