import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.function.Function;

public class InputsController {
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

    @FXML
    FileChooser chooser;


    public void initialize() {
        sliderMolecules.valueProperty()
                       .addListener(l -> txtMolecules.setText(String.valueOf(sliderMolecules.getValue())));
    }

    @FXML
    //TODO this does not work ???
    public void btnLoadAction() throws IOException {
//        animationPane.getChildren()
//                     .clear();
//        calculationStatusLabel.setText("Load in progress");
//        CSVHandler csv = new CSVHandler();
//        FileChooser fileChooser = new FileChooser();
//        File file = fileChooser.showOpenDialog(new Stage());
//        System.out.println(file.getAbsolutePath());
//
//        Molecules molecules = csv.load(file.getAbsolutePath());
//        pathList = new ArrayList<>();
//        ptr = new ArrayList<>();
//
//        for (int i = 0; i < molecules.getMoleculesQuantity(); i++) {
//            pathList.add(new Path());
//        }
//
//        prepareAnimationData(molecules);
//        atoms = new ArrayList<>();
//        ptr = new ArrayList<>();
//
//        double rScaled = molecules.getR() / molecules.getBoxSize() * animationPane.getWidth();
//        for (int i = 0; i < molecules.getMoleculesQuantity(); i++) {
//            atoms.add(new Circle(rScaled));
//        }
//
//        reloadChartData(molecules);
//        animateMolecules(molecules);
//        calculationStatusLabel.setText("Calculation done");
    }

    @FXML
    public void btnSaveAction() throws FileNotFoundException {
//        CSVHandler csv = new CSVHandler();
//        csv.save(molec, "GUISaveTest.csv");
    }

    @FXML
    public void btnOkAction() {
        SimulationConditions simulationConditions = readSimulationConditions();

        SimulationConditionsConfirmedEvent simulationConditionsConfirmedEvent = new SimulationConditionsConfirmedEvent(simulationConditions);
        EventDispatcher.getInstance().dispatchEvent(simulationConditionsConfirmedEvent);
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
