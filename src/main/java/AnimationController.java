import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class AnimationController {
    @FXML
    Pane animationPane;

    @FXML
    Button tbtnPlay;

    @FXML
    Button tbtnStop;

    @FXML
    Button tbtnPause;

    List<Path> pathList;
    List<PathTransition> ptr;
    ArrayList<Circle> atoms;
    boolean stop = true;

    Label calculationStatusLabel = new Label();

    public void initialize() {
        EventDispatcher eventDispatcher = EventDispatcher.getInstance();
        eventDispatcher.registerEventHandler(SimulationConditionsConfirmedEvent.class, this::simulationConditionsConfirmedEventHandler);
        eventDispatcher.registerEventHandler(SimulationCalculationsFinishedEvent.class, this::simulationCalculationsFinishedEventHandler);
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

    private void simulationConditionsConfirmedEventHandler(SimulationConditionsConfirmedEvent event) {
        if (!animationPane.getChildren().isEmpty())
            animationPane.getChildren().clear();

        tbtnPause.setDisable(true);
        tbtnPlay.setDisable(true);
        tbtnStop.setDisable(true);

        atoms = new ArrayList<>();
        pathList = new ArrayList<>();

        calculationStatusLabel.setText("Calculation in progress");

        SimulationConditions simulationConditions = event.getSimulationConditions();

        double rScaled = simulationConditions.getMoleculeRadius() / simulationConditions.getBoxSize() * animationPane.getWidth();
        for (int i = 0; i < simulationConditions.getMoleculesQuantity(); i++) {
            atoms.add(new Circle(rScaled));
            pathList.add(new Path());
        }

        animationPane.getChildren().add(calculationStatusLabel);
    }

    private void simulationCalculationsFinishedEventHandler(SimulationCalculationsFinishedEvent event) {
        Molecules molecules = event.getMolecules();
        Platform.runLater(() -> {
            prepareAnimationData(molecules);
            ptr = new ArrayList<>();
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
