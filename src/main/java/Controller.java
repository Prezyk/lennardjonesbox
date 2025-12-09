import javafx.animation.PathTransition;
import javafx.application.Platform;
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
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    ScatterChart<Number, Number> figure;

    Label calculationStatusLabel = new Label();

    List<PathTransition> ptr;
    ArrayList<Circle> atoms;
    boolean stop = true;

    @FXML
    FileChooser chooser;

    public void initialize() {
        sliderMolecules.valueProperty()
                       .addListener(l -> txtMolecules.setText(String.valueOf(sliderMolecules.getValue())));
        initFigure();
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
        root.getChildren().clear();
        for (Circle C : atoms) {
            root.getChildren().add(C);
        }
        tbtnPlay.setDisable(false);
        tbtnPause.setDisable(false);
        tbtnStop.setDisable(false);
    }

    @FXML
    public void tbtnChartAction() {
        root.getChildren().clear();
        root.getChildren().add(figure);

        tbtnPause.setDisable(true);
        tbtnPlay.setDisable(true);
        tbtnStop.setDisable(true);
    }

    @FXML
    //TODO this does not work ???
    public void btnLoadAction() throws IOException {
        root.getChildren()
            .clear();
        calculationStatusLabel.setText("Load in progress");
        CSVHandler csv = new CSVHandler();
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(new Stage());
        System.out.println(file.getAbsolutePath());

        Molecules molecules = csv.load(file.getAbsolutePath());
        pathList = new ArrayList<>();
        ptr = new ArrayList<>();

        for (int i = 0; i < molecules.getMoleculesQuantity(); i++) {
            pathList.add(new Path());
        }

        prepareAnimationData(molecules);
        atoms = new ArrayList<>();
        ptr = new ArrayList<>();

        double rScaled = molecules.getR() / molecules.getBoxSize() * root.getWidth();
        for (int i = 0; i < molecules.getMoleculesQuantity(); i++) {
            atoms.add(new Circle(rScaled));
        }

        reloadChartData(molecules);
        animateMolecules(molecules);
        calculationStatusLabel.setText("Calculation done");
        tbtnAnim.setDisable(false);
        tbtnChart.setDisable(false);
    }

    @FXML
    public void btnSaveAction() throws FileNotFoundException {
//        CSVHandler csv = new CSVHandler();
//        csv.save(molec, "GUISaveTest.csv");
    }

    @FXML
    public void btnOkAction() {
        if (!root.getChildren().isEmpty())
            root.getChildren().clear();

        tbtnPause.setDisable(true);
        tbtnPlay.setDisable(true);
        tbtnStop.setDisable(true);

        tbtnChart.setDisable(true);
        tbtnAnim.setDisable(true);

        SimulationConditions simulationConditions = readSimulationConditions();

        atoms = new ArrayList<>();
        pathList = new ArrayList<>();


        double rScaled = simulationConditions.getMoleculeRadius() / simulationConditions.getBoxSize() * root.getWidth();
        for (int i = 0; i < simulationConditions.getMoleculesQuantity(); i++) {
            atoms.add(new Circle(rScaled));
            pathList.add(new Path());
        }

        MolecularDynamics molecularDynamics = new MolecularDynamics(simulationConditions);

        root.getChildren().add(calculationStatusLabel);
        calculationStatusLabel.setText("Calculation in progress");

        CompletableFuture<Molecules> futureSimulationResult = molecularDynamics.calculateSimulationConcurrent();
        futureSimulationResult.thenAccept(molecules -> Platform.runLater(() -> {
            prepareAnimationData(molecules);
            ptr = new ArrayList<>();
            reloadChartData(molecules);
            animateMolecules(molecules);
            calculationStatusLabel.setText("Calculation done");
            tbtnAnim.setDisable(false);
            tbtnChart.setDisable(false);
        }));
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

    private void initFigure() {
        NumberAxis x = new NumberAxis();
        NumberAxis y = new NumberAxis();
        figure = new ScatterChart<>(x, y);
        figure.setLegendVisible(true);
        figure.setMinSize(root.getWidth(), root.getHeight());
    }

    private void reloadChartData(Molecules molecules) {
        ChartMapper chartMapper = new ChartMapper(molecules);
        resetChartsIfNeeded(chartMapper);
        loadAllCharts(chartMapper);
    }

    private void resetChartsIfNeeded(ChartMapper chartMapper) {
        List<String> currentChartNames = figure.getData()
                                               .stream()
                                               .map(XYChart.Series::getName)
                                               .collect(Collectors.toList());
        if (chartMapper.getNames().size() != currentChartNames.size() || !chartMapper.getNames().containsAll(currentChartNames) ) {
            figure.getData().clear();
            initCharts(chartMapper);
        }
    }

    private void initCharts(ChartMapper chartMapper) {
        chartMapper.getNames().forEach(chartName -> {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(chartName);
            figure.getData().add(series);
        });
    }


    private void prepareAnimationData(Molecules molecules) {
        for (int i = 0; i < molecules.getN(); i++) {
            for (int a = 0; a < molecules.getMoleculesQuantity(); a++) {

                double xCoord = molecules.getrVectors()[a][i][0] * root.getWidth() / molecules.getBoxSize();
                double yCoord = root.getHeight() - molecules.getrVectors()[a][i][1] * root.getHeight() / molecules.getBoxSize();
                pathList.get(a)
                        .getElements()
                        .add(i == 0 ? new MoveTo(xCoord, yCoord) : new LineTo(xCoord, yCoord));

            }
            tbtnAnim.setDisable(false);
            tbtnChart.setDisable(false);
        }
    }

    private void animateMolecules(Molecules molecules) {
        for (int i = 0; i < molecules.getMoleculesQuantity(); i++) {
            ptr.add(new PathTransition());
            ptr.get(ptr.size() - 1)
               .setDuration(Duration.seconds(molecules.getTime()[molecules.getTime().length - 1]));
            ptr.get(ptr.size() - 1)
               .setPath(pathList.get(i));
            ptr.get(ptr.size() - 1)
               .setNode(atoms.get(i));
        }
    }

    private void loadAllCharts(ChartMapper chartMapper) {
        figure.getData()
              .forEach(series -> loadChart(series, chartMapper.getTimeSeries(series.getName()), chartMapper.getTimePoints()));
    }

    private void loadChart(XYChart.Series<Number, Number> series, double[] timeSeries, double[] timePoints) {
        series.getData().clear();

        if (timeSeries.length != timePoints.length)
            throw new IllegalArgumentException("Time series should have the same amount of points as time points.");

        for (int i = 0; i < timePoints.length; i++) {
            series.getData().add(new XYChart.Data<>(timePoints[i], timeSeries[i]));
        }
    }

    private SimulationConditions readSimulationConditions() {
        return SimulationConditions.builder()
                .time(getValidValue(txtTime, labelInvalidTime, (timeToValidate) -> timeToValidate <= 0))
                .timeStep(getValidValue(txtStep, labelInvalidStep, (stepToValidate) -> stepToValidate <= 0 || stepToValidate > 0.05))
                .moleculeRadius(getValidValue(txtR0, labelInvalidR0))
                .epsilon(getValidValue(txtEps, labelInvalidEps))
                .mass(getValidValue(txtMass, labelInvalidMass, massToValidate -> massToValidate <= 0))
                .moleculesQuantity(getValidValue(txtMolecules, labelInvalidMolecules, moleculesToValidate -> moleculesToValidate <= 0 || (moleculesToValidate - moleculesToValidate.intValue()) > 0).intValue())
                .boxSize(getValidValue(txtBoxSize, labelInvalidMass, massToValidate -> massToValidate <= 0))
                .wallStiffness(getValidValue(txtWallStiffness, labelInvalidWallStiffness, wallStiffnessToValidate -> wallStiffnessToValidate <= 0))
                .build();
    }
}
