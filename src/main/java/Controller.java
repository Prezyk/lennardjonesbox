import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class Controller {

    @FXML
    Pane animationPane;

    @FXML
    Pane chartPane;

    @FXML
    GridPane mainLayout;

    @FXML
    Button tbtnPlay;

    @FXML
    Button tbtnStop;

    @FXML
    Button tbtnPause;

    List<Path> pathList;

    ScatterChart<Number, Number> figure;

    Label calculationStatusLabel = new Label();

    List<PathTransition> ptr;
    ArrayList<Circle> atoms;
    boolean stop = true;

    public void initialize() {
        EventDispatcher eventDispatcher = EventDispatcher.getInstance();
        eventDispatcher.registerEventHandler(SimulationConditionsConfirmedEvent.class, this::simulationConditionsConfirmedEventHandler);
        eventDispatcher.registerEventHandler(SimulationCalculationsFinishedEvent.class, this::simulationCalculationsFinishedEventHandler);
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


    private void initFigure() {
        NumberAxis x = new NumberAxis();
        NumberAxis y = new NumberAxis();
        figure = new ScatterChart<>(x, y);
        figure.setLegendVisible(true);
        figure.setMinSize(chartPane.getWidth(), chartPane.getHeight());
        if (chartPane.getChildren().isEmpty())
            chartPane.getChildren().add(figure);
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

                double xCoord = molecules.getrVectors()[a][i][0] * animationPane.getWidth() / molecules.getBoxSize();
                double yCoord = animationPane.getHeight() - molecules.getrVectors()[a][i][1] * animationPane.getHeight() / molecules.getBoxSize();
                pathList.get(a)
                        .getElements()
                        .add(i == 0 ? new MoveTo(xCoord, yCoord) : new LineTo(xCoord, yCoord));

            }
        }
    }

    private void animateMolecules(Molecules molecules) {
        for (int i = 0; i < molecules.getMoleculesQuantity(); i++) {
            ptr.add(createPathTransition(atoms.get(i), pathList.get(i), molecules.getDuration()));
        }
    }

    private PathTransition createPathTransition(Circle node, Path path, double time) {
        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.seconds(time));
        pathTransition.setPath(path);
        pathTransition.setNode(node);
        return pathTransition;
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

    private void simulationConditionsConfirmedEventHandler(SimulationConditionsConfirmedEvent event) {
        if (!animationPane.getChildren().isEmpty())
            animationPane.getChildren().clear();

        tbtnPause.setDisable(true);
        tbtnPlay.setDisable(true);
        tbtnStop.setDisable(true);

        SimulationConditions simulationConditions = event.getSimulationConditions();
        atoms = new ArrayList<>();
        pathList = new ArrayList<>();


        double rScaled = simulationConditions.getMoleculeRadius() / simulationConditions.getBoxSize() * animationPane.getWidth();
        for (int i = 0; i < simulationConditions.getMoleculesQuantity(); i++) {
            atoms.add(new Circle(rScaled));
            pathList.add(new Path());
        }

        MolecularDynamics molecularDynamics = new MolecularDynamics(simulationConditions);

        animationPane.getChildren().add(calculationStatusLabel);
        calculationStatusLabel.setText("Calculation in progress");

        CompletableFuture<Molecules> futureSimulationResult = molecularDynamics.calculateSimulationConcurrent();

        futureSimulationResult.thenAccept(molecules -> EventDispatcher.getInstance()
                                                                      .dispatchEvent(new SimulationCalculationsFinishedEvent(molecules)));
    }

    private void simulationCalculationsFinishedEventHandler(SimulationCalculationsFinishedEvent event) {
        Molecules molecules = event.getMolecules();
        Platform.runLater(() -> {
            prepareAnimationData(molecules);
            ptr = new ArrayList<>();
            reloadChartData(molecules);
            animateMolecules(molecules);
            calculationStatusLabel.setText("Calculation done");
            tbtnPause.setDisable(false);
            tbtnPlay.setDisable(false);
            tbtnStop.setDisable(false);
            for (Circle C : atoms) {
                animationPane.getChildren().add(C);
            }
        });
    }
}
