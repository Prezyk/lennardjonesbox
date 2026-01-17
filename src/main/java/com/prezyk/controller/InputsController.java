package com.prezyk.controller;

import com.prezyk.event.EventDispatcher;
import com.prezyk.event.SimulationConditionsConfirmedEvent;
import com.prezyk.event.SimulationFunctionRunEvent;
import com.prezyk.md.SimulationInput;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
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
    TextField txtSigma;

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
    Label labelInvalidSigma;

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
        //        com.prezyk.util.CSVHandler csv = new com.prezyk.util.CSVHandler();
        //        FileChooser fileChooser = new FileChooser();
        //        File file = fileChooser.showOpenDialog(new Stage());
        //        System.out.println(file.getAbsolutePath());
        //
        //        com.prezyk.md.Molecules molecules = csv.load(file.getAbsolutePath());
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
        //        com.prezyk.util.CSVHandler csv = new com.prezyk.util.CSVHandler();
        //        csv.save(molec, "GUISaveTest.csv");
    }

    @FXML
    public void btnOkAction() {
        SimulationInput simulationConditions = readSimulationConditions();

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

    @FXML
    public void btnSimulateFunction() {
        SimulationInput simulationInput = readSimulationConditions();
        BigDecimal[] distanceVector = new BigDecimal[simulationInput.getBoxSize().intValue()];
        for (int i = 0; i < simulationInput.getBoxSize().intValue(); i++) {
            distanceVector[i] = BigDecimal.valueOf(i+2);
        }
        EventDispatcher.getInstance().dispatchEvent(new SimulationFunctionRunEvent(distanceVector, simulationInput));
    }

    private BigDecimal getValidValue(TextField valueField, Label errorLabel) {
        return getValidValue(valueField, errorLabel, dummy -> Boolean.FALSE);
    }

    private BigDecimal getValidValue(TextField valueField, Label errorLabel, Function<BigDecimal, Boolean> invalidityChecker) {
        BigDecimal value = BigDecimal.ZERO;
        try {
            errorLabel.setVisible(false);
            value = BigDecimal.valueOf(Double.parseDouble(valueField.getText()));
            if (invalidityChecker.apply(value))
                throw new IllegalArgumentException();

        } catch (IllegalArgumentException e) {
            errorLabel.setVisible(true);
        }
        return value;
    }


    private SimulationInput readSimulationConditions() {
        return SimulationInput.builder()
                              .time(getValidValue(txtTime, labelInvalidTime, (timeToValidate) -> timeToValidate.compareTo(BigDecimal.ZERO) <= 0))
                              .timeStep(getValidValue(txtStep, labelInvalidStep, (stepToValidate) -> (stepToValidate.compareTo(BigDecimal.ZERO) <= 0) || (stepToValidate.compareTo(BigDecimal.valueOf(0.05)) > 0)))
                              .moleculeRadius(getValidValue(txtR0, labelInvalidR0))
                              .epsilon(getValidValue(txtEps, labelInvalidEps))
                              .mass(getValidValue(txtMass, labelInvalidMass, massToValidate -> massToValidate.compareTo(BigDecimal.ZERO) <= 0))
                              .moleculesQuantity(getValidValue(txtMolecules, labelInvalidMolecules, moleculesToValidate -> moleculesToValidate.compareTo(BigDecimal.ZERO) <= 0).intValue())
                              .boxSize(getValidValue(txtBoxSize, labelInvalidMass, massToValidate -> massToValidate.compareTo(BigDecimal.ZERO) <= 0))
                              .wallStiffness(getValidValue(txtWallStiffness, labelInvalidWallStiffness, wallStiffnessToValidate -> wallStiffnessToValidate.compareTo(BigDecimal.ZERO) <= 0))
                .sigma(getValidValue(txtSigma, labelInvalidSigma, sigmaToValidate -> sigmaToValidate.compareTo(BigDecimal.ZERO)<= 0))
                              .build();
    }
}