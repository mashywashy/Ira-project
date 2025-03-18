package org.ira.iraeval;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ira.iraeval.Process.StudentEval;
import org.ira.iraeval.Process.Subject;

public class SubjectInputController {

    @FXML
    private VBox subjectsContainer;

    @FXML
    private Button submitButton;

    @FXML
    private Button backButton;

    private List<SubjectEntry> subjectEntries = new ArrayList<>();
    private String program;
    private String studentName;
    private String studentId;
    private int year;
    private int semester;

    private class SubjectEntry {
        private ComboBox<String> subjectComboBox;
        private TextField gradeTextField;

        public SubjectEntry(ComboBox<String> subjectComboBox, TextField gradeTextField) {
            this.subjectComboBox = subjectComboBox;
            this.gradeTextField = gradeTextField;
        }

        public String getSubject() {
            return subjectComboBox.getValue();
        }

        public String getGrade() {
            return gradeTextField.getText();
        }
    }

    @FXML
    public void initialize() {
        submitButton.setOnAction(this::handleSubmit);
        backButton.setOnAction(this::handleBack);
    }

    public void setupSubjects(int count, String program, String studentName, String studentId, int year, int semester) {
        this.program = program;
        this.studentName = studentName;
        this.studentId = studentId;
        this.year = year;  // Store year
        this.semester = semester;  // Store semester

        // Get all subjects for the program to populate the ComboBoxes
        StudentEval studentEval = new StudentEval(program);
        List<Subject> allProgramSubjects = studentEval.getAllSubjects();
        List<String> subjectCodes = new ArrayList<>();

        for (Subject subject : allProgramSubjects) {
            if(subject.getCode() != null)
                subjectCodes.add(subject.getCode().toUpperCase());
        }

        subjectCodes = subjectCodes.stream().sorted().toList();

        // Create the specified number of subject entries
        for (int i = 0; i < count; i++) {
            createSubjectEntry(i + 1, subjectCodes);
        }
    }

    private void createSubjectEntry(int index, List<String> subjectCodes) {
        HBox entryContainer = new HBox(10);
        entryContainer.setPadding(new Insets(5, 10, 5, 10));

        Label indexLabel = new Label("Subject " + index + ":");
        indexLabel.setPrefWidth(80);

        ComboBox<String> subjectComboBox = new ComboBox<>();
        subjectComboBox.getItems().addAll(subjectCodes);
        subjectComboBox.setPrefWidth(150);
        subjectComboBox.setPromptText("Select Subject");

        Label gradeLabel = new Label("Grade:");
        gradeLabel.setPrefWidth(50);

        TextField gradeTextField = new TextField();
        gradeTextField.setPrefWidth(100);

        // Restrict input to numeric values with optional decimal
        TextFormatter<String> textFormatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("[0-9]*\\.?[0-9]*")) {
                return change;
            }
            return null;
        });
        gradeTextField.setTextFormatter(textFormatter);

        entryContainer.getChildren().addAll(indexLabel, subjectComboBox, gradeLabel, gradeTextField);
        subjectsContainer.getChildren().add(entryContainer);

        subjectEntries.add(new SubjectEntry(subjectComboBox, gradeTextField));
    }

    private void handleSubmit(ActionEvent event) {
        boolean isValid = true;
        List<String> errorMessages = new ArrayList<>();

        for (int i = 0; i < subjectEntries.size(); i++) {
            SubjectEntry entry = subjectEntries.get(i);
            String subject = entry.getSubject();
            String gradeText = entry.getGrade().trim();

            if (subject == null) {
                isValid = false;
                errorMessages.add("Subject " + (i + 1) + ": Please select a subject.");
            }
            if (gradeText.isEmpty()) {
                isValid = false;
                errorMessages.add("Subject " + (i + 1) + ": Grade is required.");
            } else {
                try {
                    double grade = Double.parseDouble(gradeText);
                    if (grade < 1.0 || grade > 5.0) {
                        isValid = false;
                        errorMessages.add("Subject " + (i + 1) + ": Grade must be between 1.0 and 5.0.");
                    }
                } catch (NumberFormatException e) {
                    isValid = false;
                    errorMessages.add("Subject " + (i + 1) + ": Invalid grade format.");
                }
            }
        }

        if (!isValid) {
            showAlert(AlertType.ERROR, "Error", String.join("\n", errorMessages));
            return;
        }

        Map<String, Boolean> subjectStatusMap = new HashMap<>();
        for (SubjectEntry entry : subjectEntries) {
            String subject = entry.getSubject().toLowerCase();
            double grade = Double.parseDouble(entry.getGrade().trim());
            subjectStatusMap.put(subject, grade <= 3.0);
        }

        StudentEval studentEval = new StudentEval(program);
        List<Subject> recommendedSubjects = studentEval.getRecommendedSubjects(subjectStatusMap, year, semester);
        openRecommendedSubjectsWindow(recommendedSubjects);
    }

    private void handleBack(ActionEvent event) {
        try {
            // Load the main screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("screen1.fxml"));
            Parent root = loader.load();

            javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();

            // Create new scene with the main screen
            Scene scene = new Scene(root);

            // Get the current stage and set the new scene
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setWidth(screenBounds.getWidth());
            stage.setHeight(screenBounds.getHeight());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Navigation Error", "Could not return to main screen.");
        }
    }

    private void openRecommendedSubjectsWindow(List<Subject> recommendedSubjects) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("subject_output.fxml"));
            Parent root = loader.load();

            SubjectOutputController controller = loader.getController();
            controller.setupRecommendedSubjects(studentName, studentId, program, recommendedSubjects);

            Stage stage = new Stage();
            stage.setTitle("Recommended Subjects");
            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.show();

            // Close the current window
            ((Stage) submitButton.getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
